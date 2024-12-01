# ElyTools - плагин с фани функционалом для майнкрафт сервера

## Функционал

Данный плагин предоставляет отображение ника при нажатии по игроку, изменение описания игрока отображаемого при нажатии
на него

Также предоставляет команду `/size`, которая может изменять размер игрока

Описание имеет два режима отображения:

* ActionBar - показывает только ник игрока

* Chat - показывает описание игрока вместе с ником

## Команды

`/elytools` - перезагрузка плагина

`/description toggle` - переключить режим отображения описания

`/description clear` - очистить описание

`/description set` - поставить описание

`/size <размер>` - изменение размера

## Конфигурация

```yaml
# Модуль size
size:
  # Минимальный размер
  sizeMin: 0.7
  # Максимальный размер
  sizeMax: 1.1
  # Шаг между размерами
  sizeStep: 0.1
  # Как это работает?
  # Задается минимальный и максимальный размер например 0.6 и 1.2 с шагом 0.2
  # Тогда доступные игроку размеры будут [0.6, 0.8, 1.0, 1.2]

  messages:
    usage: "Использование: /size <размер>"
    failMessage: "Введите одно из предложенных значений!"
    successMessage: "Ваш размер изменён на %size%"
    invalidSize: "Недопустимый размер! Выберите одно из: "

  needPermission: true

# Модуль PlayerClick
# Форматирование с помощью https://docs.advntr.dev/minimessage/format.html
playerClick:
  messages:
    noDescription: "Введите описание!"
    descriptionUpdated: "Ваше описание обновлено!"
    descriptionTooLong: "Описание не должно превышать 16 символов!"
    clickPlayer: "Вас толкнул %player%"
    clickingPlayer: "Вы толкнули %player%\nОписание: %description%"
    clickWithoutDesc: "Вы толкнули %player%"
    clickingPlayerNoDescription: "%player%"
    invalidArgument: "Неизвестная команда. Попробуйте: toggle, set, clear"

# Модуль описание для PlayerClick
description:
  messages:
    enabled: "Описание включено."
    disabled: "Описание отключено."

# Данные для подключения к базе данных
# url для базы данных задается в виде "jdbc:driver://host:port/database"
database:
  url: ""
  user: ""
  password: ""

# Общее сообщение когда у игрока нет прав
noPermission: "У тебя нет права использовать эту команду!"
# Префикс плагина
prefix: ""
```

## Права

`elytools.reload` - использование `/elytools` для перезагрузки плагина

`elytools.size` - использование команды `/size`

`elytools.description` - использование команды `/description`