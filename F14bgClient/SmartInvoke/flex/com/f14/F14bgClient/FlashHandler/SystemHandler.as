
/*******************************************************************************
* Copyright (c) 2009 SmartInvoke.
* 此代理类由CodeTransform工具自动生成
* 您可以通过 
*  网站:http://smartinvoke.cn/ 
*  邮件:pzxiaoxiao130130@gmail.com
*  QQ：90636900*  联系到作者^_^ 
*******************************************************************************/ 
package com.f14.F14bgClient.FlashHandler
{
 import cn.smartinvoke.RemoteObject;
 public class SystemHandler extends RemoteObject {
  public function SystemHandler(){
	 super();
	  //调用服务方构造SystemHandler类型服务对象
	 	this.createRemoteObject();
  }

   public function loadLocalProperties():String{
	 var retObj:Object=this.call("loadLocalProperties",arguments);
	 return retObj as String;

   }
   public function dispose():void{
	 var retObj:Object=this.call("dispose",arguments);

   }
   public function connectToServer(host:String,port:int):void{
	 var retObj:Object=this.call("connectToServer",arguments);

   }
   public function loadServerList():void{
	 var retObj:Object=this.call("loadServerList",arguments);

   }
 }

}
