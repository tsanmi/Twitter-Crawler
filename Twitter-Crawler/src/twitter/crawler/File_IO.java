
package twitter.crawler;

import java.io.File;
import java.util.List;
import twitter4j.*;
import java.io.FileWriter;

import java.io.IOException;
import java.util.Map;



public class File_IO {
    //ftiaxnoume kapoies final string times gia na dimiourgisoume ta arxeia
    //epilegoume san stoixeio xwerismou to komma kai san stoixeio neas gramis to \n
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String TWEET_HEADER = "id,created_at,text,user";
    private static final String User_Header = "id,name,screen_name,location,url,description,verified,followers_count,created_at,profile_image_url";
    private File dir;
    private File followers_dir;
    FileWriter fileWriter = null;
    
    public void make_dir(String s)
    {
        //dimiourgoume gia kathe xristi enan fakelo me to onoma tou 
       
        File theDir = new File("C:\\Users\\Dimitris_Admin\\Documents\\GitHub\\Twitter-Crawler\\Twitter Crawler\\Users\\"+s);
        

// if the directory does not exist, create it
if (!theDir.exists()) {
    //System.out.println("creating directory: " + theDir.getName());
    boolean result = false;

    try{
        theDir.mkdir();
        this.dir=theDir;
        
        result = true;
        
    }catch(Exception e)
    {e.printStackTrace();}
}
}
    

    
    


    
    public void saveTweet(Status s)
    {
             File f=new File(dir.getAbsoluteFile()+"\\"+s.getUser().getName()+"'s"+"tweet "+s.getId());
       if(!f.exists())
       {
                       try{
                   
        
               fileWriter = new FileWriter(f);
               
               fileWriter.append(TWEET_HEADER.toString());
                fileWriter.append(NEW_LINE_SEPARATOR);

               
               fileWriter.append(String.valueOf(s.getId()));
               fileWriter.append(COMMA_DELIMITER);
               fileWriter.append(String.valueOf(s.getCreatedAt()));
               fileWriter.append(COMMA_DELIMITER);
               fileWriter.append(s.getText());
               fileWriter.append(COMMA_DELIMITER);
               fileWriter.append(s.getUser().getName());
               
               fileWriter.flush();
               fileWriter.close();
               
               

        
        }
        
        
        catch(Exception e)
            
        {e.printStackTrace();}
       }
    
    }
    public void saveUser(User u)
    {
       File f=new File(dir.getAbsoluteFile()+"\\"+u.getName()+"'s info");
       if(!f.exists())
       {
                       try{
                   
        
               fileWriter = new FileWriter(f);
               
               fileWriter.append(User_Header.toString());
                fileWriter.append(NEW_LINE_SEPARATOR);

               
               fileWriter.append(String.valueOf(u.getId()));
               fileWriter.append(COMMA_DELIMITER);
               fileWriter.append(u.getName());
               fileWriter.append(COMMA_DELIMITER);
               fileWriter.append(u.getScreenName());
               fileWriter.append(COMMA_DELIMITER);
               
              
               
               

        
        }
        
        
        catch(Exception e)
            
        {System.out.println("Eror kata tin apothikeusi tou user");
         e.printStackTrace();}
                       
                       
                        finally
                               {try{
                                   fileWriter.flush();
                                   fileWriter.close(); }
                               catch(Exception e){
                                   System.out.println("Eror kata to kleisimo arxeio ston user");
                                    e.printStackTrace();}
                               }
       }
       
      
    }   
   
   
}


        
       
        
        

