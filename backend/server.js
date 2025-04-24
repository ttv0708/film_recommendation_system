const express = require('express');
const cors = require('cors');
const { exec } = require('child_process');
const fetchMoviePoster = require('./src/fetchMoviePoster');
const app = express();
const port = 3000;

app.use(cors());

// Hàm chuyển đổi dữ liệu phim thành JSON
function convertMoviesToJSON(moviesData) {
    const moviesJSON = [];
    
    moviesData.forEach(movieStr => {
      // Loại bỏ "Movie [" ở đầu và "]" hoặc "]\r" ở cuối
      const content = movieStr.replace('Movie [', '').replace(/\]\r?$/, '');
      
      const movieObj = {};
      let currentKey = '';
      let currentValue = '';
      let inValue = false;
      let i = 0;
      
      // Phân tích từng trường dữ liệu
      while (i < content.length) {
        // Tìm tên trường (key)
        if (!inValue) {
          const keyEndIndex = content.indexOf('=', i);
          if (keyEndIndex !== -1) {
            currentKey = content.substring(i, keyEndIndex).trim();
            i = keyEndIndex + 1;
            inValue = true;
            currentValue = '';
          } else {
            break;
          }
        } 
        // Tìm giá trị của trường (value)
        else {
          // Nếu đây là trường cuối cùng, lấy tất cả nội dung còn lại
          const nextCommaIndex = content.indexOf(', ', i);
          const nextKeyIndex = nextCommaIndex !== -1 ? 
            content.indexOf('=', nextCommaIndex) : -1;
          
          // Nếu không còn dấu phẩy nào, hoặc đây là trường cuối cùng
          if (nextCommaIndex === -1 || nextKeyIndex === -1) {
            currentValue = content.substring(i).trim();
            // Xử lý các trường đặc biệt
            processField(movieObj, currentKey, currentValue);
            break;
          } else {
            // Lấy giá trị cho đến dấu phẩy trước key tiếp theo
            currentValue = content.substring(i, nextCommaIndex).trim();
            processField(movieObj, currentKey, currentValue);
            i = nextCommaIndex + 2; // Di chuyển qua dấu phẩy và khoảng trắng
            inValue = false;
          }
        }
      }
      
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

        const moviesList = stdout.trim().split('\n');
        const moviesJSON = convertMoviesToJSON(moviesList);

        try {
            const moviesListWithPoster = await fetchMoviePoster(moviesJSON);
            res.json({ moviesJSON: moviesListWithPoster });
            
            //console.log(moviesJSON);
            //res.json({ recommendations: moviesList });
        } catch (err) {
            console.error('Lỗi khi fetch thông tin phim:', err);
            res.status(500).send('Không thể lấy thông tin phim');
        }
    });
});

app.listen(port, () => {
    console.log(`Server running at http://localhost:${port}`);
});

const fs = require('fs');
const path = require('path');
const { stderr } = require('process');

app.use(express.json()); // Thêm nếu chưa có

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
    const command = `java -cp "recommend.jar;lib/commons-csv-1.10.0.jar" RecommendationRunner ${raterId}`;

    exec(command, async (error, stdout, stderr) => {
        if (error) {
            console.error(`exec error: ${error}`);
            return res.status(500).json({ error: 'Lỗi khi chạy RecommendationRunner' });
        }

        const moviesList = stdout.trim().split('\n');
        const moviesJSON = convertMoviesToJSON(moviesList);

        try {
            // Gọi Java để lấy avgRating từng phim
            const enrichedMovies = await Promise.all(moviesJSON.map(movie => {
                return new Promise((resolve, reject) => {
                    const cmd = `java -cp "recommend.jar;lib/commons-csv-1.10.0.jar" MovieRunnerAverage ${movie.id}`;
                    exec(cmd, (err, avgOut, avgErr) => {
                        if (err) {
                            console.error(`Error getting avgRating for ${movie.id}:`, err);
                            return resolve({ ...movie, avgRating: null }); // fallback
                        }

                        try {
                            const avgJson = JSON.parse(avgOut.trim());
                            resolve({ ...movie, avgRating: avgJson.avgRating });
                        } catch (parseErr) {
                            console.error('Lỗi khi parse JSON:', parseErr);
                            resolve({ ...movie, avgRating: null }); // fallback
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


