const API = 'http://localhost:8080/api/v1';

document.querySelector('#registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const login = document.querySelector('#registerLogin').value.trim();
    const password = document.querySelector('#registerPassword').value.trim();
    const result = document.querySelector('#registerResult');

    if (login.length < 3 || login.length > 30 || password.length < 3 || password.length > 30) {
        result.innerText = 'Логин и пароль должны содержать от 3 до 30 символов';
        return;
    }

    try {
        const res = await fetch(`${API}/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ login, password })
        });

        const data = await res.json();

        if (!res.ok) throw new Error(data.message || 'Ошибка регистрации');

        localStorage.setItem('userId', data.id);
        result.innerText = `Пользователь создан: ${login}`;
    } catch (err) {
        result.innerText = `Ошибка регистрации: ${err.message}`;
    }
});

document.querySelector('#loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const login = document.querySelector('#login').value.trim();
    const password = document.querySelector('#password').value.trim();
    const result = document.querySelector('#loginResult');

    if (login.length < 3 || login.length > 30 || password.length < 3 || password.length > 30) {
        result.innerText = 'Введите логин и пароль от 3 до 30 символов';
        return;
    }

    try {
        const res = await fetch(`${API}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ login, password })
        });

        const data = await res.json();

        if (!res.ok) throw new Error(data.message || 'Ошибка входа');

        localStorage.setItem('userId', data.id);
        window.location.href = 'main.html';
    } catch (err) {
        result.innerText = `Ошибка входа: Неверный логин или пароль`;
    }
});
