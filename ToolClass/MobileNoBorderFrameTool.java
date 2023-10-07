package com.ToolClass;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class MobileNoBorderFrameTool {
	
	private Point mouseLastLocation=null,frameLocation=null;		//鼠标移动前的位置和窗口的绝对坐标
	private boolean isDraging = false;		//用于判断鼠标是否按下
	private Point setWindow_Location=new Point();		//用于重新绘制窗口时做位置参数
	
	public MobileNoBorderFrameTool(Component frame)
	{
		super();
		init(frame);		//初始化
	}
	
	private void init(Component frame)
	{
		mouseLastLocation=new Point();		//鼠标移动前的位置
		frameLocation=new Point();		//窗口的绝对坐标
		
		/*确保取消标题栏以及边框以后，窗口仍然能接受鼠标点击事件*/
		{
			frame.addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{//按下鼠标时触发的动作
					if(e.getButton()==MouseEvent.BUTTON1)
					{//左键点击时触发
						isDraging = true;
						mouseLastLocation=e.getLocationOnScreen();		//获取鼠标移动前的绝对坐标
					}
				}

				public void mouseReleased(MouseEvent e)
				{//松开鼠标触发的事件
					isDraging = false;
				}
			});
		}
		
		frame.addMouseMotionListener(new MouseMotionAdapter()
		{//鼠标移动时的动作
			public void mouseDragged(MouseEvent e)
			{
				if (isDraging)
				{
			    	frameLocation= frame.getLocationOnScreen();		//获取当前窗口在屏幕上的位置
			    	setWindow_Location.x=frameLocation.x+e.getXOnScreen()-mouseLastLocation.x;		//重绘窗口时的横坐标
			    	setWindow_Location.y=frameLocation.y+e.getYOnScreen()-mouseLastLocation.y;		//重绘窗口时的纵坐标
			    	/*重新绘制位置*/
			    	frame.setLocation(setWindow_Location);
                    mouseLastLocation=e.getLocationOnScreen();		//将当前鼠标绝对位置赋值，以备下次使用(相对于下次移动鼠标而已，当前位置即为上次位置)
                 }
				
			}
		});
	}
	
}
