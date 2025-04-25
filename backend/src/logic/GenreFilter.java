public class GenreFilter  implements Filter {
    private String myGenre;
    
    public GenreFilter(String genre) {
        myGenre = genre;
    }
    
    @Override
    public boolean satisfies(String id) {
        String genres = MovieDatabase.getGenres(id);
        return genres.toLowerCase().contains(myGenre.toLowerCase());
    }

}