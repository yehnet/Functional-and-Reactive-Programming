package bgu.atd.a1;

import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * an abstract class that represents private states of an actor
 * it holds actions that the actor has executed so far 
 * IMPORTANT: You can not add any field to this class.
 */
public abstract class PrivateState implements Serializable{
	
	// holds the actions' name what were executed
	private List<String> history = new ArrayList<String>();

	public List<String> getLogger(){
		return history;
	}
	
	/**
	 * add an action to the records
	 *  
	 * @param actionName
	 */
	public void addRecord(String actionName){
		history.add(actionName);
	}
	
	
}
