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

public class SpiderTest
{
	public static void main(String[] args)
    {
        Spider spider = new Spider("", 2);

        // This function should be replaced by a function that reads seeds in from file
        spider.testSeedInit();

        while(! spider.isPopulated() ){};

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
}