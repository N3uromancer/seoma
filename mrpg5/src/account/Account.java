package account;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * represents a players account
 * @author Jack
 *
 */
public final class Account
{
	/**
	 * the actual account name, cannot be changed
	 */
	private String accountName = "";
	/**
	 * the player name displayed during gameplay, can be changed
	 */
	private String playerName = "";
	/**
	 * the message displayed whenever this player kills another player,
	 * can be changed
	 */
	private String killMessage = "";
	private long kills = 0;
	private long deaths = 0;
	private long gamesPlayed = 0;
	private long wins = 0;
	
	/**
	 * creates a new account with the specified account name
	 * @param accountName
	 */
	public Account(String accountName)
	{
		this.accountName = accountName;
		playerName = "Another Random Player";
		killMessage = "rapestroyed";
	}
	/**
	 * loads the account file
	 * @param f
	 */
	public Account(File f)
	{
		try
		{
			FileInputStream fis = new FileInputStream(f);
			DataInputStream dis = new DataInputStream(fis);
			
			readString(accountName, dis);
			readString(playerName, dis);
			readString(killMessage, dis);
			kills = dis.readLong();
			deaths = dis.readLong();
			gamesPlayed = dis.readLong();
			wins = dis.readLong();
		}
		catch(IOException e)
		{
			System.out.println("failed to load account file");
		}
	}
	/**
	 * gets the name of the account
	 * @return
	 */
	public String getAccountName()
	{
		return accountName;
	}
	public long getKills()
	{
		return kills;
	}
	public long getDeaths()
	{
		return deaths;
	}
	/**
	 * returns the total number of wins
	 * @return
	 */
	public long getWins()
	{
		return wins;
	}
	/**
	 * returns the total number of games played
	 * @return
	 */
	public long getGamesPlayed()
	{
		return gamesPlayed;
	}
	/**
	 * returns the total number of losses
	 * @return
	 */
	public long getLosses()
	{
		return gamesPlayed-wins;
	}
	/**
	 * reads a string from the data stream to the passed destination string
	 * @param dest
	 * @param dis
	 */
	private void readString(String dest, DataInputStream dis) throws IOException
	{
		int length = dis.readInt();
		for(int i = 0; i < length; i++)
		{
			dest+=dis.readChar();
		}
	}
}
