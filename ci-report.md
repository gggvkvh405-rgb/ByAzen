# Отчёт CI по сборке ByAzen

Коммит: 15f82c84a4aed86e7f4a3d4bc7455a0868ea42ae

## Аудио-тракт (MP3 -> PCM, как в игре)
```
frames   = 12
samples  = 27648
rate     = 44100 Hz
channels = 2
длит.    = 0.313 c
PCM      = 55296 байт
peak     = 0.0001
rms      = 0.0869
silent   = 27469 сэмплов нуля
AUDIO SMOKE: OK
```

## Аудио-тракт (OGG Vorbis -> PCM, идея №21)
```
файл     = src/main/resources/assets/byazen/sounds/charmsfxpack/echocharmmain.ogg
кадров   = 151
сэмплов  = 291482
rate     = 48000 Hz
channels = 2
длит.    = 3.036 c
PCM      = 145741 байт
peak     = 0.5030
rms      = 0.1079
silent   = 324 сэмплов нуля
поток    = дочитан до конца
минимум  = 2.00 c
OGG SMOKE: OK
```

## Проверка собранного jar
```
файл:             build/libs/ByAzen-1.9.4.jar
mod id:           byazen
версия в jar:     1.9.4 (ожидается 1.9.4)
объявлено jar:    6
лежит в jar:      6
entrypoints:      client,main
миксины:          109 общих / 53 клиентских
VERIFY: OK
```

## Lite-сборка (идея №187)
```
-rw-r--r-- 1 runner runner 80536966 Sep 23 13:32 build/libs/ByAzen-lite-1.9.4.jar
маркер внутри jar: lite
```

## Манифест обновлений (идеи №206–№208)
```
{
  "version": "1.9.4",
  "jar": "ByAzen-1.9.4.jar",
  "url": "https://raw.githubusercontent.com/gggvkvh405-rgb/ByAzen/arena/01a0bf74-byazen/ByAzen-1.9.4.jar",
  "urls": [
    "https://raw.githubusercontent.com/gggvkvh405-rgb/ByAzen/arena/01a0bf74-byazen/ByAzen-1.9.4.jar"
  ],
  "sha256": "faea88f62dd3f69c1151bb3dc5711c1ccf6ceaf6f130435df042f297edb021ba",
  "size": 81091388,
  "released": "2026-09-23",
  "minecraft": "1.21.11",
  "loader": "0.19.3",
  "java": 21,
  "notes": "Журнал старта с «хлебными крошками». Клиент пишет шаг инициализации в лог",
  "generated": "CI"
}
```

Это то, что увидит клиент при проверке обновлений (модуль Updater).
