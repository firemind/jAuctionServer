package models;

import java.util.HashMap;

import net.sf.json.JSONObject;


public class Mutation {
	 private long id;
	 private JSONObject json;
	 public Mutation(String name, JSONObject data, long id){
		   HashMap message = new HashMap();
		   message.put("command", name);
		   message.put("data", data);
		   message.put("mutation_id", id);
		   json = JSONObject.fromObject( message); 
	 }
	 public JSONObject getJson(){
		 return this.json;
	 }
	 protected long getId(){
		 return this.id;
	 }

}