package serverCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import server.Connection;

import models.Resource;
import net.sf.json.JSONObject;

public class GetResourceAll extends ServerCommand {
	
	
	GetResourceAll(Connection con){
		super(con);
	}
	
	public String name(){
		return "get_resource_all";
	}
	
	public JSONObject requestSpecification(){
		HashMap data = new HashMap();
		return super.specificationMapper("request", null);
	}
	
	public JSONObject responseSpecification(){
		HashMap data = new HashMap();
		ArrayList<HashMap> resources = new ArrayList();
		HashMap res1 = new HashMap();
		res1.put("resource_id", "Integer");
		res1.put("name", "String");
		resources.add(res1);
		data.put("resources", resources);

		return super.specificationMapper("response", data);
	}
	
	public boolean parseJson(JSONObject data){
		return true;
	}
	
	public void run(){
	  		HashMap data = new HashMap();
			ArrayList<HashMap> resources = new ArrayList();
			Iterator it = con.jAuctionServer.resourceStore.getResources().keySet().iterator();
	    	while(it.hasNext()) {
	    		Resource res  = con.jAuctionServer.resourceStore.getResources().get(it.next());
				long resource_id =  res.getId();
				HashMap response = new HashMap();
		  		response.put("resource_id", resource_id);
		  		response.put("name", res.getName());
		  		resources.add(response);
			}
			data.put("resources", resources);
	  		con.respond(this.responseName(), data);
	}
}