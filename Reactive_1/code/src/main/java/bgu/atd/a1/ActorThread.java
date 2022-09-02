/**
 * The algorithm: each thread trying to find an actor with a task to perform
 * by iterating over the actors list, if no task found the the last_working_round
 * will be different then the work_round and the Thread will go to sleep until
 * a new task as submitted or a thread finished processing a task
 */

package bgu.atd.a1;

import java.util.Map;

public class ActorThread implements Runnable{

    private Map<String, Actor> actors;
    private ActorThreadPool actors_thread_pool;
    private int work_round = 0;
    private int last_working_round = 0;

    public ActorThread(Map<String, Actor> actors, ActorThreadPool pool){
        this.actors = actors;
        this.actors_thread_pool = pool;
    }

    public void run(){
        while (!Thread.interrupted()){
            work_round++;
            for (Actor actor : actors.values()){
                Action<?> curr_action = actor.get_task();

                if (curr_action != null) {
                    last_working_round = work_round;
                    curr_action.handle(actors_thread_pool,
                                         actor.get_name(),
                                         actor.get_state());

                    Promise<?> promise = curr_action.getResult(); // if after handling the action it is not resolved then put the promise back to queue
                    //System.out.println("Thread " + Thread.currentThread().getName() + " processing action name: " + curr_action.getActionName());
                    if (!promise.isResolved()) actor.add_task(curr_action);

                    actor.release(); // other threads can now fetch task from this actor.
                    System.out.println(Thread.currentThread().getName() + " released " + actor.get_name());
                    synchronized (actors) {actors.notifyAll();}
                }
            }
            
            try {
                if (work_round != last_working_round) {
                    //System.out.println("Thread " + Thread.currentThread().getName() + " waits\n");
                    synchronized(actors) {actors.wait();}
                }
            }
            catch (InterruptedException e) {
                
                //System.out.println("Thread " + Thread.currentThread().getName() + " stopped\n");
                Thread.currentThread().interrupt();
            }
        }
        //System.out.println("thread " + Thread.currentThread().getName() + " ended\n");
        
    }

}