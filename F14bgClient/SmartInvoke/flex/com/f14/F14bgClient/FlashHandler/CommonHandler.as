
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
 import com.f14.F14bgClient.User;
 public class CommonHandler extends RemoteObject {
  public function CommonHandler(){
	 super();
	  //调用服务方构造CommonHandler类型服务对象
	 	this.createRemoteObject();
  }

   public function dispose():void{
	 var retObj:Object=this.call("dispose",arguments);

   }
   public function getLocalUser():User{
	 var retObj:Object=this.call("getLocalUser",arguments);
	 return retObj as User;

   }
 }

}
