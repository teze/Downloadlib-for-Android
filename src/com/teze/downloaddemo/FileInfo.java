package com.teze.downloaddemo;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

public class FileInfo  implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@DatabaseField(generatedId = true)
	public int id;
	@DatabaseField
	public String filePath;
	@DatabaseField
	public String url;
	@DatabaseField
	public String imgUri;
	@DatabaseField
	public String name;
	@DatabaseField
	public int progress;
	@DatabaseField
	public int fileSize;
	
	 FileInfo(){
	}
	
	
}


