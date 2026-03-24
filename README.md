# Todo List — Kotlin Multiplatform

Приложение "Список задач" на Kotlin Multiplatform с MVVM архитектурой.

## Ссылки
- Видео/Скриншоты демонстрации: https://disk.yandex.ru/d/z40N9XJCUbFEkw
- Релиз APK: https://github.com/LavDaDi/X5_tech_testing/releases/tag/v1.0.0



## Функциональность

- Добавление новой задачи (с возможностью присвоить тег категории)
- Редактирование текста существующей задачи
- Удаление задачи (по кнопке или жестом Swipe-To-Dismiss)
- Переключение статуса выполнения (чекбокс)
- Фильтрация: Все / Активные / Выполненные
- Счётчик выполненных/всего задач с визуализацией прогресса

## Архитектура

**MVVM** с разделением на модули:

```text
model/        — domain-модели (TodoItem, TodoTag, TodoFilter) и репозиторий
feature/      — ViewModel, UI (Compose), DI-модуль
composeApp/   — Android entry point (Application, Activity)
```
### Стек технологий

- **Kotlin Multiplatform** — общий код в `commonMain`
- **Compose Multiplatform** — декларативный UI
- **Koin** — Dependency Injection
- **Kotlinx Coroutines** — асинхронность, `StateFlow`
- **Kotlinx Serialization** — сериализация моделей
- **Ktor Client** — зависимость исключена из итогового решения по принципу YAGNI (приложение полностью локальное, внедрение HTTP-клиента вело к оверинжинирингу).

### Управление состоянием

`TodoViewModel` использует `combine` для объединения потоков данных (список задач, фильтр, текст ввода) в единый `StateFlow<TodoScreenState>`. Это обеспечивает **single source of truth** для UI.

## Запуск

```bash
# Сборка APK
./gradlew :app:assembleDebug

# Запуск тестов
./gradlew :feature:testDebugUnitTest
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## Тесты

6 unit-тестов для `TodoViewModel`:

| Тест | Описание |
|------|----------|
| `should_add_new_todo_item_to_list` | Добавление задачи в список |
| `should_toggle_todo_completion_status` | Переключение статуса выполнения |
| `should_filter_todos_by_status` | Фильтрация по статусу |
| `should_calculate_correct_statistics` | Подсчёт статистики |
| `should_delete_todo_item` | Удаление задачи |
| `should_not_add_blank_todo` | Пустые задачи не добавляются |

## Использованные ИИ-инструменты

- **Claude Code (CLI)** — генерация кода, настройка проекта
