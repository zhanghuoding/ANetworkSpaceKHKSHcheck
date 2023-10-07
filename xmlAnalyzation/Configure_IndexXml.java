package com.xmlAnalyzation;

import java.io.IOException;
import java.util.LinkedList;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Configure_IndexXml {

	private static Configure_IndexXml instance=null;		//单例
	private static final Object synLock=new Object();
	
	private DocumentBuilderFactory dbf=null;		//创建解析xml文档的解析器类的工厂类对象
	private DocumentBuilder db=null;		//创建解析xml文档的解析器
	private Document doc=null;		//index.xml文件的描述工具
	private NodeList ndl=null;	
	private Element node=null;
	
	private LinkedList<Configure_IndexXml_Type> Configure_Index=null;		//用于存储index.xml配置文件中信息的链表
	
	private Configure_IndexXml()
	{
		init();
	}
	public static Configure_IndexXml GetConfigure_IndexXml()
	{
		if(instance==null)
		{
			synchronized(synLock)
			{
				if(instance==null)
					instance=new Configure_IndexXml();
			}
		}
		return instance;
	}
	private void init()
	{
		dbf=DocumentBuilderFactory.newInstance();
		try {
			db=dbf.newDocumentBuilder();
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, "对不起，在创建index.xml文档解析器时出错了！", "创建.xml文档解析器错误", 0);
		}
		
		try {
			doc=db.parse("xml/index.xml");//加载index.xml文档
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,  "对不起，在加载index.xml文档时抛出了一个SAXException类型的\n错误,造成此错误的原因可能是index.xml文件根元素不正确！",
					"加载index.xml错误", 0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,  "对不起，在加载index.xml文档时抛出了一个IOException类型的错误！", "加载index.xml错误", 0);
		}
		
		setConfigure_Index(new LinkedList<Configure_IndexXml_Type>());
		ndl=doc.getElementsByTagName("resid");//获得所有名为resid的元素的有序列表
		for(int i=0;i<ndl.getLength();i++)
		{//循环遍历结点列表ndl，并构造Configure_Index链表
			
			node=(Element)ndl.item(i);
			Configure_Index.add(new Configure_IndexXml_Type(i, node.getAttribute("type"), node.getAttribute("name"), node.getAttribute("file")));
		}
		
		dbf=null;//这五条语句是释放在加载读取xml文件时所用的工具对象，以便于java内存回收机制回收内存空间
		db=null;
		doc=null;
		ndl=null;
		node=null;
		
		/**
		 * for(int i=0;i<Configure_Index.size();i++)
		{//循环遍历输出index.xml文件中的内容
			System.out.println(Configure_Index.get(i).getIndex()+"\t"+Configure_Index.get(i).getType()+"\t"+Configure_Index.get(i).getName()+"\t"+Configure_Index.get(i).getFile());
		}
		 */
		
	}
	public static void update()
	{//更新实例，即在有些设置改变之后，调用此函数，将之前的对象抛弃，由java虚拟机进行垃圾回收，在下次调用的时候，重新构建对象
		instance=null;
	}
	public LinkedList<Configure_IndexXml_Type> getConfigure_Index() {
		return Configure_Index;
	}
	public void setConfigure_Index(LinkedList<Configure_IndexXml_Type> configure_Index) {
		Configure_Index = configure_Index;
	}
	public Configure_IndexXml_Type getConfigure_IndexXml_Property(String name)
	{
		for(int i=0;i<Configure_Index.size();i++)
		{
			if(Configure_Index.get(i).getName().equals(name))
				return Configure_Index.get(i);
		}
		return null;
	}
}
