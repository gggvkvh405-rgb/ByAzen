# Отчёт CI по сборке ByAzen

Коммит: 1b1dee6e2919fb8aef07c489f2524bed15dc97c1

## Аудио-тракт (MP3 -> PCM, как в игре)
```
frames   = 4
samples  = 9216
rate     = 44100 Hz
channels = 2
длит.    = 0.104 c
PCM      = 18432 байт
peak     = 0.0001
rms      = 0.0744
silent   = 9171 сэмплов нуля
AUDIO SMOKE: OK
```

## Проверка собранного jar
```
файл:             /home/runner/work/ByAzen/ByAzen/build/libs/ByAzen-1.3.0.jar
mod id:           byazen
версия в jar:     1.3.0 (ожидается 1.3.0)
объявлено jar:    6
лежит в jar:      6
entrypoints:      client,main
миксины:          109 общих / 51 клиентских
VERIFY: OK
```
