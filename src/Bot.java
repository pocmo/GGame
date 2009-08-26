import org.jibble.pircbot.*;
import java.util.*;
import java.text.NumberFormat;

public class Bot extends PircBot {
	private Game game;
	private Random rand = new Random();
	private NumberFormat numberFormat = NumberFormat.getInstance(Locale.GERMAN);
	
	public Bot()
	{
		this.setName("GGame");
		this.setAutoNickChange(true);
		this.setVersion("GGame " + Main.VERSION);
		this.setLogin("ggame");
		try {
			this.setEncoding("UTF-8");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		game = new Game(this);
	}
	
	public void connect()
	{
		try {
			this.connect("irc.epd-me.net");
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	protected void onConnect()
	{
		this.joinChannel(Main.CHANNEL);
	}
	
	protected void onMessage(String channel, String sender, String login, String hostname, String message)
	{
		
	}
	
	protected void onNickChange(String oldNick, String login, String hostname, String newNick)
	{
		Player p = game.getPlayer(oldNick);
		p.setNickname(newNick);
		if (game.getCurrentPlayer().equals(oldNick))
		{
			game.setCurrentPlayer(newNick);
		}
	}
	
	protected void onPrivateMessage(String sender, String login, String hostname, String message)
	{
		message = message.toLowerCase();
		
		if (message.startsWith("!google ")) {
			String word = message.substring(8);
			Player player = game.getPlayer(sender);
			if (player != null) {
				if (!player.hasSearched() && game.getCurrentWord() != null) {
					if (word.startsWith(game.getCurrentWord()) && word.length() >= game.getCurrentWord().length() + 3 && word.matches("^[a-z]{3,50}$")) {
						int result = Google.search(word);
						this.sendMessage(sender, numberFormat.format(result) + " fuer " + Colors.BOLD + word + Colors.NORMAL + " (" + numberFormat.format(result/1000) + " Punkte)");
						say("*" + sender + "*");
						player.setWord(word);
					}
					else {
						this.sendMessage(sender, "Das Wort muss mit " + game.getCurrentWord() + " beginnen und mindestens 3 Zeichen laenger sein! Nur Buchstaben (A-Z), ohne Umlaute.");
					}
				}
				else {
					this.sendMessage(sender, "Du hast schon ein Suchwort angegeben oder bist zu spaet!");
				}
			}
			else {
				this.sendMessage(sender, "Du spielt noch nicht mit. Benutze !spielen");
			}
		}
		if (message.startsWith("!word ") || message.startsWith("!wort "))
		{
			if (sender.equals(game.getCurrentPlayer())) {
				String word = message.substring(6);
				if (word.matches("^[a-z]{3,15}$")) {
					if (game.getCache().contains(word)) {
						sendMessage(sender, "Das hatten wir schon!");
					} else {
						game.nextRound();
						game.getCache().add(word);
						game.setCurrentWord(word);
						game.setCurrentPlayer("_____");
						game.resetPlayers();
						game.sayAll("Naechstes Wort: " + Colors.BOLD + word);
						say("Runde " + game.getRound() + " - Naechstes Wort: " + Colors.BOLD + word + Colors.NORMAL + " - 30 Sekunden ab JETZT!");
						new Timer().schedule(new BotTask(this), 30000);
					}
				} else {
					this.sendMessage(sender, "Nur Woerter mit Buchstaben (A-Z)! Mindestens 3 Zeichen, maximal 15 Zeichen!");
				}
			}
			else {
				this.sendMessage(sender, "Du bist nicht dran!");
			}
		}
		if (message.equals("!spielen"))
		{
			if (game.addPlayer(sender))
			{
				if (game.getPlayers().size() == 1) {
					game.setCurrentPlayer(sender);
					this.sendMessage(sender, "Du spielst jetzt mit und darfst das erste Wort bestimmen!");
					say(sender + " spielt jetzt mit und darf das erste Wort bestimmen!");
				}
				else {
					this.sendMessage(sender, "Du spielst jetzt mit!");
					say(sender + " spielt jetzt mit!");
				}
			}
			else {
				this.sendMessage(sender, "Aehm, du spielst schon mit!");
			}
		}
		if (message.equals("!ende"))
		{
			Player p = game.getPlayer(sender);
			if (p != null)
			{
				say(Colors.BOLD + sender + Colors.NORMAL + " steigt aus mit " + numberFormat.format(p.getPoints()) + " Punkten");
				game.getPlayers().remove(p);
				if (p.getNickname().equals(game.getCurrentPlayer())) {
					Player pNew = game.getPlayers().get(0);
					if (pNew != null)
					{
						game.setCurrentPlayer(pNew.getNickname());
						say(Colors.BOLD + pNew.getNickname() + Colors.NORMAL + " muss dann das nächste Wort bestimmen!");
					}
				}
			}
		}
		if (message.equals("!hilfe"))
		{
			sendMessage(sender, "!spielen          Zum mitspielen");
			sendMessage(sender, "!wort meinwort    Das naechste Wort setzen");
			sendMessage(sender, "!google meinwort  Dein Wort abgeben");
			sendMessage(sender, "!ende             Aus dem Spiel aussteigen");
			sendMessage(sender, "!hilfe            Die Hilfe anzeigen");
		}
	}
	
	public void timeOver()
	{
		say("Zeit fuer " + Colors.BOLD + game.getCurrentWord() + Colors.NORMAL + " vorbei!");
		game.setCurrentWord(null);
		
		String nextPlayer = "";
		int nextPoints = -2;
		
		for (Player player : game.getPlayers())
		{
			String word = player.getWord();
			String nickname = player.getNickname();
			int points = -1;
			int results = 0;
			
			if (word != null) {
				results = Google.search(word);
			}
			else {
				word = "---";
			}
			
			points = results / 1000;
			player.addPoints(points);
			say(" * " + Colors.BOLD + nickname + Colors.NORMAL + ": " + numberFormat.format(results) + " fuer " + Colors.BOLD + word + Colors.NORMAL + " (+" + numberFormat.format(results / 1000) + " Punkte, Gesamt: " + numberFormat.format(player.getPoints()) + ")");
			
			if (points > nextPoints) {
				nextPlayer = nickname;
				nextPoints = points;
			}
			if (points == nextPoints && rand.nextBoolean()) {
				nextPlayer = nickname;
			}
		}
		
		game.setCurrentPlayer(nextPlayer);
		say("Das naechste Wort bestimmt: " + nextPlayer);
	}
	
	public void say(String txt)
	{
		sendMessage(Main.CHANNEL, Colors.BOLD + "Super" + Colors.BLUE + "G" + Colors.RED + "G" + Colors.YELLOW + "a" + Colors.BLUE + "m" + Colors.GREEN + "e" + Colors.RED + "R" + Colors.NORMAL + " - " + txt);
	}
}
