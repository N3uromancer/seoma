package TCP;

public abstract class TCP_Client_Callbacks {
	private int callbackVer = 1;
	
	//Data has been received on the socket
	public abstract void DataReceived(byte[] data);
	
	//The socket has caused an exception to be thrown
	//when attempting to receive
	//!!! THE SOCKET IS DEAD WHEN THIS IS CALLED !!!
	public abstract void ReceiveException(Exception e);
	
	public void setCallbackVersion(int callVer) {
		callbackVer = callVer;
	}
	
	public int getCallbackVersion() {
		return callbackVer;
	}
}