# tspManager

**网络请求Module**

	进度：
	    基本功能完成，优化细节，增添功能中
		1.已完成基础网络请求，并编写Demo测试成功
		2.已完成基础网络下载，并编写Demo测试成功  
	下一阶段：
		1.增加下载文件完整性校验
		2.增加断点续传 
		3.增加DataStore封装，实现proto datastore，替代SP
		4.下载线程优化，使用协程或Rxjava（已使用协程） 
	功能：
		Ver.20220606
			基础网络请求
			基础网络下载
			新增协程Tools，可直接启动IO操作协程写入下载文件，暂时废弃AsyncTask异步线程
		Ver.20220607
			增加DataStore数据存储支持，实现proto datastore
		Ver.20220610
			增加断点续传功能
