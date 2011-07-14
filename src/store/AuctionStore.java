package store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import server.AuctionEnder;
import server.JAuctionServer;

import models.Auction;
import models.Model;
import models.Resource;
import models.User;

public class AuctionStore extends Store{

    public AuctionStore() {
		super(new Auction());
	}

    public AuctionStore(JAuctionServer jAuctionServer){
    	this();
    	this.jAuctionServer = jAuctionServer;
    	this.userStore = jAuctionServer.userStore;
    	this.auctionEnder = new AuctionEnder(this);
		synchronized (this.auctionEnder){
	    	this.auctionEnder.start();
		}
    }

	private HashMap<Long, Auction> auctions = new HashMap<Long, Auction>();
    long nextAuctionId = 0;
    private JAuctionServer jAuctionServer;
    private AuctionEnder auctionEnder;
    public UserStore userStore;
    

    
	 /**
     * Tries to let user buy auction                           
     *
     * @param  user_id Id of the user trying to buy the auction
     * @param  auction_id Id of the auction to be bought
     * @return boolean indicating weather buying worked
     */
    public synchronized boolean buyAuction(long user_id, long auction_id){
    	if(!this.auctions.containsKey(auction_id))
    	  return false;
    	if(!this.jAuctionServer.userStore.has(user_id))
      	  return false;
    	Auction auction = this.auctions.get(auction_id);
    	User buyer = (User) this.userStore.get(user_id);
    	if(buyer.getMoney() < auction.getPrice())
    	  return false;
    	
    	auction.bid(buyer, auction.getPrice());
    	
    	sellAuction(auction.getId());
    	
    	return true;
    }

    public void endAuction(Auction auction){
  	  if(auction.hasBidder()){
  		sellAuction(auction.getId());
  	  }else{
  	    cancelAuction(auction.getId());
  	  }
  	  auction.end();
    }
    
    public synchronized boolean sellAuction(long auction_id){
    	Auction auction = this.auctions.get(auction_id);
    	User buyer = auction.getHighestBidder();
    	
    	buyer.buyAuction(auction);

    	User seller = auction.getUser();
    	seller.sellAuction(auction);
    	
    	auction.getUser().send("auction_sold", auction.soldJson());
    	this.removeAuction(auction_id);
    	
    	return true;
    }
    
    /**
     * Tries to let user bid on auction                           
     *
     * @param  user_id Id of the user trying to bid on the auction
     * @param  auction_id Id of the auction to be bid on
     * @return boolean indicating weather bidding worked
     */
    public synchronized boolean bid(long user_id, long bid, long auction_id){
    	if(!this.auctions.containsKey(auction_id))
    	  return false;
    	if(!this.userStore.has(user_id))
      	  return false;
    	Auction auction = this.auctions.get(auction_id);
    	User buyer = (User) this.userStore.get(user_id);
    	if(buyer.getMoney() < bid)
      	  return false;
    	if(bid < auction.getNextHigherBid())
    	  return false;
    	
    	buyer.bidOn(auction, bid);
    	
    	return true;
    }    
    
    /**
     * Tries to cancel the auction                           
     *
     * @param  user_id Id of the user trying to buy the auction
     * @param  auction_id Id of the auction to be bought
     * @return boolean indicating weather buying worked
     */
    public synchronized boolean cancelAuction(long auction_id){
    	if(!this.auctions.containsKey(auction_id))
    	  return false;
    	Auction auction = this.auctions.get(auction_id);
    	
    	User seller = auction.getUser();
    	seller.cancelAuction(auction);
    	
    	this.removeAuction(auction_id);
    	
    	return true;
    }
    
    private synchronized boolean removeAuction(long auction_id){
    	if(!this.auctions.containsKey(auction_id))
      	  return false;
      	Auction auction = this.auctions.get(auction_id);

    	this.auctions.remove(auction.getId());
    	this.auctionEnder.removeAuction(auction);
    	
      	this.setChanged();
      	this.notifyObservers(auction_id);
    	
    	return true;
    }
    
    
    /**
     * Creates new auction and adds it to the list of active auctions. Adds mutation. 
     * Takes stock from user who created auction and adds auction to that users auctions.
     *
     * @param  amount amount of resource sold in this auction
     * @param  resource resource that is to be sold
     * @param duration how long the auction will be running
     * @param user user that owns this auction
     * @param price amount of money for which the auction is to be sold
     * @return Auction Object
     */
    public Auction addAuction(int amount, Resource resource, int duration, User user, int price){

    	Auction auc = new Auction(nextAuctionId, amount, resource, duration, user, price);
    	this.auctions.put(nextAuctionId++, auc);
    	synchronized (this.auctionEnder) {
    	  auctionEnder.notify();
    	}
    	auctionEnder.addAuction(auc);
    	user.createAuction(auc);
    	
      	this.setChanged();
      	this.notifyObservers(auc);
      	
    	return auc;
    }
    
    /**
     * 
     * @param auction_id
     * @return
     */
    public Auction getAuctionById(long auction_id){
    	return this.auctions.get(auction_id);
    }
    
    
    /**
     * Filters all active auctions by resource_id and returns ArrayList of matches                
     *
     * @param  resource_id Id of resource to be filtered by.
     * @return ArrayList of auctions
     */
    public ArrayList<Auction> getAuctionsByResourceId(long resource_id){
    	ArrayList<Auction> filtered = new ArrayList<Auction>();
    	Iterator<Long> it = this.auctions.keySet().iterator();
    	while(it.hasNext()) {
    		Auction auc = this.auctions.get(it.next());
    		if(auc.getResource().getId() == resource_id){
    			filtered.add(auc);
    		}		    
    	}

    	return filtered;
    }
    
    
    /**      
     * @return ArrayList of all active auctions
     */
    public HashMap<Long, Auction> getAuctions(){
    	  return this.auctions;
    } 
}
