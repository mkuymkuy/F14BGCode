package com.f14.F14bgClient.manager;

/**
 * 业务类容器
 * 
 * @author F14eagle
 *
 */
public class ManagerContainer {
	public static ConnectionManager connectionManager;
	public static PropertiesManager propertiesManager;
	public static PathManager pathManager;
	public static ShellManager shellManager;
	public static CodeManager codeManager;
	public static FileManager fileManager;
	public static ResourceManager resourceManager;
	public static ActionManager actionManager;
	public static UpdateManager updateManager;
	public static NotifyManager notifyManager;
	
	static{
		connectionManager = new ConnectionManager();
		propertiesManager = new PropertiesManager();
		pathManager = new PathManager();
		shellManager = new ShellManager();
		codeManager = new CodeManager();
		fileManager = new FileManager();
		resourceManager = new ResourceManager();
		actionManager = new ActionManager();
		updateManager = new UpdateManager();
		notifyManager = new NotifyManager();
	}
	
}
