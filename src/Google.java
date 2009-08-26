import java.net.*;
import java.io.*;
import java.util.regex.*;
import java.util.*;

public class Google {
	private static HashMap<String, Integer> cache = new HashMap<String, Integer>();
	
	/**
	 * Google for word and return number of search results
	 * 
	 * @param word
	 * @return
	 */
	public static synchronized int search(String word) {
		if (cache.containsKey(word)) {
			return cache.get(word);
		}
		try {
			word = word.toLowerCase();
			//URL url = new URL("http://www.google.de/search?hl=de&q=" + word + "&btnG=Google-Suche&meta=");
			URL url = new URL("http://www.google.de/search?hl=de&safe=off&q=%22" + word + "%22&btnG=Suche&meta=lr%3Dlang_de");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; MSIE 7.0; Windows NT 6.0; en-US)");
			InputStream stream = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
			String line;
			Pattern p = Pattern.compile(".*</b> von ungefähr <b>([0-9\\.]+)</b> Seiten.*");
			
			while((line = reader.readLine()) != null) {
				Matcher m = p.matcher(line);
				if (m.matches()) {
					String results = m.group(1).replace(".", "");
					int num = Integer.parseInt(results);
					cache.put(word, num);
					return num;
				}
			}
		}
		catch(MalformedURLException e)
		{
			System.out.println("Malformed Url: " + e.getMessage());
		}
		catch(IOException e)
		{
			System.out.println("IO: " + e.getMessage());
		}
		return 0;
	}
	
	public static void main(String[] args)
	{
		System.out.println(Google.search("orangecounty"));
	}
}
