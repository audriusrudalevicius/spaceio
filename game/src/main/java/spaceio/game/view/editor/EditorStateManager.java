package spaceio.game.view.editor;

import com.jme3.util.SafeArrayList;

import java.util.Arrays;
import java.util.List;

public class EditorStateManager {

    private final SafeArrayList<EditorStateInterface> initializing = new SafeArrayList<>(EditorStateInterface.class);
    private final SafeArrayList<EditorStateInterface> states = new SafeArrayList<>(EditorStateInterface.class);
    private final SafeArrayList<EditorStateInterface> terminating = new SafeArrayList<>(EditorStateInterface.class);

    private static EditorStateManager instance;

    public static EditorStateManager getInstance()
    {
        if (instance == null) {
            instance = new EditorStateManager();
        }

        return instance;
    }

    protected EditorStateInterface[] getInitializing() {
        synchronized (states){
            return initializing.getArray();
        }
    }

    protected EditorStateInterface[] getTerminating() {
        synchronized (states){
            return terminating.getArray();
        }
    }

    protected EditorStateInterface[] getStates() {
        synchronized (states) {
            return states.getArray();
        }
    }

    public boolean attach(EditorStateInterface state){
        synchronized (states){
            if (!states.contains(state) && !initializing.contains(state)){
                state.stateAttached(this);
                initializing.add(state);
                return true;
            }else{
                return false;
            }
        }
    }

    public boolean detach(EditorStateInterface state){
        synchronized (states){
            if (states.contains(state)){
                state.stateDetached(this);
                states.remove(state);
                terminating.add(state);
                return true;
            } else if(initializing.contains(state)){
                state.stateDetached(this);
                initializing.remove(state);
                return true;
            }else{
                return false;
            }
        }
    }

    public boolean hasState(EditorStateInterface state){
        synchronized (states){
            return states.contains(state) || initializing.contains(state);
        }
    }

    protected void initializePending(){
        EditorStateInterface[] array = getInitializing();
        if (array.length == 0)
            return;

        synchronized( states ) {
            // Move the states that will be initialized
            // into the active array.  In all but one case the
            // order doesn't matter but if we do this here then
            // a state can detach itself in initialize().  If we
            // did it after then it couldn't.
            List<EditorStateInterface> transfer = Arrays.asList(array);
            states.addAll(transfer);
            initializing.removeAll(transfer);
        }
        for (EditorStateInterface state : array) {
            state.initialize(this);
        }
    }

    protected void terminatePending(){
        EditorStateInterface[] array = getTerminating();
        if (array.length == 0)
            return;

        for (EditorStateInterface state : array) {
            state.cleanup();
        }
        synchronized( states ) {
            // Remove just the states that were terminated...
            // which might now be a subset of the total terminating
            // list.
            terminating.removeAll(Arrays.asList(array));
        }
    }

    public <T extends EditorStateInterface> T getState(Class<T> stateClass) {
        synchronized (this) {

            EditorStateInterface[] array = getStates();
            for (EditorStateInterface state : array) {
                if (stateClass.isAssignableFrom(state.getClass())) {
                    return (T) state;
                }
            }

            throw new RuntimeException(String.format("Class %s not found", stateClass.getCanonicalName()));
        }
    }

    public void cleanup(){
        EditorStateInterface[] array = getStates();
        for (EditorStateInterface state : array){
            state.cleanup();
        }
    }
}
