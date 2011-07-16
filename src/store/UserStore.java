package store;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import server.JAuctionLogger;

import models.Auction;
import models.Model;
import models.Resource;
import models.User;

public class UserStore extends Store{

    public UserStore() {
		super(new User());
		loadFromDB();
	}
    
    private Session session;
    private SessionFactory sessionFactory;
    
    /**
     * Creates new user and adds it to the list of users.                 
     *
     * @param  username Userame of user to be created
     * @param  password Password of user to be created
     * @return User Object
     */
    public User addUser(String username, String password){
    	if(this.getUserByUsername(username) == null){
        	User user = new User( username, password);
        	user.save();
        	return (User) add(user);
    	}else{
    	  return null;	
    	}
    }

    public User getUserByUsername(String username){
    	List<Model> users = this.model.getByAttribute("username", username);
    	if(users.isEmpty()){
    		return null;
    	}else{
    		return (User) users.get(0);
    	}
    }

    /**
     * Authenticates User by username and password            
     *
     * @param  username Userame of user to be authenticated
     * @param  password Password of user to be authenticated
     * @return User Object or null
     */
    public User authenticateUser(String username, String password){
    	User user = null;
    	Iterator<Long> it = this.records.keySet().iterator();
    	while(it.hasNext()) {
    		User tmp_user = (User) this.records.get(it.next());
    		if(tmp_user.authenticate(username, password)){
    			user = tmp_user;
    		}
    	}
    	return user;
    }  

    
}
