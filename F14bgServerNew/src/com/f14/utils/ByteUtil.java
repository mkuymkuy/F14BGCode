package com.f14.utils;

public class ByteUtil {

	/**
	 * 将4个byte转换成int
	 * 
	 * @param ba
	 * @return
	 */
	public static int b2toi(byte[] ba){
		return (ba[0]<<8 & 0xff00) | (ba[1] & 0xff);
	}
	
	/**
	 * 将4个byte转换成int
	 * 
	 * @param ba
	 * @return
	 */
	public static int b4toi(byte[] ba){
		return (ba[0]<<24) | ((ba[1]<<24)>>>8) |( (ba[2]<<8) & 0xff00) | (ba[3] & 0xff);
	}
	
	/**
	 * 将int转换为2个byte
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] itob2(int i){
		byte[] b=new byte[2];
        b[1] = (byte)(i & 0xff);
        b[0] = (byte)((i >> 8) & 0xff);
        return b;
	}
	
	/**
	 * 将int转换为4个byte
	 * 
	 * @param i
	 * @return
	 */
	public static byte[] itob4(int i){
		byte[] b=new byte[4];
        b[3] = (byte)(i & 0xff);
        b[2] = (byte)((i >> 8) & 0xff);
        b[1]= (byte)((i >> 16) & 0xff);
        b[0]= (byte)(i >>> 24);
        return b;
	}
	
	public static void main(String[] args){
		byte[] ba = itob2(1);
		for(byte e : ba){
			System.out.println(e);
		}
		
		System.out.println(b2toi(ba));
	}
}
