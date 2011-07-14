package server;

import net.sf.json.JSONObject;

public class JAuctionLogger {
    
    public static void log(String line){
    	System.out.println(line);
    }
    
    public static void log(JSONObject json){
    	System.out.println(json.toString());
    }
}
