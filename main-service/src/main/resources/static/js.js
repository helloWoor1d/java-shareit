console.log("✅ js.js загружен");

// ===================
// 🔐 Обмен кода на токен
// ===================
async function exchangeCodeForToken() {
    console.log("🔑 exchangeCodeForToken fired, query:", window.location.search);

    const params = new URLSearchParams(window.location.search);
    const code = params.get("code");
    const state = params.get("state");
    const storedState = sessionStorage.getItem("oauth_state");
    const codeVerifier = sessionStorage.getItem("code_verifier");

    if (!code || !state || !codeVerifier || state !== storedState) {
        console.error("❌ Ошибка OAuth-потока: параметры не совпадают или отсутствуют");
        return;
    }

    try {
        const tokenResponse = await fetch("http://localhost:9093/oauth2/token", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
                grant_type: "authorization_code",
                code,
                redirect_uri: "http://localhost:9090/index.html",
                client_id: "shareit-frontend",
                code_verifier: codeVerifier
            })
        });

        const tokenData = await tokenResponse.json();

        if (tokenData.access_token) {
            sessionStorage.setItem("access_token", tokenData.access_token);
            history.replaceState(null, "", "/");
            setTimeout(() => PageShow('Профиль'), 0);
        } else {
            console.error("❌ Не удалось получить токен", tokenData);
        }
    } catch (e) {
        console.error("❌ Ошибка обмена токена:", e);
    }
}

// ===================
// 📄 Загрузка страницы
// ===================
window.addEventListener("load", async () => {
    const params = new URLSearchParams(window.location.search);
    if (params.get("code")) {
        await exchangeCodeForToken();
        return;
    }

    const token = sessionStorage.getItem("access_token");
    if (token) {
        try {
            const resp = await fetch("/users/me", {
                headers: { Authorization: "Bearer " + token }
            });
            if (resp.ok) {
                const user = await resp.json();
                sessionStorage.setItem("user", JSON.stringify(user));
            }
        } catch (e) {
            console.warn("⚠️ Ошибка при загрузке пользователя:", e);
        }
    }
});

// ===================
// 📦 Основной скрипт
// ===================
$(document).ready(function() {

    // Анимация при наведении на аватар
    $('.container').on('mouseenter', '.profile-avatar, .creator-avatar', function () {
        $(this).css('transform', 'scale(1.05)');
    }).on('mouseleave', '.profile-avatar, .creator-avatar', function () {
        $(this).css('transform', 'scale(1)');
    });

    // Универсальный рендер страниц
    window.PageShow = function (page) {
        $('.container').html('');

        if (page === 'Главная') {
            $('.container').load("main.html");
        }

        if (page === 'Каталог') {
            $('.container').append('<div class="products"></div>');

            $.ajax({
                url: "/items",
                method: "GET",
                success: function (data) {
                    data.forEach(product => {
                        const productHTML = `
                            <div class="product-card">
                                <img src="${product.imageUrl}" alt="${product.name}" class="product-image">
                                <div class="product-info">
                                    <h3 class="product-title">${product.name}</h3>
                                    <p class="price">${product.available ? 'Доступно' : 'Не доступно'}</p>
                                    <p class="product-description">${product.description}</p>
                                </div>
                                <button class="add-to-cart">Нравится</button>
                            </div>
                        `;
                        $('.container .products').append(productHTML);
                    });
                },
                error: function () {
                    $('.container').html('<p>Ошибка загрузки товаров</p>');
                }
            });
        }

        if (page === 'Контакты') {
            $('.container').load("cont.html");
        }

        if (page === 'Профиль') {
            const token = sessionStorage.getItem("access_token");

            if (!token) {
                // Обязательно вызываем повторный вход, если токена нет
                if (typeof startOAuthLogin === 'function') {
                    startOAuthLogin();
                } else {
                    console.error("❌ Функция startOAuthLogin не найдена");
                }
                return;
            }

            $.ajax({
                url: "/users/me",
                method: "GET",
                headers: {
                    Authorization: "Bearer " + token
                },
                success: function (user) {
                    const avatarUrl = user.avatarUrl || "https://filkiniada-4sc.ucoz.org/80781_3.jpg";
                    const profileHTML = `
                        <div class="profile-container">
                            <div class="profile-header">
                                <img
                                    src="${avatarUrl}"
                                    alt="Фото профиля"
                                    class="profile-avatar"
                                />
                                <div class="profile-info">
                                    <h1 class="profile-name">${user.name || "Без имени"}</h1>
                                    <p class="profile-username">${user.email || "Нет email"}</p>
                                    <p class="profile-bio">${user.bio || "Пока пусто :)"}</p>
                                </div>
                            </div>
                            <button class="logout-btn">Выйти</button>
                        </div>
                    `;
                    $(".container").html(profileHTML);
                },
                error: function (xhr) {
                    $(".container").html("<p>Ошибка загрузки профиля</p>");
                    console.error("Ошибка при получении профиля:", xhr);
                }
            });
        }
    };

    $('.container').off('click', '.logout-btn').on('click', '.logout-btn', async function () {
        const token = sessionStorage.getItem("access_token");

        try {
            // ⛔ локальный logout на backend'е приложения
            await fetch("/logout", {
                method: "POST",
                headers: {
                    Authorization: "Bearer " + token
                }
            });
        } catch (e) {
            console.warn("⚠️ Ошибка при POST /logout", e);
        }

        try {
            // 🔒 logout на сервере авторизации (SAS)
            await fetch("http://localhost:9093/logout", {
                method: "GET",
                credentials: "include"
            });
        } catch (e) {
            console.warn("⚠️ Ошибка при GET /logout SAS", e);
        }

        // 🧹 очистка всего sessionStorage
        sessionStorage.clear();

        // ✅ редирект на главную
        location.href = "/";
    });

    // Обработка переходов в меню и кнопки "Профиль"
    $('header .main_menu a, header .auth-btn').click(function (e) {
        e.preventDefault();
        PageShow($(this).text());
    });

    // Стартовая загрузка - Главная
    $('.main_menu a:contains("Главная")').click();
});
