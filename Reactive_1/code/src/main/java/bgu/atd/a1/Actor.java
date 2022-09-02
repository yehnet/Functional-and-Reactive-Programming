package bgu.atd.a1;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;



public class Actor {
    private String actor_id;
    private PrivateState state;
    private Promise<?> last_promise;
    private AtomicBoolean processing = new AtomicBoolean(false);
    private Queue<Action<?>> actions = new ConcurrentLinkedQueue<Action<?>>();

    public Actor(String actor_id, PrivateState state) {
        this.actor_id = actor_id;
        this.state = state;
    }

    public void add_task(Action<?> task) {
        actions.add(task);
    }

    public Action<?> get_task() {
        // check atomic if no thread is handling the actor at the moment
        if (processing.compareAndSet(false, true)) {
            if (actions.isEmpty() == false) {
                System.out.println(Thread.currentThread().getName() + " locked " + actor_id);
                return actions.remove();
            }
            else { 
                release();
                return null;
            }
        }
        else {
            return null;
        }
    }

    public Boolean release() {
        return processing.compareAndSet(true, false);
    }

    public PrivateState get_state() {return state;}

    public String get_name() {return actor_id;}

    public Promise<?> get_last_promise() {return last_promise;}
}


