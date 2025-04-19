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
            `;
            tbody.appendChild(tr);
        });
    });
