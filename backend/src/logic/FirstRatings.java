//import edu.duke.*;
import java.util.*;
import org.apache.commons.csv.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.File;

public class FirstRatings {
    public ArrayList<Movie> loadMovies(String filename){
        ArrayList<Movie> movies = new ArrayList<Movie>();
        
        File file = new File(filename);
        
        try (Reader reader = new FileReader(file);
        CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())){
            for (CSVRecord record : parser){
                //System.out.print(record);
                Movie m = new Movie(
                record.get(0),
                record.get(1),
                record.get(2),
                record.get(3),
                record.get(4),
                record.get(5),
                Integer.parseInt(record.get(6).trim()),
                record.get(7)
                );
                
                movies.add(m);
            }
            
        } catch (Exception e){
            e.printStackTrace();
        }
        
        return movies;
    }
    
    public void printMoviesList(ArrayList<Movie> movies){
        for(Movie m : movies){
            System.out.print(m+"\n");
        }
    }
    
    public int countMovieOfComedyGenre(ArrayList<Movie> movies){
        int cnt = 0;
        for(Movie m : movies){
            if(m.getGenres().contains("Comedy")) cnt++;
        }
        return cnt;
    }
    
    public int countMovieLargerThan150Minutes(ArrayList<Movie> movies){
        int cnt = 0;
        for(Movie m : movies){
            if(m.getMinutes() > 150) cnt++;
        }
        return cnt;
    }
    
    public List<Map.Entry<String, Integer>> maximumNumberOfMovies(ArrayList<Movie> movies){
        HashMap<String, Integer> map = new HashMap<>();
        
        for(Movie m : movies){
            String[] directors = m.getDirector().split(",");
            for(String director : directors){
                director = director.trim();
                
                if(!map.containsKey(director)) 
                    map.put(director,1);
                else map.put(director, map.get(director) + 1);
            }
        }
        
        int maxMovies = Collections.max(map.values());
        List<Map.Entry<String, Integer>> maxDirectors = new ArrayList<>();
        
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == maxMovies) {
                maxDirectors.add(entry);
            }
        }
        
        return maxDirectors;
    }
    
    public void testLoadMovies(){
        ArrayList<Movie> movies = loadMovies("D:\\StepOneStarterProgram\\data\\ratedmoviesfull.csv");
    
        System.out.print("The number of movies: " + movies.size()+"\n");
        //printMoviesList(movies);
        System.out.print("The number of movies of comedy genre: " + countMovieOfComedyGenre(movies));
        System.out.print("The number of movies that are larger than 150 minutes: " + countMovieLargerThan150Minutes(movies) + "\n");
        List<Map.Entry<String, Integer>> maxDirectors = maximumNumberOfMovies(movies);
        System.out.print("The maximum number of movies by any director: " + maxDirectors.get(0).getValue() + " with director:\n");
        for(Map.Entry<String, Integer> d: maxDirectors){
            System.out.print("- " + d.getKey() + "\n");
        }
    }
    
    public ArrayList<Rater> loadRaters(String filename){
        ArrayList<Rater> raters = new ArrayList<>();
        HashMap<String, Rater> raterMap = new HashMap<>();
    
        File file = new File(filename);
    
        try (Reader reader = new FileReader(file);
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
    
            for (CSVRecord record : parser){
                String raterId = record.get("rater_id").trim();
                String movieId = record.get("movie_id").trim();
                double rating = Double.parseDouble(record.get("rating").trim());
    
                Rater rater;
                if (raterMap.containsKey(raterId)) {
                    rater = raterMap.get(raterId);
                } else {
                    rater = new EfficientRater(raterId);
                    raterMap.put(raterId, rater);
                }
    
                rater.addRating(movieId, rating); 
            }
    
        } catch (Exception e){
            e.printStackTrace();
        }
    
        // Chuyển từ HashMap sang ArrayList
        raters.addAll(raterMap.values());
        return raters;
    }
    
    public void printRatersList(ArrayList<Rater> raters){
        for(Rater r : raters){
            System.out.print(r.getID()+" has rated: "+r.numRatings() + " movies\n");
            for (Rating rg : r.getRatingList().values()) {
                System.out.println("- " + rg.toString());
            }

        }
    }
    
    public Rater getRaterById(ArrayList<Rater> raters, String raterId) {
        for (Rater r : raters) {
            if (r.getID().equals(raterId)) {
                return r; 
            }
        }
        return null; 
    }
    
    public int numberOfRatingsForAParticularRater(ArrayList<Rater> raters, String raterID){
        Rater r = getRaterById(raters,raterID);
        if(r!=null)
            return r.numRatings();
        return -1; // không tìm thấy rater_id;
    }
    
    public List<Map.Entry<String, Integer>> maximumNumberOfRatings(ArrayList<Rater> raters){
        HashMap<String, Integer> map = new HashMap<>();
        
        for(Rater r : raters){
            String raterID = r.getID();
            int num = numberOfRatingsForAParticularRater(raters,raterID);
            if(!map.containsKey(raterID)) 
                map.put(raterID,num);
            else map.put(raterID, num);
        }
        
        int maxRatings = Collections.max(map.values());
        List<Map.Entry<String, Integer>> maxRaters = new ArrayList<>();
        
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (entry.getValue() == maxRatings) {
                maxRaters.add(entry);
            }
        }
        
        return maxRaters;
    }
    
    public int numberOfRatingsAParticularMovieHas(ArrayList<Rater> raters, String movieID){
        //int num = 0;
        
        //for(Rater r : raters){
        //    if(r.hasRating(movieID)) num++;
        //}
        //return num;
        int num = 0;
        for (Rater rater : raters){
            for (Rating rating : rater.getRatingList().values()){
                if(rating.getItem().equals(movieID)){
                    num+=1;
                }
            }
        }
        return num;
    }
    
    public int numOfDifferentMoviesHasBeenRated(ArrayList<Rater> raters){
        ArrayList<String> ratedMovies = new ArrayList<String>();
        
        for(Rater r : raters){
            ArrayList<String> ls = r.getItemsRated();
            for(String movie: ls){
                if(!ratedMovies.contains(movie)) ratedMovies.add(movie);
            }
        }
        return ratedMovies.size();
    }
    
    public void testLoadRaters(){
        ArrayList<Rater> raters = loadRaters("D:\\StepOneStarterProgram\\data\\ratings.csv");
        System.out.print("The total number of raters: " + raters.size() + "\n");
        printRatersList(raters);
        System.out.print("The rater whose rater_id is 2 for the file ratings_short.csv has: " + numberOfRatingsForAParticularRater(raters,"193")+"ratings\n");
        List<Map.Entry<String, Integer>> maxRatings = maximumNumberOfRatings(raters);
        System.out.print("The maximum number of ratings by any rater: " + maxRatings.get(0).getValue() + " with rater:\n");
        for(Map.Entry<String, Integer> d: maxRatings){
            System.out.print("- " + d.getKey() + "\n");
        }
        System.out.print("The number of ratings that movie with ID \"1798709\" has: " + numberOfRatingsAParticularMovieHas(raters,"1798709")+"\n");
        System.out.print("The number of different movies have been rated by all these raters: " + numOfDifferentMoviesHasBeenRated(raters)+"\n");
    }
}
