import java.util.*;
public class BotTask extends TimerTask {
	private Bot bot;
	
	public BotTask(Bot bot)
	{
		this.bot = bot;
	}
	
	public void run()
	{
		try
		{
			bot.say(3 + "..");
			Thread.sleep(1000);
			bot.say(2 + "..");
			Thread.sleep(1000);
			bot.say(1 + "..");
			Thread.sleep(1000);
		}
		catch(Exception e) {}
		bot.timeOver();
	}
}
