import java.util.*;

public class FourthRatings {
    //As we can see, every instance variable has been eliminated as requested
    public FourthRatings() {
        this("ratings.csv");
    }
    
    public FourthRatings(String ratingsfile){
        RaterDatabase.addRatings("data/" + ratingsfile);
    }
    
    public Rater getRater(String rater_id){
        return RaterDatabase.getRater(rater_id);
    }
    
    public int getRaterSize(){
        return RaterDatabase.size();
    }
    //Modified as requested in this assignment by using RaterDatabase class:
    public double getAverageByID(String id, int minimalRaters){
        ArrayList<Rater> Raters = RaterDatabase.getRaters();
        double count = 0;
        double numRatings = 0;
        double average = 0;
        if(minimalRaters == 0){
            return 0.0;
        }
        for (Rater r : Raters){
            HashMap<String,Rating> Ratings = r.getRatingList();
            for(Rating rat : Ratings.values()){
                if(rat.getItem().equals(id)){
                    double value = rat.getValue();
                    numRatings++;
                    count = count + value;
                }
            }
        }
        if(numRatings< minimalRaters){
            return -1;
        }
        else{
            average = count/numRatings;
            return average;
        }
    }
    
    public ArrayList<Rating> getAverageRatings(int minimalRaters){
        ArrayList<Rating> averageRatings = new ArrayList<Rating>();
        ArrayList<String> movies = MovieDatabase.filterBy(new TrueFilter());
        for(String m : movies){
            getAverageByID(m,minimalRaters);
            Rating a = new Rating(m,getAverageByID(m,minimalRaters));
            if(a.getValue() > -1){
                averageRatings.add(a);
            }
        }
        return averageRatings;
    }
    
    public ArrayList<Rating> getAverageRatingsByFilter(int minimalRaters, Filter filterCriteria){
        ArrayList<Rating> averageAndFilter = new ArrayList<Rating>();
        ArrayList<String> movies = MovieDatabase.filterBy(filterCriteria);
        for(String m : movies){
            getAverageByID(m,minimalRaters);
            Rating a = new Rating(m,getAverageByID(m,minimalRaters));
            if(a.getValue() > -1){
                averageAndFilter.add(a);
            }
        }
        return averageAndFilter;
    }
    
    /*The following method works as required even though I don't think it is the most efficient code
     * we could build. I had to create a new ArrayList to be able to iterate over an "i" loop twice,
     * which it might make it quite clumsy. It works though, and perfectly. I also commented out every
       print statement I use for testing to make debugging easier if necessary.*/
    private double dotProduct(Rater me, Rater r){
        /*This method returns a double value with the affinity between two Raters. The higher the number,
        the higher the affinity as well*/
        //HashMap<String,Rating> myRatings = me.getRatingList();
        //HashMap<String,Rating> hisRatings = r.getRatingList();
        
        HashMap<String, Rating> myRatings = new HashMap<>();
        for (String item : me.getItemsRated()) {
            double value = me.getRating(item);
            //System.out.println("rating: " + value);
            myRatings.put(item, new Rating(item, value));
        }
        // System.out.println("Danh sách rating của me :" + me.getID());
        // for (Map.Entry<String, Rating> entry : myRatings.entrySet()) {
        //     String movieID = entry.getKey();
        //     Rating rating = entry.getValue();
        //     System.out.println("Movie ID: " + movieID + " | Rating: " + rating.getValue());
        // }

        
        HashMap<String, Rating> hisRatings = new HashMap<>();
        for (String item : r.getItemsRated()) {
            double value = r.getRating(item);
            hisRatings.put(item, new Rating(item, value));
        }
        // System.out.println("Danh sách rating của r :" + r.getID());
        // for(Map.Entry<String, Rating> entry : hisRatings.entrySet()){
        //     String movieID = entry.getKey();
        //     Rating rating = entry.getValue();
        //     System.out.println("Movie ID: " + movieID + " | Rating: " + rating.getValue());
        // }

        double dotProduct = 0;
        //System.out.println("MyRatings has " +myRatings.size()+ " movies");
        //System.out.println("hisRatings has " + hisRatings.size() + " movies");
        for(int i=0; i<myRatings.size();i++){
            ArrayList<Rating> movieA = new ArrayList<Rating>();
            for(Rating a : myRatings.values()){
                 movieA.add(a);
            }
            for(Rating b : hisRatings.values()){
                if (movieA.toString().contains(b.getItem())){
                    for(i=0;i<movieA.size();i++){
                        if(movieA.get(i).getItem().equals(b.getItem())){
                            double finalValue =0;
                            //System.out.println("Equal movie found: " + b.getItem() + " " + b.getValue() + " and " + movieA.get(i).getItem()+ " " + movieA.get(i).getValue());
                            finalValue = (b.getValue()-5)* (movieA.get(i).getValue()-5);
                            //System.out.println("finalValue = " + finalValue);
                            dotProduct = dotProduct+ finalValue;
                            //System.out.println("dotProduct = " + dotProduct);
                        }
                    }
                }
            }
        }
        return dotProduct;
    }
    
    private ArrayList<Rating> getSimilarities(String id){
        ArrayList<Rating> similarRatings = new ArrayList<Rating>();
        Rater me = RaterDatabase.getRater(id);
        if(me == null){
            System.out.println("Rater not found");
            return null;
        }
        
        for(Rater r : RaterDatabase.getRaters()){
            if(!r.getID().equals(id)){
                //System.out.println("Dot product between " + me.getID() + " and " + r.getID() + " is " + dotProduct(me,r));
                if(dotProduct(me,r)>0){
                    similarRatings.add(new Rating(r.getID(),dotProduct(me,r)));
                }
            }
        }
        Collections.sort(similarRatings,Collections.reverseOrder());
        // System.out.println("Top 5 similar raters:");
        // for (int i = 0; i < Math.min(5, similarRatings.size()); i++) {
        //     Rating r = similarRatings.get(i);
        //     System.out.println((i + 1) + ". Rater ID: " + r.getItem() + " | Similarity: " + r.getValue());
        // }
        return similarRatings;
    }

    // private ArrayList<Rating> getSimilarities(String id){
    //     ArrayList<Rating> similarRatings = new ArrayList<>();
    
    //     // Thêm 5 rating giả lập để test
    //     similarRatings.add(new Rating("68646", 8.5));
    //     similarRatings.add(new Rating("1091191", 7.2));
    //     similarRatings.add(new Rating("1454468", 6.9));
    //     similarRatings.add(new Rating("2883512", 9.1));
    //     similarRatings.add(new Rating("985699", 5.5));
    
    //     System.out.println("Top 5 similar raters (fake for test):");
    //     for (int i = 0; i < similarRatings.size(); i++) {
    //         Rating r = similarRatings.get(i);
    //         System.out.println((i + 1) + ". Rater ID: " + r.getItem() + " | Similarity: " + r.getValue());
    //     }
    
    //     return similarRatings;
    // }
    
    
    public ArrayList<Rating> getSimilarRatings(String id, int numSimilarRaters, int minimalRaters){
        try{
            // danh sách độ tương đồng giữa rater_id và các rater khác sắp xếp giảm dần
            ArrayList<Rating> similarRatings = getSimilarities(id);
            if (similarRatings == null || similarRatings.isEmpty()) {
                System.out.println("similarRatings is empty.");
                return new ArrayList<>();
            }
            
            //chỉ lấy rating của top numSimilarRaters
            int limit = Math.min(numSimilarRaters, similarRatings.size());

            ArrayList<Rating> getRatings = new ArrayList<Rating>();
            ArrayList<String> movies = MovieDatabase.filterBy(new TrueFilter());
            HashMap<String, ArrayList<Double>> favRaters = new HashMap<String, ArrayList<Double>>();
            
            for(String movieID : movies){
                for(int i=0; i<limit; i++){
                    Rating r = similarRatings.get(i);
                    //This gets how big is the similarity:
                    double coef = r.getValue();
                    //This gets each similar rater id:
                    String rater_id = r.getItem();
                    ArrayList<Rater> Raters = RaterDatabase.getRaters();
                    
                    for(Rater rat : Raters){
                        if(rater_id.equals(rat.getID())){
                            //HashMap<String,Rating> Ratings = rat.getRatingList();
                            HashMap<String, Rating> Ratings = new HashMap<>();
                            for (String item : rat.getItemsRated()) {
                                double value = rat.getRating(item);
                                Ratings.put(item, new Rating(item, value));
                            }

                            for(Rating rats : Ratings.values()){
                                //If the movie was voted by my "soulmates":
                                if(rats.getItem().equals(movieID)){
                                    //if my hash does not have the movie, add movie and similarity value:
                                    ArrayList<Double> coefs = new ArrayList<Double>();
                                    if(!favRaters.containsKey(rats.getItem())){
                                        coefs.add(coef*rats.getValue());
                                        favRaters.put(rats.getItem(),coefs);
                                    }
                                    //If it is already in hashmap, add the similarity rate to the value hashmap:
                                    else{
                                        ArrayList<Double> mine = favRaters.get(rats.getItem());
                                        mine.add(coef*rats.getValue());
                                        favRaters.put(rats.getItem(),mine);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for ( String s : favRaters.keySet()){
                if( favRaters.get(s).size() >=minimalRaters){
                    double total =0;
                    for(double num : favRaters.get(s)){
                        total = total+ num;
                    }
                    //Final calculation for the similarity rate, according to minimalRaters parameter:
                    double finalValue = total/favRaters.get(s).size();
                    getRatings.add(new Rating(s,finalValue));
                }
            }
            //We sort them to have a rational TOP:
            Collections.sort(getRatings,Collections.reverseOrder());
            //System.out.println("getRatings:" + getRatings);
            return getRatings;
       }
       catch (Exception e){
           System.out.println("error: One of the variables is out of bounds, insert smaller parameter variables or another user");
           return null;
       }
   }
   
   public ArrayList<Rating> getSimilarRatingsByFilter(String id, int numSimilarRaters, int minimalRaters, Filter filterCriteria){
        try{
        ArrayList<Rating> similarRatings = getSimilarities(id);
        if (similarRatings == null || similarRatings.isEmpty()) {
            System.out.println("similarRatings is empty.");
            return new ArrayList<>();
        }

        ArrayList<Rating> getRatings = new ArrayList<Rating>();
        //Same method as before, but as easy as running a filterCriteria.
        ArrayList<String> movies = MovieDatabase.filterBy(filterCriteria);
        //System.out.println("filter size: " + movies.size());
        //return similarRatings;
        int limit = Math.min(numSimilarRaters, similarRatings.size());
        HashMap<String,ArrayList<Double>>favRaters = new HashMap<String,ArrayList<Double>>();
        for(String movieID : movies){
            for(int i=0; i<limit; i++){
                Rating r = similarRatings.get(i);
                
                double coef = r.getValue();
                
                String rater_id = r.getItem();
                ArrayList<Rater> Raters = RaterDatabase.getRaters();
                
                for(Rater rat : Raters){
                   
                    HashMap<String, Rating> Ratings = new HashMap<>();
                    for (String item : rat.getItemsRated()) {
                        double value = rat.getRating(item);
                        Ratings.put(item, new Rating(item, value));
                    }
                    if(rater_id.equals(rat.getID())){
                        for(Rating rats : Ratings.values()){
                            if(rats.getItem().equals(movieID)){
                                ArrayList<Double> coefs = new ArrayList<Double>();
                                if(!favRaters.containsKey(rats.getItem())){
                                    coefs.add(coef*rats.getValue());
                                    favRaters.put(rats.getItem(),coefs);
                                }
                                else{
                                    ArrayList<Double> mine = favRaters.get(rats.getItem());
                                    mine.add(coef*rats.getValue());
                                    favRaters.put(rats.getItem(),mine);
                                }
                            }
                        }
                    }
                }
            }
        }
        for ( String s : favRaters.keySet()){
            if( favRaters.get(s).size() >=minimalRaters){
                double total =0;
                for(double num : favRaters.get(s)){
                    total = total+ num;
                }
                double finalValue = total/favRaters.get(s).size();
                getRatings.add(new Rating(s,finalValue));
            }
        }
        Collections.sort(getRatings,Collections.reverseOrder());
        return getRatings;
       }
       catch (Exception e){
           System.out.println("One of the variables is out of bounds, insert smaller parameter variables or another user");
           return null;
       }
    }
}