# Отчёт CI по сборке ByAzen

Коммит: b215b1351cca7409450798a42b75944c9943c1cb

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
файл:             build/libs/ByAzen-1.9.5.jar
mod id:           byazen
версия в jar:     1.9.5 (ожидается 1.9.5)
объявлено jar:    6
лежит в jar:      6
entrypoints:      client,main
миксины:          109 общих / 53 клиентских
VERIFY: OK
```

## Lite-сборка (идея №187)
```
-rw-r--r-- 1 runner runner 80537025 Sep 23 15:25 build/libs/ByAzen-lite-1.9.5.jar
маркер внутри jar: lite
```

## Манифест обновлений (идеи №206–№208)
```
{
  "version": "1.9.5",
  "jar": "ByAzen-1.9.5.jar",
  "url": "https://raw.githubusercontent.com/gggvkvh405-rgb/ByAzen/arena/01a0bf74-byazen/ByAzen-1.9.5.jar",
  "urls": [
    "https://raw.githubusercontent.com/gggvkvh405-rgb/ByAzen/arena/01a0bf74-byazen/ByAzen-1.9.5.jar"
  ],
  "sha256": "b5ced490ed4ca2be40c4dc2bb4d592c485956868a4759dfadc7486f810b081bd",
  "size": 81091447,
  "released": "2026-09-23",
  "minecraft": "1.21.11",
  "loader": "0.19.3",
  "java": 21,
  "notes": "Главное: исправлено падение Mixin transformation of net.minecraft.class_636 failed …",
  "generated": "CI"
}
```

Это то, что увидит клиент при проверке обновлений (модуль Updater).
