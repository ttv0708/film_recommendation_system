import java.util.*;

public class SecondRatings extends FirstRatings {
    private ArrayList<Movie> myMovies;
    private ArrayList<Rater> myRaters;
    
    public SecondRatings() {
        // default constructor
        this("./data/ratedmoviesfull.csv", "./data/ratings.csv");
    }
    
    public SecondRatings(String moviefile,String ratingsfile) {
        myMovies = loadMovies(moviefile);
        myRaters = loadRaters(ratingsfile);
    }
    
    public int getMovieSize(){
        return myMovies.size();
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
        for(Movie movie : myMovies){
            double avg = getAverageByID(movie.getID(),minimalRaters);
            if(avg!=0.0){
                Rating r = new Rating(movie.getID(),avg);
                result.add(r);
            }
        }
        return result;
    }
    
    public String getTitle(String id){
        for(Movie movie : myMovies){
            if(movie.getID().equals(id)){
                return movie.getTitle();
            }
        }
        return "Movie with id = " + id + " is not found.";
    }
    
    public String getID(String title){
        for(Movie movie : myMovies){
            if(movie.getTitle().equals(title)){
                return movie.getID();
            }
        }
        return "NO SUCH TITLE.";
    }
}