package com.Parameter;

public class ControlParameter {
	
	/*
	 * 此类包含程序中所有与交互有关的信息类的实例，把所有实例都集中到一起，是为了便于函数之间的参数传递，使它更简单易懂
	 */
	
	private static ControlParameter instance=null;		//单例模式的自身的静态对象
	private final static Object synLock=new Object();		//用于在初始化该类的唯一实例的时候实施线程锁
	
	private ControlParameter(){}
	public static ControlParameter GetControlParameter()
	{//单例模式的获取唯一实例的工具类
		if(instance==null)
		{
			synchronized(synLock){
				if(instance==null)
					instance=new ControlParameter();
			}
		}
		return instance;
	}
	public static void update()
	{//更新实例，即在有些设置改变之后，调用此函数，将之前的对象抛弃，由java虚拟机进行垃圾回收，在下次调用的时候，重新构建对象
		instance=null;
	}

}
