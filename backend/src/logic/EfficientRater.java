
/**
 * Write a description of class Rater here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import java.util.*;

public class EfficientRater implements Rater {
    private String myID;
    private HashMap<String,Rating> myRatings;

    public EfficientRater(String id) {
        myID = id;
        myRatings = new HashMap<String,Rating>();
    }

    public void addRating(String item, double rating) {
        myRatings.put(item, new Rating(item,rating));
    }

    public boolean hasRating(String item) {
        if(myRatings.containsKey(item)) return true;
        
        return false;
    }

    public String getID() {
        return myID;
    }
    
    public HashMap<String,Rating> getRatingList(){
        return myRatings;
    }
    
    public double getRating(String item) {
        for(Rating rating : myRatings.values()){
            //System.out.println(rating.getItem());
            if (rating != null && Integer.parseInt(rating.getItem())==Integer.parseInt(item)) {
                return rating.getValue();
            }
        }
        
        return -1;
    }

    public int numRatings() {
        return myRatings.size();
    }

    public ArrayList<String> getItemsRated() {
        ArrayList<String> list = new ArrayList<String>();
        for(int k=0; k < myRatings.size(); k++){
            list.add(myRatings.get(k).getItem());
        }
        
        return list;
    }
    
    
}
