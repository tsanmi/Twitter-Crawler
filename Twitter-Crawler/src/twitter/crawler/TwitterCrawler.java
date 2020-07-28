
package twitter.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import twitter4j.IDs;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.RateLimitStatus;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterCrawler {
      
      

  
    public static void main(String[] args) {
         ConfigurationBuilder cf=new ConfigurationBuilder();
    cf.setDebugEnabled(true).setOAuthConsumerKey("xxxxx").
            setOAuthConsumerSecret("xxxxx")
            .setOAuthAccessToken("1946495047-xxxxx")
            .setOAuthAccessTokenSecret("xxxxx");
            TwitterFactory tf=new TwitterFactory(cf.build());
            twitter4j.Twitter twitter=tf.getInstance(); 
            ArrayList<Status> retweet=new ArrayList<Status>();
            String s="#NBA";
            

            //briskoume ta retweet 
            //kai pairnoume ta arxika tweets pou eginan
            
            retweet=search_query(twitter,s);
            
            //afou exoume brei tin lista me ta retweet
            //exoume brei ousiastika to prwto tweet to opoio
            //exei ginei retweet
            //gia kathe tetoio tweet
            //ftiaxnoume ena thread
            //to opoio ksekinaei apo to arxiko thread
            //kai psanxei tou followers tou na dei poios exei kanei
            //retweet
            ExecutorService executor=Executors.newFixedThreadPool(retweet.size());
            for(int i=0;i<retweet.size();i++)
            {
                Runnable tweet=new Tweet(twitter,retweet.get(i),s);
                executor.execute(tweet);
            }
            executor.shutdown();
            while(!executor.isTerminated())
            {}
            System.out.println("All threads finished");
            
           
             
      
           
    }
    public static ArrayList<Status> search_query(Twitter t,String s)
    {   
           ArrayList<Status> retweet=new ArrayList<Status>();
        
          try
        {
            //Xrisimopoioume to standar search API ara mas epistrefei 7 imerwn tweets
            //pou tairiazoun me to hastag pou exoume balei
            //to query den ginetai se kapoio timeline
            //alla genika
            
            Query q=new Query(s);
             QueryResult result;
            result=t.search(q);
             RateLimitStatus tweet_limits = t.getRateLimitStatus("search").get("/search/tweets");
             System.out.println(tweet_limits.getRemaining());
           
            do {
                //pairnoume ola ta tweets
                result = t.search(q);
                List<Status> tweets = result.getTweets();
                
                for (Status tweet : tweets) {
                
                   
                  //An to retweet status den einai null
                  //pairnoume to arxiko tweet pou exei ginei retweet
                  //me tin ipothesi oti i methodos getRetweetedStatus() pairnei apo to tweet
                  //to pedio retweeted_status kathws den eksigei sto api tis twitter4j an kanei auto akribws
                  
                   if(tweet.getRetweetedStatus()!=null)
                   {
                   retweet.add(tweet.getRetweetedStatus());
                   
                   }
                   
                   //elegxoume an einai kai Quoted
                   else if(tweet.getQuotedStatus()!=null)
                   {    
                       
                   }
                    //alliws to tweet einai apo emas
                   else
                   { 
                      
                   
                   }
                   
                }
} while ((q= result.nextQuery()) != null);
             
        }
        
        catch(Exception e)
        {
            e.printStackTrace();
        }
          return retweet;
        
    }
    
}
