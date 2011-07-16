package store;

import java.util.HashMap;

import server.JAuctionLogger;
import models.Resource;
import models.User;

public class ResourceStore extends Store<Resource>{
    public ResourceStore() {
		super(new Resource());
		/*addResource("Gold");
		addResource("Silver");
		addResource("Copper");*/
		loadFromDB();
	}
    
    
    /**
     * Creates new resource and adds it to the list of resources.            
     *
     * @param  name Name of resource to be created
     * @return Resource Object
     */
    public Resource addResource(String name){
    	Resource res = new Resource( name);
    	res.save();
    	return add(res);
    }
    
    
    /**            
     *
     * @return HashMap of all resources.
     */
    public HashMap<Long, Resource> getResources(){
    	return getAll();
    }

    /**              
     *
     * @param resource_id The id of the resource to be returned
     * @return Resouce Object associated with resource_id 
     */
    public Resource getResource(long resource_id){
    	return get(resource_id);
    }
}
