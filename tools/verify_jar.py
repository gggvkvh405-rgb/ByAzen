#!/usr/bin/env python3
"""Проверка собранного jar в CI.

Смотрит то, из-за чего клиент однажды уже не загрузился:
  * версия в fabric.mod.json должна совпадать с gradle.properties;
  * список "jars" в fabric.mod.json должен совпадать с реальными вложенными jar
    (объявленный, но отсутствующий jar = Fabric не запускает мод);
  * все ключевые классы и вложенные моды на месте.
"""

import io
import json
import os
import re
import sys
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))


def expected_version():
    with open(os.path.join(ROOT, "gradle.properties"), encoding="utf-8") as handle:
        for line in handle:
            if line.startswith("mod_version="):
                return line.split("=", 1)[1].strip()
    return "0"


def main(archive):
    problems = []
    with zipfile.ZipFile(archive) as jar:
        names = jar.namelist()
        mod = json.loads(jar.read("fabric.mod.json").decode("utf-8"))
        version = expected_version()

        print("файл:            ", archive)
        print("mod id:          ", mod.get("id"))
        print("версия в jar:    ", mod.get("version"), "(ожидается %s)" % version)
        if mod.get("id") != "byazen":
            problems.append("mod id не byazen")
        if mod.get("version") != version:
            problems.append("версия в fabric.mod.json не совпадает с gradle.properties")

        declared = set()
        for entry in mod.get("jars", []):
            declared.add(entry.get("file") if isinstance(entry, dict) else entry)
        physical = {n for n in names if n.startswith("META-INF/jars/") and n.endswith(".jar")}
        print("объявлено jar:   ", len(declared))
        print("лежит в jar:     ", len(physical))
        missing = sorted(declared - physical)
        extra = sorted(physical - declared)
        if missing:
            problems.append("объявлены, но отсутствуют: %s" % ", ".join(missing))
        if extra:
            problems.append("лежат, но не объявлены: %s" % ", ".join(extra))

        required = [
            "rtx/byazen/ByAzen.class",
            "rtx/byazen/api/music/MusicEngine.class",
            "rtx/byazen/api/music/MusicCovers.class",
            "rtx/byazen/api/music/RadioCatalog.class",
            "rtx/byazen/api/ui/MusicPlayerScreen.class",
            "rtx/byazen/api/drags/components/MusicComp.class",
            "rtx/byazen/api/modules/impl/Interface/MusicPlayerModule.class",
            "byazen.mixins.json",
        ]
        absent = [r for r in required if r not in names]
        if absent:
            problems.append("нет классов: %s" % ", ".join(absent))

        # плеер должен уметь играть MP3: декодер должен быть вложен в jar
        jlayer = [n for n in physical if "jlayer" in n.lower()]
        if not jlayer:
            problems.append("нет вложенного декодера MP3 (jlayer)")
        else:
            nested = zipfile.ZipFile(io.BytesIO(jar.read(jlayer[0])))
            if not any(n.startswith("javazoom/jl/decoder/") for n in nested.namelist()):
                problems.append("в jlayer нет классов декодера javazoom/jl/decoder")

        entrypoints = mod.get("entrypoints", {})
        print("entrypoints:     ", ",".join(sorted(entrypoints.keys())))
        if "client" not in entrypoints:
            problems.append("нет клиентской точки входа")

        mixins = json.loads(jar.read("byazen.mixins.json").decode("utf-8"))
        print("миксины:         ", len(mixins.get("mixins", [])), "общих /", len(mixins.get("client", [])), "клиентских")

    if problems:
        print()
        for problem in problems:
            print("ПРОБЛЕМА:", problem)
        print("VERIFY: FAILED")
        return 1
    print("VERIFY: OK")
    return 0


if __name__ == "__main__":
    target = sys.argv[1] if len(sys.argv) > 1 else None
    if target is None:
        libs = os.path.join(ROOT, "build", "libs")
        candidates = [os.path.join(libs, n) for n in sorted(os.listdir(libs)) if n.endswith(".jar")]
        target = candidates[-1] if candidates else ""
    if not target or not os.path.exists(target):
        print("не найден собранный jar")
        sys.exit(2)
    sys.exit(main(target))
