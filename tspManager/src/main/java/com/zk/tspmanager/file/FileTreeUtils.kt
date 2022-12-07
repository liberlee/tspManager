package com.zk.tspmanager.file

import android.text.TextUtils
import com.zk.tspmanager.scope.ScopeName
import com.zk.tspmanager.scope.ScopeTools
import com.zk.tspmanager.utils.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream

/**
 * 文件遍历工具
 */
object FileTreeUtils {

    const val TAG: String = "FileTreeUtils";
    const val flagPath: String = "/data/ota_package/complete.flag";
    const val basePath: String = "/data/ota_package/";
    const val mcuShellPath: String = "/share/mcu_package/";//这个是android侧的路径，建立文件夹后/nfs会自动跟随创建同样的
    const val mcuQnxPath: String = "/nfs/mcu_package/";//给MCU传升级路径时用这个+升级文件

    /**
     * 遍历该目录下的zip文件
     */
    fun getFileTreeZip(path: String): MutableList<String> {
        var fileNames: MutableList<String> = mutableListOf<String>()
        //在该目录下走一圈，得到文件目录树结构
        val fileTree: FileTreeWalk = File(path).walk()
        fileTree.maxDepth(1) //需遍历的目录层级为1，即无需检查子目录
            .filter { it.isFile } //只挑选文件，不处理文件夹
            .filter { it.extension == "zip" } //选择扩展名为txt的文本文件 also: it.extension in listOf("png","jpg")
            .forEach { fileNames.add(it.name) } //循环处理符合条件的文件
        return fileNames;
    }

    /**
     * 遍历该目录下的bin文件
     */
    fun getFileTreeBin(path: String): MutableList<String> {
        var fileNames: MutableList<String> = mutableListOf<String>()
        //在该目录下走一圈，得到文件目录树结构
        val fileTree: FileTreeWalk = File(path).walk()
        fileTree.maxDepth(1) //需遍历的目录层级为1，即无需检查子目录
            .filter { it.isFile } //只挑选文件，不处理文件夹
            .filter { it.extension == "bin" } //选择扩展名为txt的文本文件 also: it.extension in listOf("png","jpg")
            .forEach { fileNames.add(it.name) } //循环处理符合条件的文件
        return fileNames;
}

    /**
     * 遍历该目录下的bin & zip文件
     */
    fun getFileTreeBinAndZip(path: String): MutableList<String> {
        var fileNames: MutableList<String> = mutableListOf<String>()
        //在该目录下走一圈，得到文件目录树结构
        val fileTree: FileTreeWalk = File(path).walk()
        fileTree.maxDepth(1) //需遍历的目录层级为1，即无需检查子目录
            .filter { it.isFile } //只挑选文件，不处理文件夹
            .filter {
                it.extension in listOf(
                    "bin", "zip"
                )
            } //选择扩展名为txt的文本文件 also: it.extension in listOf("png","jpg")
            .forEach { fileNames.add(it.name) } //循环处理符合条件的文件
        return fileNames;
    }

    /**
     * 复制文件
     */
    private fun copyFile(srcFilePath: String, destFilePath: String): Boolean {
        try {
            val destFile = File(destFilePath)
            if (destFile.exists()) {
                LogUtil.ilog(TAG, "destFile exists, delete old file!")
                destFile.deleteRecursively()
            }
            destFile.createNewFile()
            FileReader(srcFilePath).use { fis ->
                FileWriter(destFilePath).use { fos ->
                    LogUtil.ilog(TAG, "FileWriter start copy")
                    //创建字符流输入缓冲流
                    val bis = fis.buffered()
                    //创建字符流输出缓冲流
                    val bos = fos.buffered()
                    //数据复制
                    val size = bis.copyTo(bos)
                    LogUtil.dlog(TAG, "复制完毕：复制得到的字符数量：$size")
                    bos.flush()//关闭
                    bos.close()
                    bis.close()
                    fos.close()
                    fis.close()
                    return true
                }
            }
        } catch (ioe: Exception) {
            ioe.printStackTrace()
            return false
        }
    }

    /**
     * 单个文件复制
     */
    private fun copyFileWithProgress(srcFilePath: String, destFilePath: String, listener: CopyStatusListener): Boolean {
        LogUtil.ilog(TAG, "copyFileWithProgress start copy")
        val srcFile = File(srcFilePath)
        val destFile = File(destFilePath)
        if (destFile.exists()) {
            LogUtil.ilog(TAG, "destFile exists, delete old file!")
            destFile.deleteRecursively()
        }
        if (!destFile.exists()) {
            if(destFile.isDirectory){
                var mkdirSuccess = destFile.mkdirs();
                if (!mkdirSuccess) {
                    LogUtil.ilog(TAG, "mkdirSuccess false")
                    return false
                }
            } else {
                if (destFile.parentFile != null) {
                    destFile.parentFile.mkdirs()
                    destFile.createNewFile()
                }
            }
        }
        var fis = FileInputStream(srcFile);
        var fos = FileOutputStream(destFile)
        var bis = BufferedInputStream(fis)
        var bos = BufferedOutputStream(fos)
        try {
            val srcTotalLen = srcFile.length()
            LogUtil.ilog(TAG, "srcFile.length(): $srcTotalLen")
            var buf = ByteArray(2048)

            var len = 0;
            var totalCopy = 0;
            while (true) {
                len = bis.read(buf)
                totalCopy += len
                //LogUtil.ilog(TAG, "total copy len: $totalCopy")
                listener.onCopyProgress("$totalCopy", "$srcTotalLen")
                if (len == -1) break;
                bos.write(buf, 0, len)
            }
            return true
        } catch (exp: Exception) {
            exp.printStackTrace()
            return false
        } finally {
            bos.close()
            bis.close()
            fis.close()
            fos.close()
        }
    }

    /**
     * 异步协程拷贝
     */
    fun copyFileInScope(srcFile: String, destFile: String, listener: CopyStatusListener) {
        ScopeTools.executeIO(ScopeName.COPY_UPDATE_FILE.scopeName, object : ScopeTools.IExecutor {
            override suspend fun doInScope(): Boolean {
                LogUtil.ilog(TAG, "copyFileInScope")
                val copy = copyFileWithProgress(srcFile, destFile, listener)
                if (copy) {
                    listener.onCopyStatus(COPY_COMPLETE)
                } else {
                    val dFile = File(destFile)
                    if (dFile.exists()) {
                        dFile.deleteRecursively()
                    }
                    listener.onCopyStatus(COPY_FAIL)
                }
                return true
            }
        })
    }

    /**
     * 异步双升级包协程拷贝
     */
    fun copyFileInScope(srcFile1: String, srcFile2: String, destFile1: String, destFile2: String, listener: CopyStatusListener) {
        ScopeTools.executeIO(ScopeName.COPY_UPDATE_FILE.scopeName, object : ScopeTools.IExecutor {
            override suspend fun doInScope(): Boolean {
                val copy = copyFileWithProgress(srcFile1, destFile1, listener)
                if (copy) {
                    val copy2 = copyFileWithProgress(srcFile2, destFile2, listener)
                    if (copy2) {
                        //创建新文件
                        withContext(Dispatchers.IO) {
                            val flagFile = File(flagPath)
                            if (!flagFile.exists()) {
                                flagFile.createNewFile()
                            }
                        }
                        listener.onCopyStatus(COPY_COMPLETE)
                    } else {
                        val dFile1 = File(destFile1)
                        val dFile2 = File(destFile2)
                        if (dFile1.exists()) {
                            dFile1.deleteRecursively()
                        }
                        if (dFile2.exists()) {
                            dFile2.deleteRecursively()
                        }
                        listener.onCopyStatus(COPY_FAIL)
                    }
                } else {
                    val dFile = File(destFile1)
                    if (dFile.exists()) {
                        dFile.deleteRecursively()
                    }
                    listener.onCopyStatus(COPY_FAIL)
                }
                return true
            }
        })
    }

    /**
     * 升级完成清除升级文件
     */
    fun clearUpdateFile(listener: ClearStatusListener) {
        ScopeTools.executeIO(ScopeName.CLEAR_UPDATE_FILE.scopeName, object : ScopeTools.IExecutor {
            override suspend fun doInScope(): Boolean {
                //删除flag文件
                val flagFile = File(flagPath)
                if (flagFile.isFile && flagFile.exists()) {
                    flagFile.delete()
                }
                //删除zip文件
                val zipList: MutableList<String> = getFileTreeZip(basePath);
                //遍历删除zip
                zipList.forEach {
                    LogUtil.dlog(TAG, "zipList: $it")
                    var path: String = it
                    if (!it.contains("data/ota_package")) {
                        path = basePath + it
                    }
                    LogUtil.dlog(TAG, "delete path: $path")
                    val deleteFile = File(path)
                    if (deleteFile.isFile && deleteFile.exists()) {
                        deleteFile.delete()
                    }
                }
                listener.onClearStatus(CLEAR_COMPLETE)
                return true
            }
        })
    }

    /**
     * 升级完成清除MCU升级文件
     */
    fun clearMcuUpdateFile(listener: ClearStatusListener) {
        ScopeTools.executeIO(ScopeName.CLEAR_MCU_UPDATE_FILE.scopeName, object : ScopeTools.IExecutor {
            override suspend fun doInScope(): Boolean {
                //删除zip & bin文件
                val zipAndBinList: MutableList<String> = getFileTreeBinAndZip(mcuShellPath);
                //遍历删除zip & bin
                zipAndBinList.forEach {
                    LogUtil.dlog(TAG, "zipList: $it")
                    var path: String = it
                    if (!it.contains("share/zkang/mcu_package")) {
                        path = mcuShellPath + it
                    }
                    LogUtil.dlog(TAG, "delete path: $path")
                    val deleteFile = File(path)
                    if (deleteFile.isFile && deleteFile.exists()) {
                        deleteFile.delete()
                    }
                }
                listener.onClearStatus(CLEAR_COMPLETE)
                return true
            }
        })
    }

    /**
     * 解压文件
     */
    fun unZipFileInScope(zipFile: String, targetFile: String, listener: UnZipStatusListener) {
        LogUtil.ilog(TAG, "unZipFileInScope in")
        ScopeTools.executeIO(ScopeName.UNZIP_FILE.scopeName, object : ScopeTools.IExecutor {
            override suspend fun doInScope(): Boolean {
                LogUtil.ilog(TAG, "unZipFileInScope")
//                val unzipped = unZipFile(zipFile, targetFile, listener)
                val unzipped = unzip(zipFile, targetFile, listener)
                if (unzipped) {
                    listener.onUnZipStatus(COPY_COMPLETE)
                } else {
                    val dFile = File(targetFile)
                    if (dFile.exists()) {
                        dFile.deleteRecursively()
                    }
                    listener.onUnZipStatus(COPY_FAIL)
                }
                return true
            }
        })
    }

    /**
     * 创建文件夹
     */
    private fun mkdir(file: File) {
        if (null == file || file.exists()) {
            return
        } else {
            file.parentFile.mkdir()
            file.mkdir()
        }
    }

    /**
     * 解压文件
     * 目前支持范围：无文件夹压缩包和带文件夹压缩包
     */
    private fun unzip(zipFilePath: String, desDirectory: String, listener: UnZipStatusListener) : Boolean {
        var desDir = File(desDirectory)
        if (!desDir.exists()) {
            var mkdirSuccess = desDir.mkdir();
            if (!mkdirSuccess) {
                throw Exception("创建解压目标文件夹失败")
                return false
            }
        }
        val zf = ZipFile(zipFilePath)
        val entries = zf.entries()
        LogUtil.ilog(TAG, "entries.toList: ${entries.toList()}")
        val listSize = 3
        var totalUnzip = 0

        // 读入流（第二个参数，处理压缩文件中文异常。如果没有中文，可以不写第二个参数）
        var zipInputStream = ZipInputStream(FileInputStream(zipFilePath), Charset.forName("UTF-8"))
        // 遍历每一个文件
        var zipEntry = zipInputStream.nextEntry
        try {
            while (zipEntry != null) {
                LogUtil.ilog(TAG, zipEntry.toString())
                var unzipFilePath = desDirectory + File.separator + zipEntry.name
                if (zipEntry.isDirectory) {
                    // 直接创建（注意，不是使用系统的mkdir,自定义方法）
                    mkdir(File(unzipFilePath))
                } else {
                    var file = File(unzipFilePath)
                    // 创建父目录（注意，不是使用系统的mkdir,自定义方法）
                    file.parentFile?.let { mkdir(it) }
                    // 写出文件
                    var bufferedOutputStream = BufferedOutputStream(FileOutputStream(unzipFilePath))
                    val bytes = ByteArray(1024)
                    var readLen: Int
                    // Java 与 Kotlin的不同之处，需要特别关注。
                    // while ((readLen = zipInputStream.read(bytes))!=-1){
                    while (zipInputStream.read(bytes).also { readLen = it } > 0) {
                        bufferedOutputStream.write(bytes, 0, readLen)
                    }
                    bufferedOutputStream.close()
                }
                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry

                totalUnzip += 1
                listener.onUnZipProgress("$totalUnzip", "$listSize")
            }
        } catch (exp: Exception) {
            exp.printStackTrace()
            return false
        } finally {
            zipInputStream.close()
            //File(zipFilePath).delete()
        }
        return true
    }

    private fun unZipFile(zipFile: String, targetFile: String, listener: UnZipStatusListener): Boolean {
        if (TextUtils.isEmpty(zipFile) || TextUtils.isEmpty(targetFile)) {
            LogUtil.ilog(TAG, "unZipFile file path empty!")
            return false
        }
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null
        LogUtil.ilog(TAG, "unZipFile zipFile: $zipFile targetFile: $targetFile")
        try {
            File(targetFile).run {
                if (!exists()) {
                    mkdirs()
                }
            }
            val zf = ZipFile(zipFile)
            val entries = zf.entries()
            LogUtil.ilog(TAG, "entries.toList: ${entries.toList()}")
            val listSize = entries.toList().size
            var totalUnzip = 0
            while (entries.hasMoreElements()) {
                val zipEntry: ZipEntry = entries.nextElement()
                val zipEntryName = zipEntry.name
                LogUtil.ilog(TAG, "zipEntryName: $zipEntryName")
                inputStream = zf.getInputStream(zipEntry)
                if (zipEntryName.startsWith("__MACOSX")) {
                    //忽略macos系统残留文件
                    continue
                }
                File(targetFile, zipEntryName).apply {
                    if (isDirectory) {
                        if (!exists()) {
                            mkdirs()
                        }
                    } else {
                        createNewFile()
                        writeStreamToFile(inputStream, this)
                    }
                }
                totalUnzip += 1
                listener.onUnZipProgress("$totalUnzip", "$listSize")
                inputStream?.close()
                outputStream?.close()
            }
            //File(zipFile).delete()
            return true
        } catch (exp: Exception) {
            exp.printStackTrace()
            return false
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

    private fun writeStreamToFile(stream: InputStream, file: File) {
        try {
            val fos = FileOutputStream(file)

            val buffer = ByteArray(1024)
            var len: Int
            var total = 0
            while ((stream.read(buffer).also { len = it }) != -1) {
                fos.write(buffer, 0, len)
                total += len
            }

            fos.flush()
            fos.close()
            stream.close()
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
    }


    const val UNZIP_COMPLETE: Int = 0
    const val UNZIP_FAIL: Int = 1

    interface UnZipStatusListener {
        fun onUnZipStatus(status: Int);
        fun onUnZipProgress(progress: String, total: String);
    }

    const val COPY_COMPLETE: Int = 0
    const val COPY_FAIL: Int = 1

    interface CopyStatusListener {
        fun onCopyStatus(status: Int);
        fun onCopyProgress(progress: String, total: String);
    }


    const val CLEAR_COMPLETE: Int = 0
    const val CLEAR_FAIL: Int = 1

    interface ClearStatusListener {
        fun onClearStatus(status: Int);
    }

}
