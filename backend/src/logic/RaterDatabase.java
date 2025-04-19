import java.util.*;
import org.apache.commons.csv.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class RaterDatabase {
    private static HashMap<String,Rater> ourRaters;
     
    private static void initialize() {
        // this method is only called from addRatings 
        if (ourRaters == null) {
            ourRaters = new HashMap<String,Rater>();
            addRatings("data/ratings.csv");
        }
    }
    
    public static void initialize(String filename) {
         if (ourRaters == null) {
             ourRaters= new HashMap<String,Rater>();
             addRatings("data/" + filename);
         }
     }    
    
    public static void addRatings(String filename) {
    initialize(); 
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = br.readLine()) != null) {
            // Assuming CSV is comma-separated, split the line into tokens
            StringTokenizer st = new StringTokenizer(line, ",");
            if (st.countTokens() >= 3) {
                String id = st.nextToken().trim(); // rater_id
                String item = st.nextToken().trim(); // movie_id
                String rating = st.nextToken().trim(); // rating
                addRaterRating(id, item, Double.parseDouble(rating));
            }
        }
    } catch (IOException e) {
        e.printStackTrace(); // Handle file reading errors
    }
}
    
    public static void addRaterRating(String raterID, String movieID, double rating) {
        initialize(); 
        Rater rater =  null;
                if (ourRaters.containsKey(raterID)) {
                    rater = ourRaters.get(raterID); 
                } 
                else { 
                    rater = new EfficientRater(raterID);
                    ourRaters.put(raterID,rater);
                 }
                 rater.addRating(movieID,rating);
    } 
             
    public static Rater getRater(String id) {
        initialize();
        
        return ourRaters.get(id);
    }
    
    public static ArrayList<Rater> getRaters() {
        initialize();
        ArrayList<Rater> list = new ArrayList<Rater>(ourRaters.values());
        
        return list;
    }
 
    public static int size() {
        return ourRaters.size();
    }
    
    
        
}