# Отчёт CI по сборке ByAzen

Коммит: 110e16eb80fe7fe4aa78916f49a0d2acc5eebc93

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
файл:             build/libs/ByAzen-1.9.1.jar
mod id:           byazen
версия в jar:     1.9.1 (ожидается 1.9.1)
объявлено jar:    6
лежит в jar:      6
entrypoints:      client,main
миксины:          109 общих / 53 клиентских
VERIFY: OK
```

## Lite-сборка (идея №187)
```
-rw-r--r-- 1 runner runner 80400654 Sep 22 15:32 build/libs/ByAzen-lite-1.9.1.jar
маркер внутри jar: lite
```
