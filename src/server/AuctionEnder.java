package server;


import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import store.AuctionStore;

import models.Auction;


public class AuctionEnder extends Thread{
	SortedSet<Auction> auctions = Collections.synchronizedSortedSet(new TreeSet<Auction>(new AuctionComparator()));
	
	private AuctionStore auctionStore;
	
	public AuctionEnder(AuctionStore auctionStore){
		this.auctionStore = auctionStore;
	}
	
	public void addAuction(Auction newAuction){
		auctions.add(newAuction);
	}
	
	public void removeAuction(Auction removeAuction){
		auctions.remove(removeAuction);
	}
	
	public void waitUntilNextAuction(){
		try{
			  if(auctions.size() == 0){
			      this.wait(10000);
			  }else{
				  Auction nextAuction = auctions.first();
				  if(nextAuction.getTimeleftSec() > 0){
						this.wait(nextAuction.getTimeleftSec());
				  }else{
					  this.auctionStore.endAuction(nextAuction);
					  removeAuction(nextAuction);
				  }
				  
			  }
			}catch (InterruptedException e){
				System.out.println("Interrupted Auction Ender");
			}
	}
	
	@Override
	public synchronized void run(){
		while(true){
			this.waitUntilNextAuction();
		}
	}
	
	public class AuctionComparator implements Comparator<Auction> {
		 
		  @Override
		  public int compare(Auction a1, Auction a2) {
		    if (a1.getTimeleftSec() == a2.getTimeleftSec()) {
		      return 0;
		    }
		    if (a1.getTimeleftSec() > a2.getTimeleftSec()) {
		      return 1;
		    }
		    return -1;
		  }
		}
}
