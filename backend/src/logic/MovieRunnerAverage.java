
import java.util.ArrayList;
import java.util.Arrays;
import java.util.*;

public class MovieRunnerAverage {
    public void printRatingList(SecondRatings secondRating, int minimalRaters){
        ArrayList<Rating> ratingList = secondRating.getAverageRatings(minimalRaters);
        Collections.sort(ratingList);
        
        for(Rating rating : ratingList){
            System.out.println(rating.getValue() + " " + secondRating.getTitle(rating.getItem()));
        }
    }
    
    public void printAverageRatings(){
        SecondRatings secondRating = new SecondRatings("D:\\StepThreeJavaFiles\\data\\ratedmoviesfull.csv","D:\\StepThreeJavaFiles\\data\\ratings.csv");
        System.out.println("the number of movies: " + secondRating.getMovieSize());
        System.out.println("the number of raters: " + secondRating.getRaterSize());
    
        printRatingList(secondRating, 12);
        //getAverageRatingOneMovie();
        ArrayList<Rating> r = secondRating.getAverageRatings(50);
        System.out.println(r.size());
    }
    
    public void getAverageRatingOneMovie(){
        SecondRatings secondRating = new SecondRatings("D:\\StepThreeJavaFiles\\data\\ratedmoviesfull.csv","D:\\StepThreeJavaFiles\\data\\ratings.csv");
        String id = secondRating.getID("Moneyball");
        double avg = secondRating.getAverageByID(id,1);
        System.out.println("Moneyball average rating: " + avg);
        String id1 = secondRating.getID("Vacation");
        double avg1 = secondRating.getAverageByID(id1,1);
        System.out.println("Vacation average rating: " + avg1);
    }
}
