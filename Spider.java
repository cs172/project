package com.ucr.cs172.project.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileReader;
import java.net.URI;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Spider
{

	private String seed = "http://arstechnica.com/";
	private Document htmlDocument;

	private String seedFilePath;
	private int maxHopDistance;
	private boolean seedPopulated = false;
	private final long DOMAIN_WAIT_TIME_MILLI = 2000;

	//LinkedBlockingQueue is to be used without the blocking capabilities
	//ArrayList of queues that will track the hop depth from original seed urls
	private List<LinkedBlockingQueue<String>> queueArrayList;

	//This hash map will keep track 
	private ConcurrentHashMap<String, Long> visitedUrlHashMap = new ConcurrentHashMap<String, Long>();
	private ConcurrentHashMap<String, Long> visitedDomainHashMap =  new ConcurrentHashMap<String, Long>();

	public Spider(String seedFilePath, int maxHopDistance)
	{
		this.seedFilePath = seedFilePath;
		this.maxHopDistance = maxHopDistance;

		queueArrayList = new ArrayList<LinkedBlockingQueue<String>>();

		for (int i = 0; i <= maxHopDistance; i++)
		{
			queueArrayList.add(new LinkedBlockingQueue<String>());
		}

		this.seedPopulated = true;
	}

	public List<LinkedBlockingQueue<String>> getQueueArrayList()
	{
		return this.queueArrayList;
	}

	public ConcurrentHashMap<String, Long> getConcurrentHashMap()
	{
		return this.visitedUrlHashMap;
	}

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
            for(Element link : linksOnPage)
            {
            	if(queueNumber >= 0)
            	{
                	this.queueArrayList.get(queueNumber).put(link.absUrl("href"));
            	}
            }
            return true;
        }
        catch(Exception e)
        {
        	System.out.println("crawl exception");
            // We were not successful in our HTTP request
            return false;
        }
	}

	public String nextUrl(int queueNumber)
	{
		String nextUrl;

		do
		{
			nextUrl = this.queueArrayList.get(queueNumber).poll();

			nextUrl = normalizeUrl(nextUrl);

			while(nextUrl == "")
			{
				nextUrl = this.queueArrayList.get(queueNumber).poll();

				nextUrl = normalizeUrl(nextUrl);
			}
		}
		while(this.visitedUrlHashMap.containsKey(nextUrl));

		this.visitedUrlHashMap.put(nextUrl, System.currentTimeMillis());

		String hostUrl = getHost(nextUrl);
		System.out.println(hostUrl);

        if(this.visitedDomainHashMap.containsKey(hostUrl))
        {
        		long hostElapsedTime = System.currentTimeMillis() - this.visitedDomainHashMap.get(hostUrl);
        		System.out.println("" + hostElapsedTime);

            	if( hostElapsedTime < DOMAIN_WAIT_TIME_MILLI)
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

	public void testSeedInit()
	{
		try
		{
			queueArrayList.get(0).put(seed);
		}
		catch(Exception e)
		{
			System.out.println("testSeedInit Error");
		}
	}

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

	public List<String> getUrlSeeds (String seedsFilePath) {
		List<String> seeds = new ArrayList<String>();

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

}