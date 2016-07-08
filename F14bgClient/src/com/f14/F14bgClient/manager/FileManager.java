package com.f14.F14bgClient.manager;

import java.io.File;
import java.io.FileInputStream;

public class FileManager {

	/**
	 * 读取文件,返回byte数组
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public byte[] loadFile(String path) throws Exception{
		File file = new File(path);
		FileInputStream is = new FileInputStream(file);
		int size = is.available();
		byte[] bs = new byte[size];
		is.read(bs);
		return bs;
	}
	
	/**
	 * 读取文件
	 * 
	 * @param gameType
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public byte[] loadFile(String gameType, String file) throws Exception{
		String path = ManagerContainer.pathManager.getImagePath(gameType, file);
		return this.loadFile(path);
	}
}
