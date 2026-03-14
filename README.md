Bank Card Management System API

🚧 В разработке — проект активно развивается. Планируется добавление новых функций и улучшений. Следите за обновлениями!

REST API для управления банковскими счетами и картами с JWT-аутентификацией и разграничением ролей.

Технологический стек

Java 17+ + Spring Boot

Spring Security — аутентификация и авторизация

JWT (JJWT) — access и refresh токены

Spring Data JPA + Hibernate

PostgreSQL — основная база данных

Liquibase — версионирование схемы БД

Bean Validation — валидация входящих данных

Запуск проекта
Требования

Java 17+

PostgreSQL (порт 5438)

Maven

Настройка базы данных

Создай БД в PostgreSQL:

CREATE DATABASE postgres;

По умолчанию приложение ожидает:

Параметр	Значение
URL	localhost:5438
БД	postgres
Username	postgres
Password	54321

Все параметры можно изменить в src/main/resources/application.properties.

Запуск
mvn spring-boot:run

Приложение запустится на порту 8087.
Liquibase автоматически применит миграции при старте.

Аутентификация

Используется схема Access Token + Refresh Token.

Параметр	Значение
Access TTL	15 минут
Refresh TTL	7 дней

Все защищённые эндпоинты требуют заголовок:

Authorization: Bearer <access_token>
API Endpoints
Auth — /api/auth
Метод	URL	Описание	Авторизация
POST	/api/auth/register	Регистрация нового пользователя	Нет
POST	/api/auth/login	Вход, получение токенов	Нет
POST	/api/auth/refresh	Обновление access токена	Нет

Регистрация — тело запроса:

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "secret123"
}

Логин — ответ:

{
  "accessToken": "eyJ...",
  "refreshToken": "uuid-token"
}
Users — /api/user
Метод	URL	Описание	Роль
GET	/api/user/me	Получить свой профиль	USER
PUT	/api/user/me	Обновить свой профиль	USER
GET	/api/user	Список всех пользователей	ADMIN
DELETE	/api/user/{userId}	Удалить пользователя	ADMIN
Accounts — /api/accounts
Метод	URL	Описание	Роль
POST	/api/accounts	Создать счёт	USER
GET	/api/accounts	Список своих счётов (pageable)	USER
GET	/api/accounts/{id}	Получить счёт по ID	USER

Создание счёта — тело запроса:

{
  "currency": "USD"
}

Каждый счёт автоматически получает уникальный IBAN и номер счёта.

Cards — /api/cards
Метод	URL	Описание	Роль
GET	/api/cards	Список своих карт (pageable)	USER
POST	/api/cards	Создать карту для счёта	USER
DELETE	/api/cards/{cardId}	Удалить карту	USER
POST	/api/cards/{cardId}/block	Заблокировать карту	USER
POST	/api/cards/{cardId}/activate	Активировать карту	USER

Создание карты — тело запроса:

{
  "accountId": 1
}

PAN карты хранится в зашифрованном виде

В ответе возвращаются только последние 4 цифры

Transfers — /api/transfers
Метод	URL	Описание	Роль
POST	/api/transfers	Перевод между счетами	USER

Тело запроса:

{
  "fromId": 1,
  "toId": 2,
  "amount": 500.00
}

Сервис проверяет:

достаточность средств на счёте отправителя

совпадение валют счётов

активность обоих счётов

Структура проекта
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
    └── jpa/            # SecurityConfig, JwtFilter, конвертеры
Модель данных
User
 └── Account (1..N)
      ├── Card (1..N)
      └── Transaction (участвует как from/to)

User — пользователь системы, роль USER или ADMIN

Account — банковский счёт с IBAN, балансом и валютой

Card — привязана к счёту, PAN хранится зашифрованным

Transaction — история переводов между счетами

RefreshToken — привязан к пользователю, хранится в БД

Обработка ошибок

Все исключения перехватываются GlobalExceptionHandler. Основные кастомные исключения:

Исключение	Ситуация
InsufficientFundsException	Недостаточно средств
CurrencyMismatchException	Разные валюты при переводе
AccountInactiveException	Счёт неактивен
AccountNotFoundException	Счёт не найден
CardNotFoundException	Карта не найдена
InvalidTransferException	Некорректный перевод
TransferConflictException	Конфликт при параллельном переводе
TransferFailedException	Общая ошибка перевода
