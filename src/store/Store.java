package store;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import models.Auction;
import models.Model;

public class Store <T extends Model> extends Observable{

    protected Model model;
    protected HashMap<Long, T> records = new HashMap<Long, T>();
    
    public Store(Model class1){
    	this.model = class1;
    }
    
    protected void loadFromDB(){
    	Map<? extends Long, ? extends T> all = (Map<? extends Long, ? extends T>) this.model.all();
		this.records.putAll(all);
    }
    
    protected void saveRecord(T record){
    	record.save();
    }
    
    public T get(long user_id){
    	return this.records.get(user_id);
    }
    
    
    public HashMap<Long, T> getAll(){
    	return this.records;
    }
    
    public boolean has(long id){
    	return this.records.containsKey(id);
    }
    
    protected T add(T record){
    	
    	this.records.put(record.getId(), record);
    	return record;
    }
    
    
}
