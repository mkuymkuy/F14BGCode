import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Test {

	public static void main(String[] args) throws InterruptedException,
			IOException {
		// Random ran = new Random();
		ProcessBuilder pb = new ProcessBuilder("f14bgServer.bat");
		pb.directory(new File("E:/f14bg_server/"));
		Process p = pb.start();
		byte[] b = new byte[1024];

		int readbytes = -1;

		// 读取进程输出值

		// 在JAVA IO中,输入输出是针对JVM而言,读写是针对外部数据源而言

		InputStream in = p.getInputStream();
		try {
			while ((readbytes = in.read(b)) != -1) {
				System.out.println(new String(b, 0, readbytes));
				System.exit(0);
				break;
			}
		} catch (IOException e1) {

		} finally {
		}
	}
}
