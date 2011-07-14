package server;

/**
 * Java Auctionhouse
 *
 * @author Michael Gerber
 * @version 0.1.0
 */


import java.util.Hashtable;
import java.util.HashMap;
import java.util.List;

import models.Resource;
import models.User;

import org.hibernate.*;
import org.hibernate.cfg.*;

import serverCommands.ServerCommandFactory;
import store.AuctionStore;
import store.MutationStore;
import store.ResourceStore;
import store.UserStore;

public class JAuctionServer {


    public MutationStore mutationStore;
    public AuctionStore auctionStore;
    public UserStore userStore;
    public ResourceStore resourceStore;
    
    public ServerCommandFactory serverCommandFactory = new ServerCommandFactory();
    
    public JAuctionServer(){

    	this.mutationStore 	= new MutationStore();
    	this.auctionStore 	= new AuctionStore(this);
    	this.userStore 		= new UserStore();
    	this.resourceStore 	= new ResourceStore();
    	this.auctionStore.addObserver(this.mutationStore);
   	
    }
    
    /**               
     *
     * @return HashMap of all users.
     */
    public HashMap<Long, User> getUsers(){
    	return this.userStore.getAll();
    }
    
    /**
     * @return Hashtable of all ServerCommands that can be created by ServerCommandFactory
     */
    public Hashtable<String, String> getServerCommands(){
    	return this.serverCommandFactory.getServerCommands();
    }
   

}
