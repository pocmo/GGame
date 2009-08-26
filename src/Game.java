import java.util.*;

public class Game {
	public static final int STATUS_WAITING = 0; // Waiting for Players
	public static final int STATUS_WORD    = 1; // Waiting for new Word
	public static final int STATUS_GOOGLE  = 2; // Waiting for Google
	public static final int STATUS_NOTHING = 3; // Nothing :)
	
	private String currentWord = "___";
	private String currentPlayer = "___";
	
	private int round = 1;
	
	private Vector<Player> players = new Vector<Player>();
	private Vector<String> cache = new Vector<String>();
	
	private Bot bot;
	
	public Game(Bot bot)
	{
		this.bot = bot;
	}
	
	public Vector<String> getCache()
	{
		return cache;
	}
	
	public boolean addPlayer(String nickname)
	{
		if(this.getPlayer(nickname) == null)
		{
			players.add(new Player(nickname));
			return true;
		}
		return false;
	}
	
	public Player getPlayer(String nickname)
	{
		for (Player player : players)
		{
			if (player.getNickname().equals(nickname)) {
				return player;
			}
		}
		return null;
	}
	
	public void nextRound()
	{
		round++;
	}
	
	public int getRound()
	{
		return round;
	}
	
	public Vector<Player> getPlayers()
	{
		return players;
	}
	
	public void sayAll(String txt)
	{
		for (Player player : players)
		{
			bot.sendMessage(player.getNickname(), txt);
		}
	}

	public String getCurrentWord()
	{
		return currentWord;
	}

	public void setCurrentWord(String currentWord)
	{
		this.currentWord = currentWord;
	}

	public String getCurrentPlayer()
	{
		return currentPlayer;
	}

	public void setCurrentPlayer(String currentPlayer)
	{
		this.currentPlayer = currentPlayer;
	}
	
	public void resetPlayers()
	{
		for (Player player : this.players)
		{
			player.setWord(null);
		}
	}
}
