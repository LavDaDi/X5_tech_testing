# Todo List — Kotlin Multiplatform

## Описание задания

Тестовое задание для стажировки X5 Tech.
Цель — разработать экран со списком задач с возможностью добавления,
удаления и отметки выполненных задач на Kotlin Multiplatform
с использованием MVVM архитектуры.

## Ссылки
- Видео/Скриншоты демонстрации: https://disk.yandex.ru/d/z40N9XJCUbFEkw
- Рели- Релиз APK: https://github.com/LavDaDi/X5_tech_testing/releases/tag/v1.0

## Функциональность
- Добавление новой задачи (с возможностью присвоить тег категории)
- Редактирование текста существующей задачи
- Удаление задачи (по кнопке или жестом Swipe-To-Dismiss)
- Переключение статуса выполнения (чекбокс)
- Фильтрация: Все / Активные / Выполненные
- Счётчик выполненных/всего задач с визуализацией прогресса

## Требования к коду (Соблюдены)

Проект полностью соответствует строгим требованиям к код-стайлу:
- **Detekt:** Настроен через `detekt.yml`, проходит без предупреждений.
- **Видимость:** По умолчанию используется `internal` (публичными оставлены только межмодульные контракты).
- **Trailing commas:** Включены и расставлены по всему коду.
- **Max line length:** Установлен лимит в 120 символов, код отформатирован.

## Архитектура

**MVVM** с разделением на модули:

```text
model/        — domain-модели (TodoItem, TodoTag, TodoFilter) и репозиторий
feature/      — ViewModel, UI (Compose), DI-модуль
composeApp/   — Android entry point (Application, Activity)
```
## Стек технологий

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

10 unit-тестов для `TodoViewModel`:

| Тест | Описание |
|------|----------|
| `should_add_new_todo_item_to_list` | Добавление задачи в список |
| `should_toggle_todo_completion_status` | Переключение статуса выполнения |
| `should_filter_todos_by_status` | Фильтрация по статусу |
| `should_calculate_correct_statistics` | Подсчёт статистики |
| `should_delete_todo_item` | Удаление задачи |
| `should_not_add_blank_todo` | Пустые задачи не добавляются |
| `should_add_todo_with_tag` | Добавление задачи с тегом |
| `should_toggle_tag_selection` | Выбор и снятие тега |
| `should_edit_todo_text` | Редактирование текста задачи |
| `should_cancel_editing` | Отмена редактирования |
