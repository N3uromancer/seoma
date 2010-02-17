package network.client;

import java.io.IOException;
import java.util.Properties;

import com.sun.sgs.client.simple.SimpleClient;

import display.Display;
import display.screen.HomeScreen;

public class ClientStarter
{
	public static void main(String[] args)
	{
		SimpleClient client = new SimpleClient(new ClientListener());
		Properties connectProps = new Properties();
		connectProps.put("host", "localhost");
		connectProps.put("port", "1139");
		
		try
		{
			System.out.print("connecting...");
			client.login(connectProps);
			System.out.println(" done!");
			
			
			Display d = new Display();
			d.loadScreen(new HomeScreen());
			d.updateDisplay();
		}
		catch(IOException e)
		{
			System.out.print(" failed");
			e.printStackTrace();
		}
	}
}
