package server;

import gameEngine.world.owner.Owner;

import java.io.Serializable;

public class Game implements Serializable {
		private static final long serialVersionUID = -443607570575906060L;
		public long seed;
		public String[] aiFiles;
		public int winner;
		public Owner[] o;
		
		public Game(long seed, String[] ais, int winnerIdx, Owner[] o)
		{
			this.seed = seed;
			this.aiFiles = ais;
			this.winner = winnerIdx;
			this.o = o;
		}
}
