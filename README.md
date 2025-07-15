# ShareIt — Сервис аренды техники

## 📌 Описание

**ShareIt** — это веб-приложение, позволяющее пользователям размещать и арендовать технику друг у друга, реализуя принципы экономики совместного потребления. Проект разработан с использованием микросервисной архитектуры и включает в себя современные меры обеспечения безопасности: OAuth 2.0, JWT, валидацию, rate-limiting и централизованное логирование.

Сервис решает следующие задачи:

- Упрощает аренду техники между частными лицами.
- Снижает потребность в покупке редко используемых вещей.
- Предоставляет безопасный доступ к функциональности сервиса на основе токенов.

---

## 🧰 Технологии

- Java 21
- Spring Boot 3
- Spring Authorization Server
- Spring Security (JWT, OAuth 2.0)
- PostgreSQL
- Apache Kafka
- Redis (Rate Limiting)
- Docker + Docker Compose

---

## 🚀 Установка и запуск

> ⚠️ Убедитесь, что у вас установлен Docker и Docker Compose

### 1. Клонируйте репозиторий:

```bash
git clone https://github.com/helloWoor1d/java-shareit.git
cd shareit
````

### 2. Соберите проект:

```bash
mvn clean install
```

### 3. Запустите сервисы:

```bash
docker-compose up
```

После запуска:

* Gateway: `http://localhost:9091`
* Auth Server: `http://localhost:9093`
* Main Server: `http://localhost:9099`
* Main Db (PostgreSQL): `localhost:5433`
* Log Server: `http://localhost:9092`
* Log Db (PostgreSQL): `localhost:5434`
* Kafka: `localhost:9098`
* Redis: `localhost:6380`

---

## 📈 В планах

* [ ] UI-интерфейс для арендаторов и владельцев
* [ ] Интеграция с внешними провайдерами авторизации (VK, Telegram)
* [ ] Автоматическое определение уровня логов (`LogLevel`) по типу события (`LogEvent`)

---

## 📄 Лицензия

Проект создан в образовательных целях. Не предназначен для коммерческого распространения.

---
