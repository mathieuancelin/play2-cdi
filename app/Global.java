import play.*;

import org.jboss.weld.environment.se.ShutdownManager;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class Global extends GlobalSettings {

	private WeldContainer weld;

	@Override
	public void onStart(Application app) {
		weld = new Weld().initialize();
	}

	@Override
	public void onStop(Application app) {
		shutdown(weld);
	}

	@Override
	public <A> A getControllerInstance(Class<A> clazz) {
		return weld.instance().select(clazz).get();
	}

	private void shutdown(WeldContainer weld) {
        ShutdownManager shutdownManager = weld.instance().select(ShutdownManager.class).get();
        shutdownManager.shutdown();
    }
}