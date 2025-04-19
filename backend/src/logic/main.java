import java.util.ArrayList;

public class main {
    public static void main(String[] args) {
        RecommendationRunner runner = new RecommendationRunner();
        ArrayList<String> movies = runner.getItemsToRate();
        for (int i = 0;i<10;i++){
            System.out.println(MovieDatabase.getMovie(movies.get(i)));
        } // In ra 5 id phim đầu tiên
    }
}
