package bgu.atd.a1;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.ArrayList;
import java.lang.Thread;
import java.lang.Runnable;


/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {

	private ArrayList<Thread> threads = new ArrayList<Thread>();
	private Map<String, Actor> actors = new ConcurrentHashMap<String, Actor>(100);

	/**
	 * creates a {@link ActorThreadPool} which has nthreads. Note, threads
	 * should not get started until calling to the {@link #start()} method.
	 *
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads
	 *            the number of threads that should be started by this thread
	 *            pool
	 */
	public ActorThreadPool(int nthreads) {
		for (int i=0; i < nthreads; i++){
			/**
			 * the runnable iterates over the actors list looking for an available 
			 * action to process. if no available action or actor found, waits until 
			 * an actor is available.
			 */
			Runnable task = new ActorThread(this.actors, this);
			threads.add(new Thread(task));
		}
	}

	/**
	 * getter for actors
	 * @return actors
	 */
	public Map<String, PrivateState> getActors(){
		HashMap<String, PrivateState> actors_map = new HashMap<String, PrivateState>();
		for (Actor actor : actors.values()) {
			actors_map.put(actor.get_name(), actor.get_state());
		}
		return actors_map;
	}
	
	/**
	 * getter for actor's private state
	 * @param actorId actor's id
	 * @return actor's private state
	 */
	public PrivateState getPrivateState(String actorId){
		return actors.get(actorId).get_state();
	}


	/**
	 * submits an action into an actor to be executed by a thread belongs to
	 * this thread pool
	 *
	 * @param action
	 *            the action to execute
	 * @param actorId
	 *            corresponding actor's id
	 * @param actorState
	 *            actor's private state (actor's information)
	 */
	public void submit(Action<?> action, String actorId, PrivateState actorState) {
		// new actor
		//System.out.println("a task named " + action.toString() + "is about to be submitted\n");
		actors.putIfAbsent(actorId, new Actor(actorId, actorState));
		actors.get(actorId).add_task(action);
		//System.out.println("a task named " + action.toString() + "had been submitted\n");
		synchronized (this.actors) {this.actors.notifyAll();}
		
	}

	/**
	 * closes the thread pool - this method interrupts all the threads and waits
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 *
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException
	 *             if the thread that shut down the threads is interrupted
	 */
	public void shutdown() throws InterruptedException {
		for (Thread t : threads){
			t.interrupt();
			}
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		System.out.println("Number of threads is " + threads.size());
		for (Thread t : threads){
			t.start();
		}
	}
}
