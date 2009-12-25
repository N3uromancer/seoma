package server;

import gameEngine.StartSettings;
import gameEngine.world.owner.Owner;

import java.io.Serializable;

public class Game implements Serializable {
		private static final long serialVersionUID = -443607570575906060L;
		public long seed;
		public String[] aiFiles;
		public int winner;
		public StartSettings ss;
		
		public Game(long seed, String[] ais, int winnerIdx, StartSettings ss)
		{
			this.seed = seed;
			this.aiFiles = ais;
			this.winner = winnerIdx;
			this.ss = ss;
		}
}
