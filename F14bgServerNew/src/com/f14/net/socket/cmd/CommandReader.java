package com.f14.net.socket.cmd;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;

import com.f14.F14bg.consts.CmdConst;
import com.f14.net.socket.exception.ConnectionException;
import com.f14.utils.ByteUtil;
import com.f14.utils.ZipUtils;

public class CommandReader {
	private static Logger log = Logger.getLogger(CommandReader.class);
	
	public static String TAG_HEAD = "HEAD:";
	public static String TAG_FLAG = "FLAG:";
	public static String TAG_SIZE = "SIZE:";
	public static String TAG_CONTENT = "CONTENT:";
	public static String TAG_TAIL = "TAIL:";
	
	public static int CACHE_SIZE = 1024 * 100;
	
	protected InputStream is;
	protected ChannelBuffer cb = new DynamicChannelBuffer(128);
	
	public CommandReader(InputStream is){
		this.is = is;
	}
	
	/**
	 * 连接成功时需要进行检验
	 * 检验前2个byte是否符合规定
	 * 
	 * @return
	 */
	public synchronized boolean validateConnection(){
		try {
			byte[] ba = new byte[2];
			ba[0] = (byte)is.read();
			ba[1] = (byte)is.read();
			if(ByteUtil.b2toi(ba)==CmdConst.APPLICATION_FLAG){
				return true;
			}
		} catch (IOException e) {
			log.error(e, e);
		}
		return false;
	}
	
	/**
	 * 读取指令,如果返回null,则表示读取指令失败
	 * 
	 * 指令格式为:2位头信息+2位标志+4位长度信息+指令内容+2位结束信息
	 * 
	 * @return
	 * @throws IOException
	 */
	public synchronized ByteCommand readCommand() throws IOException, ConnectionException{
		try {
			ByteCommand cmd = new ByteCommand();
			int read = 0, step = 0;//, avail = 0;
			byte[] ba = new byte[256];
			byte[] bs;
			read:
			do{
				switch(step){
				case 0: //读取头信息
					if(cb.readableBytes()>=14){
						bs = new byte[2];
						cb.readBytes(bs, 0, 2);
						cmd.head = ByteUtil.b2toi(bs);
						if(cmd.head!=ByteCommand.BYTE_HEAD){
							log.error("非法头信息!切断连接!");
							is.close();
							return null;
						}
						//标志信息
						cb.readBytes(bs, 0, 2);
						cmd.flag = ByteUtil.b2toi(bs);
						bs = new byte[4];
						//房间信息
						cb.readBytes(bs, 0, 4);
						cmd.roomId = ByteUtil.b4toi(bs);
						//压缩信息
						cb.readBytes(bs, 0, 2);
						cmd.zip = ByteUtil.b2toi(bs);
						//长度信息
						cb.readBytes(bs, 0, 4);
						cmd.size = ByteUtil.b4toi(bs);
						step++;
						continue;
					}
					break;
				case 1: //读取内容
					if(cb.readableBytes()>=cmd.size){
						cmd.contentBytes = new byte[cmd.size];
						cb.readBytes(cmd.contentBytes, 0, cmd.size);
						step++;
						continue;
					}
					break;
				case 2: //读取尾信息
					if(cb.readableBytes()>=2){
						bs = new byte[2];
						cb.readBytes(bs, 0, 2);
						cmd.tail = ByteUtil.b2toi(bs);
						if(cmd.tail!=ByteCommand.BYTE_TAIL){
							log.error("非法结束信息!切断连接!");
							is.close();
							return null;
						}
						//读取指令完成,跳出循环,返回指令
						//判断数据是否经过压缩,如果经过压缩,则需要解压
						if(cmd.zip==1){
							cmd.contentBytes = ZipUtils.decompressBytes(cmd.contentBytes);
						}
						break read;
					}
					break;
				}
				//if((avail=is.available())>0){
				read = is.read(ba);
				cb.writeBytes(ba, 0, read);
			}while(is.available()!=-1);
			//清除已读的信息
			cb.discardReadBytes();
			return cmd;
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ConnectionException("远程连接发生错误!", e);
		}
	}

	/**
	 * 读取指令,如果返回null,则表示读取指令失败
	 * 
	 * 指令格式为:2位头信息+2位标志+4位长度信息+指令内容+2位结束信息
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
//	public static ByteCommand readCommand(InputStream is) throws IOException{
//		byte[] ba = new byte[2];
//		if(is.read(ba)==-1){
//			log.warn("读取头信息时连接关闭!");
//			is.close();
//			return null;
//		}
//		ByteCommand cmd = new ByteCommand();
//		cmd.head = ByteUtil.b2toi(ba);
//		if(cmd.head!=ByteCommand.BYTE_HEAD){
//			log.error("非法头信息!切断连接!");
//			is.close();
//			return null;
//		}
//		
//		ba = new byte[2];
//		if(is.read(ba)==-1){
//			log.warn("读取标志信息时连接关闭!");
//			is.close();
//			return null;
//		}
//		cmd.flag = ByteUtil.b2toi(ba);
//		
//		ba = new byte[4];
//		if(is.read(ba)==-1){
//			log.warn("读取长度信息时连接关闭!");
//			is.close();
//			return null;
//		}
//		cmd.size = ByteUtil.b4toi(ba);
//		
//		cmd.contentBytes = new byte[cmd.size];
//		int c = 0;
//		int restSize = cmd.size;
//		while(restSize>0){
//			c = (restSize>CACHE_SIZE)?CACHE_SIZE:restSize;
//			if(is.read(cmd.contentBytes, cmd.size-restSize, c)==-1){
//				log.warn("读取内容信息时连接关闭!");
//				is.close();
//				return null;
//			}
//			restSize -= c;
//		}
//		
//		ba = new byte[2];
//		if(is.read(ba)==-1){
//			log.warn("读取结束信息时连接关闭!");
//			is.close();
//			return null;
//		}
//		cmd.tail = ByteUtil.b2toi(ba);
//		if(cmd.tail!=ByteCommand.BYTE_TAIL){
//			log.error("非法结束信息!切断连接!");
//			is.close();
//			return null;
//		}
//		
//		return cmd;
//	}
	
//	class ByteReader{
//		InputStream is;
//		ChannelBuffer cb = new DynamicChannelBuffer(128);
//		
//		ByteReader(InputStream is){
//			this.is = is;
//		}
//		
//		public ByteCommand readCommand() throws IOException{
//			ByteCommand cmd = new ByteCommand();
//			int read = 0, step = 0;
//			byte[] ba = new byte[128];
//			byte[] bs;
//			read:
//			while(is.available()!=-1){
//				read = is.read(ba);
//				cb.writeBytes(ba, 0, read);
//				switch(step){
//				case 0: //读取头信息
//					if(cb.readableBytes()>=8){
//						bs = new byte[2];
//						cb.readBytes(bs, 0, 2);
//						cmd.head = ByteUtil.b2toi(bs);
//						if(cmd.head!=ByteCommand.BYTE_HEAD){
//							log.error("非法头信息!切断连接!");
//							is.close();
//							return null;
//						}
//						//标志信息
//						cb.readBytes(bs, 0, 2);
//						cmd.flag = ByteUtil.b2toi(bs);
//						//长度信息
//						cb.readBytes(bs, 0, 4);
//						cmd.size = ByteUtil.b2toi(bs);
//						step++;
//					}
//					break;
//				case 1: //读取内容
//					if(cb.readableBytes()>=cmd.size){
//						cmd.contentBytes = new byte[cmd.size];
//						cb.readBytes(cmd.contentBytes, 0, cmd.size);
//						step++;
//					}
//					break;
//				case 2: //读取尾信息
//					if(cb.readableBytes()>=2){
//						bs = new byte[2];
//						cb.readBytes(bs, 0, 2);
//						cmd.tail = ByteUtil.b2toi(bs);
//						if(cmd.tail!=ByteCommand.BYTE_TAIL){
//							log.error("非法结束信息!切断连接!");
//							is.close();
//							return null;
//						}
//						//读取指令完成,跳出循环,返回指令
//						break read;
//					}
//					break;
//				}
//			}
//			//清除已读的信息
//			cb.discardReadBytes();
//			return cmd;
//		}
//	}
//	
//	public static void main(String[] args){
//		ChannelBuffer cb = new DynamicChannelBuffer(2);
//		byte[] bb = new byte[]{1,2,3,4};
//		//Arrays.fill(bb, (byte)1);
//		cb.writeBytes(bb);
//		Arrays.fill(bb, (byte)1);
//		cb.writeBytes(bb);
//		bb = new byte[4];
//		System.out.println(cb.readableBytes());
//		cb.readBytes(bb, 0, 2);
//		cb.readBytes(bb, 2, 2);
//		System.out.println(cb.readableBytes());
//		cb.discardReadBytes();
//		System.out.println(cb.readableBytes());
//		cb.readBytes(bb, 0, 4);
//		System.out.println(cb.readableBytes());
//		cb.discardReadBytes();
//	}
}
