# Отчёт CI по сборке ByAzen

Коммит: 1fb0e7741c084af2c38219962a46101bc0394b69

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
файл:             /home/runner/work/ByAzen/ByAzen/build/libs/ByAzen-1.5.0.jar
mod id:           byazen
версия в jar:     1.5.0 (ожидается 1.5.0)
объявлено jar:    6
лежит в jar:      6
entrypoints:      client,main
миксины:          109 общих / 52 клиентских
VERIFY: OK
```
