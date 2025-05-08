const express = require('express');
const cors = require('cors');
const { exec } = require('child_process');
const fetchMoviePoster = require('./src/fetchMoviePoster');
const app = express();
const port = 3000;
const csv = require('csv-parser');
const fs = require('fs');
const path = require('path');
const { stderr } = require('process');

app.use(cors());
app.use(express.json()); // Thêm nếu chưa có

function convertMoviesToJSON(moviesData) {
  const moviesJSON = [];

  moviesData.forEach(movieStr => {
    // Loại bỏ "Movie [" ở đầu và "]" hoặc "]\r" ở cuối
    const content = movieStr.replace(/^Movie \[/, '').replace(/\]\r?$/, '');

    const keyValuePairs = [];
    let current = '';
    let inValue = false;

    for (let i = 0; i < content.length; i++) {
      const char = content[i];
      if (char === ',' && !inValue) {
        keyValuePairs.push(current.trim());
        current = '';
      } else {
        if (char === '=' && !inValue) {
          inValue = true;
        } else if (char === ',' && content[i + 1] === ' ') {
          // kiểm tra có phải kết thúc một key-value không
          const nextEqual = content.indexOf('=', i + 1);
          const nextComma = content.indexOf(', ', i + 1);
          if (nextEqual !== -1 && (nextComma === -1 || nextEqual < nextComma)) {
            keyValuePairs.push(current.trim());
            current = '';
            i++; // bỏ qua khoảng trắng
            inValue = false;
            continue;
          }
        }
        current += char;
      }
    }
    if (current) keyValuePairs.push(current.trim());

    const movieObj = {};
    keyValuePairs.forEach(pair => {
      const [key, ...valueParts] = pair.split('=');
      const keyTrimmed = key.trim();
      const value = valueParts.join('=').trim(); // phòng trường hợp có dấu '=' trong value
      processField(movieObj, keyTrimmed, value);
    });

    moviesJSON.push(movieObj);
  });

  return moviesJSON;
}

  // Hàm xử lý các trường dữ liệu đặc biệt
  function processField(movieObj, key, value) {
    switch (key) {
      case 'id':
        movieObj[key] = value;
        break;
      case 'title':
        movieObj[key] = value;
        break;
      case 'year':
        movieObj[key] = parseInt(value);
        break;
      case 'genres':
        movieObj[key] = value.split(', ').map(genre => genre.trim()).filter(Boolean);
        break;
      case 'country':
        movieObj[key] = value.split(', ').map(country => country.trim()).filter(Boolean);
        break;
      case 'directors':
        movieObj[key] = value.split(', ').map(director => director.trim()).filter(Boolean);
        break;
      case 'minutes':
        movieObj[key] = parseInt(value);
        break;
      case 'poster':
        movieObj[key] = value;
        break;
      default:
        movieObj[key] = value;
    }
  }
  
app.get('/api/recommendations', async (req, res) => {
    exec('java -cp "./src/logic/recommend.jar;./lib/commons-csv-1.10.0.jar" main', async (error, stdout, stderr) => {
        if (error) {
            console.error(`Lỗi thực thi Java: ${error.message}`);
            return res.status(500).send('Lỗi khi chạy chương trình Java');
        }
        if (stderr) {
            console.error(`Lỗi Java stderr: ${stderr}`);
            return res.status(500).send('Lỗi từ Java');
        }
        //console.log('stdout:', stdout);
        const moviesList = stdout.trim().split('\n');
        const moviesJSON = convertMoviesToJSON(moviesList);

        try {
            const moviesListWithPoster = await fetchMoviePoster(moviesJSON);
            res.json({ moviesJSON: moviesListWithPoster });
        } catch (err) {
            console.error('Lỗi khi fetch thông tin phim:', err);
            res.status(500).send('Không thể lấy thông tin phim');
        }
    });
});

app.post('/api/submit-rating', (req, res) => {
    const { rater_id, movie_id, rating, time } = req.body;

    if (!rater_id || !movie_id || !rating || !time) {
        return res.status(400).json({ success: false, message: 'Thiếu thông tin đánh giá' });
    }

    const filePath = path.join('./data', 'ratings.csv');
    const line = `${rater_id},${movie_id},${rating},${time}\n`;

    // Nếu file chưa tồn tại, ghi dòng tiêu đề
    if (!fs.existsSync(filePath)) {
        fs.writeFileSync(filePath, 'rater_id,movie_id,rating,time\n');
    }

    // Ghi đánh giá vào file
    fs.appendFile(filePath, line, (err) => {
        if (err) {
            console.error('Lỗi ghi file:', err);
            return res.status(500).json({ success: false, message: 'Không thể ghi dữ liệu' });
        }
        res.json({ success: true, message: 'Đánh giá đã được lưu thành công' });
    });
});

app.get('/recommend', (req, res) => {
  const userId = req.query.user;

  if (!userId) {
      return res.status(400).send('Thiếu user ID');
  }

  exec(`java -cp "./src/logic/recommend.jar;./lib/commons-csv-1.10.0.jar" RecommendationRunner ${userId}`, (error, stdout, stderr) => {
      if (error) {
          console.error(`Lỗi thực thi Java: ${error.message}`);
          return res.status(500).send('Lỗi khi chạy chương trình Java');
      }
      if (stderr) {
          console.error(`stderr: ${stderr}`);
      }

      // Trả về HTML từ chương trình Java
      res.send(stdout);
  });
});

// app.post('/api/run-recommender', (req, res) => {
//   const raterId = req.body.rater_id || '1'; // fallback nếu không có id

//   const command = `java -cp "recommend.jar;lib/commons-csv-1.10.0.jar" RecommendationRunner ${raterId}`;

//   exec(command, (error, stdout, stderr) => {
//       if (error) {
//           console.error(`exec error: ${error}`);
//           return res.status(500).json({ error: 'Lỗi khi chạy Java' });
//       }
//       res.json({ output: stdout });
//       console.log('Output:', stdout);
//   });
// });


app.get('/api/get-recommendations/:user_id', async (req, res) => {
  const raterId = req.params.user_id;
  const genreFilter = req.query.genre; // lấy genre từ query nếu có

  // Tùy điều kiện mà chạy lệnh Java khác nhau
  const command = genreFilter
    ? `java -cp "recommend.jar;lib/commons-csv-1.10.0.jar" MovieRunnerSimilarRatings ${raterId} ${genreFilter}`
    : `java -cp "recommend.jar;lib/commons-csv-1.10.0.jar" RecommendationRunner ${raterId}`;
  //console.log("raterID: " + raterId);
  //console.log("genreFilter: " + genreFilter);

  exec(command, async (error, stdout, stderr) => {
    if (error) {
      console.error(`exec error: ${error}`);
      return res.status(500).json({ error: 'Lỗi khi chạy chương trình Java' });
    }
    //console.log('Java stdout:', stdout);

    const moviesList = stdout.trim().split('\n');
    const moviesJSON = convertMoviesToJSON(moviesList);

    try {
      const enrichedMovies = await Promise.all(moviesJSON.map(movie => {
        return new Promise((resolve, reject) => {
          const cmd = `java -cp "recommend.jar;lib/commons-csv-1.10.0.jar" MovieRunnerAverage ${movie.id}`;
          exec(cmd, (err, avgOut, avgErr) => {
            if (err) {
              console.error(`Error getting avgRating for ${movie.id}:`, err);
              return resolve({ ...movie, avgRating: null });
            }

            try {
              const avgJson = JSON.parse(avgOut.trim());
              resolve({ ...movie, avgRating: avgJson.avgRating });
            } catch (parseErr) {
              console.error('Lỗi khi parse JSON:', parseErr);
              resolve({ ...movie, avgRating: null });
            }
          });
        });
      }));

      const withPoster = await fetchMoviePoster(enrichedMovies);
      res.json({ moviesJSON: withPoster });

    } catch (err) {
      console.error('Lỗi khi xử lý movie list:', err);
      res.status(500).send('Không thể xử lý danh sách phim.');
    }
  });
});


app.get('/api/get-my-ratings/:user_id', async (req,res)=>{
  const raterId = req.params.user_id;

  const command = `java -cp "recommend.jar;lib/commons-csv-1.10.0.jar" RaterDatabase ${raterId}`;

  exec(command, async (error, stdout, stderr) => {
      if (error) {
          console.error(`exec error: ${error}`);
          return res.status(500).json({ error: 'Lỗi khi chạy Java' });
      }
      const moviesList = stdout.trim().split('\n');
      const moviesJSON = convertMoviesToJSON(moviesList);

      try {
          const moviesListWithPoster = await fetchMoviePoster(moviesJSON);
          res.json({ moviesJSON: moviesListWithPoster });
          
          //console.log(moviesJSON);
          //res.json({ recommendations: moviesList });
      } catch (err) {
          console.error('Lỗi khi fetch thông tin phim đã đánh giá:', err);
          res.status(500).send('Không thể lấy thông tin phim đã đánh giá.');
      }
  });
})

function getAllMovies() {
    return new Promise((resolve, reject) => {
        const results = [];
        fs.createReadStream(path.join('./data', 'ratedmoviesfull.csv'))
            .pipe(csv())
            .on('data', (data) => results.push(data))
            .on('end', () => resolve(results))
            .on('error', reject);
    });
}

app.get('/api/search-movie', async (req, res) => {
  const query = req.query.query?.toLowerCase();
  if (!query) {
      return res.status(400).json({ error: 'Thiếu từ khóa tìm kiếm' });
  }

  try {
      const movies = await getAllMovies();
      const matchedMovies = movies.filter(movie =>
          movie.title.toLowerCase().includes(query)
      );

      const moviesListWithPoster = await fetchMoviePoster(matchedMovies);
      res.json({ results: moviesListWithPoster });
  } catch (err) {
      console.error('Lỗi khi tìm kiếm phim:', err);
      res.status(500).json({ error: 'Không thể tìm kiếm phim' });
  }
});

app.get('/api/get-all-genres',async (req,res)=>{
  const command = `java -cp "recommend.jar;lib/commons-csv-1.10.0.jar" MovieDatabase`;

  exec(command, async (error, stdout, stderr) => {
      if (error) {
          console.error(`exec error: ${error}`);
          return res.status(500).json({ error: 'Lỗi khi chạy Java' });
      }
      const genresList = stdout.trim().split('\n');
      const cleanGenres = genresList
        .map(g => g.trim())       // Loại bỏ \r, khoảng trắng thừa
        .filter(g => g !== "N/A"); // Loại bỏ "N/A"

      try {
        res.json(cleanGenres);
          
      } catch (err) {
          console.error('Lỗi khi fetch thông tin phim tất cả genre:', err);
          res.status(500).send('Không thể lấy thông tin tất cả phim.');
      }
  });
})

app.listen(port, () => {
  console.log(`Server running at http://localhost:${port}`);
});