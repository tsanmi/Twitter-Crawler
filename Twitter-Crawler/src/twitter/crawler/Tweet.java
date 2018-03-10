
package twitter.crawler;
import java.util.ArrayList;
    import java.util.List;
import java.util.logging.Level;
    import twitter4j.*;

public class Tweet implements Runnable {
    
    //posous follower prepei na exoun oi followers gia na aksizei na elengxoun
  final static int Threshold_Of_Followers=500;
  final static int Cap=1;
   private Status tweet;
   private Twitter t;
   private String hashtag;  
   File_IO file=new File_IO();
   
    
    public  Tweet(Twitter tw,Status tweet,String h)
    {
       this.tweet=tweet;
       this.t=tw;
       this.hashtag=h;
       
    }
    
    
    public   void Search(User user) throws InterruptedException 
    {
        //arxika dimiourgia fakelou gia ton current xristi
        //kai apothikeusi twn stoixeiwn tou
        //kathws kai tou tweet
       file.make_dir(user.getName());
        file.saveTweet(this.tweet);
        file.saveUser(user);
       
        ArrayList<Long> followers_id = new ArrayList<Long>();
         RateLimitStatus follower_limits = null;
        
        
        
      
       IDs follower_id = null;
       IDs followers_follower_id = null;
             //pairnoume tous followers tou xristi
       

          synchronized (this){ 
       do
   {    
       try {  
           follower_limits = t.getRateLimitStatus("followers").get("/followers/ids");   
           
         //elegxoume an logo periorismo mporoume na paroume
         //tin lista me tous followers
        System.out.println(Thread.currentThread().getName()+" Apomenoun gia followers eksw apo to if :"+follower_limits.getRemaining());
         if(follower_limits.getRemaining()>=Cap)
         {
              follower_id=this.t.getFollowersIDs(user.getId(),-1); 
         System.out.println(Thread.currentThread().getName()+" Apomenoun gia followers meta to getFollowers :"+follower_limits.getRemaining());
      
       
       for (long id : follower_id.getIDs()) {
            System.out.println(Thread.currentThread().getName()+" Apomenoun gia followers meta to getIDS :"+follower_limits.getRemaining());
        //gia kathe follower pairnoume tous followers
       twitter4j.User follower = t.showUser(id);
       //edw mporoume na apothikeusoume oti pliroforia
       //theloume gia tous followers
       do
       {
           try{
           //gia kathe follower pairnoume tin lista me tous dikous tou followers
           //an to megethos tis listas einai panw apo to katwfli 
           //kai uparxei sto timeline tou xristi to hastag
           //tote o xristis mas kanei kai ton prosthetoume stin lista me ta ids
           //pali tha prepei na kanoume ton idio elegxo
           //gia tin lista me ta follower ids tou follower
            System.out.println("Apomenoun gia followers 2 :"+follower_limits.getRemaining());
           if(follower_limits.getRemaining()>=Cap )
           {
                followers_follower_id=this.t.getFollowersIDs(follower.getId(),-1);
          
          
           
           long[] fofIDS=followers_follower_id.getIDs();
           
           System.out.println("Plithos followers :"+fofIDS.length);
           if(fofIDS.length>Threshold_Of_Followers&& this.check_timeline(user))
           {System.out.println("Plirei");
           followers_id.add(user.getId());}
           
           }
           else if(follower_limits.getRemaining()<Cap)
           {this.stop(follower_limits.getSecondsUntilReset());}
           else if(followers_follower_id==null)
           {break;}
           
           
           }
                    catch(TwitterException ex)
         {
             
             if(ex.getErrorCode()==88){System.out.println("Exception logw telos twn stous followers twn follower:");
             this.stop(follower_limits.getSecondsUntilReset());
             continue;
             }
         }
         catch(NullPointerException exe)
         {exe.printStackTrace();
            System.out.println("Cursor null pointer stous fof");
         
         }
         
           
       }while ((followers_follower_id.getNextCursor()) != 0);
       
       }
       
         }
         //alliws kanoume to current thread pou exei analabei
         //to psasksimo na perimenei gia oso xrono prepei
         else if(follower_limits.getRemaining()<Cap)
         {  System.out.println("Telos twn limits");
            this.stop(follower_limits.getSecondsUntilReset());}
         else if(follower_id==null)
         {break;}
   
         
       }
                catch(TwitterException ex)
         {
             
             if(ex.getErrorCode()==88){System.out.println("Exception logw telos twn limits :");
             this.stop(follower_limits.getSecondsUntilReset());
             continue;
             
             }
         }
         catch(NullPointerException exe)
         {exe.printStackTrace();
            System.out.println("Cursor null pointer");
         
         }
         
   }while ((follower_id.getNextCursor()) != 0);
          }
       //an i lista exei followers
       //sunexise anadromika
       //gia tous followers stin lista
       
       if(followers_id.size()!=0)
       {
    
       for(int i=0;i<followers_id.size();i++)
       {
           try
           {
         
           //ftiaxnoume ena object gia kathe follower tou follower
           twitter4j.User follower;
           follower=t.showUser(followers_id.get(i));
           
           //gia tous followers tou follower
           //
          //kane otidipote
          //apothikeuse ton xristi
          file.saveFollower(user);
        //telos kalese tin idia sunartisi
           //wste anadromika na psaksei kai gia ekeinous
           //ta idia pragmata
           //i anadromi otan ston se kapoion apo tous xristes
           //den brethei to hastag se kanenan xristi
           //i den plirei to sunolo twn aparaititwn follower
           //i lista tha einai keni kai to thread tha diekopi
           this.Search(follower);
           }
           catch(TwitterException te)
           {te.printStackTrace();}
           
       }
    }
       //alliws diekopse to thread
       //pou diaxeirizetai to sugkekrimeno object
       else if(followers_id.size()==0)
       {Thread.currentThread().interrupt();}
         
       
        

        
         
    }
    
    //pame sto timeline tou user kai blepoume an uparxei to tweet me to 
    //hastag pou theloume, an exei ginei diladi retweet
    //edw uparxei omws periorismos
    //epistrefei ta 3,200 pio prosfata Tweets
    
    public boolean check_timeline(User u)
    {String key=this.hashtag.replace("#","");
       
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
                 {
                 e.printStackTrace();} 
       
       return answer;
    }
   

    

    //otan kapoio apo ta limits ftasei konta sto 1
    //to thread tha stamataei gia 15 lepta sun 10 deuterolepta gia safe
    public void stop(int time) throws InterruptedException
    {
        System.out.println(Thread.currentThread().getName()+" is sleeping");
        Thread.sleep(((time)*1000)+100);
        
        
    }

    @Override
    public void run() {
      
         
      try {
          this.Search(this.tweet.getUser());
      } catch (InterruptedException ex) {
          java.util.logging.Logger.getLogger(Tweet.class.getName()).log(Level.SEVERE, null, ex);
      }
     
         
             
}
         // java.util.logging.Logger.getLogger(Tweet.class.getName()).log(Level.SEVERE, null, ex);
      }
       
    

   
    
    
   

