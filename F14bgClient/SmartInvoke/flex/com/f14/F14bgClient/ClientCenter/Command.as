
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
 public class Command extends RemoteObject {
  public function Command(){
	 super();
	  //调用服务方构造Command类型服务对象
	 	this.createRemoteObject();
  }

   public function getStr1():String{
	 var retObj:Object=this.call("getStr1",arguments);
	 return retObj as String;

   }
   public function getStr2():String{
	 var retObj:Object=this.call("getStr2",arguments);
	 return retObj as String;

   }
   public function setInt1(int1:int):void{
	 var retObj:Object=this.call("setInt1",arguments);

   }
   public function setStr2(str2:String):void{
	 var retObj:Object=this.call("setStr2",arguments);

   }
   public function setStr1(str1:String):void{
	 var retObj:Object=this.call("setStr1",arguments);

   }
   public function getInt1():int{
	 var retObj:Object=this.call("getInt1",arguments);
	 return Number(retObj);

   }
 }

}
