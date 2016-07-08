
public class NotifyTest {

	public static void main(String[] args) throws InterruptedException{
		final Object lock = new Object();
		
		for(int i=0;i<5;i++){
			new Thread(new Runnable(){
	
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized (lock) {
						System.out.println("lock notify!");
						lock.notify();
					}
				}
				
			}).start();
		}
		
		synchronized (lock) {
			System.out.println("lock waiting...");
			lock.wait();
		}
		
	}
}
