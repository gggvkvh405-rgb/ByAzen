# Модули ByAzen — документация

Файл собран автоматически: `python3 tools/gen_module_docs.py`.
Каждый модуль можно найти в клиенте командой `.find <слово>`, а показать, как он работает, — командой `.how <модуль>` или кнопкой «Инструкция строки» на вкладке «Гайд» в хабе.

Всего модулей: **177**. Разделы: **Display** (38), **Events** (1), **Utils** (80), **Visuals** (58).


## Раздел «Display»

### Accessibility

Режим для слабовидящих: крупный шрифт, высокий контраст, звуковые уведомления.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/AccessibilityModule.java`
* Настройки: Крупный шрифт (Boolean), Размер шрифта (Slider), Высокий контраст (Boolean), Звуковые уведомления (Boolean), Меньше анимаций (Boolean)

### Armor

Надетая броня: перетаскиваемый блок, авто верт./гориз. ориентация.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/ArmorModule.java`
* Настройки: переключатель включения и клавиша

### ArrayList

Перемещаемый список включённых модулей.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/ArrayListModule.java`
* Настройки: переключатель включения и клавиша

### ClickGui

Открывает клик-меню клиента.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/ClickGui.java`
* Настройки: переключатель включения и клавиша

### ClickGui Backdrops

Анимированные фоны клик-меню: градиент темы, частицы и оттенок обложки трека.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/ClickGuiBackdropsModule.java`
* Настройки: переключатель включения и клавиша

### Clock

Часы и дата в HUD: свой формат, секунды, дата. Виджет таскается в редакторе интерфейса.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/ClockModule.java`
* Настройки: Часы (Separator), 24-часовой формат (Boolean), Секунды (Boolean), Дата (Boolean), Иконка (Select)

### Cooldowns

Перемещаемый список перезарядки предметов.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/CooldownsModule.java`
* Настройки: переключатель включения и клавиша

### Custom Hotbar

Заменяет ванильный хотбар клиентским стеклянным дизайном.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/CustomHotbar.java`
* Настройки: переключатель включения и клавиша

### Custom Text

Свой текст в HUD с плейсхолдерами: %fps%, %ping%, %coords%, %world%, %speed%, %memory%, %time%, %date%.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/CustomTextModule.java`
* Настройки: Текст (Separator), Строки (Text), Размер (Slider), Фон (Boolean), Тень (Boolean), Иконка (Select)

### Fonts

Свой шрифт интерфейса (TTF/OTF) с проверкой на сглаженность и общий масштаб текста.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/FontsModule.java`
* Настройки: Шрифты (Separator), Обновить список (Button), Свой шрифт (Select), Масштаб текста (Slider)

### Hotkeys

Перемещаемый список назначенных клавиш модулей.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/HotKeysModule.java`
* Настройки: переключатель включения и клавиша

### HPFocus

Перемещаемое зазумленное окно хотбара, брони, еды и хп при низком ХП.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/HPFocus.java`
* Настройки: Масштаб (Number), Ширина захвата (Number), Высота захвата (Number), Порог ХП (Number)

### HUD Groups

Объединяет виджеты, стоящие в одной колонке, в один блок с общей подложкой.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/HudGroupsModule.java`
* Настройки: Группы виджетов (Separator), Направление (Mode), Допуск выравнивания (Slider), Отступ в группе (Slider), Поля подложки (Slider), Скругление (Slider), Плотность (Slider), Стеклянная подложка (Boolean), Обводка группы (Boolean), Разделители (Boolean)

### HUD Polish

Плавное появление HUD при входе в мир и магнитная сетка при перетаскивании виджетов.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/HudPolishModule.java`
* Настройки: Появление (Separator), Плавное появление (Boolean), Длительность, мс (Slider), Перетаскивание (Separator), Магнитная сетка (Boolean), Шаг сетки, px (Slider)

### HUD Profiles

Профили HUD одним нажатием: «PvP», «Анархия», «Стрим» и «Полный».

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/HudProfilesModule.java`
* Настройки: Профили (Separator), PvP (Button), Анархия (Button), Стрим (Button), Полный (Button)

### HUD Style

Кастомизация подложек HUD: прозрачность, скругление, обводка и тень — по отдельности.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/HudStyleModule.java`
* Настройки: Форма (Separator), Прозрачность, % (Slider), Доп. скругление, px (Slider), Обводка (Boolean), Толщина обводки (Slider), Мягкая тень (Boolean)

### Info

Статичная сводка на экране (bps, координаты, пинг, tps).

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/InfoModule.java`
* Настройки: переключатель включения и клавиша

### Interface

Общий стиль (стекло, свечение, цвета) для всех элементов интерфейса.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/InterfaceModule.java`
* Настройки: Скругление углов (Number), Размытие фона (Number), Преломление (Number), Градиент (Mode), Цвет клиента (Mode), Скорость радуги (Number), Разброс радуги (Number), Насыщенность радуги (Number), Цвет (Color), Второй цвет (Boolean), Цвет 2 (Color), Движение цвета (Boolean), Сила края (Number), Резкость края (Number), Свечение (Boolean), Яркость свечения (Number), Радиус свечения (Number), Иконки (Boolean), Перетаскивание (Mode), Тряска при перетаскивании (Boolean), Волны при перетаскивании (Boolean), Наклон при перетаскивании (Boolean)

### Inventory

Сетка предметов инвентаря с разделительными линиями.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/InventoryModule.java`
* Настройки: переключатель включения и клавиша

### InventoryPlus

Кастомный инвентарь: сортировка, фильтр с подсветкой, счётчики предметов и избранные ячейки.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/InventoryPlus.java`
* Настройки: Способ (Mode), Сортирую хотбар (Boolean), Звук сортировки (Boolean), Фильтр (Mode), Затемнять лишнее (Boolean), Сила затемнения, % (Slider), Цвет рамки (Color), Поиск по названию (Boolean), Латинские буквы и цифры (Boolean), Счётчики предметов (Boolean), Избранные ячейки (Boolean), Подсказка на ячейке (Boolean)

### KeyStrokes

Нажатия клавиш движения и кликов: перетаскиваемый блок.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/KeyStrokesModule.java`
* Настройки: Кнопки мыши (Boolean), CPS (Boolean), Пробел (Boolean)

### Language

Переключатель RU/EN для интерфейса клиента: экраны, подсказки, уведомления.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/LanguageModule.java`
* Настройки: Язык (Separator), Язык интерфейса (Mode)

### Minimap

Мини-карта: рельеф, руды, игроки, путевые точки, координаты и компас.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/MinimapModule.java`
* Настройки: Мини-карта (Separator), Размер, px (Slider), Радиус, блоки (Slider), Поворот (Mode), Скругление (Slider), Прозрачность, % (Slider), Обновление, тик (Slider), Отметки (Separator), Игроки (Boolean), Мобы (Boolean), Путевые точки (Boolean), Стрелка и компас (Boolean), Координаты (Boolean)

### Music Player

Музыкальный плеер: интернет-радио и треки из онлайн-каталога прямо в игре, без скачивания файлов.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/MusicPlayerModule.java`
* Настройки: Плеер (Separator), Библиотека (Button), Громкость (Slider), Виджет в HUD (Boolean), Плашка под музыку (Separator), Плашка в HUD (Boolean), Цвет плашки (Select), Обложка трека (Boolean), Полоса прогресса (Boolean), Эквалайзер (Boolean), Очередь (Separator), Повтор (Select), Перемешивание (Boolean), Нормализация громкости (Boolean), Поведение (Separator), Визуализатор (Select), Автопауза (Boolean), Таймер сна (Slider), Эфир в уведомлении (Boolean), Пропускать «не нравится» (Boolean), Переходы и звук (Separator), Кроссфейд (Select), Эквалайзер (Boolean), Пресет эквалайзера (Select), Полосы эквалайзера (Button), Плейлисты (Button), Клавиши плеера (Separator), Пауза / играть (Bind), Следующий трек (Bind), Предыдущий трек (Bind), Громче (Bind), Тише (Bind), Запуск (Separator), Возобновлять при входе (Boolean), Станция по умолчанию (Select), Клавиша меню (Bind), Клавиша поиска (Bind), Тост при смене трека (Boolean)

### Next Event

Плашка с ближайшим ивентом сервера: данные приходят по LiteApi.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/NextEventModule.java`
* Настройки: Следующий ивент (Separator), Виджет в HUD (Boolean), Обратный отсчёт (Boolean), Подробности (Boolean), Прятать без данных (Boolean), Обновление, сек (Slider)

### Notifications

Тосты-уведомления в правом нижнем углу.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/NotificationsModule.java`
* Настройки: переключатель включения и клавиша

### Perf Graph

График FPS и мониторинг статтеров: средний FPS, «1% low» и картинка за последние минуты.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/PerfGraphModule.java`
* Настройки: График (Separator), Виджет в HUD (Boolean), Окно, сек (Slider), Показывать 1% low (Boolean), Предупреждать о просадках (Boolean), Сбросить статистику (Button)

### Potions

Перемещаемый список активных эффектов.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/PotionsModule.java`
* Настройки: переключатель включения и клавиша

### Screen Tone

Тёмная и светлая тема окон: хаб, плеер, подсказки, окна клиента.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/ScreenToneModule.java`
* Настройки: Тема окон (Mode)

### Screenshots

Скриншоты: галерея в игре, уведомления о новых снимках, быстрый просмотр.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/ScreenshotsModule.java`
* Настройки: Галерея (Separator), Открыть галерею (Button), Клавиша галереи (Bind), Разметить последний снимок (Button), Клавиша разметки (Bind), Уведомления (Separator), Уведомлять о снимке (Boolean), Подсказка про F2 (Boolean), Авто-снимки (№110) (Separator), Снимок при смерти (Boolean), Снимок при достижении (Boolean), Закат и рассвет (Boolean), Новая местность (Boolean), Пауза между снимками, сек (Slider), Сообщать о снимке (Boolean)

### Stream Overlay

Слой для OBS: чат, счётчики, музыка и события на прозрачном фоне.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/StreamOverlayModule.java`
* Настройки: Чат (Boolean), Счётчики (Boolean), Музыка (Boolean), Лента событий (Boolean)

### TargetHud

Перемещаемый HUD с информацией о цели.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/TargetHudModule.java`
* Настройки: Режим (Mode), Режим цвета полосы хп (Mode), Броня (Boolean), Следовать (Boolean)

### Theme

Полные темы: тёмная, светлая, неоновая — вместе с ванильными экранами.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/ThemeLookModule.java`
* Настройки: Тема оформления (Separator), Тема (Mode), Перекрашивать текст (Boolean), Перекрашивать панели (Boolean), Ванильные экраны (Boolean), Неоновое свечение (Boolean)

### Theme Studio

Тема коротким кодом и редактор: цвета из обложек трека и подбор оттенков.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/ThemeStudioModule.java`
* Настройки: переключатель включения и клавиша

### Timer Alarms

Алармы таймеров: предупреждения об окончании зелий и кулдаунов предметов.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/TimerAlarms.java`
* Настройки: Окончание зелья (Boolean), Порог, сек (Slider), Только полезные (Boolean), Звук (Boolean), Сообщение в чат (Boolean), Всплывашка в HUD (Boolean)

### UI Scale

Свой масштаб интерфейса клиента для 4K и широких мониторов.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/UiScaleModule.java`
* Настройки: Масштаб клиента (Separator), Масштаб (Slider), Менять ClickGui (Boolean), Сброс при расстановке HUD (Boolean)

### Watermark

Водяной знак клиента: ник, время, fps, пинг и tps; три стиля и свой цвет.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/WatermarkModule.java`
* Настройки: Стиль (Separator), Вид (Mode), Позиция (Mode), Цвет (Mode), Свой цвет (Color), Данные (Separator), Ник (Boolean), Время (Boolean), FPS (Boolean), Пинг (Boolean), TPS (Boolean), Стрим (Separator), Прятать ник на стриме (Boolean)

### Waypoints

Путевые точки: столб света, метка на земле, список с расстоянием и команды.

* Раздел: `Display`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/WaypointsModule.java`
* Настройки: Путевые точки (Separator), Столб света (Boolean), Высота луча (Slider), Яркость луча, % (Slider), Метка на земле (Boolean), Список в HUD (Boolean), Точек в списке (Slider), Сохранить точку (Bind), Отмечать в чате (Boolean), Команда перехода (Text)


## Раздел «Events»

### Fight Review

Разбор боя после смерти: причина, урон, тотемы, советы.

* Раздел: `Events`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/FightReviewModule.java`
* Настройки: переключатель включения и клавиша


## Раздел «Utils»

### Achievements

Достижения за игру и функции клиента: 30 целей, очки, уровень, косметика в награду.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/AchievementsModule.java`
* Настройки: переключатель включения и клавиша

### Auto Accept

Автоматически принимает запросы на телепорт.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/AutoTpAccept.java`
* Настройки: переключатель включения и клавиша

### Auto Commands

Автоматически отправляет выбранные команды по кулдауну, по очереди.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/AutoCommands.java`
* Настройки: переключатель включения и клавиша

### Auto Duel

Автоматизирует запросы на дуэли.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/AutoDuel.java`
* Настройки: переключатель включения и клавиша

### Auto Resell

Перевыставляет предметы на аукционе.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/AutoResell.java`
* Настройки: переключатель включения и клавиша

### Auto Respawn

Автоматически возрождает после смерти.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/AutoRespawn.java`
* Настройки: переключатель включения и клавиша

### Auto Setup

Авто-настройки под железо: подбирает графику и профиль под ваш ПК.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/AutoSetupModule.java`
* Настройки: Предлагать при запуске (Boolean)

### Auto Swap

Свапает боевой предмет в оффхенд по бинду одним кликом, без открытия инвентаря.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/AutoSwap.java`
* Настройки: переключатель включения и клавиша

### AutoReconnect

Авто-переподключение: возврат на сервер после обрыва, чтение очереди и обратный отсчёт в HUD.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/AutoReconnect.java`
* Настройки: Попытки (Separator), Задержка, сек (Slider), Растущая задержка (Boolean), Максимум попыток (Slider), Очередь (Mode), Осторожность (Separator), Не входить после бана (Boolean), Уважать пароль и капчу (Boolean), Уведомлять (Boolean)

### AutoSprint

Удерживает спринт при движении вперёд

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/AutoSprint.java`
* Настройки: переключатель включения и клавиша

### Build Tier

Lite-профиль для слабых ПК: тяжёлые визуалы выключены, кэши урезаны.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/BuildTierModule.java`
* Настройки: переключатель включения и клавиша

### Cache Limits

Ограничивает кэш иконок, обложек и GIF, чтобы память не росла.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/CacheLimitsModule.java`
* Настройки: переключатель включения и клавиша

### Camera Settings

Плавный зум камеры и плавное переключение F5.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/CameraSettings.java`
* Настройки: переключатель включения и клавиша

### Chat Translate

Перевод чата встроенным словарём: рус ↔ англ, без интернета и внешних сервисов.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ChatTranslateModule.java`
* Настройки: переключатель включения и клавиша

### ClickPearl

Бросает эндер-жемчуг из хотбара по нажатию клавиши.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ClickPearl.java`
* Настройки: переключатель включения и клавиша

### Client Presets

Пресеты клиента в один клик: Анархия, PvP, Выживание, Стример, Минимум.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ClientPresetsModule.java`
* Настройки: Пресет (Mode)

### Client Sounds

Звуки клиента для действий модулей и меню.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ClientSounds.java`
* Настройки: переключатель включения и клавиша

### Clipboard

Недавние записи буфера обмена: координаты и сообщения всегда под рукой, клик возвращает их в буфер.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ClipboardModule.java`
* Настройки: Недавние (Separator), Виджет в HUD (Boolean), Хранить записей (Slider), Только координаты и ссылки (Boolean), Записывать в чат (Boolean)

### Cloud Configs

Версии настроек с откатом: снимки, история, выгрузка коротким кодом.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/CloudConfigsModule.java`
* Настройки: переключатель включения и клавиша

### Compat Check

Проверяет Java и Fabric, миксины и раскладку окон — до того, как что-то сломается.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/CompatCheckModule.java`
* Настройки: переключатель включения и клавиша

### Config Doctor

Проверка настроек: устаревшие записи, неизвестные настройки, неподходящие значения.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ConfigDoctorModule.java`
* Настройки: переключатель включения и клавиша

### Config Import

Импорт настроек других клиентов: клавиши и основные ползунки переезжают на ByAzen.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ConfigImportModule.java`
* Настройки: переключатель включения и клавиша

### Config Journal

История настроек: что, когда и на какое значение поменялось.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ConfigJournalModule.java`
* Настройки: переключатель включения и клавиша

### Config Migration

Переносит старые конфиги в текущий формат без потери настроек.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ConfigMigrationModule.java`
* Настройки: переключатель включения и клавиша

### ConfigBackups

Автобэкапы конфигов: копии при запуске, по таймеру, вручную и перед сменой профиля.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ConfigBackupModule.java`
* Настройки: Автоматически (Separator), Копия при запуске (Boolean), Копия по таймеру (Boolean), Минут между копиями (Slider), Перед профилями (Boolean), Сколько копий хранить (Slider), Вручную (Separator), Сделать копию (Button), Восстановить последнюю (Button), Показать список (Button), Открыть папку копий (Button)

### ConfigTransfer

Обмен конфигурацией: один файл на все настройки, QR-код профиля модуля и импорт в один клик.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ConfigTransferModule.java`
* Настройки: Окно (Separator), Открыть обмен конфигурацией (Button), Клавиша обмена (Bind), Файл (Separator), Сохранить файл конфигурации (Button), Импорт из последнего файла (Button), Скопировать код (Button), Осторожность (Separator), Копия перед импортом (Boolean), Писать в чат (Boolean)

### Coop Missions

Кооп-миссии: вместе пройти, добыть, победить — с наградой косметикой.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/CoopMissionsModule.java`
* Настройки: переключатель включения и клавиша

### Cosmetic Vote

Голосование за следующую косметику: свои голоса, коды для друзей и идеи.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/CosmeticVoteModule.java`
* Настройки: переключатель включения и клавиша

### Crystal Optimizer

Убирает задержку разбивания энд-кристаллов при удержании атаки.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/CrystalOptimizer.java`
* Настройки: переключатель включения и клавиша

### Daily Quests

Три задания на день по вашей же игре: время, путь, добыча, бои, музыка, чат. За серию — косметика.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/DailyQuestsModule.java`
* Настройки: переключатель включения и клавиша

### Death Coords

Выводит координаты смерти в чат.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/DeathCoords.java`
* Настройки: переключатель включения и клавиша

### Death History

История смертей: координаты, мир и время — с сохранением в конфиг.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/DeathHistoryModule.java`
* Настройки: Поведение (Separator), Уведомление (Boolean), Писать в чат (Boolean), Очистить историю (Button)

### Elytra Swap

Легитный свап элитры на нагрудник через открытие инвентаря.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ElytraSwap.java`
* Настройки: переключатель включения и клавиша

### Fast Start

Ускоряет запуск: тяжёлые интеграции готовятся при входе в мир, а не сразу.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/FastStartModule.java`
* Настройки: переключатель включения и клавиша

### FastExp

Убирает задержку при использовании бутылочек опыта.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/FastExp.java`
* Настройки: переключатель включения и клавиша

### Feature Vote

Голосование за фичи: список пожеланий, свои идеи и обмен кодами голосов.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/FeatureVoteModule.java`
* Настройки: За какую фичу (Mode)

### FPS Benchmark

Замер кадров до и после настроек: средний FPS, 1 % худших, минимум.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/FpsBenchmarkModule.java`
* Настройки: переключатель включения и клавиша

### Freelook

Свободный обзор камерой от третьего лица при зажатии клавиши

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/Freelook.java`
* Настройки: переключатель включения и клавиша

### Globals

Показывает метку клиента у других пользователей ByAzen.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/Globals.java`
* Настройки: переключатель включения и клавиша

### GraphicsPresets

Пресеты графики: «PvP 240 fps», «Баланс», «Кино 60 fps» и «Стрим» одним нажатием.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/GraphicsPresets.java`
* Настройки: Пресеты (Separator), Режим графики (Mode), Применить пресет (Button), Вернуть как было (Button), Автоматика (Separator), Применять при включении (Boolean), Писать в чат (Boolean), Сохранять в файл игры (Boolean)

### Handbook

Оффлайн-справочник: команды, клавиши, координаты и порталы, зелья, зачарования, тайминги.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/HandbookModule.java`
* Настройки: Окно (Separator), Открыть справочник (Button), Клавиша справочника (Bind), Команды в чат (Button), Порталы в чат (Button), Зелья в чат (Button), При входе (Separator), Подсказка при входе (Boolean)

### HandSwap

Смена ведущей руки одним нажатием клавиши, без меню.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/HandSwap.java`
* Настройки: переключатель включения и клавиша

### Hit Sound

Проигрывает звук при попадании.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/HitSound.java`
* Настройки: переключатель включения и клавиша

### HolyWorld Helper

Показывает зоны HolyWorld-предметов после использования.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/HolyWorldHelper.java`
* Настройки: переключатель включения и клавиша

### HUD API

Публичный API: сторонние моды рисуют свои элементы в HUD ByAzen.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/HudApiModule.java`
* Настройки: переключатель включения и клавиша

### IRC

Общий чат клиента через локальный мост. Используйте .irc для общения.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/Irc.java`
* Настройки: переключатель включения и клавиша

### ItemScroller

Прокручивает подходящие предметы в инвентаре с клавишами-модификаторами.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ItemScroller.java`
* Настройки: переключатель включения и клавиша

### Logs

Логи клиента: путь к логу, копирование последних строк для багрепорта.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/LogsModule.java`
* Настройки: Багрепорты (Separator), Строк для копирования (Slider), Сводка в чат (Boolean), Скопировать лог (Button), Путь к логам (Button)

### Mobile Companion

Страница для телефона: статистика, координаты, музыка и лента событий.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/MobileCompanionModule.java`
* Настройки: Порт (Slider)

### Mod Detect

Находит Iris, OptiFine, Sodium и другие моды рядом и предупреждает о конфликтах.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ModDetectModule.java`
* Настройки: переключатель включения и клавиша

### ModsManager

Менеджер модов: список, клавиши с модом-владельцем, поиск по содержимому и конфликты.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ModsManager.java`
* Настройки: Окно (Separator), Открыть менеджер модов (Button), Отчёты (Separator), Показать сводку (Button), Предупреждать о конфликтах (Boolean), Искать внутри файлов (Boolean)

### Module Guard

Ловит ошибки модулей, показывает причину и отключает виновника.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ModuleGuardModule.java`
* Настройки: переключатель включения и клавиша

### Notes

Заметки с координатами: команда note <текст>, список с копированием координат в один клик.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/NotesModule.java`
* Настройки: Заметки (Separator), Хранить заметок (Slider), Мир в списке (Boolean), Последняя заметка (Button), Удалить все (Button)

### Optimization

Поднимает FPS: куллинг сущностей, частиц и тайлов, дистанция, облака.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/Optimization.java`
* Настройки: переключатель включения и клавиша

### Party

Метки для участников Party. Нажмите бинд, чтобы поставить ping.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/Party.java`
* Настройки: переключатель включения и клавиша

### Private Data

Шифрует локальные данные клиента и возвращает их обратно по кнопке.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/PrivateDataModule.java`
* Настройки: переключатель включения и клавиша

### Profiler

Собирает причины просадок FPS и сохраняет отчёт при выключении.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/Profiler.java`
* Настройки: переключатель включения и клавиша

### Quick Chat

Быстрые ответы и шаблоны команд по клавишам: ответ в ЛС, заготовки, частые команды.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/QuickChatModule.java`
* Настройки: Быстрый ответ (Separator), Текст ответа (Text), Ответ в ЛС (Boolean), Клавиша ответа (Bind), Шаблоны (Separator), Шаблон 1 (Text), Клавиша 1 (Bind), Шаблон 2 (Text), Клавиша 2 (Bind), Шаблон 3 (Text), Клавиша 3 (Bind)

### Quick Hide

Прячет весь HUD по клавише (по умолчанию H) и возвращает его обратно.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/QuickHideModule.java`
* Настройки: Клавиша (Bind), Уведомление (Boolean)

### Reminders

Планировщик напоминаний: «через 30 минут выпить воды», команда remind <минуты> <текст>.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/RemindersModule.java`
* Настройки: Поведение (Separator), Тост (Boolean), Писать в чат (Boolean), Звук (Boolean), Готовые напоминания (Separator), Вода (Button), Размяться (Button), Проверить AFK (Button), Сбросить (Button)

### Render Profiler

Замеряет нагрузку модулей и выключает те, что съедают кадры.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/RenderProfilerModule.java`
* Настройки: переключатель включения и клавиша

### ResourcePackReview

Ревизор ресурспаков: версия, размер, крупные текстуры, перекрытия и ошибки упаковки.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ResourcePackReview.java`
* Настройки: Проверка (Separator), Проверять при входе (Boolean), Предел размера, МБ (Slider), Следить за текстурами (Boolean), Вручную (Separator), Проверить сейчас (Button), Краткий итог (Button), Открыть папку ресурспаков (Button)

### Scripts

Свои правила: «если HP ниже 8 — напомни». Безопасный мини-язык и магазин наборов.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ScriptsModule.java`
* Настройки: переключатель включения и клавиша

### Season Events

Сезоны клиента: своя палитра, поздравление и набор косметики в награду.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/SeasonEventsModule.java`
* Настройки: переключатель включения и клавиша

### Server Profiles

Профили модулей и темы под каждый сервер: своё окружение на знакомых адресах.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ServerProfilesModule.java`
* Настройки: переключатель включения и клавиша

### ServerBinds

Профили клавиш под сервер: свой набор биндов для каждого адреса, общий — для остальных.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ServerBinds.java`
* Настройки: Профили клавиш (Separator), Применять при входе (Boolean), Запоминать новый сервер (Boolean), Только удалённые сервера (Boolean), Писать в чат (Boolean), Сохранить профиль сервера (Button), Применить профиль сервера (Button), Сохранить общий профиль (Button), Забыть профиль сервера (Button)

### ServerCheck

Проверка серверов: пинг, игроки, версия, избранные адреса и быстрый вход.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ServerCheck.java`
* Настройки: Окно (Separator), Открыть проверку серверов (Button), При подключении (Separator), Сообщать состояние (Boolean), Проверять избранные (Boolean)

### Session Stats

Статистика сессии: время, дистанция, блоки, убийства — в HUD.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/SessionStatsModule.java`
* Настройки: Что показывать (Separator), Время в игре (Boolean), Пройдено (Boolean), Добыто блоков (Boolean), Убито мобов (Boolean), Сбросить (Button)

### Shulker Preview

Показывает содержимое шалкера. SHIFT — заглянуть, CTRL — закрепить.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/ShulkerPreview.java`
* Настройки: переключатель включения и клавиша

### Smart Hints

Ненавязчивые подсказки по ситуации: мало FPS, мало памяти, нет копии настроек, вышло обновление.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/SmartHintsModule.java`
* Настройки: Режим (Mode), Писать в чат (Boolean)

### Stats Export

Выгружает статистику сессии в CSV и markdown: игра, бои, музыка, настройки.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/StatsExportModule.java`
* Настройки: переключатель включения и клавиша

### Streamer Mode

Скрывает ваш ник, ники друзей/пати и координаты в отображаемом тексте.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/StreamerMode.java`
* Настройки: переключатель включения и клавиша

### TalTracker

Сообщает в чат когда у противника лопается тотем и был ли он зачарован.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/TalTracker.java`
* Настройки: переключатель включения и клавиша

### Tape Mouse

Автокликер выбранной кнопкой мыши с настраиваемой скоростью.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/TapeMouse.java`
* Настройки: переключатель включения и клавиша

### Test Settings

Отладочный модуль: каждый тип настройки с коротким и длинным названием.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/TestSettings.java`
* Настройки: переключатель включения и клавиша

### Tutorial

Интерактивный тур по клиенту: что где лежит и как этим пользоваться.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Interface/TutorialModule.java`
* Настройки: Подсказка при входе (Boolean)

### Updater

Автообновление клиента: проверка версии, скачивание и перезапуск в один клик.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/UpdaterModule.java`
* Настройки: Проверять при запуске (Boolean)

### Voice Commands

Команды своим голосом: координаты, свет, карта, музыка. Микрофон — только по нажатию.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/VoiceCommandsModule.java`
* Настройки: переключатель включения и клавиша

### Web Dashboard

Дашборд в браузере: графики FPS и памяти, статистика, рейтинг сессий.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/WebDashboardModule.java`
* Настройки: переключатель включения и клавиша

### Why Lag

Почему лагает: чанки, сущности, шейдеры и модули — простыми словами.

* Раздел: `Utils`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Utils/WhyLagModule.java`
* Настройки: переключатель включения и клавиша


## Раздел «Visuals»

### Ambience

Изменяет время, погоду и атмосферу мира.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Ambience.java`
* Настройки: переключатель включения и клавиша

### Animated Capes

Плащи ByAzen с физикой: градиент, ветер, вес ткани и пресеты наборов.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/AnimatedCapes.java`
* Настройки: переключатель включения и клавиша

### Anti Aliasing

Сглаживает края картинки пост-процессом: четыре режима качества и субпиксельный режим.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/AntiAliasing.java`
* Настройки: Качество (Mode), Порог края, % (Slider), Сила, % (Slider), Субпиксельная мягкость, % (Slider), Резкость, % (Slider), Не трогать интерфейс (Boolean)

### Aspect Ratio

Изменяет соотношение сторон проекции мира.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/AspectRatio.java`
* Настройки: переключатель включения и клавиша

### Better Minecraft

Небольшие визуальные улучшения ванильного рендера.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/BetterMinecraft.java`
* Настройки: переключатель включения и клавиша

### Block Highlight

Градиентный контур вокруг руд, контейнеров, станций, порталов и своих блоков.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/BlockHighlight.java`
* Настройки: Что подсвечивать (Separator), Категории (MultiSelect), Свои блоки (Text), Поиск (Separator), Радиус поиска (Slider), Максимум блоков (Slider), Слоёв за тик (Slider), Контур (Separator), Цвет 1 (Color), Цвет 2 (Color), Анимация (Mode), Скорость (Slider), Толщина (Slider), Заливка (Boolean), Прозрачность заливки, % (Slider)

### Block Overlay

Рисует кастомную анимацию разрушения блока.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/BlockOverlay.java`
* Настройки: переключатель включения и клавиша

### Block Shards

Осколки своего цвета при разрушении блока: мягко, без пиксельных текстур.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/BlockShards.java`
* Настройки: Осколки (Separator), Количество (Slider), Размер, % (Slider), Время жизни, мс (Slider), Разлёт, % (Slider), Гравитация (Slider), Вид (Separator), Частица (Mode), Цвет блока (Boolean), Свой цвет (Color), Пыль (Boolean), Блеск руды (Boolean)

### Bloom

Мягкое свечение вокруг лавы, порталов, кристаллов, руд и источников света.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Bloom.java`
* Настройки: Свечение (Separator), Сила, % (Slider), Радиус, % (Slider), Пульс (Slider), Искры (Boolean), Поиск (Separator), Радиус поиска (Slider), Максимум блоков (Slider), Слоёв за тик (Slider), Что светится (Separator), Категории (MultiSelect), Цвет лавы (Color), Цвет порталов (Color), Цвет кристаллов (Color), Цвет руд (Color), Руды в цвет блока (Boolean), Цвет источников света (Color), Цвет огня (Color)

### Camera Plus

Плавное следование взгляда, отдача при ударе и кинематографический режим камеры.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/CameraPlus.java`
* Настройки: Плавное следование (Boolean), Сила сглаживания, % (Slider), Скорость догона (Slider), Крен в движении (Boolean), Сила крена, % (Slider), Отдача при ударе (Boolean), Сила отдачи, % (Slider), Затухание отдачи (Slider), Кинематограф (Boolean), Скорость кадра (Slider), Дыхание зума, % (Slider)

### China Hat

Рисует китайскую шляпу-конус на игроках.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/ChinaHat.java`
* Настройки: переключатель включения и клавиша

### Cinematic

Кино-режим: виньетка, полосы кадра, лёгкое зерно и плавные переходы.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Cinematic.java`
* Настройки: Кадр (Separator), Виньетка (Boolean), Сила виньетки, % (Slider), Полосы кино (Boolean), Высота полос, px (Slider), Плёнка (Separator), Зерно плёнки (Boolean), Плотность зерна, % (Slider), Управление (Separator), Кино-режим (Bind), Плавность (Slider)

### Cosmetics 3D

Каталог косметики ByAzen: наборы «Зима», «Неон» и «Космос», 3D-превью и настройка посадки аксессуаров.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Cosmetics3D.java`
* Настройки: переключатель включения и клавиша

### Crosshair

Изменяет внешний вид прицела.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Crosshair.java`
* Настройки: переключатель включения и клавиша

### Custom Particles

Свои частицы мира: снежинки, листья, искры, пыль и пузыри со своей физикой.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/CustomParticles.java`
* Настройки: Авто по окружению (Boolean), Стили (MultiSelect), Частота, в секунду (Slider), Радиус (Slider), Высота над землёй (Slider), Размер, % (Slider), Время жизни, мс (Slider), Цвет снега (Color), Цвет листвы (Color), Цвет искр (Color), Цвет пыли (Color), Цвет пузырей (Color), Пыль в цвет блока (Boolean)

### Custom Pet

Клиентский питомец-компаньон рядом с игроком

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/CustomPet.java`
* Настройки: переключатель включения и клавиша

### Custom Swords

Заменяет модели ванильных мечей.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/CustomSwords.java`
* Настройки: переключатель включения и клавиша

### Customization

Кастомизация скина: головной убор, крылья и питомец.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Customization.java`
* Настройки: переключатель включения и клавиша

### Damage Effects

Мягкая виньетка урона, пульс при низком здоровье и указатель направления.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/DamageEffects.java`
* Настройки: Виньетка (Separator), Мягкая виньетка (Boolean), Плотность, % (Slider), Скорость проявления (Slider), Цвет (Color), Низкое здоровье (Separator), Пульс на сердце (Boolean), Порог, % (Slider), Период пульса, сек (Slider), Направление урона (Separator), Показывать, откуда прилетело (Boolean), Время показа, мс (Slider), Только от игроков (Boolean)

### Death Effects

Разлёт частиц и мягкая вспышка при смерти моба или игрока.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/DeathEffects.java`
* Настройки: Разлёт (Separator), Количество (Slider), Размер, % (Slider), Время жизни, мс (Slider), Разлёт, % (Slider), Гравитация (Slider), Частица (Mode), Вид (Separator), Цвет моба (Boolean), Цвет (Color), Вспышка (Boolean), Цвет вспышки (Color), Кому (Separator), Цели (MultiSelect)

### Emotions

Круговое меню эмоций с анимацией модели игрока.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Emotions.java`
* Настройки: переключатель включения и клавиша

### FakePlayer

Спавнит клиентского фейк-игрока для битья/теста.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/FakePlayer.java`
* Настройки: переключатель включения и клавиша

### Fog Blur

Размывает удалённые пиксели мира по глубине, создавая эффект тумана.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/FogBlur.java`
* Настройки: переключатель включения и клавиша

### Footprints

Светящиеся следы под ногами: размер, шаг, цвет и время жизни.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Footprints.java`
* Настройки: переключатель включения и клавиша

### Graffiti

Свои граффити в мире: векторный редактор, PNG-слои, свечение и плавное появление.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Graffiti.java`
* Настройки: Файл рисунка (String), Клавиша установки (Bind), Сменить рисунок (Button), Поставить по прицелу (Button), Редактор граффити (Button), Снять все наклейки (Button), Размер, блоки (Slider), Поворот, ° (Slider), Прозрачность, % (Slider), Свечение (Boolean), Плавное появление (Boolean), Дальность, блоков (Slider)

### Hit Bubbles

Искажает пространство волной в месте удара.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/HitBubbles.java`
* Настройки: переключатель включения и клавиша

### Hit Color

Меняет цвет игрока при получении урона.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/HitColor.java`
* Настройки: переключатель включения и клавиша

### Hit Particles

Добавляет красивые частицы от ударов.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/HitParticles.java`
* Настройки: переключатель включения и клавиша

### Hitboxes

Окрашивает хитбоксы сущностей.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Hitboxes.java`
* Настройки: переключатель включения и клавиша

### HP Counter

Показывает урон и лечение видимых игроков.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/HpCounter.java`
* Настройки: переключатель включения и клавиша

### Item Physics

Добавляет физику падения выброшенным предметам.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/ItemPhysics.java`
* Настройки: переключатель включения и клавиша

### ItemHighlight

Подсвечивает фон ячеек для нужных предметов.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/ItemHighlight.java`
* Настройки: переключатель включения и клавиша

### Jump Circle

Рисует эффект круга при прыжке.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/JumpCircle.java`
* Настройки: переключатель включения и клавиша

### Kill Effect

Эффект сканирования при убийстве с виньеткой, волной и звуками.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/KillEffect.java`
* Настройки: переключатель включения и клавиша

### Kill Effect 3D

3D-эффекты убийства: модели, анимации и выбор эффекта. Пока модуль выключен, эффекты не появляются.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/KillEffect3D.java`
* Настройки: переключатель включения и клавиша

### Kill FX

Свои килл-эффекты ByAzen: космос, дракон, вода, гроза и аврора — геометрия и частицы.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/KillFX.java`
* Настройки: Эффект (Mode), Длительность, мс (Slider), Размер, % (Slider), Плотность частиц, % (Slider), Кольца и лучи (Boolean), Вспышка (Boolean), Свои цвета (Boolean), Цвет 1 (Color), Цвет 2 (Color), Мои убийства (Boolean), Чужие смерти (Boolean)

### NameTags

Рисует таблички с именами только над видимыми игроками.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/NameTags.java`
* Настройки: переключатель включения и клавиша

### Night Vision

Ярче видно ночью и в шахтах: плавный переход, без правки ванильной гаммы.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/NightVision.java`
* Настройки: Яркость (Separator), Сила яркости (Slider), Плавный переход (Boolean), Уважать зелье (Boolean), Ограничения (Separator), Только ночью (Boolean), Не в аду и краю (Boolean), Скорость перехода (Slider)

### No Render

Скрывает выбранные визуальные эффекты.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/NoRender.java`
* Настройки: переключатель включения и клавиша

### Pet Style

Оформление питомца: цвет, размер, имя и аксессуары, а также мини-игры с опытом и настроением.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/PetStyle.java`
* Настройки: переключатель включения и клавиша

### PetPlus

Питомцы 2.0: реакции на бой, эмоции, звуки и живые анимации питомца.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/PetPlus.java`
* Настройки: Реакции на бой (Boolean), Реакция на тотем (Boolean), Реакция на смерть (Boolean), Реакция на прыжок (Boolean), Эмоции (Boolean), Игры при простое (Boolean), Частота, с (Slider), Яркость эмоций, % (Slider), Звуки питомца (Boolean), Громкость, % (Slider), Акцент в эмоциях (Boolean), Искры вместо свечения (Boolean)

### Predict

Показывает траекторию и место падения летящих снарядов.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Predictions.java`
* Настройки: переключатель включения и клавиша

### Profile Covers

Анимированные GIF-обложки профиля: выбор, сезонный режим, карточка игрока.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/ProfileCoversModule.java`
* Настройки: переключатель включения и клавиша

### ProjectileHelper

Показывает траекторию полёта снарядов

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/ProjectileHelper.java`
* Настройки: переключатель включения и клавиша

### Pulse Cosmetics

Косметика Pulse: меню выбора, чужая косметика по категориям, питомец от первого лица и граффити.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/PulseCosmetics.java`
* Настройки: переключатель включения и клавиша

### SeeInvisible

Позволяет видеть невидимых игроков и сущностей.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/SeeInvisible.java`
* Настройки: переключатель включения и клавиша

### Self Tag

Показывает ванильную табличку с ником над вами (от 3-го лица).

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/SelfTag.java`
* Настройки: переключатель включения и клавиша

### Shader ESP

Подсвечивает сущности шейдерным контуром и свечением.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/GlowEsp.java`
* Настройки: переключатель включения и клавиша

### ShaderHands

Шейдерные руки: стекло (чекбокс) + свечение — Старый (глоу/обводка) или Новый (пламя).

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/ShaderHands.java`
* Настройки: переключатель включения и клавиша

### Shadows

Мягкие контактные тени под игроками, мобами и предметами.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Shadows.java`
* Настройки: Тени (Separator), Размер, % (Slider), Плотность, % (Slider), Мягкость края (Slider), Кому рисовать (Separator), Цели (MultiSelect), Затухание, блоков (Slider), Цвет (Color)

### Swing Animation

Кастомные анимации взмаха от первого лица.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/SwingAnimation.java`
* Настройки: переключатель включения и клавиша

### Target ESP

Подсвечивает последнюю атакованную цель.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/TargetESP.java`
* Настройки: переключатель включения и клавиша

### Trails

Оставляет след за вами (свечение или энергетические осколки).

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Trails.java`
* Настройки: переключатель включения и клавиша

### Trails 2.0

Завиток за элитрами, шлейф стрел, снарядов и предметов.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/TrailsPlus.java`
* Настройки: Завиток от элитр (Boolean), Шлейф стрел и трезубцев (Boolean), Шлейф снарядов (Boolean), След предметов (Boolean), Плотность (Slider), Время жизни, мс (Slider), Размер, % (Slider), Разброс (Slider), Частица (Mode), Цвет элитр (Color), Цвет стрел (Color), Цвет снарядов (Color), Цвет предметов (Color)

### Trajectories

Куда растечётся вода, что толкнёт поршень, куда пойдёт редстоун и как упадёт песок.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Trajectories.java`
* Настройки: Что показывать (MultiSelect), Радиус поиска (Slider), Максимум блоков (Slider), Слоёв за тик (Slider), Прозрачность, % (Slider), Пульс (Boolean), Цвет воды (Color), Цвет лавы (Color), Цвет поршней (Color), Цвет редстоуна (Color), Цвет песка (Color)

### ViewModel

Дважды кликните по руке в чате, чтобы перетащить её, и колёсиком измените размер.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/ViewModel.java`
* Настройки: переключатель включения и клавиша

### Wings

Косметические крылья с физикой ткани: колышутся от ветра и раскрываются в полёте.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/Wings.java`
* Настройки: Форма (Mode), Размер, % (Slider), Размах, % (Slider), Провис, % (Slider), Ветер (Slider), Взмах (Slider), Раскрывать в падении (Boolean), Складывать на земле (Boolean), Основной цвет (Color), Цвет кончиков (Color), Свечение акцента (Boolean), Искры (Boolean), Себе (Boolean), Другим игрокам (Boolean), Только друзьям (Boolean)

### World Particles

Рендерит красивые частицы вокруг вас.

* Раздел: `Visuals`
* Файл: `src/main/java/rtx/byazen/api/modules/impl/Visuals/WorldParticles.java`
* Настройки: переключатель включения и клавиша
