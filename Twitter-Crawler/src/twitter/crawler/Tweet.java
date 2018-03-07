
package twitter.crawler;
import java.util.ArrayList;
    import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
    import twitter4j.*;
    import twitter4j.auth.AccessToken;
    import twitter4j.conf.ConfigurationBuilder;

public class Tweet implements Runnable {
    
    //posous follower prepei na exoun oi followers gia na aksizei na elengxoun
  final static int Threshold_Of_Followers=500;
   private Status tweet;
   private Twitter t;
   private String hastag;  
   File_IO file=new File_IO();
   final Lock lock = new ReentrantLock();
   final Condition notFull  = lock.newCondition(); 
   final Condition notEmpty = lock.newCondition(); 
    
    public  Tweet(Twitter tw,Status tweet,String h)
    {
       this.tweet=tweet;
       this.t=tw;
       this.hastag=h;
       file.make_dir(tweet.getUser().getName());
    }
    
    
    public  synchronized void Find_Followers_of_Followers(User user) throws InterruptedException 
    {
        
        //pairnoume ton xristi tou tweet
        ArrayList<Long> followers_id = new ArrayList<Long>();
         RateLimitStatus follower_limits = null;
        
        
         try {
       long cursor1=-1;
       long cursor2=-1;
       IDs follower_id = null;
       IDs followers_follower_id = null;
             //pairnoume tous followers tou xristi
         follower_limits = t.getRateLimitStatus("followers").get("/followers/ids");   

       do
   {
        
       
         //elegxoume an logo periorismo mporoume na paroume
         //tin lista me tous followers
         System.out.println("Apomenoun gia followers :"+follower_limits.getRemaining());
         if(follower_limits.getRemaining()>=1)
         {
       follower_id=this.t.getFollowersIDs(user.getId(),-1); 
       
       for (long id : follower_id.getIDs()) {
        //gia kathe follower pairnoume tous followers
       twitter4j.User follower = t.showUser(id);
       //edw mporoume na apothikeusoume oti pliroforia
       //theloume gia tous followers
       do
       {
           //gia kathe follower pairnoume tin lista me tous dikous tou followers
           //an to megethos tis listas einai panw apo to katwfli 
           //kai uparxei sto timeline tou xristi to hastag
           //tote o xristis mas kanei kai ton prosthetoume stin lista me ta ids
           //pali tha prepei na kanoume ton idio elegxo
           //gia tin lista me ta follower ids tou follower
           System.out.println("Apomenoun gia followers 2 :"+follower_limits.getRemaining());
           if(follower_limits.getRemaining()>=1)
           {
           followers_follower_id=this.t.getFollowersIDs(follower.getId(),-1);
           
           long[] fofIDS=followers_follower_id.getIDs();
           
           System.out.println("Plithos followers :"+fofIDS.length);
           if(fofIDS.length>Threshold_Of_Followers&& this.check_timeline(user))
           {System.out.println("Plirei: ");
           followers_id.add(user.getId());}
           
           }
           else if(follower_limits.getRemaining()<1)
           {this.stop(follower_limits.getSecondsUntilReset());}
           else if(followers_follower_id.getNextCursor()==0)
           {break;}
       }while ((followers_follower_id.getNextCursor()) != 0);
       
       }
       
         }
         //alliws kanoume to current thread pou exei analabei
         //to psasksimo na perimenei gia oso xrono prepei
         else if(follower_limits.getRemaining()<1)
         {  System.out.println("Telos twn limits");
            this.stop(follower_limits.getSecondsUntilReset());}
         else if(follower_id.getNextCursor()==0)
         {break;}
       
   }while ((follower_id.getNextCursor()) != 0);
   
   
    
       for(int i=0;i<followers_id.size();i++)
       {
         
           //ftiaxnoume ena object gia kathe follower tou follower
           twitter4j.User fofollower;
           fofollower=t.showUser(followers_id.get(i));
           //gia tous followers tou follower
           //
          //kane otidipote
          //apothikeuse ton xristi
        //telos kalese tin idia sunartisi
           //wste anadromika na psaksei kai gia ekeinous
           //ta idia pragmata
           //i anadromi otan ston se kapoion apo tous xristes
           //den brethei to hastag se kanenan xristi
           //i den plirei to sunolo twn aparaititwn follower
           //i lista tha einai keni kai den tha klithei peretero i sunartisi
           this.Find_Followers_of_Followers(fofollower);
           
       }
    }
        
         catch(TwitterException ex)
         {
             
             if(ex.getErrorCode()==88){System.out.println("Code :"+ex.getExceptionCode());
             this.stop(follower_limits.getSecondsUntilReset());}
         }
         catch(NullPointerException exe)
         {System.out.println("Tellos follwers");
         System.exit(0);
         }
        
         
    }
    
    //pame sto timeline tou user kai blepoume an uparxei to tweet me to 
    //hastag pou theloume, an exei ginei diladi retweet
    //edw uparxei omws periorismos
    //epistrefei ta 3,200 pio prosfata Tweets
    //
    public boolean check_timeline(User u)
    {String key=this.hastag.replace("#","");
        System.out.println("Hastag xwris #: "+key);
       boolean answer=false;
       try{
List<Status> statusList = t.getUserTimeline(u.getId());
 for (Status status : statusList) {
     twitter4j.HashtagEntity h[];
     h=status.getHashtagEntities();
     for(int i=0;i<h.length;i++)
    {
        if(h[i].getText().equals(key))
                {answer=true;}
     }
     
     
  
 } 
   
    } 
       catch(Exception e)
                 {System.out.println("AOYA2");
                 e.printStackTrace();} 
       System.out.println("Brethike tweet: "+answer);
       return answer;
    }
   

    

    //otan kapoio apo ta limits ftasei konta sto 1
    //to thread tha stamataei gia 15 lepta sun 10 deuterolepta gia safe
    public void stop(int time) throws InterruptedException
    {
        Thread.sleep(((time)*1000)+10000);
        
    }

    @Override
    public void run() {
      
         
      try {
          this.Find_Followers_of_Followers(this.tweet.getUser());
      } catch (InterruptedException ex) {
          java.util.logging.Logger.getLogger(Tweet.class.getName()).log(Level.SEVERE, null, ex);
      }
     
         
              try {
              this.stop((15*1000)+10000);
              } catch (InterruptedException ex1) {
                 // java.util.logging.Logger.getLogger(Tweet.class.getName()).log(Level.SEVERE, null, ex1);
              }
}
         // java.util.logging.Logger.getLogger(Tweet.class.getName()).log(Level.SEVERE, null, ex);
      }
       
    

   
    
    
   

