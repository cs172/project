import java.net.*;
import java.io.*;
import java.util.*;



public class test {
    public static void main(String[] args) {

        URL url;

        try {
            url = new URL("https://www.ucr.edu/about/ranks-and-facts");

            Robot robot = new Robot();

            if (robot.isCrawlingAllowed(url)) {
                System.out.println("Test Complete");
            }

        } catch (MalformedURLException e) {
            //TODO: handle exception
        }
    } 
}