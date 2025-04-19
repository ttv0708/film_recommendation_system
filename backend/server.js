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
