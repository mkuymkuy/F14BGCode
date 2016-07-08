package com.f14.F14bg.network;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashMap;

import org.apache.log4j.Logger;

import com.f14.F14bg.manager.PathManager;
import com.f14.F14bg.manager.ResourceManager;
import com.f14.F14bg.utils.ResourceUtils;
import com.f14.F14bg.utils.UpdateUtil;
import com.f14.bg.hall.GameHall;
import com.f14.f14bgdb.F14bgdb;
import com.f14.f14bgdb.model.BoardGame;
import com.f14.f14bgdb.util.CodeUtil;
import com.f14.f14bgdb.util.ScoreUtil;
import com.f14.net.socket.server.SimpleServer;

public class F14bgServer extends SimpleServer{
	protected static Logger log = Logger.getLogger(F14bgServer.class);
	/**
	 * 清理超时用户的周期 - 15分钟
	 */
	public static long CLEAN_DURATION = 1000 * 60 * 15;
	private static F14bgServer instance;
	protected GameHall hall;
	protected LinkedHashMap<String, PlayerHandler> handlers = new LinkedHashMap<String, PlayerHandler>();
	
	public static F14bgServer getInstance(){
		if(instance==null){
			try {
				instance = new F14bgServer();
			} catch (IOException e) {
				log.fatal("创建服务器实例出错!", e);
			}
		}
		return instance;
	}
	
	private F14bgServer() throws IOException {
		super();
		hall = new GameHall();
	}
	
	public GameHall getGameHall(){
		return this.hall;
	}
	
	@Override
	public void startupServer() {
		try {
			PathManager.init();
			log.info("初始化持久层...");
			F14bgdb.init();
			log.info("持久层初始化完成!");
			CodeUtil.loadAllCodes();
			ScoreUtil.init();
			log.info("装载资源...");
			initResourceManager();
			log.info("资源装载完成!");
			UpdateUtil.init();
			log.info("启动自动清理超时用户的线程...");
			new RecentUserCleaner().start();
		} catch (Exception e) {
			log.error("服务器启动时发生错误! ", e);
			System.exit(-1);
		}
	}
	
	@Override
	public int getTimeoutTime() {
		return 1000 * 60 * 120;
	}


	@Override
	protected void handlerSocket(Socket s) {
		PlayerHandler handler = new PlayerHandler(s);
		handler.server = this;
		Thread t = new Thread(handler);
		t.start();
		handlers.put(handler.toString(), handler);
	}
	
	/**
	 * 初始化资源管理器
	 * @throws Exception
	 */
	protected void initResourceManager() throws Exception{
		ResourceManager rm;
		Class<?> gameClass;
		for(BoardGame bg : CodeUtil.getBoardGames()){
			log.info("装载 " + bg.getCnname() + " 的游戏资源...");
			rm = (ResourceManager)Class.forName(bg.getResourceClass()).newInstance();
			gameClass = Class.forName(bg.getGameClass());
			ResourceUtils.addResourceManager(gameClass, rm);
		}
		/*log.info("装载银河竞逐资源...");
		rm = new RaceResourceManager();
		ResourceUtils.addResourceManager(RFTG.class, rm);
		log.info("装载波多黎各资源...");
		rm = new PRResourceManager();
		ResourceUtils.addResourceManager(PuertoRico.class, rm);
		log.info("装载穿越历史资源...");
		rm = new TTAResourceManager();
		ResourceUtils.addResourceManager(TTA.class, rm);*/
		
		//测试
		//TTAEventTest.test();
//		TTAConfig config = new TTAConfig();
//		config.versions.add(BgVersion.BASE);
//		config.playerNumber = 2;
//		List<TTACard> cards = ((TTAResourceManager)rm).getCivilCards(config, 3);
//		TTAPlayer player = new TTAPlayer();
//		for(TTACard card : cards){
//			if("17.0".equals(card.cardNo)){
//				((CivilCard)card).addWorkers(3);
//				player.playCardDirect(card);
//			}
//			if("115".equals(card.id)){
//				player.playCardDirect(card);
//				System.out.println(card.name);
//			}
//		}
	}
	
	class RecentUserCleaner extends Thread{
		
		@Override
		public void run() {
			while(true){
				try {
					Thread.sleep(CLEAN_DURATION);
				} catch (InterruptedException e) {
					log.error("等待清理线程发生错误!", e);
				}
				hall.clearRecentUsers();
			}
		}
	}
	
	public static void main(String[] args) throws Exception{
		if(args.length!=1){
			throw new Exception("启动参数错误,请输入启动端口!");
		}
		SimpleServer.listenPort = Integer.valueOf(args[0]);
		F14bgServer server = null;
		int retry = 3;
		for(int i=0;i<retry;i++){
			//如果启动失败,则等待30秒后重试,可以重试3次
			server = F14bgServer.getInstance();
			if(server!=null){
				break;
			}else{
				log.fatal("Socket服务器启动失败!等待30秒后重试...");
			}
			if(i<retry-1){
				Thread.sleep(30000);
			}
		}
		if(server==null){
			log.fatal("Socket服务器启动失败!请检查端口是否被占用!");
		}else{
			Thread t = new Thread(server);
			t.start();
		}
	}

}
