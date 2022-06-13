package com.zk.tspmanager.file

import java.io.File

/**
 * 文件遍历工具
 */
class FileTreeUtils {

    /**
     * 遍历该目录下的zip文件
     */
    fun getFileTreeZip(path: String): MutableList<String> {
        lateinit var fileNames: MutableList<String>
        //在该目录下走一圈，得到文件目录树结构
        val fileTree: FileTreeWalk = File(path).walk()
        fileTree.maxDepth(1) //需遍历的目录层级为1，即无需检查子目录
            .filter { it.isFile } //只挑选文件，不处理文件夹
            .filter { it.extension == "zip" } //选择扩展名为txt的文本文件 also: it.extension in listOf("png","jpg")
            .forEach { fileNames.add(it.name) } //循环处理符合条件的文件
        return fileNames;
    }
}