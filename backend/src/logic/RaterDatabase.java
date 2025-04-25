import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class RaterDatabase {
    private static HashMap<String,Rater> ourRaters;
     
    private static void initialize() {
        // this method is only called from addRatings 
        if (ourRaters == null) {
            ourRaters = new HashMap<String,Rater>();
            addRatings("./data/ratings.csv");
        }
    }
    
    public static void initialize(String filename) {
         if (ourRaters == null) {
             ourRaters= new HashMap<String,Rater>();
             addRatings("./data/" + filename);
         }
     }    
    
    public static void addRatings(String filename) {
    initialize(); 
    try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
        String line;
        boolean isFirstLine = true;
        while ((line = br.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;
                continue; // Bỏ qua dòng đầu tiên (header)
            }

            StringTokenizer st = new StringTokenizer(line, ",");
            if (st.countTokens() >= 3) {
                String id = st.nextToken().trim();     // rater_id
                String item = st.nextToken().trim();   // movie_id
                String rating = st.nextToken().trim(); // rating

                try {
                    addRaterRating(id, item, Double.parseDouble(rating));
                } catch (NumberFormatException e) {
                    System.err.println("Không thể chuyển '" + rating + "' thành số. Dòng: " + line);
                }
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
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

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Thiếu webRaterID");
            return;
        }
        String webRaterID = args[0];
        Rater r = RaterDatabase.getRater(webRaterID);
       
        if (r == null) {
            System.out.println("Không tìm thấy người dùng có ID: " + webRaterID);
        } else {
            for (Rating rating : r.getRatingList().values()){
                System.out.println(MovieDatabase.getMovie(rating.getItem()).toString() + ", rating= " + rating.getValue());
            }
        }
    }
}