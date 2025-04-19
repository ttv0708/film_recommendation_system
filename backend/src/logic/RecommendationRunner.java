import java.util.*;
import java.io.*;

public class RecommendationRunner implements Recommender {
    public ArrayList<String> getItemsToRate (){
        MovieDatabase.initialize("ratedmoviesfull.csv"); // Khởi tạo database
        ArrayList<String> allIDs = MovieDatabase.filterBy(new TrueFilter());

        Collections.shuffle(allIDs); // Trộn ngẫu nhiên

        int numberToPick = Math.min(10, allIDs.size());
        ArrayList<String> random10 = new ArrayList<String>();

        for (int i = 0; i < numberToPick; i++) {
            random10.add(allIDs.get(i));
        }

        //System.out.println("10 ID phim ngẫu nhiên:");
        //for (String id : random10) {
        //    System.out.println(MovieDatabase.getMovie(id));
        //}
        
        return random10;
    };

    public void saveRatedItems(String webRaterID){
        
    }
    
    public void printRecommendationsFor(String webRaterID) {
        FourthRatings fourthrating = new FourthRatings();
        ArrayList<Rating> rcm = fourthrating.getSimilarRatings(webRaterID, 1, 0);
    
        System.out.println("<html>");
        System.out.println("<head>");
        System.out.println("<style>");
        System.out.println("table { border-collapse: collapse; width: 80%; margin: auto; }");
        System.out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }");
        System.out.println("th { background-color: #f2f2f2; }");
        System.out.println("</style>");
        System.out.println("</head>");
        System.out.println("<body>");
        System.out.println("<h2 style='text-align:center;'>Goi y phim cho ban</h2>");
    
        if (rcm == null || rcm.isEmpty()) {
            System.out.println("<p style='text-align:center;'>Khong co phim nao duoc goi y cho ban.</p>");
        } else {
            System.out.println("<table>");
            System.out.println("<tr><th>Poster</th><th>Title</th><th>Year</th><th>Genres</th></tr>");
    
            for (int i = 0; i < Math.min(10, rcm.size()); i++) {
                Rating r = rcm.get(i);
                System.out.println("<tr>");
                System.out.println("<td><img src='" + MovieDatabase.getPoster(r.getItem()) + "' height='100'></td>");
                System.out.println("<td>" + MovieDatabase.getTitle(r.getItem()) + "</td>");
                System.out.println("<td>" + MovieDatabase.getYear(r.getItem()) + "</td>");
                System.out.println("<td>" + MovieDatabase.getGenres(r.getItem()) + "</td>");
                System.out.println("</tr>");
            }
    
            System.out.println("</table>");
        }
    
        System.out.println("</body>");
        System.out.println("</html>");
    };

}
