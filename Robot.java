import java.net.*;
import java.io.*;
import java.util.*;
import java.io.BufferedReader;


public class Robot {

  /**************************************************************
   * Method purpose: It checks if a url passed as it's parameter
   *                 is allowed to be crawled by the crawler
   * 
   * Return value: 
   *    -> true if crawling is allowed
   *    -> false if exception or crawling not allowed 
   **************************************************************/
  public boolean isCrawlingAllowed(URL url) {
    String strHost, strProtocol, strFile = " ";
    final String robotsFile = "/robots.txt";
    final String DISALLOW = "Disallow:";

    try {
        // Create the url for the host robots.txt file
        strHost = url.getHost();
        strProtocol = url.getProtocol();

        URL nUrl =  new URL(strProtocol, strHost, robotsFile);
        

        // Get the file 
        BufferedReader in = new BufferedReader(new InputStreamReader(nUrl.openStream()));
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            strFile += "\n" + inputLine;

        // close the reader stream 
        in.close();
    } catch (MalformedURLException e) {
        // Error with URL formation don't crawl
        return false;
    } catch (IOException e) {
        // No robots.txt file so it's okay to crawl
        return true;
    }


	// Search for "Disallow:" fields
	String strURL = url.getFile();
    int index = 0;

	while ((index = strFile.indexOf(DISALLOW, index)) != -1) {
	    index += DISALLOW.length();
	    String strPath = strFile.substring(index);
	    StringTokenizer st = new StringTokenizer(strPath);

	    if (!st.hasMoreTokens())
		    break;
	    
	    String strBadPath = st.nextToken();

	    // If path starts with Disallowed: then skip 
	    if (strURL.indexOf(strBadPath) == 0)
		    return false;
    }
    
    // If we made it this far then the we are allowed to crawl the url
    return true;
  }

}