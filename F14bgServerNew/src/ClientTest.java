import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import com.f14.net.socket.cmd.ByteCommand;
import com.f14.net.socket.cmd.CommandFactory;
import com.f14.utils.ByteUtil;
import com.sun.corba.se.impl.ior.ByteBuffer;


public class ClientTest {

	public static void main(String[] args) throws UnknownHostException, IOException{
		Socket s = new Socket("localhost", 8181);
		PrintWriter pw = new PrintWriter(s.getOutputStream());
		pw.write("<policy-file-xxquest/>");
		pw.flush();
		
		ByteCommand cmd = CommandFactory.createCommand(1, 0, "asbc");
		pw.print(toByte(cmd));
		pw.flush();
	}
	
	/**
	 * 将cmd转换成byte数组
	 * 
	 * @param cmd
	 * @return
	 */
	public static byte[] toByte(ByteCommand cmd){
		ByteBuffer bb = new ByteBuffer();
		//byte[] ba = ByteUtil.itob2(cmd.head);
		byte[] ba = ByteUtil.itob4(0xf14f);
		for(byte e : ba){
			bb.append(e);
		}
		ba = ByteUtil.itob2(cmd.flag);
		for(byte e : ba){
			bb.append(e);
		}
		ba = ByteUtil.itob4(cmd.size);
		for(byte e : ba){
			bb.append(e);
		}
		for(byte e : cmd.contentBytes){
			bb.append(e);
		}
		ba = ByteUtil.itob2(cmd.tail);
		for(byte e : ba){
			bb.append(e);
		}
		bb.trimToSize();
		return bb.toArray();
	}
}
