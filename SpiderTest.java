package com.ucr.cs172.project.crawler;
import java.lang.Thread;

class MultithreadTest extends Thread
{
    Spider spider;

    public MultithreadTest(Spider spider)
    {
        this.spider = spider;
    }

    public void run()
    { 
        try{
            int listSize = spider.listSize();
            for(int i = 0; i < listSize; i++)
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
        }
        catch(Exception e)
        {
            System.out.println("MultithreadTest Fail");
        }
    }
}

class ComandArguments
{
    public String seedFilePath;
    public int maxHopDistance;
}

public class SpiderTest
{
	public static void main(String[] args)
    {
        
        ComandArguments commandArgs = new ComandArguments();
        getArguments(commandArgs," ", 2, args);
        Spider spider = new Spider(commandArgs.seedFilePath, commandArgs.maxHopDistance);

        MultithreadTest one = new MultithreadTest(spider);
        MultithreadTest two = new MultithreadTest(spider);
        MultithreadTest three = new MultithreadTest(spider);
        MultithreadTest four = new MultithreadTest(spider);
        //MultithreadTest five = new MultithreadTest(spider);
        //MultithreadTest six = new MultithreadTest(spider);
        //MultithreadTest seven = new MultithreadTest(spider);

        one.start();
        two.start();
        three.start();
        four.start();

        /*
        
        // unsure if I will have this functionality moved to Spider class
        // can possible leave this functionality for thread. Requires testing
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
        */
    }

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