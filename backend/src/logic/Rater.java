import java.util.*;

public interface Rater {
    public void addRating(String item, double rating);
    public boolean hasRating(String item);
    public double getRating(String item);
    public ArrayList<String> getItemsRated();
    public String getID();
    public HashMap<String,Rating> getRatingList();
    public int numRatings();
}
