
package twitter.crawler;

import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;


public class TwitterCrawler {

  
    public static void main(String[] args) {
         ConfigurationBuilder cf=new ConfigurationBuilder();
    cf.setDebugEnabled(true).setOAuthConsumerKey("SgWka3I6DTkjo5O3frJPztcrN").setOAuthConsumerSecret("cd7ZAKbvwAYZMKdVrafLTEFft33eZ4EKXi245hu6yFgOIarqIf")
            .setOAuthAccessToken("1946495047-dWuXKxgm8lx3qYtxiNLU5KO9LKuuIgsfvFyqH3A")
            .setOAuthAccessTokenSecret("xRlJN7amSChUv318AigXL5E1r04yVdVkXDO8nNgvJspPe");
            TwitterFactory tf=new TwitterFactory(cf.build());
            twitter4j.Twitter twitter=tf.getInstance();
    
             //Tweet tweet=new Tweet("#NBA",twitter);
             Thread t1=new Thread(new Tweet("#NBA",twitter));
             t1.run();
    }
    
}
