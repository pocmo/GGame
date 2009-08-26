public class Player {
	private int points;
	private String nickname;
	
	private String word = null;
	
	public Player(String nickname)
	{
		this.nickname = nickname;
		this.points = 0;
	}
	
	public String getNickname()
	{
		return nickname;
	}
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}
	public int getPoints()
	{
		return points;
	}
	
	public void setPoints(int points)
	{
		this.points = points;
	}
	
	public void addPoints(int points)
	{
		this.points += points;
	}
	
	public boolean hasSearched()
	{
		return word != null;
	}
	
	public String getWord()
	{
		return word;
	}
	
	public void setWord(String word)
	{
		this.word = word;
	}
}
