function getRaterId() {
    let raterId = localStorage.getItem('rater_id');
    if (!raterId) {
        raterId = 'rater_' + Math.floor(Math.random() * 1000000000);
        localStorage.setItem('rater_id', raterId);
    }
    return raterId;
}

const ratings = {}; // Lưu rating của từng phim

fetch('http://localhost:3000/api/recommendations')
    .then(res => res.json())
    .then(data => {
        const tbody = document.getElementById('movie-table-body');

        data.moviesJSON.forEach((movie, index) => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${movie.id}</td>
                <td>${movie.title}</td>
                <td>${movie.poster ? `<img src="${movie.poster}" style="max-width:100px" />` : 'N/A'}</td>
                <td>${movie.year}</td>
                <td>${movie.genres}</td>
                <td>${movie.country}</td>
                <td>${movie.directors}</td>
                <td>${movie.minutes}</td>
                <td class="rating-cell" data-index="${index}"></td>
            `;
            tbody.appendChild(tr);
            ratings[movie.id] = 0;
            // Tạo 10 ngôi sao
            const ratingCell = tr.querySelector('.rating-cell');
            for (let i = 1; i <= 10; i++) {
                const star = document.createElement('span');
                star.innerHTML = '★';
                star.style.cursor = 'pointer';
                star.style.color = 'gray';
                star.dataset.value = i;
                star.addEventListener('click', function () {
                    const stars = ratingCell.querySelectorAll('span');
                    stars.forEach((s, idx) => {
                        s.style.color = idx < i ? 'gold' : 'gray';
                    });
                    ratings[movie.id] = i;
                    console.log(`User rated movie ID ${movie.id} with ${i} stars`);
                    // Bạn có thể gửi rating về server tại đây nếu cần
                });
                ratingCell.appendChild(star);
            }
        });
    });

let raterId = getRaterId();

// Gửi toàn bộ đánh giá khi bấm nút
document.getElementById('submit-all').addEventListener('click', () => {
    const raterId = getRaterId();
    const time = Date.now();

    const ratedMovies = Object.entries(ratings).filter(([_, rating]) => rating > 0);
    if (ratedMovies.length === 0) {
        alert('Bạn chưa đánh giá phim nào!');
        return;
    }

    // Gửi đánh giá
    Promise.all(ratedMovies.map(([movie_id, rating]) =>
        fetch('http://localhost:3000/api/submit-rating', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ rater_id: raterId, movie_id, rating, time })
        })
    ))
    .then(() => {
        alert('Đã gửi toàn bộ đánh giá! Đang chạy gợi ý...');
        // Điều hướng đến tab 3 (giả sử bạn dùng hệ thống tab với ID)
        document.getElementById('tab3').scrollIntoView({ behavior: 'smooth' });

        // Gọi API lấy gợi ý
        return fetch(`http://localhost:3000/api/get-recommendations/${raterId}`);
    })
    .then(res => res.json())
    .then(data => {
        //console.log('Gợi ý phim:', data.output);
        alert("Đây là danh sách phim được gợi ý.");
    })
    .catch(err => {
        console.error('Lỗi:', err);
    });
});

function loadRecommendations() {
    const raterId = getRaterId();
    fetch(`http://localhost:3000/api/get-recommendations/${raterId}`)
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('movie-recommendation-table');
            tbody.innerHTML = ''; // Xoá cũ trước khi render mới

            data.moviesJSON.forEach((movie, index) => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${movie.id}</td>
                    <td>${movie.title}</td>
                    <td>${movie.poster ? `<img src="${movie.poster}" style="max-width:100px" />` : 'N/A'}</td>
                    <td>${movie.year}</td>
                    <td>${movie.genres}</td>
                    <td>${movie.country}</td>
                    <td>${movie.directors}</td>
                    <td>${movie.minutes}</td>
                    <td class="rating-cell" data-index="${index}"></td>
                `;
                tbody.appendChild(tr);
                
                const ratingCell = tr.querySelector('.rating-cell');
                for (let i = 1; i <= 10; i++) {
                    const star = document.createElement('span');
                    star.innerHTML = '★';
                    star.style.cursor = 'pointer';
                    star.style.color = i <= Math.round(movie.avgRating) ? 'gold' : 'gray';
                    star.dataset.value = i;
                    ratingCell.appendChild(star);
                }
            });
        })
        .catch(err => {
            console.error('Lỗi khi lấy gợi ý phim:', err);
            alert('Không thể lấy gợi ý phim!');
        });
}

function loadMyRatings() {
    const raterId = getRaterId();
    fetch(`http://localhost:3000/api/get-my-ratings/${raterId}`)
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('movie-my-ratings-table');
            tbody.innerHTML = ''; // Xoá cũ trước khi render mới

            data.moviesJSON.forEach((movie, index) => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${movie.id}</td>
                    <td>${movie.title}</td>
                    <td>${movie.poster ? `<img src="${movie.poster}" style="max-width:100px" />` : 'N/A'}</td>
                    <td>${movie.year}</td>
                    <td>${movie.genres}</td>
                    <td>${movie.country}</td>
                    <td>${movie.directors}</td>
                    <td>${movie.minutes}</td>
                    <td class="rating-cell" data-index="${index}"></td>
                `;
                tbody.appendChild(tr);

                const ratingCell = tr.querySelector('.rating-cell');
                for (let i = 1; i <= 10; i++) {
                    const star = document.createElement('span');
                    star.innerHTML = '★';
                    star.style.cursor = 'pointer';
                    star.style.color = i <= movie.rating ? 'gold' : 'gray';
                    star.dataset.value = i;
                    ratingCell.appendChild(star);
                }
            });
        })
        .catch(err => {
            console.error('Lỗi khi lấy danh sách phim đã đánh giá:', err);
            alert('Không thể lấy danh sách phim đã đánh giá!');
        });
}

function openTab(tabId) {
    document.querySelectorAll('.tab-content').forEach(tab => tab.style.display = 'none');
    const selected = document.getElementById(tabId);
    if (selected) selected.style.display = 'block';

    document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
    const active = document.querySelector(`[onclick="openTab('${tabId}')"]`);
    if (active) active.classList.add('active');

    if (tabId === 'tab3') {
        loadRecommendations();
        fetchGenres();
    }
    
    if (tabId === 'tab2') {
        loadMyRatings();
    }
}

document.getElementById('search-button').addEventListener('click', () => {
    const query = document.getElementById('search-input').value.trim();
    if (!query) {
        alert('Vui lòng nhập từ khóa tìm kiếm');
        return;
    }

    fetch(`http://localhost:3000/api/search-movie?query=${encodeURIComponent(query)}`)
        .then(res => res.json())
        .then(data => {
            const tbody = document.getElementById('movie-table-body');
            tbody.innerHTML = ''; // Xoá kết quả cũ

            if (data.results.length === 0) {
                tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;">Không tìm thấy phim</td></tr>';
                return;
            }

            data.results.forEach((movie, index) => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${movie.id}</td>
                    <td>${movie.title}</td>
                    <td>${movie.poster ? `<img src="${movie.poster}" style="max-width:100px"/>` : 'N/A'}</td>
                    <td>${movie.year}</td>
                    <td>${movie.genres}</td>
                    <td>${movie.country}</td>
                    <td>${movie.directors}</td>
                    <td>${movie.minutes}</td>
                    <td class="rating-cell" data-index="${index}"></td>
                `;
                tbody.appendChild(tr);

                // Hiển thị 10 sao để đánh giá
                const ratingCell = tr.querySelector('.rating-cell');
                for (let i = 1; i <= 10; i++) {
                    const star = document.createElement('span');
                    star.innerHTML = '★';
                    star.style.cursor = 'pointer';
                    star.style.color = 'gray';
                    star.dataset.value = i;
                    star.addEventListener('click', function () {
                        const stars = ratingCell.querySelectorAll('span');
                        stars.forEach((s, idx) => {
                            s.style.color = idx < i ? 'gold' : 'gray';
                        });
                        ratings[movie.id] = i;
                        console.log(`User rated movie ID ${movie.id} with ${i} stars`);
                    });
                    ratingCell.appendChild(star);
                }
            });
        })
        .catch(err => {
            console.error('Lỗi khi tìm kiếm:', err);
            alert('Đã xảy ra lỗi khi tìm kiếm phim.');
        });
});

async function fetchGenres() {
    try {
      // Fetch genres from API
      const response = await fetch('http://localhost:3000/api/get-all-genres');
      const genres = await response.json();
      
      // Get the container and clear any existing content
      const filterContainer = document.getElementById('genre-filter');
      filterContainer.innerHTML = '';
      
      // Create the toggle button
      const toggleDiv = document.createElement('div');
      toggleDiv.className = 'genre-toggle';
      
      const toggleIcon = document.createElement('span');
      toggleIcon.className = 'toggle-icon';
      toggleIcon.textContent = '+';
      
      const toggleText = document.createElement('span');
      toggleText.className = 'toggle-text';
      toggleText.textContent = 'Chọn thể loại';
      
      toggleDiv.appendChild(toggleIcon);
      toggleDiv.appendChild(toggleText);
      
      // Create the genre list
      const genreList = document.createElement('div');
      genreList.className = 'genre-list';
      
      // Add genres to the list
      genres.forEach(genre => {
        const div = document.createElement('div');
        div.className = 'genre';
        div.textContent = genre;
      
        div.addEventListener('click', function(e) {
          e.stopPropagation(); // Ngăn sự kiện lan truyền
      
          // Bỏ class 'selected' khỏi tất cả các genre khác
          const allGenres = document.querySelectorAll('.genre');
          allGenres.forEach(g => g.classList.remove('selected'));
      
          // Gán class 'selected' cho genre vừa được click
          this.classList.add('selected');
      
          // Gọi logic lọc phim nếu cần
          console.log(`Genre ${genre} selected`);
        });
      
        genreList.appendChild(div);
      });
      
      
      // Add toggle functionality
      toggleDiv.addEventListener('click', function() {
        // Toggle classes for styling
        this.classList.toggle('active');
        genreList.classList.toggle('show');
        
        // Update the toggle icon
        toggleIcon.textContent = genreList.classList.contains('show') ? '−' : '+';
      });
      
      // Append elements to the container
      filterContainer.appendChild(toggleDiv);
      filterContainer.appendChild(genreList);
      
      console.log('Genre filter initialized with', genres.length, 'genres');
      
    } catch (err) {
      console.error('Lỗi khi tải thể loại:', err);
      
      // Display error in the UI
      const filterContainer = document.getElementById('genre-filter');
      filterContainer.innerHTML = '<div class="error-message">Error loading genres. Please try again later.</div>';
    }
  }
  