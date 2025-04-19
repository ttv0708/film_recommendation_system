
/**
 * Write a description of MovieRunnerWithFilters here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.util.*;

public class MovieRunnerWithFilters {
    public void printRatingList(ArrayList<Rating> ratingList){
        Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        for(Rating rating : ratingList){
            System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem()));
        }
    }
    
    public void printAverageRatings(){
        ThirdRatings thirdRating = new ThirdRatings("D:\\StepThreeJavaFiles\\data\\ratings.csv");
      
        System.out.println("the number of raters: " + thirdRating.getRaterSize());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        
        ArrayList<Rating> r = thirdRating.getAverageRatings(35);
        printRatingList(r);
    }
    
    public void printRatingListByYear(ArrayList<Rating> ratingList){
        Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        for(Rating rating : ratingList){
            System.out.println(rating.getValue() + " " + MovieDatabase.getYear(rating.getItem()) + " " + MovieDatabase.getTitle(rating.getItem()));
        }
    }
    
    public void printAverageRatingsByYear(){
        ThirdRatings thirdRating = new ThirdRatings("D:\\StepThreeJavaFiles\\data\\ratings.csv");
      
        System.out.println("the number of raters: " + thirdRating.getRaterSize());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        YearAfterFilter yFilter = new YearAfterFilter(2000);
        ArrayList<Rating> r = thirdRating.getAverageRatingsByFilter(20, yFilter);
        printRatingListByYear(r);
    }
    
    public void printRatingListByGenre(ArrayList<Rating> ratingList){
        Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        for(Rating rating : ratingList){
            System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("\t" + MovieDatabase.getGenres(rating.getItem()));
        }
    }
    
    public void printAverageRatingsByGenre(){
        ThirdRatings thirdRating = new ThirdRatings("D:\\StepThreeJavaFiles\\data\\ratings.csv");
      
        System.out.println("the number of raters: " + thirdRating.getRaterSize());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        GenreFilter gFilter = new GenreFilter("Comedy");
        ArrayList<Rating> r = thirdRating.getAverageRatingsByFilter(20, gFilter);
        printRatingListByGenre(r);
    }
    
    public void printRatingListByMinutes(ArrayList<Rating> ratingList){
        Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        //for(Rating rating : ratingList){
        //    System.out.println(rating.getValue() + " " + " Time: " + MovieDatabase.getMinutes(rating.getItem()) + " " + MovieDatabase.getTitle(rating.getItem()));
        //}
    }
    
    public void printAverageRatingsByMinutes(){
        ThirdRatings thirdRating = new ThirdRatings("D:\\StepThreeJavaFiles\\data\\ratings.csv");
      
        System.out.println("the number of raters: " + thirdRating.getRaterSize());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        MinutesFilter mFilter = new MinutesFilter(105,135);
        ArrayList<Rating> r = thirdRating.getAverageRatingsByFilter(5, mFilter);
        printRatingListByMinutes(r);
    }
    
    public void printRatingListByDirector(ArrayList<Rating> ratingList){
        Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        for(Rating rating : ratingList){
            System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("\t" + MovieDatabase.getDirector(rating.getItem()));
        }
    }
    
    public void printAverageRatingsByDirector(){
        ThirdRatings thirdRating = new ThirdRatings("D:\\StepThreeJavaFiles\\data\\ratings.csv");
      
        System.out.println("the number of raters: " + thirdRating.getRaterSize());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        ArrayList<String> dr = new ArrayList<>(Arrays.asList("Clint Eastwood","Joel Coen","Martin Scorsese","Roman Polanski","Nora Ephron","Ridley Scott","Sydney Pollack"));

        DirectorsFilter dFilter = new DirectorsFilter(dr);
        ArrayList<Rating> r = thirdRating.getAverageRatingsByFilter(4, dFilter);
        printRatingListByDirector(r);
    }
    
    public void printRatingListByYearAfterAndGenre(ArrayList<Rating> ratingList){
        Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        //for(Rating rating : ratingList){
        //    System.out.println(rating.getValue() + " " +  MovieDatabase.getYear(rating.getItem()) + " " + MovieDatabase.getTitle(rating.getItem()));
        //    System.out.println("\t" + MovieDatabase.getGenres(rating.getItem()));
        //}
    }
    
    public void printAverageRatingsByYearAfterAndGenre(){
        ThirdRatings thirdRating = new ThirdRatings("D:\\StepThreeJavaFiles\\data\\ratings.csv");
      
        System.out.println("the number of raters: " + thirdRating.getRaterSize());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        
        AllFilters aFilter = new AllFilters();
        GenreFilter gFilter = new GenreFilter("Drama");
        YearAfterFilter yFilter = new YearAfterFilter(1990);
        aFilter.addFilter(gFilter);
        aFilter.addFilter(yFilter);
        
        ArrayList<Rating> r = thirdRating.getAverageRatingsByFilter(8, aFilter);
        printRatingListByYearAfterAndGenre(r);
    }
    
    public void printRatingListByDirectorsAndMinutes(ArrayList<Rating> ratingList){
        Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        //for(Rating rating : ratingList){
        //   System.out.println(rating.getValue() + " " + " Times: " + MovieDatabase.getMinutes(rating.getItem()) + " " + MovieDatabase.getTitle(rating.getItem()));
        //    System.out.println("\t" + MovieDatabase.getDirector(rating.getItem()));
        //}
    }
    
    public void printAverageRatingsByDirectorsAndMinutes(){
        ThirdRatings thirdRating = new ThirdRatings("D:\\StepThreeJavaFiles\\data\\ratings.csv");
      
        System.out.println("the number of raters: " + thirdRating.getRaterSize());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        
        AllFilters aFilter = new AllFilters();
        ArrayList<String> dr = new ArrayList<>(Arrays.asList( "Clint Eastwood","Joel Coen","Tim Burton","Ron Howard","Nora Ephron","Sydney Pollack"));

        DirectorsFilter dFilter = new DirectorsFilter(dr);
        MinutesFilter mFilter = new MinutesFilter(90,180);
        aFilter.addFilter(dFilter);
        aFilter.addFilter(mFilter);
        
        ArrayList<Rating> r = thirdRating.getAverageRatingsByFilter(3, aFilter);
        printRatingListByDirectorsAndMinutes(r);
    }
}
