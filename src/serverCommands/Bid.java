package serverCommands;

import java.util.HashMap;

import server.Connection;


import net.sf.json.JSONObject;

public class Bid extends ServerCommand {
	
	private String auth_key;
	private long auction_id;
	private long bid;
	
	Bid(Connection con){
		super(con);
	}
	
	public String name(){
		return "bid";
	}
	
	public JSONObject requestSpecification(){
		HashMap data = new HashMap();
		data.put("auction_id", "Integer");
		data.put("bid", "Long");
		data.put("auth_key", "String");
		return super.specificationMapper("request", data);
	}
	
	public JSONObject responseSpecification(){
		HashMap data = new HashMap();
		data.put("auction_id", "Integer");
		data.put("success", "Boolean");
		return super.specificationMapper("response", data);
	}
	
	public boolean parseJson(JSONObject data){
		try {
			this.auth_key 		= data.getString("auth_key");
			this.bid 			= data.getLong("bid");
			this.auction_id 	= data.getLong("auction_id");
		}catch(net.sf.json.JSONException e){
			con.badRequest();
			return false;
		}
		return true;
	}
	
	public void run(){
	  	if(authenticate(auth_key)){
	  		
	  		boolean success = this.con.jAuctionServer.auctionStore.bid(con.user.getId(), bid, auction_id);
	  		
	  		HashMap data = new HashMap(); 
	  		data.put("auction_id", auction_id);
	  		data.put("success", success);
	  		con.respond(this.responseName(), data);
	  	}
	}
}