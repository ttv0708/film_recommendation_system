/**
 * Write a description of SecondRatings here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import java.util.*;

public class ThirdRatings extends FirstRatings {
    private ArrayList<Rater> myRaters;
    
    public ThirdRatings() {
        // default constructor
        this("ratings.csv");
    }
    
    public ThirdRatings(String ratingsfile) {
        myRaters = loadRaters(ratingsfile);
    }
    
    public int getRaterSize(){
        return myRaters.size();
    }
    
    protected double getAverageByID(String id, int minimalRaters){
        double totalRating = 0;
        int numRaters = 0;
        for(Rater rater : myRaters){
            for(Rating rating : rater.getRatingList().values()){
                if(rating.getItem().equals(id)){
                    totalRating += rating.getValue();
                    numRaters++;
                    break;
                }
            }
        }
        
        if (numRaters < minimalRaters) return 0.0;
        return totalRating/numRaters;
    }
    
    public ArrayList<Rating> getAverageRatings(int minimalRaters){
        ArrayList<Rating> result = new ArrayList<Rating>();
        ArrayList<String> movies = MovieDatabase.filterBy(new TrueFilter());
        for(String movie : movies){
            double avg = getAverageByID(movie,minimalRaters);
            if(avg!=0.0){
                Rating r = new Rating(movie,avg);
                result.add(r);
            }
        }
        return result;
    }
    
    public ArrayList<Rating> getAverageRatingsByFilter(int minimalRaters, Filter filterCriteria){
        ArrayList<Rating> result = new ArrayList<Rating>();
        ArrayList<String> movies = MovieDatabase.filterBy(filterCriteria);
        for(String movie : movies){
            double avg = getAverageByID(movie,minimalRaters);
            if(avg!=0.0){
                Rating r = new Rating(movie,avg);
                result.add(r);
            }
        }
        return result;
    }
}