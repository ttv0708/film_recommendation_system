import java.util.*;

public class MovieRunnerSimilarRatings {
    public void printRatingList(ArrayList<Rating> ratingList){
        Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        //for(Rating rating : ratingList){
        //    System.out.println(rating.getValue() + " " + MovieDatabase.getTitle(rating.getItem()));
        //}
    }
    
    public void printAverageRatings(){
        FourthRatings fourthRating = new FourthRatings();
        
        RaterDatabase.initialize("ratings.csv");
        System.out.println("the number of raters: " + RaterDatabase.size());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        
        ArrayList<Rating> r = fourthRating.getAverageRatings(35);
        printRatingList(r);
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
        FourthRatings fourthRating = new FourthRatings();
        
        RaterDatabase.initialize("ratings.csv");
        System.out.println("the number of raters: " + RaterDatabase.size());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        
        AllFilters aFilter = new AllFilters();
        GenreFilter gFilter = new GenreFilter("Drama");
        YearAfterFilter yFilter = new YearAfterFilter(1990);
        aFilter.addFilter(gFilter);
        aFilter.addFilter(yFilter);
        
        ArrayList<Rating> r = fourthRating.getAverageRatingsByFilter(8, aFilter);
        printRatingListByYearAfterAndGenre(r);
    }
    
    public void printSimilarRatingsList(ArrayList<Rating> ratingList){
        //Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        Rating top = ratingList.get(0);
        System.out.println("Top movie:");
        System.out.println(top.getValue() + " " + top.getItem());
        System.out.println("\t" + MovieDatabase.getTitle(top.getItem()));

        //for(Rating rating : ratingList){
        //    System.out.println(rating.getValue() + " " + rating.getItem());
        //    System.out.println("\t" + MovieDatabase.getTitle(rating.getItem()));
        //}
    }
    
    public void printSimilarRatings(){
        FourthRatings fourthRating = new FourthRatings();
        //ArrayList<Rating> list =  fourthRating.getSimilarRatings("1",4,4);
        RaterDatabase.initialize("ratings.csv");
        System.out.println("the number of raters: " + RaterDatabase.size());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        
        ArrayList<Rating> list = fourthRating.getSimilarRatings("71",20,5);
        printSimilarRatingsList(list);
    }
    
    public void printSimilarRatingsListByGenre(ArrayList<Rating> ratingList){
        //Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        for(Rating rating : ratingList){
            System.out.println(rating.getValue() + " " + rating.getItem());
            System.out.println("\t" + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("\t" + MovieDatabase.getGenres(rating.getItem()));
        }
    }
    
    public void printSimilarRatingsListByDirector(ArrayList<Rating> ratingList){
        //Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        for(Rating rating : ratingList){
            System.out.println(rating.getValue() + " " + rating.getItem());
            System.out.println("\t" + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("\t" + MovieDatabase.getDirector(rating.getItem()));
        }
    }
    
    public void printSimilarRatingsByDirector(){
        FourthRatings fourthRating = new FourthRatings();
        //ArrayList<Rating> list =  fourthRating.getSimilarRatings("1",4,4);
        RaterDatabase.initialize("ratings.csv");
        System.out.println("the number of raters: " + RaterDatabase.size());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        
        ArrayList<String> dr = new ArrayList<>(Arrays.asList(
            "Clint Eastwood",
            "J.J. Abrams",
            "Alfred Hitchcock",
            "Sydney Pollack",
            "David Cronenberg",
            "Oliver Stone",
            "Mike Leigh"
        ));
        ;
        DirectorsFilter dFilter = new DirectorsFilter(dr);
        ArrayList<Rating> list = fourthRating.getSimilarRatingsByFilter("120",10,2,dFilter);
        printSimilarRatingsList(list);
    }
    
    public void printSimilarRatingsListByGenreAndMinutes(ArrayList<Rating> ratingList){
        //Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        for(Rating rating : ratingList){
            System.out.println(rating.getValue() + " " + rating.getItem());
            System.out.println("\t" + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("\t" + MovieDatabase.getGenres(rating.getItem()));
            System.out.println("\t" + MovieDatabase.getMinutes(rating.getItem()));
        }
    }
    
    public void printSimilarRatingsByGenreAndMinutes(){
        FourthRatings fourthRating = new FourthRatings();
        //ArrayList<Rating> list =  fourthRating.getSimilarRatings("1",4,4);
        RaterDatabase.initialize("ratings.csv");
        System.out.println("the number of raters: " + RaterDatabase.size());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        
        AllFilters aFilter = new AllFilters();
        MinutesFilter mFilter = new MinutesFilter(80,160);
        aFilter.addFilter(mFilter);
        GenreFilter gFilter = new GenreFilter("Drama");
        aFilter.addFilter(gFilter);
        
        ArrayList<Rating> list = fourthRating.getSimilarRatingsByFilter("168",10,3,aFilter);
        printSimilarRatingsList(list);
    }
    
    public void printSimilarRatingsListByYearAfterAndMinutes(ArrayList<Rating> ratingList){
        //Collections.sort(ratingList);
        System.out.println("found " + ratingList.size() + "movies");
        for(Rating rating : ratingList){
            System.out.println(rating.getValue() + " " + rating.getItem());
            System.out.println("\t" + MovieDatabase.getTitle(rating.getItem()));
            System.out.println("\t" + MovieDatabase.getYear(rating.getItem()));
            System.out.println("\t" + MovieDatabase.getMinutes(rating.getItem()));
        }
    }
    
    public void printSimilarRatingsByYearAfterAndMinutes(){
        FourthRatings fourthRating = new FourthRatings();
        //ArrayList<Rating> list =  fourthRating.getSimilarRatings("1",4,4);
        RaterDatabase.initialize("ratings.csv");
        System.out.println("the number of raters: " + RaterDatabase.size());
        MovieDatabase.initialize("ratedmoviesfull.csv");
        System.out.println("The number of movies: " + MovieDatabase.size());
        
        AllFilters aFilter = new AllFilters();
        MinutesFilter mFilter = new MinutesFilter(70,200);
        aFilter.addFilter(mFilter);
        YearAfterFilter yFilter = new YearAfterFilter(1975);
        aFilter.addFilter(yFilter);
        
        ArrayList<Rating> list = fourthRating.getSimilarRatingsByFilter("314",10,5,aFilter);
        printSimilarRatingsList(list);
    }

    public void printSimilarRatingsByGenre(String userID, String genre){
        FourthRatings fourthRating = new FourthRatings();
        RaterDatabase.initialize("ratings.csv");
        MovieDatabase.initialize("ratedmoviesfull.csv");
        
        GenreFilter gFilter = new GenreFilter(genre);
        ArrayList<Rating> list = fourthRating.getSimilarRatingsByFilter(userID,100,1,gFilter);

        //System.out.println("Found " + list.size() + " movies");

        int limit = Math.min(10, list.size());

        for (int i = 0; i < limit; i++) {
            String movieID = list.get(i).getItem();
            //System.out.println("movieID: " + movieID);
            System.out.println(MovieDatabase.getMovie(movieID));
        }
    }
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Thiếu user_id hoặc movie genre");
            return;
        }
        String userId = args[0];
        String movieGenre = args[1];
        MovieRunnerSimilarRatings runner = new MovieRunnerSimilarRatings();

        runner.printSimilarRatingsByGenre(userId, movieGenre);
    }
}
