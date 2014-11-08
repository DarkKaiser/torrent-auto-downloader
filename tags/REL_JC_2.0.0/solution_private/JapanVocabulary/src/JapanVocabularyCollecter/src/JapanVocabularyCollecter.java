import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;

public class JapanVocabularyCollecter {

	public static void main(String[] args) throws IOException {
		// http://www.tutorialspoint.com/sqlite/sqlite_java.htm
		Connection c = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection("jdbc:sqlite:test.db");
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Opened database successfully");
	    
	    
	    
	    ////////////////////////////
	    
	    URL url = null;
		try {
			url = new URL("http://www.website.com");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    URLConnection spoof = url.openConnection();

	    //Spoof the connection so we look like a web browser
	    spoof.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0;    H010818)" );
	    BufferedReader in = new BufferedReader(new InputStreamReader(spoof.getInputStream()));
	    String strLine = "";
	    String finalHTML = "";
	    //Loop through every line in the source
	    while ((strLine = in.readLine()) != null){
	       finalHTML += strLine;
	    }
	    
	    System.out.println(finalHTML);
	}

}
