package com.ucr.cs172.project.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;

import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.net.URI;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Attribute;
import org.jsoup.select.Elements;

import org.apache.commons.io.FileUtils;


import java.net.URL; 
import java.net.MalformedURLException; 

public class Spider
{

	private String seed = "https://www.usa.gov";	// Test url, will be replaced by seeds from file
	private Document htmlDocument;


	private boolean isFinished = false;
	private int siteNumber;

	private String seedFilePath;
	private int maxSites;
	private int maxHopDistance;
	//private boolean seedPopulated = false;				// Will be used to prevent threads from initiating work before queuee is seeded
	private final long DOMAIN_WAIT_TIME_MILLI = 1000;	// How long to wait before requests to same server

	//LinkedBlockingQueue is to be used without the blocking capabilities
	//ArrayList of queues that will track the hop depth from original seed urls
	private List<LinkedBlockingQueue<String>> queueArrayList;

	//This hash map will keep track 
	private ConcurrentHashMap<String, Long> visitedUrlHashMap = new ConcurrentHashMap<String, Long>();
	private ConcurrentHashMap<String, Long> visitedDomainHashMap =  new ConcurrentHashMap<String, Long>();

	public Spider(String seedFilePath, int maxSites, int maxHopDistance)
	{
		this.seedFilePath = seedFilePath;
		this.maxSites = maxSites;
		this.maxHopDistance = maxHopDistance;
		this.siteNumber = 0;

		queueArrayList = new ArrayList<LinkedBlockingQueue<String>>();

		for (int i = 0; i <= maxHopDistance; i++)
		{
			queueArrayList.add(new LinkedBlockingQueue<String>());
		}

		ArrayList<String> seeds = getUrlSeeds(seedFilePath);

		for(int i = 0; i < seeds.size(); i++)
		{
			try
			{
				queueArrayList.get(0).put(seeds.get(i));
			}
			catch(Exception e){}
		}
	}

	public List<LinkedBlockingQueue<String>> getQueueArrayList()
	{
		return this.queueArrayList;
	}

	public ConcurrentHashMap<String, Long> getConcurrentHashMap()
	{
		return this.visitedUrlHashMap;
	}//		}


	// Return value currently not used
	// Most of the work is handled here, requires object that handles Document storage
	public boolean crawl(String url, int queueNumber)
	{
		try
        {
            Connection connection = Jsoup.connect(url);
            Document htmlDocument = connection.get();
            this.htmlDocument = htmlDocument;

            if(connection.response().statusCode() == 200) // 200 is the HTTP OK status code
                                                          // indicating that everything is great.
            {
                System.out.println("\n**Visiting** Received web page at " + url);
            }
            if(!connection.response().contentType().contains("text/html"))
            {
                System.out.println("**Failure** Retrieved something other than HTML");
                return false;
            }
            Elements linksOnPage = htmlDocument.select("a[abs:href]");
            System.out.println("Found (" + linksOnPage.size() + ") links adding to queue number: " + queueNumber);

            updateSiteNumber();
            downloadPage();

            for(Element link : linksOnPage)
            {
            	if(queueNumber >= 0)
            	{
                	this.queueArrayList.get(queueNumber).put(link.absUrl("href"));
            	}
            }
            return true;
        }
        catch(IOException e)
        {
        	System.out.println("crawl exception");
            // We were not successful in our HTTP request
            return false;
        }
        catch(Exception i)
        {
        	System.out.println("./storage/ direcotry does not exist: Exiting...");
        	return false;
        }	
	}

	public void downloadPage() throws Exception {
		final Document doc = htmlDocument;
		if(fileSize()) {
			final File f = new File("storage/"+ doc.hashCode() + ".html");
			
			FileUtils.writeStringToFile(f, doc.outerHtml(), "UTF-8");
		}
	}
	
	public boolean fileSize() {
		long size = FileUtils.sizeOfDirectory(new File("storage/"));
		double GB = 1073741824;
		if(size >= 5*GB)
		{
			isFinished = true;
			System.out.println("File is at or past 5GigaBytes");
			return false; // 5 GB to Bytes = 5368709120
		}
		return true;
	}

	public boolean isFinished()
	{
		return isFinished;
	}

	public synchronized void updateSiteNumber()
	{
		siteNumber++;
		if(siteNumber >= maxSites)
		{
			isFinished = true;
		}
	}

	public String nextUrl(int queueNumber)
	{
		String nextUrl;

		do
		{
			nextUrl = this.queueArrayList.get(queueNumber).poll();

			nextUrl = normalizeUrl(nextUrl);

			while(nextUrl == "" || !confirmDotGov(nextUrl))
			{
				nextUrl = this.queueArrayList.get(queueNumber).poll();

				nextUrl = normalizeUrl(nextUrl);
			}
		}
		while(this.visitedUrlHashMap.containsKey( removeProtocol(nextUrl) ));

		this.visitedUrlHashMap.put( removeProtocol(nextUrl), System.currentTimeMillis());

		String hostUrl = getHost(nextUrl);
		// Testing print statement
		System.out.println("Host URL: " + hostUrl);

        if(this.visitedDomainHashMap.containsKey(hostUrl))
        {
        		long hostElapsedTime = System.currentTimeMillis() - this.visitedDomainHashMap.get(hostUrl);
        		// Testing print statement	
        		System.out.println("Last millis since last visit of: " + hostUrl + ": " + hostElapsedTime);

            	while( (System.currentTimeMillis() - this.visitedDomainHashMap.get(hostUrl)) < DOMAIN_WAIT_TIME_MILLI)
            	{
            		try
            		{
            			Thread.sleep(hostElapsedTime);
            		}
            		catch(Exception e) 
            		{
            			System.out.println("Interrupted");
            		}
            	}
        }

        this.visitedDomainHashMap.put(hostUrl, System.currentTimeMillis());

		return nextUrl;
	}

	public ArrayList<String> getUrlSeeds (String seedsFilePath) {
		ArrayList<String> seeds = new ArrayList<String>();

		// Read from the file and store the urls in a List
		try {
			// Get the file 
			FileReader fr = new FileReader(seedsFilePath);
			BufferedReader in = new BufferedReader(fr);
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				seeds.add(inputLine);

			// close the reader stream 
			in.close();
		} catch (IOException e) {
			System.out.println("File I/O Error!!!");
		}

		return seeds;
	}
	
	public int listSize()
	{
		try
		{	
			//System.out.println("Queue size: " + queueArrayList.size());
			return queueArrayList.size();
		}
		catch(Exception e)
		{
			System.out.println("listSize Error");
			return 0;
		}
	}

	/*
	public boolean isPopulated()
	{
		return seedPopulated;
	}

	// Placeholder function, needs to be replaced by function that reeds seeds from file
	public void testSeedInit()
	{
		try
		{
			queueArrayList.get(0).put(seed);

			if(this.maxHopDistance > 0)
			{
				this.crawl( this.nextUrl(0) , 1);
				this.seedPopulated = true;
			}
		}
		catch(Exception e)
		{
			System.out.println("testSeedInit Error");
		}
	}
	*/

	// Testing function - depricated
	public void printSeedQ()
	{
		try
		{
			System.out.println("Seed Size: " + queueArrayList.get(0).size());
		}
		catch(Exception e)
		{
			System.out.println("printSeedQ Error");
		}
	}
	
	public String removeTrailingSlash(String url)
	{
		if(url.length() > 0)
		{
			if(url.charAt(url.length() - 1) == '/')
			{
				return new String(url.substring(0, url.length()-1));
			}
		}
		
		return url;
	}

	public String removeBookmark(String url)
	{
		if(url.lastIndexOf('#') >= 0)
		{
			return "";
		}

		return url;
	}

	public String removeProtocol(String url)
	{
		if(url.indexOf("https://") >= 0)
		{
			return url.substring(9);
		}
		else if(url.indexOf("http://") >= 0)
		{
			return url.substring(8);
		}

		return url;
	}

	//Will be used to verify .gov urls once we clear testing phase
	public boolean confirmDotGov(String url)
	{
		if(url.indexOf(".gov") != -1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// calls helper functions to normalize url and return normalized url if valid
	// otherwise returns empty string ""
	public String normalizeUrl(String url)
	{
		String temp = removeBookmark(url);

		if (temp == "")
		{
			return "";
		}
		else
		{
			return removeTrailingSlash(temp); 
		} 
	}

	// returns host url which is used to track requests to specific server
	public String getHost(String url)
	{
		try
		{
			URI temp = new URI(url);

			return temp.getHost();
		}
		catch(Exception e)
		{	
			return "";
		}

	}
}