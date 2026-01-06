![image](image.gif)

# Recipes App

**Recipes App** — Android-приложение для работы с рецептами на XML.

## ✨ Описание

- Главный экран с категориями блюд и навигацией
- Каталог рецептов (`RecyclerView`)
- Экран рецепта:
    - ингредиенты
    - `SeekBar` для изменения количества порций
    - автоматический пересчёт ингредиентов
- Избранные рецепты
- Локальное хранение данных
- Интерфейс в стиле Material Design

## 🧱 Архитектура

Приложение построено на основе **MVVM**:

- **View** — `Activity`, `Fragment`
- **ViewModel** — управление состоянием и логикой экрана
- **Model** — работа с сетью и локальными источниками данных
- **Repository** — единая точка доступа к данным (сеть + локальная БД)

Архитектура масштабируемая и подходит для дальнейшего развития проекта.

## 🛠️ Стек технологий

### UI / UX
- XML Layouts
- Material Design
- RecyclerView
- Fragment + Activity
- Jetpack Navigation Component
- Figma (дизайн-макеты)

### Архитектура
- MVVM
- ViewModel
- LiveData
- Repository Pattern

### Данные
- Retrofit
- OkHttp
- Room
- SharedPreferences
- Parcelable

### Инструменты
- Hilt (Dependency Injection)
- Glide (загрузка изображений)
- Git / Git Flow
- Pull Request + Code Review

## 🧑‍💻 Подход к разработке

- Feature-ветки под каждую задачу
- Осмысленные commit-сообщения
- Code review перед мержем
- Clean Code и Android best practices
- Код ориентирован на поддержку и масштабируемость
