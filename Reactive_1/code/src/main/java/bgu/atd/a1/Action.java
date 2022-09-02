package bgu.atd.a1;

import java.util.Collection;

/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!
 *
 * @param <R> the action result type
 */
public abstract class Action<R> {

    private Promise<R> promise;
    private callback callback;
    private String actionName;
    private boolean suspended = false;
    protected ActorThreadPool actors_thread_pool;
    private Collection<? extends Action<?>> remainActions;
    protected String actorID;
    protected PrivateState actorState;

    protected Action(String actionName, ActorThreadPool actors_thread_pool){
        this.actionName = actionName;
        this.actors_thread_pool = actors_thread_pool;
        promise = new Promise<R>();
    }

    /**
     * start handling the action - note that this method is protected, a thread
     * cannot call it directly.
     */
    protected abstract void start();

    /**
     * start/continue handling the action
     * <p>
     * this method should be called in order to start this action
     * or continue its execution in the case where it has been already started.
     * <p>
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     */
    /*package*/
    final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {
        actors_thread_pool = pool;
        this.actorID = actorId;
        this.actorState = actorState;


        if (!suspended) { //run for the first time.
            start();
        }
        suspended = true; //suspended or already started.

        if (remainActions != null) {
            // iterates over the action list, each time remove an action a new iterator is required
            boolean removed = false;
            do {
                removed = false;
                for (Action<?> a : remainActions) {
                    if (a.getResult().isResolved()) {
                        remainActions.remove(a);
                        removed = true;
                        break;
                    }
                }
            } while (removed);    

            if (remainActions.isEmpty() && callback != null) {
                callback.call();
            }
        }
        else if (callback != null) { callback.call();}
    }

    /**
     * add a callback to be executed once *all* the given actions results are
     * resolved
     * <p>
     * Implementors note: make sure that the callback is running only once when
     * all the given actions completed.
     *
     * @param actions
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void then(Collection<? extends Action<?>> actions, callback callback) {
        //idea to implement: then() will only update the actions collection and callback.
        remainActions = actions;
        this.callback = callback;
    }

    /**
     * resolve the internal result - should be called by the action derivative
     * once it is done.
     *
     * @param result - the action calculated result
     */
    protected final void complete(R result) {
        promise.resolve(result);
    }

    /**
     * @return action's promise (result)
     */
    public final Promise<R> getResult() {
        return this.promise;
    }

    /**
     * send an action to an other actor
     *
     * @param action     the action
     * @param actorId    actor's id
     * @param actorState actor's private state (actor's information)
     */
    public void sendMessage(
            Action<?> action,
            String actorId,
            PrivateState actorState
    ) {
        actors_thread_pool.submit(action, actorId, actorState);
    }

    /**
     * set action's name
     *
     * @param actionName
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * @return action's name
     */
    public String getActionName() {
        return this.actionName;
    }
}
