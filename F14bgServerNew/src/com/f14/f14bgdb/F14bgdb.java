package com.f14.f14bgdb;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.f14.bg.exception.BoardGameException;

public class F14bgdb {
	private static ApplicationContext context;

	public static void init(){
		context = new FileSystemXmlApplicationContext("classpath:applicationContext.xml");
	}
	
	public static void init(ApplicationContext context){
		F14bgdb.context = context;
	}
	
	@SuppressWarnings("unchecked")
	public static <C> C getBean(String bean){
		return (C)context.getBean(bean);
	}
	
	public static void main(String[] args) throws BoardGameException{
//		F14bgdb.init();
//		
//		PkGenDao pd = F14bgdb.getBean("pkGenDao");
//		UserManager d = F14bgdb.getBean("userManager");
//		BoardGameDao bd = F14bgdb.getBean("boardGameDao");
//		BgInstanceDao bid = F14bgdb.getBean("bgInstanceDao");
		
//		User u = new User();
//		u.setLoginName("f14eagle");
//		u.setPassword("123456");
//		u.setUid(pd.getNextValue("UID"));
//		d.createUser(u);
//		
//		u = new User();
//		u.setLoginName("f14aaa");
//		u.setPassword("123456");
//		u.setUid(pd.getNextValue("UID"));
//		d.createUser(u);
		
//		BoardGame bg = bd.get("RFTG");
//		BgInstance bi = new BgInstance();
//		bi.setBoardGame(bg);
//		
//		List<User> l = d.query(new User());
//		for(User e : l){
//			BgInstanceRecord rec = new BgInstanceRecord();
//			rec.setUser(e);
//			rec.setRank(1);
//			rec.setScore(((Number)(Math.random()*100)).intValue());
//			bi.addBgInstanceRecord(rec);
//			System.out.println(e.getLoginName());
//		}
//		
//		bid.save(bi);
		
//		List<BgInstance> list = bid.query(new BgInstance());
//		for(BgInstance e : list){
//			//System.out.println(e.getBoardGame().getCnname());
//			//for(BgInstanceRecord e1 : e.getBgInstanceRecords()){
//			//	System.out.println(e1.getUser().getUserName() + " " + e1.getScore());
//			//}
//			bid.delete(e.getId());
//		}
	}
}
