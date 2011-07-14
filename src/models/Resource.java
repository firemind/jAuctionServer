package models;

import java.io.Serializable;


public class Resource extends Model{
	private String name;
	private long id;
	
	public long getId(){
		return this.id;
	}
	
	public Resource( String n){
		this();
		this.name = n;
	}
	
	public Resource(){
		super("Resource");
	}
	
	public String getName(){
		return this.name;
	}
}
