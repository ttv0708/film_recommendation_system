const axios = require('axios');
require('dotenv').config();

const TMDB_API_KEY = process.env.TMDB_API_KEY;

const fetchMoviePoster = async (movieList) => {
    const moviesListWithPoster = [];

    for (const movie of movieList) {
        const url = `https://api.themoviedb.org/3/search/movie?api_key=${TMDB_API_KEY}&query=${encodeURIComponent(movie.title)}`;
        try {
            // Use axios to make the request
            const response = await axios.get(url);
            const data = response.data;

            const posterPath = data.results?.[0]?.poster_path;
            moviesListWithPoster.push({
                ...movie,
                poster: posterPath ? `https://image.tmdb.org/t/p/w200${posterPath}` : null
            });
        } catch (error) {
            console.error(`Error fetching poster for ${movie.title}:`, error.message);
            moviesListWithPoster.push({
                ...movie,
                poster: null
            });
        }
    }

    return moviesListWithPoster;
};

module.exports = fetchMoviePoster;
