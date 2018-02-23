
package twitter.crawler;
    import java.util.List;
import java.util.Map;
    import twitter4j.*;
    import twitter4j.auth.AccessToken;
    import twitter4j.conf.ConfigurationBuilder;

public class Tweet implements Runnable {
  
    private String s;
    private Twitter t;
    File_IO file=new File_IO();
    
    public  Tweet(String s,Twitter t)
    {
        this.s=s;
        this.t=t;
    }
    
    public void searching()
    {
          try
        {
            Query q=new Query(this.s);
             QueryResult result;
            result=this.t.search(q);
           
            do {
                result = this.t.search(q);
                List<Status> tweets = result.getTweets();
                for (Status tweet : tweets) {
                
                   User u;
                   u=tweet.getUser();
                   //ftiaxnoume ton fakelo tou xristi
                   
                   file.make_dir(u.getName());
                   //ftiaxnoume to arxeio me ta info
                 
                //pairnoume ta remaininig limits gia tous users
                //an eimaste entaksei apothikeuoume ton xristi 
              RateLimitStatus user_limits = t.getRateLimitStatus("users").get("/users/lookup");
              
                   if(user_limits.getRemaining()>=1)
                   {
                   file.saveUser(u);
                   }
                   else
                   {this.stop(user_limits.getResetTimeInSeconds());}
                   
                   //ftiaxnoume to arxeio me ta tweet info
                   //kanoume to idio me to tweet 
                   RateLimitStatus tweet_limits = t.getRateLimitStatus("statuses").get("/statuses/lookup");
                   if(tweet_limits.getRemaining()>=1)
                   {
                   file.saveTweet(tweet);
                   }
                   else
                   {this.stop(tweet_limits.getResetTimeInSeconds());}
                   
                    //dinoume to tweet kai ton user
                   //wste na broume tous dikous tou followers
                  file.Save_Followers(u,t);
                  
                  // this.jump(u, s);
        
      
           

          
    
                    
                   
                  
                 
                
              
                    
                    
                }
} while ((q= result.nextQuery()) != null);
            
            
        }
        
        catch(Exception e)
        {
        
        }
    
       }
    
    public void jump(User u,String hastag) throws TwitterException
    {
        List<Status> statusList2 = null;
        
        List<Status> statusList = t.getUserTimeline(u.getId());
        for (Status status : statusList) {
       if(status.getText().toLowerCase().contains(hastag)){
           statusList2.add(status);
  }
 } 
    
    }
    //otan kapoio apo ta limits ftasei konta sto 1
    //to thread tha stamataei gia 15 lepta sun 10 deuterolepta gia safe
    public void stop(int time) throws InterruptedException
    {Thread.currentThread().wait(((time)*1000)+10000);
    }

    @Override
    public void run() {
        this.searching();
    }

   
    
    }
    

