package server;

import java.net.Socket;
import java.io.*;

import models.Mutation;
import models.User;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import java.util.HashMap;
import java.util.Map;
import java.util.Observer;
import java.util.Observable;
import serverCommands.*;

public class Connection implements Runnable, Observer {
    protected Socket server;
    public In in;
    public Out out;
    public User user;
    public JAuctionServer jAuctionServer;
    private long last_mutation = 0;
    
    public Connection(Socket clientSocket, JAuctionServer jAuctionServer) {
      this.jAuctionServer = jAuctionServer;
      this.server=clientSocket;
      this.in  = new In (clientSocket);
      this.out = new Out(clientSocket);
    }

    /** 
     * Searches for new Mutations and sends them through the socket
     */
    public void update(Observable obs, Object obj){
    	Mutation m = (Mutation) obj;
    	send(m.getJson());
    }
    
    public void run () {

        System.err.println("Accepted connection from client");
		String s;
		JSONObject json ;
		String command = null;
		ServerCommand sc = null;
		while ((s = in.readLine()) != null) {
			JAuctionLogger.log("Got command: "+s);
			try {
				json = (JSONObject) JSONSerializer.toJSON(s);
				command = json.getString("command");
				if(json != null && command != null){
					sc = this.jAuctionServer.serverCommandFactory.getCommand(command, this);
					JSONObject data = json.getJSONObject("data");
					if(sc != null){
						if(sc.parseJson(data)){
						   Thread t = new Thread(sc);
				           t.start();
						}else{
							badRequest();
						}
						json = null;
						command = null;
						sc = null;
					}
				}
			}catch(net.sf.json.JSONException e){
				badRequest();
				JAuctionLogger.log("bad request: "+s);
				json = null;
				command = null;
				sc = null;
			}
		}
		close();
        

    }
    
    public void close(){
    	JAuctionLogger.log("Closing connection with client");
        out.close();
        in.close();
        try {
        	server.close();
	    } catch (IOException ioe) {
	    	  JAuctionLogger.log("IOException on socket listen: " + ioe);
		      ioe.printStackTrace();
		}  
    	
    }
    
    public void badRequest(){
		out.println("bad request");
    }
    
    
    /** 
     * Helper method that sends standardized JSON notification.
     * 
     * Sent string looks like this: { command: 'notify', data: {'text': 'mytext' } }
     *    
     * @param text Text to be sent
     */
    public void notify(String text){
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("text", text);
		respond("notify", data);
    }
    
    /** 
     * Formats JSON Object and sends it through the connection
     * 
     *    
     * @param json Instance of net.sf.json.JSONObject;
     */
    public void send(JSONObject json){
    	String username = "NoUser";
    	if (this.user != null)
    	  username = this.user.getUsername();
		JAuctionLogger.log("Sending Message to "+username+": "+json.toString());
		out.println(json.toString());
    }
    
    /**
     * Formats command name and data hash to JSON and sends it through the socket.
     *
     *
     * Format should look like this: 
     * { command: 'command_name', data: { ... } }
     *
     * @param command Name of the command
     * @param data HashMap that is converted to JSON and sent as data attribute
     */
    @SuppressWarnings("unchecked")
	public void respond(String command, HashMap data){
		Map response = new HashMap();
		response.put("command", command);
		if(data != null){
		  response.put("data", data);
		}
		JSONObject jsonObject = JSONObject.fromObject( response );
		send(jsonObject);
    }
    
	public void respond(String command, JSONObject data){
		Map response = new HashMap();
		response.put("command", command);
		JSONObject jsonObject = JSONObject.fromObject( response );
		jsonObject.put("data", data);
		send(jsonObject);
    }

}
