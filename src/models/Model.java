package models;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import server.JAuctionLogger;

public abstract class Model implements ModelRules, Serializable{
    private Session session;
    private SessionFactory sessionFactory = null;
	protected String modelName;
	
	public String getTablename(){
		return getModelName();
	}
	
	//TODO, ugly, needs fixing
	public String getModelName(){
		 return modelName;
	}
	
	public Model(String modelName){
		this.modelName = modelName;

	}
	
	public HashMap<Long, Model> all(){
		this.createFactory();
		session = sessionFactory.openSession();
		session.beginTransaction();
		List result = session.createQuery( "from "+this.getTablename() ).list();
		HashMap<Long, Model> records = new HashMap<Long, Model>();
		for ( Model res : (List<Model>) result ) {
			JAuctionLogger.log("Adding "+this.getModelName()+" "+res.toString());
		    records.put(res.getId(), res);
		}
		session.getTransaction().commit();
		session.close();
		return records;
	}
	

	public void save(){
		this.createFactory();
		session = sessionFactory.openSession();
		session.beginTransaction();
		session.save( this );
		session.getTransaction().commit();
		session.close();
	}
	
	private void createFactory(){
		  if (sessionFactory == null)
			JAuctionLogger.log(this.getTablename());
			sessionFactory = new Configuration()
	        .configure() // configures settings from hibernate.cfg.xml
	        .addResource("models/"+this.getTablename()+".hbm.xml")
	        .buildSessionFactory();
		}
		

}
