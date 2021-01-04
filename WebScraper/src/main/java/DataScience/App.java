package DataScience;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Hello world!
 *
 */
public class App 
{
	static String stopwords[] = {"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};
    static List<String> stopWordsList = Arrays.asList(stopwords);
    static List<String> finalStrings = new ArrayList<String>();
    static List<String> finalStringsSource = new ArrayList<String>();
    
    public static List<String> removeStopWords(String givenString)
    {
    	String[] content = givenString.split(" ");
    	List<String> doc1 = new ArrayList<String>();
    	for(String currentString:content)
    	{
    		if(!stopWordsList.contains(currentString))
    				{
    					doc1.add(currentString.toLowerCase());
    				}
    	}
    	
    	return doc1;
    }
	public static void main( String[] args )
    {
    	Scanner sc = new Scanner(System.in);
    	String searchString = sc.next();

    	WebClient client = new WebClient();  
    	client.getOptions().setCssEnabled(false);  
    	client.getOptions().setJavaScriptEnabled(false);  
    	try {  
    		
    		String searchUrl = "https://news.google.com/search?q="+searchString+"&hl=en-IN&gl=IN&ceid=IN%3Aen";
    		HtmlPage page = client.getPage(searchUrl);
    	  
    	  
    	  List<HtmlElement> items = (List<HtmlElement>) page.getByXPath("//div[@jscontroller='d0DtYd']") ;
//
    	  items.get(0).asText();
    	  items.get(0).asXml();
    	  for (HtmlElement item : items) {
    		    List<HtmlElement> children = (List<HtmlElement>) item.getByXPath(".//article");
    		    for (HtmlElement child : children) {
    		    System.out.println(child.asText().split("\n")[0]);
    		    finalStrings.add(child.asText().split("\n")[0]);
    		    System.out.println(child.asText().split("\n")[2].split("ampvideo_youtube")[1]);
    		    finalStringsSource.add(child.asText().split("\n")[2].split("ampvideo_youtube")[1]);
    		    }
    	  }
    	  
    	  

  		String searchUrlBing = "https://www.bing.com/news/search?q="+searchString+"&qs=n&form=NWRFSH&sp=-1&pq=&sc=0-0&sk=&cvid=EDB86B50C78442CA8CE0ED515C4B597C";
  	  HtmlPage page2 = client.getPage(searchUrlBing);
  	  
  	  List<HtmlElement> items2 = (List<HtmlElement>) page2.getByXPath("//div[@class='main']") ;
  	  Iterable<DomElement> iterator = items2.get(0).getChildElements();
  	  Iterator<DomElement> iteratorActual = iterator.iterator();
  	  items2 = new ArrayList<HtmlElement>();
  	  while(iteratorActual.hasNext())
  	  {
  		  items2.add((HtmlElement) iteratorActual.next());
  	  }
//  	  
  	  iteratorActual = items2.get(1).getChildElements().iterator();
  	  items2 = new ArrayList<HtmlElement>();
  	 
  	 while(iteratorActual.hasNext())
 	  {
 		  items2.add((HtmlElement) iteratorActual.next());
 		  
 	  }

  	 for (HtmlElement item : items2) {

  		 System.out.println(item.asText().split("\n")[0]);
  		 finalStrings.add(item.asText().split("\n")[0]);
  		System.out.println(item.asText().split("\n")[2]);
  		finalStringsSource.add(item.asText().split("\n")[2]);
  		
  		
	  }
  List<double[]> test =	similarityLogic(finalStrings);
  	System.out.println("hello");
  	HashSet<Integer> ignoreSet = new HashSet<Integer>();
  	for(int i=0;i<test.size();i++)
  	{
  		for(int j=0;j<test.size();j++)
  		{
  			if(i!=j)
  			{
  				double similarityQuotient = cosineSimilarity(test.get(i),test.get(j));
  				if(similarityQuotient>0.09)
  				{
  					System.out.println("similarity between "+i+"and "+j+"is "+similarityQuotient);
  					System.out.println(finalStrings.get(i)+"-"+finalStringsSource.get(i));
  					System.out.println(finalStrings.get(j)+"-"+finalStringsSource.get(j));
  				}
  				
  			}
  		}
  	}
    	}catch(Exception e){
    	  e.printStackTrace();
    	}
    }
    
    private static List<double[]> similarityLogic(List<String> finalStrings) {
    	HashSet<String> wordVector = new HashSet<String>(); 
    	List<double[]> vectorList = new ArrayList<double[]>();
    	List<List<String>> strippedList = new ArrayList<List<String>>();
    	TFIDFCalculator tfidfcalc = new TFIDFCalculator();
    	for(String current:finalStrings)
    	{	
    		
    		strippedList.add(removeStopWords(current));
    		List<String> currStrings = strippedList.get(strippedList.size()-1);
    		for(String currString:currStrings)
    		{
    			wordVector.add(currString);
    		}
    	}
    	
		
    	for(List<String> currList:strippedList)
    	{
    		double [] currDoubleList = new double[wordVector.size()];
    		int counter = 0;
    		for(String currString:wordVector)
        	{
    			
    			double temp = tfidfcalc.tf(currList, currString)*tfidfcalc.idf(strippedList, currString);
    			currDoubleList[counter++] = temp;
    			
        	}
    		vectorList.add(currDoubleList);
    	}
    	return vectorList;
    	
	}
	public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }   
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}




