import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class ProxyTest {

	public void setProxy() {
		Properties prop = System.getProperties();
		prop.setProperty("http.proxyHost", "212.116.220.149");
		// 设置http访问要使用的代理服务器的端口
		prop.setProperty("http.proxyPort", "443");
		// 设置不需要通过代理服务器访问的主机，可以使用*通配符，多个地址用|分隔
		prop.setProperty("http.nonProxyHosts", "localhost|10.10.*");
	}

	// 测试http
	public void showHttpProxy(Object... proxy) {
		URL url = null;
		try {
			url = new URL("http://joylink.me/bbs");
		} catch (MalformedURLException e) {
			return;
		}
		try {
			URLConnection conn = null;
			switch (proxy.length) {
			case 0:
				conn = url.openConnection();
				break;
			case 1:
				conn = url.openConnection((Proxy) proxy[0]);
				break;
			default:
				break;
			}

			if (conn == null)
				return;

			conn.setConnectTimeout(30000); // 设置连接超时时间
			InputStream in = conn.getInputStream();
			byte[] b = new byte[1024];
			try {
				while (in.read(b) > 0) {
					System.out.println(new String(b));
				}
			} catch (IOException e1) {
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	// 得到一个proxy
	public Proxy getProxy(Proxy.Type type, String host, int port) {
		SocketAddress addr = new InetSocketAddress(host, port);
		Proxy typeProxy = new Proxy(type, addr);
		return typeProxy;
	}

	public static void main(String[] args) throws IOException {
		ProxyTest proxy = new ProxyTest();
		// 测试代理服务器
		//proxy.setProxy();
		
		Proxy p = proxy.getProxy(Proxy.Type.SOCKS, "212.116.220.149", 443);
		
		Socket socket = new Socket(p);
		socket.connect(new InetSocketAddress("219.141.190.104", 8181));
		//proxy.showHttpProxy(p);

		// 下面两行是清除系统属性，而通过Proxy类指定代理服务器
		// proxy.removeLocalProxy
		// proxy.showHttpProxy(proxy.getProxy(Proxy.Type.SOCKS,"10.10.0.96",1080));

	}
}
