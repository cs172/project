package com.ucr.cs172.project.crawler;

import java.io.IOException;
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


	//LinkedBlockingQueue is to be used without the blocking capabilities
	//ArrayList of queues that will track the hop depth from original seed urls
	private List<LinkedBlockingQueue<String>> queueArrayList;

	//This hash map will keep track 
	private ConcurrentHashMap<String, Integer> visitedHashMap = new ConcurrentHashMap<String, Integer>();

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

	public ConcurrentHashMap<String, Integer> getConcurrentHashMap()
	{
		return this.visitedHashMap;
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
            Elements linksOnPage = htmlDocument.select("a[href]");
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
		while(this.visitedHashMap.containsKey(nextUrl));

		this.visitedHashMap.put(nextUrl, 0);

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
}