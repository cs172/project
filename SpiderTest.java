package com.ucr.cs172.project.crawler;
import java.awt.Robot;
import java.lang.Thread;
import java.util.ArrayList;

// Object to hold command line arguments
class ComandArguments {
	public String seedFilePath;
	public int maxHopDistance;
}

public class SpiderTest
{	
	public static void main(String[] args)
    {
		final String DEFAULTSEEDPATH = "seed.txt";
		final int DEFAULTMAXHOPS = 2;

		ComandArguments commandArgs = new ComandArguments();
		getArguments(commandArgs, DEFAULTSEEDPATH, DEFAULTMAXHOPS, args);
		Spider spider = new Spider(commandArgs.seedFilePath, commandArgs.maxHopDistance);
		

		spider.getUrlSeeds(commandArgs.seedFilePath);
        spider.testSeedInit();
        int listSize = spider.listSize();
        for(int i = 0; i < listSize; i++)
        {
        	try
        	{
        		while( spider.getQueueArrayList().get(i).size() > 0)
        		{
        			String temp = spider.nextUrl(i);

        			if(temp.length() > 0)
        			{
        				if((i+1) < listSize )
        				{
        					spider.crawl(temp, i+1);
        					//Thread.sleep(1000);
        				}
        				else
        				{
        					spider.crawl(temp, -1);
        					//Thread.sleep(1000);
        				}
        			}
        		}
        	}
        	catch(Exception e){ System.out.println("Interrupted");}
		}
	} // End main()
	

	public static void getArguments (ComandArguments arguments, String defaultFilePath, 
													 int defaultMaxHops, String[] args) 
	{

		if (args.length == 0 || args.length == 1) {
			System.out.println("*****************************************************");
			System.out.println("* No or too few arguments for seedFilePath and      *");
			System.out.println("* maxHopsDistance provided using default arguments. *");
			System.out.println("*****************************************************");;

			arguments.seedFilePath = defaultFilePath;
			arguments.maxHopDistance = defaultMaxHops;
		} else {
			arguments.seedFilePath = args[0];

			// convert string to int

			int maxHops = defaultMaxHops;
			try {
				maxHops = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				System.out.println("Conversion from string to integer error.");
			}

			arguments.maxHopDistance = maxHops;
		}
	} // End getArguments()
}