# Отчёт CI по сборке ByAzen

Коммит: 08fed7c2c698980da73810e143417b1420b69219

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
файл:             /home/runner/work/ByAzen/ByAzen/build/libs/ByAzen-1.3.2.jar
mod id:           byazen
версия в jar:     1.3.2 (ожидается 1.3.2)
объявлено jar:    6
лежит в jar:      6
entrypoints:      client,main
миксины:          109 общих / 51 клиентских
VERIFY: OK
```
