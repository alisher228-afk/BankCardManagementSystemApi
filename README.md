# 🏦 Bank Card Management System API

> 🚧 **В разработке** — проект активно развивается. Планируется добавление новых функций и улучшений.

REST API для управления банковскими счетами и картами с JWT-аутентификацией, разграничением ролей и полной историей транзакций.

---

## Технологический стек

- **Java 17** + **Spring Boot 3.5**
- **Spring Security** — аутентификация и авторизация
- **JWT (JJWT)** — access и refresh токены
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL** — основная база данных
- **Liquibase** — версионирование схемы БД
- **Bean Validation** — валидация входящих данных
- **JUnit 5** + **Mockito** — юнит-тесты
- **Swagger / OpenAPI** — документация API
- **Docker** + **Docker Compose** — контейнеризация

---

## Быстрый старт (Docker)

Самый простой способ запустить проект — через Docker Compose. Не нужно устанавливать Java, Maven или PostgreSQL локально.

### Требования

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

### Запуск

```bash
git clone https://github.com/akusher/BankCardManagementSystemApi.git
cd BankCardManagementSystemApi
docker-compose up --build
```

При первом запуске Docker соберёт образ приложения и поднимет PostgreSQL. Liquibase автоматически применит все миграции.

Приложение будет доступно на **http://localhost:8087**

Swagger UI: **http://localhost:8087/swagger-ui/index.html**

---

## Локальный запуск (без Docker)

### Требования

- Java 17+
- PostgreSQL
- Maven

### Настройка базы данных

```properties
spring.datasource.url=jdbc:postgresql://localhost:5438/postgres
spring.datasource.username=postgres
spring.datasource.password=54321
```

Все параметры можно изменить в `src/main/resources/application.properties`.

### Запуск

```bash
mvn spring-boot:run
```

---

## Тесты

```bash
mvn test
```

Покрыты юнит-тестами все методы `TransferService`: `transfer`, `deposit`, `withdraw` — включая позитивные и негативные сценарии.

---

## Аутентификация

Используется схема **Access Token + Refresh Token**.

| Параметр    | Значение |
|-------------|----------|
| Access TTL  | 15 минут |
| Refresh TTL | 7 дней   |

Все защищённые эндпоинты требуют заголовок:

```
Authorization: Bearer <access_token>
```

В Swagger UI доступна кнопка **Authorize** для ввода токена и тестирования защищённых endpoints.

---

## API Endpoints

### Auth — `/api/auth`

| Метод  | URL                  | Описание                        | Авторизация |
|--------|----------------------|---------------------------------|-------------|
| `POST` | `/api/auth/register` | Регистрация нового пользователя | Нет         |
| `POST` | `/api/auth/login`    | Вход, получение токенов         | Нет         |
| `POST` | `/api/auth/refresh`  | Обновление access токена        | Нет         |

**Регистрация — тело запроса:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "secret123"
}
```

**Логин — ответ:**
```json
{
  "accessToken": "eyJ...",
  "refreshToken": "uuid-token"
}
```

---

### Users — `/api/user`

| Метод    | URL                  | Описание                  | Роль  |
|----------|----------------------|---------------------------|-------|
| `GET`    | `/api/user/me`       | Получить свой профиль     | USER  |
| `PUT`    | `/api/user/me`       | Обновить свой профиль     | USER  |
| `GET`    | `/api/user`          | Список всех пользователей | ADMIN |
| `DELETE` | `/api/user/{userId}` | Удалить пользователя      | ADMIN |

---

### Accounts — `/api/accounts`

| Метод  | URL                              | Описание                       | Роль |
|--------|----------------------------------|--------------------------------|------|
| `POST` | `/api/accounts`                  | Создать счёт                   | USER |
| `GET`  | `/api/accounts`                  | Список своих счётов (pageable) | USER |
| `GET`  | `/api/accounts/{id}`             | Получить счёт по ID            | USER |
| `POST` | `/api/accounts/{id}/deposit`     | Пополнить счёт                 | USER |
| `POST` | `/api/accounts/{id}/withdraw`    | Снять средства со счёта        | USER |

**Создание счёта — тело запроса:**
```json
{
  "currency": "USD"
}
```

Каждый счёт автоматически получает уникальный **IBAN** и **номер счёта**.

**Deposit / Withdraw — тело запроса:**
```json
{
  "amount": 500.00
}
```

**Ответ (TransactionResponse):**
```json
{
  "id": 1,
  "fromAccountId": null,
  "toAccountId": 3,
  "amount": 500.00,
  "currency": "USD",
  "status": "COMPLETED",
  "reference": "uuid-reference",
  "description": "Deposit",
  "createdAt": "2026-03-20T10:00:00"
}
```

---

### Transfers — `/api/transfers`

| Метод  | URL                                | Описание                       | Роль |
|--------|------------------------------------|--------------------------------|------|
| `POST` | `/api/transfers`                   | Перевод между счетами          | USER |
| `GET`  | `/api/transfers/history/{accountId}` | История транзакций по счёту  | USER |

**Перевод — тело запроса:**
```json
{
  "fromId": 1,
  "toId": 2,
  "amount": 500.00
}
```

**История транзакций** поддерживает пагинацию и сортировку:
```
GET /api/transfers/history/1?page=0&size=10&sort=createdAt,desc
```

Сервис проверяет:
- достаточность средств на счёте отправителя
- совпадение валют счётов
- активность обоих счётов
- права доступа — пользователь видит только свои счета

---

### Cards — `/api/cards`

| Метод    | URL                            | Описание                     | Роль |
|----------|--------------------------------|------------------------------|------|
| `GET`    | `/api/cards`                   | Список своих карт (pageable) | USER |
| `POST`   | `/api/cards`                   | Создать карту для счёта      | USER |
| `DELETE` | `/api/cards/{cardId}`          | Удалить карту                | USER |
| `POST`   | `/api/cards/{cardId}/block`    | Заблокировать карту          | USER |
| `POST`   | `/api/cards/{cardId}/activate` | Активировать карту           | USER |

- PAN карты хранится в **зашифрованном** виде
- В ответе возвращаются только **последние 4 цифры**

---

## Безопасность

- Все операции со счётами проверяют **ownership** — пользователь не может получить доступ к чужим счетам
- Переводы защищены от **deadlock** через упорядоченную блокировку (`Math.min/max` по ID)
- Используется **оптимистичная блокировка** (`@Version`) для защиты от race condition
- Транзакции сохраняются со статусом `PENDING` → `COMPLETED` / `FAILED`

---

## Структура проекта

```
src/main/java/.../
├── controller/         # REST-контроллеры
├── service/            # Бизнес-логика
│   └── exception/      # Кастомные исключения + GlobalExceptionHandler
├── entity/             # JPA-сущности
│   └── statusAndRole/  # Enum-ы: Role, AccountStatus, CardStatus, TransactionStatus
├── dto/                # Request/Response DTO
│   └── mapping/        # Маппинг entity → DTO
├── repository/         # Spring Data репозитории
└── config/
    └── jpa/            # SecurityConfig, JwtFilter, SwaggerConfig
```

---

## Модель данных

```
User
 └── Account (1..N)
      ├── Card (1..N)
      └── Transaction (участвует как from/to)
```

- **User** — пользователь системы, роль `USER` или `ADMIN`
- **Account** — банковский счёт с IBAN, балансом и валютой
- **Card** — привязана к счёту, PAN хранится зашифрованным
- **Transaction** — история операций: deposit, withdraw, transfer
- **RefreshToken** — привязан к пользователю, хранится в БД

---

## Обработка ошибок

Все исключения перехватываются `GlobalExceptionHandler`. Основные кастомные исключения:

| Исключение                   | HTTP | Ситуация                           |
|------------------------------|------|------------------------------------|
| `InsufficientFundsException` | 400  | Недостаточно средств               |
| `CurrencyMismatchException`  | 400  | Разные валюты при переводе         |
| `AccountInactiveException`   | 400  | Счёт неактивен                     |
| `AccountNotFoundException`   | 404  | Счёт не найден                     |
| `InvalidTransferException`   | 400  | Некорректный перевод               |
| `AccessDeniedException`      | 403  | Нет прав доступа к ресурсу         |
| `TransferConflictException`  | 409  | Конфликт при параллельном переводе |
