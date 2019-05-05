
# Spring 2019 CS172 Project

The program is a multithreaded Web Crawler for gov pages written in java. The program uses the urlSeeds.txt file to seed the crawler with .gov urls. Then it follows hyperlinks on the seed pages to other sites and downloads the page (html files) if the link corresponses to a .gov site. The  [jsoup](https://jsoup.org/) library is used to parse the html webpages. You can specify the path for the file with the url seeds and the max hop distance from the seed urls via the command prompt.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 

### Prerequisites

* If Java is not installed go [here](https://java.com/en/download/help/download_options.xml#windows) to install java for your operating system. 
* Unzip the Project.
* Open a terminal
```
Windows:
Press WindowsKey+r then type "cmd"

Linux(Ubuntu):
Press Ctrl+Alt+t
```

* Change to project directory
```
Windows: (in CMD terminal)
Type cd path\to\the\project\folder 

Linux(Ubuntu):
Type cd path\to\the\project\folder 
```
* Must include a folder name "storage" in the project directory in order to run. We included this folder already in the project zip file. If it get's deleted just create another one in the project directory.

## Instructions

Easiest method:

Type in command terminal 
```
./run.sh [seed file path] [max sites] [max hop distance] 
```
Else if not working:
### Compiling the Program

```
Windows: (in CMD terminal)
javac -d . -cp ".;./jsoup-1.11.3.jar;commons-io-2.6.jar" *.java

Linux(Ubuntu):
javac -d . -cp ".:./jsoup-1.11.3.jar:commons-io-2.6.jar" *.java
```

## Running the Program

### In terminal

Argument Template: .SpiderTest   [seed file path]  [max sites] [max hop distance] 
```
Windows: (in CMD terminal)
java -cp ".;./jsoup-1.11.3.jar;commons-io-2.6.jar" com.ucr.cs172.project.crawler.SpiderTest ./seeds.txt 2000 500


Linux(Ubuntu):
java -cp ".:./jsoup-1.11.3.jar:commons-io-2.6.jar" com.ucr.cs172.project.crawler.SpiderTest ./seeds.txt 2000 500 
```

## Built With

* [GitHub]([https://github.com/](https://github.com/)) - Remote Repo for team collaboration
* [Jsoup]([[https://jsoup.org/](https://jsoup.org/)]) - Java html parser library


## Authors

* **Raudel Blazquez Munoz**
* **Ji Houn**
* **Juan Ceja**

