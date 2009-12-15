package uploadHookServer;

import java.io.File;

public interface HookCallbacks {
	public void fileReceived(File f);
	public void exception(Exception e);
}
