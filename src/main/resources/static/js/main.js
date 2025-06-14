const API = 'http://localhost:8080/api/v1'
const userId = localStorage.getItem('userId')
let loadedMovies = []
let currentEditedMovieId = null
let currentPage = 1
const moviesPerPage = 5

if (!userId) {
    window.location.href = 'index.html'
} else {
    fetch(`${API}/users/${userId}`)
        .then(res => res.ok ? init() : Promise.reject())
        .catch(() => {
            localStorage.removeItem('userId')
            window.location.href = 'main.html'
        })
}

function init() {
    fetch(`${API}/users/${userId}`)
        .then(res => res.json())
        .then(user => {
            document.getElementById('user-id').innerText = user.login
        })

    document.querySelector('#logout').addEventListener('click', () => {
        localStorage.removeItem('userId')
        window.location.href = 'index.html'
    })

    document.querySelector('#delete-user').addEventListener('click', async () => {
        if (!confirm('Вы уверены, что хотите удалить профиль?')) return
        const res = await fetch(`${API}/users/${userId}`, { method: 'DELETE' })
        if (res.ok) {
            localStorage.removeItem('userId')
            alert('Профиль удалён')
            window.location.href = 'index.html'
        }
    })

    document.querySelector('#openAdmin').addEventListener('click', () => {
        document.querySelector('#adminModal').classList.remove('hidden')
    })

    document.querySelector('#closeAdmin').addEventListener('click', () => {
        document.querySelector('#adminModal').classList.add('hidden')
        resetMovieForm()
    })

    document.querySelector('#movieForm').addEventListener('submit', handleMovieSubmit)
    document.querySelector('#deleteMovie').addEventListener('click', handleMovieDelete)
    document.querySelector('#adminSearch').addEventListener('input', handleMovieSearch)

    document.querySelectorAll('#filters button').forEach(button => {
        button.addEventListener('click', () => {
            document.querySelectorAll('#filters button').forEach(btn => btn.classList.remove('active'))
            button.classList.add('active')
            document.querySelector('#filter').value = button.dataset.filter
            applyFilters()
        })
    })

    document.querySelector('#search').addEventListener('input', applyFilters)

    loadMovies()
}

//загрузка фильмов
async function loadMovies() {
    try {
        const res = await fetch(`${API}/movies?userId=${userId}`)
        if (!res.ok) throw new Error()
        loadedMovies = await res.json()
        applyFilters()
    } catch {
        document.querySelector('#movieList').innerText = 'Не удалось загрузить фильмы'
    }
}

//фильтрация по поиску и статусу
function applyFilters() {
    const search = document.querySelector('#search').value.trim().toLowerCase()
    const filter = document.querySelector('#filter').value

    const filtered = loadedMovies.filter(m => {
        const matchTitle = m.title.toLowerCase().includes(search)
        const matchStatus =
            filter === 'all' ||
            (filter === 'watched' && m.isWatched) ||
            (filter === 'unwatched' && !m.isWatched)
        return matchTitle && matchStatus
    })

    renderMovies(filtered)
}

//рендер фильмов и пагинация
function renderMovies(movies) {
    const container = document.querySelector('#movieList')
    const countBlock = document.querySelector('#count')
    const pagination = document.querySelector('#pagination')

    container.innerHTML = ''
    pagination.innerHTML = ''

    const totalPages = Math.ceil(movies.length / moviesPerPage)
    if (currentPage > totalPages) currentPage = 1

    const start = (currentPage - 1) * moviesPerPage
    const end = start + moviesPerPage
    const pageMovies = movies.slice(start, end)

    const filter = document.querySelector('#filter').value
    countBlock.innerText = {
        all: `Всего фильмов: ${movies.length}`,
        watched: `Просмотрено фильмов: ${movies.length}`,
        unwatched: `Не просмотрено фильмов: ${movies.length}`
    }[filter]

    if (!movies.length) {
        container.innerText = 'Фильмы не найдены'
        return
    }

    pageMovies.forEach(movie => {
        const card = document.createElement('div')
        card.className = 'movieCard'
        card.innerHTML = `
      <div class="movieCardContent">
        <h3>${movie.title}</h3>
        <p>Режиссер: ${movie.author || '-'}</p>
        <p>Просмотрено: ${movie.isWatched ? 'Да' : 'Нет'}</p>
        ${!movie.isWatched ? `<button onclick="markAsWatched('${movie.id}')">Смотрел</button>` : ''}
      </div>
      ${movie.posterUrl ? `<img src="${movie.posterUrl}" alt="Постер">` : ''}
    `
        container.appendChild(card)
    })

    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement('button')
        btn.className = 'pageButton'
        btn.innerText = i
        if (i === currentPage) btn.classList.add('active')
        btn.addEventListener('click', () => {
            currentPage = i
            renderMovies(movies)
        })
        pagination.appendChild(btn)
    }
}

//отметка о просмотре
async function markAsWatched(movieId) {
    await fetch(`${API}/users/movies`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId, movieId })
    })

    await fetch(`${API}/movies`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ userId, movieId })
    })

    await loadMovies()
}

//управление фильмами - добавление или редактирование
async function handleMovieSubmit(e) {
    e.preventDefault()

    const title = document.querySelector('#movieTitle').value.trim()
    const author = document.querySelector('#movieAuthor').value.trim()
    const posterFile = document.querySelector('#moviePoster').files[0]
    const message = document.querySelector('#adminMessage')

    if (!title || !author) {
        message.innerText = 'Название и автор обязательны'
        return
    }

    const existing = loadedMovies.find(m => m.title.toLowerCase() === title.toLowerCase())
    if (!currentEditedMovieId && existing) {
        message.innerText = 'Фильм с таким названием уже существует'
        return
    }

    let posterBase64 = null
    if (posterFile) posterBase64 = await fileToBase64(posterFile)

    const payload = {
        title, author,
        poster: posterBase64 || '',
        rating: ''
    }

    const endpoint = currentEditedMovieId
        ? `${API}/movies/${currentEditedMovieId}?bucket=film-poster-base`
        : `${API}/movies?bucket=film-poster-base`

    const method = currentEditedMovieId ? 'PUT' : 'POST'

    const res = await fetch(endpoint, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })

    if (res.ok) {
        message.innerText = currentEditedMovieId ? 'Фильм обновлён' : 'Фильм добавлен'
        resetMovieForm()
        loadMovies()
    } else {
        message.innerText = 'Ошибка при сохранении фильма'
    }
}

//удаление фильма
async function handleMovieDelete() {
    const message = document.querySelector('#adminMessage')
    if (!currentEditedMovieId) {
        message.innerText = 'Сначала найдите фильм'
        return
    }

    const res = await fetch(`${API}/movies/${currentEditedMovieId}`, { method: 'DELETE' })
    if (res.ok) {
        message.innerText = 'Фильм удалён'
        resetMovieForm()
        loadMovies()
    } else {
        message.innerText = 'Ошибка при удалении фильма'
    }
}

//поиск фильмов по названию
function handleMovieSearch() {
    const query = document.querySelector('#adminSearch').value.trim().toLowerCase()
    const found = loadedMovies.find(m => m.title.toLowerCase() === query)

    document.querySelector('#adminMessage').innerText = ''

    if (found) {
        document.querySelector('#movieTitle').value = found.title
        document.querySelector('#movieAuthor').value = found.author
        currentEditedMovieId = found.id
    } else {
        currentEditedMovieId = null
        document.querySelector('#movieForm').reset()
    }
}

function resetMovieForm() {
    currentEditedMovieId = null
    document.querySelector('#movieForm').reset()
    document.querySelector('#adminSearch').value = ''
    document.querySelector('#adminMessage').innerText = ''
}

function fileToBase64(file) {
    return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = () => resolve(reader.result.split(',')[1])
        reader.onerror = reject
        reader.readAsDataURL(file)
    })
}
