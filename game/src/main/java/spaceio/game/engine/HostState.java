package spaceio.game.engine;

import com.jme3.app.Application;
import com.jme3.app.state.AppState;
import com.jme3.app.state.BaseAppState;
import com.jme3.network.ConnectionListener;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.core.VersionedHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostState extends BaseAppState implements AppState {

    static Logger log = LoggerFactory.getLogger(HostState.class);

    private GameServer gameServer;
    private int port;

    private VersionedHolder<String> hostingState;
    private VersionedHolder<String> connectionCount;

    private ConnectionListener connectionListener = new ConnectionObserver();

    private Container hostWindow;

    public HostState(int port, String hostDescriptionText) {
    }

    @Override
    protected void initialize(Application app) {

    }

    @Override
    protected void cleanup(Application app) {

    }

    @Override
    protected void onEnable() {

    }

    @Override
    protected void onDisable() {

    }
}
