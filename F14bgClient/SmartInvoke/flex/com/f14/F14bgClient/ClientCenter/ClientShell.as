
/*******************************************************************************
* Copyright (c) 2009 SmartInvoke.
* 此代理类由CodeTransform工具自动生成
* 您可以通过 
*  网站:http://smartinvoke.cn/ 
*  邮件:pzxiaoxiao130130@gmail.com
*  QQ：90636900*  联系到作者^_^ 
*******************************************************************************/ 
package com.f14.F14bgClient.ClientCenter
{
 import cn.smartinvoke.RemoteObject;
 import java.util.HashMap;
 public class ClientShell extends RemoteObject {
  public function ClientShell(){
	 super();
	  //调用服务方构造ClientShell类型服务对象
	 	this.createRemoteObject();
  }

   public function getBytes():Array{
	 var retObj:Object=this.call("getBytes",arguments);
	 return retObj as Array;

   }
   public function getCommand():HashMap{
	 var retObj:Object=this.call("getCommand",arguments);
	 return retObj as HashMap;

   }
   public function dispose():void{
	 var retObj:Object=this.call("dispose",arguments);

   }
   public function setState(state:String):String{
	 var retObj:Object=this.call("setState",arguments);
	 return retObj as String;

   }
   public function getString():String{
	 var retObj:Object=this.call("getString",arguments);
	 return retObj as String;

   }
 }

}
