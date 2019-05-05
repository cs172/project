package com.ucr.cs172.project.crawler;

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
   *    -> list with disallowed values
   **************************************************************/
  public List<String> isCrawlingAllowed(URL url) {
    String strHost, strProtocol, strFile = " ";
    final String robotsFile = "/robots.txt";
    final String DISALLOW = "Disallow:";

    List<String> disList = new ArrayList<String>();

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

        // Search for "Disallow:" fields
        int index = 0;

	    while ((index = strFile.indexOf(DISALLOW, index)) != -1) {
	        index += DISALLOW.length();
        
            String strPath = strFile.substring(index);
	        StringTokenizer st = new StringTokenizer(strPath);

	        if (!st.hasMoreTokens())
		        break;
            
            // Get bad path and add to the disallowed list
            String strBadPath = st.nextToken();
            disList.add(strBadPath);
        }
    } catch (MalformedURLException e) {
        // Error with URL formation don't crawl
        System.out.println("Int Robot.java MalformUrl exception");
    } catch (IOException e) {
        // No robots.txt file so it's okay to crawl
        System.out.println("Int Robot.java I/O exception");
    }
    
    // Return list of disallowed fields
    return disList;
  }

}