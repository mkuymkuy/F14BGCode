package com.f14.net.socket.cmd;

import java.io.UnsupportedEncodingException;

import com.f14.utils.ZipUtils;

/**
 * 远程通讯时用到的指令
 * 
 * 指令包括以下几个部分:
 * 
 * 头信息:发送指令方的信息
 * 指令标志:表示执行对应程序的代码
 * 内容长度:指令具体内容的长度
 * 内容:指令的具体内容
 * 结束符号:表示一条指令结束的符号
 * 
 * @author F14eagle
 *
 */
public class ByteCommand {
	public static int BYTE_HEAD = 0xf14f;
	public static int BYTE_TAIL = 0xf41f;
	/**
	 * 需要压缩数据的最小数据量
	 */
	public static int ZIP_LIMIT = 500;
	
	public int head;
	public int flag;
	public int roomId;
	/**
	 * 是否压缩 0:否 1:是
	 */
	public int zip = 0;
	public int size;
	public byte[] contentBytes;
	public int tail;
	
	protected String toString;
	protected String content;
	
	/**
	 * 是否已经发送完成
	 */
	public boolean isSent = false;
	
	public void setContent(String content){
		try {
			this.contentBytes = content.getBytes("UTF-8");
			if(this.contentBytes.length>=ZIP_LIMIT){
				//进行数据压缩
				this.zip = 1;
				this.contentBytes = ZipUtils.compressBytes(contentBytes);
			}else{
				this.zip = 0;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			this.contentBytes = new byte[0];
		}
		this.size = this.contentBytes.length;
	}
	
	/**
	 * 取得内容信息的字符串
	 * 
	 * @return
	 */
	public String getContent(){
		if(this.content==null){
			try {
				this.content = new String(contentBytes, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				this.content = "";
			}
		}
		return this.content;
	}
	
	@Override
	public String toString() {
		if(toString==null){
			if(this.zip==1){
				toString = head + " | " + flag + " | " + roomId + " | " + zip + " | " + size + " | " + "内容已压缩" + " | " + tail;
			}else{
				toString = head + " | " + flag + " | " + roomId + " | " + zip + " | " + size + " | " + getContent() + " | " + tail;
			}
		}
		return toString;
	}
}
