package com.ToolClass;

public class ExitAction {
	
	private static ExitAction instance=null;		//单例模式的自身静态对象
	private static final Object synLock=new Object();		//锁对象
	
	private ExitAction(){}
	
	/***单例方法***/
	public static ExitAction GetExitAction()
	{
		if(instance==null)
		{
			synchronized(synLock)
			{
				instance=new ExitAction();
			}
		}
		return instance;
	}
	
	/***退出时执行的方法***/
	public void exit()
	{
		System.exit(0);
	}
	
	public static void update()
	{
		instance=null;
	}

}
