
/*******************************************************************************
* Copyright (c) 2009 SmartInvoke.
* 此代理类由CodeTransform工具自动生成
* 您可以通过 
*  网站:http://smartinvoke.cn/ 
*  邮件:pzxiaoxiao130130@gmail.com
*  QQ：90636900*  联系到作者^_^ 
*******************************************************************************/ 
package com.f14.net.smartinvoke
{
 import cn.smartinvoke.RemoteObject;
 public class CommandHandler extends RemoteObject {
  public function CommandHandler(){
	 super();
	  //调用服务方构造CommandHandler类型服务对象
	 	this.createRemoteObject();
  }

   public function sendCommand(cmdstr:String):void{
	 var retObj:Object=this.call("sendCommand",arguments);

   }
   public function dispose():void{
	 var retObj:Object=this.call("dispose",arguments);

   }
 }

}
