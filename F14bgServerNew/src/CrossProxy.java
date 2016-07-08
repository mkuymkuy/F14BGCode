import java.net.Socket;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.DynamicChannelBuffer;


public class CrossProxy {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//Socket socket = new Socket("212.116.220.149", 443);
			Socket socket = new Socket("212.93.193.90", 443);
			//String str = "CONNECT 219.141.190.104:8181 HTTP/1.1\n";
			//String str = "CONNECT 219.133.60.173:443 HTTP/1.1\n";
			String str = "CONNECT 143.89.197.162:80 HTTP/1.1\n";
			socket.getOutputStream().write(str.getBytes());
			str = "\n";
			socket.getOutputStream().write(str.getBytes());
			
			ChannelBuffer cb = new DynamicChannelBuffer(128);
			int read = 0;
			byte[] ba = new byte[128];
			while((read = socket.getInputStream().read(ba))>-1){
				System.out.println(read);
				cb.writeBytes(ba, 0, read);
				String cmd = new String(cb.toByteBuffer().array());
				System.out.println(cmd);
				if(cmd.indexOf(" 200 ")>-1){
					str = "fuck you\n";
					socket.getOutputStream().write("<policy-file-request/>\n".getBytes());
					//socket.getOutputStream().write(str.getBytes());
				}
			}
			
			socket = new Socket("212.93.193.90", 443);
			str = "CONNECT 143.89.197.162:80 HTTP/1.1\n";
			socket.getOutputStream().write(str.getBytes());
			str = "\n";
			socket.getOutputStream().write(str.getBytes());
			while((read = socket.getInputStream().read(ba))>-1){
				System.out.println(read);
				cb.writeBytes(ba, 0, read);
				String cmd = new String(cb.toByteBuffer().array());
				System.out.println(cmd);
				if(cmd.indexOf(" 200 ")>-1){
					str = "fuck you\n";
					socket.getOutputStream().write("<policy-file-request/>\n".getBytes());
					//socket.getOutputStream().write(str.getBytes());
				}
			}
			
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
