package store;

import java.util.Observable;
import java.util.ArrayList;
import java.util.Observer;

import server.JAuctionLogger;

import models.Auction;
import models.Mutation;
import net.sf.json.JSONObject;


public class MutationStore extends Observable implements Observer{

	private ArrayList<Mutation> mutations = new ArrayList<Mutation>();
	private int next = 0;		// Index of last element
	private long id_counter = 0;
	private static int maxMutations = 2000;  // Number after which Ringpuffer resets
	
	public void update(Observable obs, Object obj){
		JAuctionLogger.log("Update called");
		if(obs instanceof AuctionStore){
			if (obj instanceof Auction){
				Auction auc = (Auction) obj;
				if (auc.ended()){
					this.addMutation("auction_removed", auc.toJson());
				}else{
			    	this.addMutation("new_auction", auc.toJson());
				}
			}
		}
	}
	
   public void addMutation(String name, JSONObject data){
	   Mutation mutation = new Mutation(name, data, id_counter++);
	   if(mutations.size() == maxMutations){
		   next = 0; 
	   }
	   mutations.add(next++, mutation);
	   setChanged();
	   notifyObservers(mutation);
   }
   
   private static  int getTrueIndex(int i){
	   if (i < 0){
		   return  maxMutations - i;
	   }else{
		   return i;
	   }
   }

   protected ArrayList<Mutation> getMutations(long last_mutation){
	   int offset = (int) (id_counter - last_mutation);
	   ArrayList<Mutation> returnMutations = new ArrayList<Mutation>();
	   if(offset > 0){
		   int start = next - offset;
		   while(start < (next-1)){
			   returnMutations.add(mutations.get(getTrueIndex(++start)));
		   }
	   }
	   return returnMutations;
   }
}
