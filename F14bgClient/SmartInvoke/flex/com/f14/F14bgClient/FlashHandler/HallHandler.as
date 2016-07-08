
/*******************************************************************************
* Copyright (c) 2009 SmartInvoke.
* �˴�������CodeTransform�����Զ����
* �����ͨ�� 
*  ��վ:http://smartinvoke.cn/ 
*  �ʼ�:pzxiaoxiao130130@gmail.com
*  QQ��90636900*  ��ϵ������^_^ 
*******************************************************************************/ 
package com.f14.F14bgClient.FlashHandler
{
 import cn.smartinvoke.RemoteObject;
 import mx.collections.ArrayCollection;
 public class HallHandler extends RemoteObject {
  public function HallHandler(){
	 super();
	  //���÷��񷽹���HallHandler���ͷ������
	 	this.createRemoteObject();
  }

   public function getCodes(codeType:String):ArrayCollection{
	 var retObj:Object=this.call("getCodes",arguments);
	 return retObj as ArrayCollection;

   }
   public function dispose():void{
	 var retObj:Object=this.call("dispose",arguments);

   }
   public function exit():void{
	 var retObj:Object=this.call("exit",arguments);

   }
   public function setLocalUser(userStr:String):void{
	 var retObj:Object=this.call("setLocalUser",arguments);

   }
   public function createRoom(gameType:String, name:String, password:String, descr:String):void{
   	 var retObj:Object=this.
   }
 }

}
