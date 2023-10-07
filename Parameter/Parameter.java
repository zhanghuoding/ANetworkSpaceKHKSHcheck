package com.Parameter;

public class Parameter {
	
	private static Parameter instance=null;		//单例模式的自身的静态对象
	private final static Object synLock=new Object();		//用于在初始化该类的唯一实例的时候实施线程锁
	
	private ControlParameter controlParameter=null;
	private DataParameter dataParameter=null;
	private GUIParameter guiParameter=null;
	private SettingParameter settingParameter=null;
	
	private Parameter(){
		
		setControlParameter(ControlParameter.GetControlParameter());
		setDataParameter(DataParameter.GetDataParameter());
		setGuiParameter(GUIParameter.GetGUIParameter());
		setSettingParameter(SettingParameter.GetSettingParameter());
	}
	public static Parameter GetParameter()
	{//单例模式的获取唯一实例的工具类
		if(instance==null)
		{
			synchronized(synLock){
				if(instance==null)
					instance=new Parameter();
			}
		}
		return instance;
	}
	public static void update()
	{//更新实例，即在有些设置改变之后，调用此函数，将之前的对象抛弃，由java虚拟机进行垃圾回收，在下次调用的时候，重新构建对象
		instance=null;
	}
	
	public ControlParameter getControlParameter() {
		return controlParameter;
	}
	public void setControlParameter(ControlParameter controlParameter) {
		this.controlParameter = controlParameter;
	}
	public DataParameter getDataParameter() {
		return dataParameter;
	}
	public void setDataParameter(DataParameter dataParameter) {
		this.dataParameter = dataParameter;
	}
	public GUIParameter getGuiParameter() {
		return guiParameter;
	}
	public void setGuiParameter(GUIParameter guiParameter) {
		this.guiParameter = guiParameter;
	}
	public SettingParameter getSettingParameter() {
		return settingParameter;
	}
	public void setSettingParameter(SettingParameter settingParameter) {
		this.settingParameter = settingParameter;
	}

}
