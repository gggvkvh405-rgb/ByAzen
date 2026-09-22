# Отчёт CI по сборке ByAzen

Коммит: 835c9abb352600fb87ec0228004fda79d59b7ff8

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

## Проверка собранного jar
```
файл:             build/libs/ByAzen-1.9.2.jar
mod id:           byazen
версия в jar:     1.9.2 (ожидается 1.9.2)
объявлено jar:    6
лежит в jar:      6
entrypoints:      client,main
миксины:          109 общих / 53 клиентских
VERIFY: OK
```

## Lite-сборка (идея №187)
```
-rw-r--r-- 1 runner runner 80404747 Sep 22 16:32 build/libs/ByAzen-lite-1.9.2.jar
маркер внутри jar: lite
```

## Манифест обновлений (идеи №206–№208)
```
{
  "version": "1.9.2",
  "jar": "ByAzen-1.9.2.jar",
  "url": "https://raw.githubusercontent.com/gggvkvh405-rgb/ByAzen/arena/01a0bf74-byazen/ByAzen-1.9.2.jar",
  "urls": [
    "https://raw.githubusercontent.com/gggvkvh405-rgb/ByAzen/arena/01a0bf74-byazen/ByAzen-1.9.2.jar"
  ],
  "sha256": "f8a720dbb36c9d1fe65bad7624ae331383d605ffe6f2982b457bcd9bc7bbc455",
  "size": 80958241,
  "released": "2026-09-22",
  "minecraft": "1.21.11",
  "loader": "0.19.3",
  "java": 21,
  "notes": "Манифест обновлений (№206). В репозитории появился update.json, но главное — он больше не",
  "generated": "CI"
}
```

Это то, что увидит клиент при проверке обновлений (модуль Updater).
