package com.xmlAnalyzation;

public class Configure_IndexXml_Type
{
	/*
	 * 本类是用来存储主要的配置文件index.xml中的项的数据类型
	 */
	private int index=0;//记录该项在LinkedList中的下标
	private String type=null;
	private String name=null;
	private String file=null;
	
	public Configure_IndexXml_Type(int index, String type, String name, String file)
	{
		this.setIndex(index);
		this.setType(type);
		this.setName(name);
		this.setFile(file);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}