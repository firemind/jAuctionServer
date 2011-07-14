import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import server.Connection;
import server.JAuctionServer;


public class Server {
	
    private static int port = 4444;
    private static int maxConnections = 0;
    
    public static void main(String[] args) throws Exception {

    	JAuctionServer jAuctionServer = new JAuctionServer();
    	
	    System.err.println("Started server on port " + port);
	
	    int con_counter=0;
	
	    try{
	      ServerSocket serverSocket = new ServerSocket(port);
	      Socket clientSocket;
	
	      while((con_counter++ < maxConnections) || (maxConnections == 0)){
	
	        clientSocket = serverSocket.accept();
	        
	        Connection conn_c= new Connection(clientSocket, jAuctionServer);
	        jAuctionServer.mutationStore.addObserver(conn_c);
	        Thread t = new Thread(conn_c);
	        t.start();
	      }
	    } catch (IOException ioe) {
	      System.out.println("IOException on socket listen: " + ioe);
	      ioe.printStackTrace();
	    }   
        
    }
}
