package serverCommands;

import java.util.HashMap;

import server.Connection;

import models.Auction;
import net.sf.json.JSONObject;

public class GetAuction extends ServerCommand {

	private long auction_id;
	
	GetAuction(Connection con){
		super(con);
	}
	
	public String name(){
		return "get_auctions";
	}
	
	public JSONObject requestSpecification(){
		HashMap data = new HashMap();
		data.put("auction_id", "Long");
		return super.specificationMapper("request", data);
	}
	
	public JSONObject responseSpecification(){
		HashMap auc1 = new HashMap();
		auc1.put("auction_id", "Long");
		auc1.put("resource_id", "Long");
		auc1.put("amount", "Integer");
		auc1.put("user_id", "Integer");
		auc1.put("price", "Integer");
		auc1.put("timeleft_sec", "Integer");

		return super.specificationMapper("response", auc1);
	}
	
	public boolean parseJson(JSONObject data){
		try {
			this.auction_id 	= data.getLong("auction_id");
		}catch(net.sf.json.JSONException e){
			con.badRequest();
			return false;
		}
		return true;
	}
	
	public void run(){
		HashMap data = new HashMap();
		Auction auc = con.jAuctionServer.auctionStore.getAuctionById(auction_id);
		if(auc != null){
			data.put("auctions", auc.toJson());
		}
		con.respond(this.responseName(), data);
	}
}