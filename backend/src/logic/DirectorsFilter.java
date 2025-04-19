import java.util.*;

public class DirectorsFilter  implements Filter {
    private ArrayList<String> myDirector;
    
    public DirectorsFilter(ArrayList<String> director) {
        myDirector = director;
    }
    
    @Override
    public boolean satisfies(String id) {
        for(String director : myDirector){
            if(MovieDatabase.getDirector(id).contains(director)) return true;
        }
        return false;
    }

}