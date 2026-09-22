#!/usr/bin/env python3
"""Проверка раскладки интерфейса (идея №168 из IDEAS.md).

Скриншот-диффы в чистом виде тут невозможны: в CI нет запущенного Minecraft.
Зато раскладку окон можно проверить по числам — это тот же смысл: если панель
незаметно поехала в размерах, дифф это покажет.

Скрипт собирает:
  * размеры окон из LayoutAudit (то, что клиент знает о своих панелях);
  * размеры окон из кода экранов (static final float W/H);
  * для каждого типового разрешения при масштабе интерфейса 1x-4x — сколько панелей не влезает.

Результат сравнивается с эталоном tools/ui_layout_golden.json.
Расхождение = панель изменила размер без обновления эталона: CI падает.
Обновить эталон (осознанно): python3 tools/ui_layout_check.py --update
"""
import json
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
AUDIT = os.path.join(ROOT, 'src', 'main', 'java', 'rtx', 'byazen', 'utils', 'ui', 'LayoutAudit.java')
UI_DIR = os.path.join(ROOT, 'src', 'main', 'java', 'rtx', 'byazen', 'api', 'ui')
GOLDEN = os.path.join(ROOT, 'tools', 'ui_layout_golden.json')

SCREEN_RE = re.compile(r'new\s+Screen\(\s*"([^"]+)"\s*,\s*(\d+)\s*,\s*(\d+)\s*\)')
RES_RE = re.compile(r'\{\s*(\d{3,5})\s*,\s*(\d{3,5})\s*\}')
SIZE_RE = re.compile(r'static\s+final\s+float\s+(W|H)\s*=\s*([0-9]+(?:\.[0-9]+)?)f')


def read(path):
    with open(path, encoding='utf-8', errors='replace') as handle:
        return handle.read()


def collect():
    text = read(AUDIT)
    screens = {}
    for name, width, height in SCREEN_RE.findall(text):
        screens[name] = [int(width), int(height)]
    resolutions = sorted({(int(w), int(h)) for w, h in RES_RE.findall(text.split('RESOLUTIONS =')[-1])})
    code_sizes = {}
    for name in sorted(os.listdir(UI_DIR)):
        if not name.endswith('Screen.java'):
            continue
        body = read(os.path.join(UI_DIR, name))
        width = height = None
        for key, value in SIZE_RE.findall(body):
            if key == 'W':
                width = int(float(value))
            else:
                height = int(float(value))
        if width and height:
            code_sizes[name] = [width, height]
    fits = {}
    fits_zoom = {}
    for rw, rh in resolutions:
        per_scale = {}
        per_scale_zoom = {}
        for scale in (1, 2, 3, 4):
            space_w, space_h = rw / scale, rh / scale
            bad = [name for name, (w, h) in screens.items() if space_w < w or space_h < h]
            per_scale[str(scale)] = sorted(bad)
            # клиент умеет уменьшать свои окна до 50 % (модуль «Масштаб UI»)
            bad_zoom = [name for name, (w, h) in screens.items() if space_w * 2 < w or space_h * 2 < h]
            per_scale_zoom[str(scale)] = sorted(bad_zoom)
        fits['%dx%d' % (rw, rh)] = per_scale
        fits_zoom['%dx%d' % (rw, rh)] = per_scale_zoom
    return {
        'screens': dict(sorted(screens.items())),
        'code_sizes': dict(sorted(code_sizes.items())),
        'resolutions': [[w, h] for w, h in resolutions],
        'not_fitting': fits,
        'not_fitting_with_zoom_50': fits_zoom,
    }


def sanity(data):
    """Простые правила, которые не должны нарушаться никогда."""
    problems = []
    for name, (w, h) in data['screens'].items():
        if w > 1024 or h > 720:
            problems.append('окно «%s» больше 1024x720: %dx%d' % (name, w, h))
        if w < 240 or h < 140:
            problems.append('окно «%s» подозрительно маленькое: %dx%d' % (name, w, h))
        if w % 2 or h % 2:
            problems.append('окно «%s» имеет нечётный размер: %dx%d' % (name, w, h))
    smallest = min((int(r[0]) for r in data['resolutions']), default=0)
    if smallest and smallest < 1280:
        problems.append('в списке разрешений есть слишком маленькое: %d' % smallest)
    if not data['screens']:
        problems.append('в LayoutAudit не найдено ни одного окна')
    return problems


def diff(golden, current):
    lines = []
    for key in sorted(set(golden) | set(current)):
        if golden.get(key) != current.get(key):
            lines.append('блок «%s» изменился' % key)
            if key in ('screens', 'code_sizes'):
                old, new = golden.get(key, {}), current.get(key, {})
                for name in sorted(set(old) | set(new)):
                    if old.get(name) != new.get(name):
                        lines.append('  %s: %s → %s' % (name, old.get(name), new.get(name)))
    return lines


def main(argv):
    data = collect()
    problems = sanity(data)
    if '--update' in argv:
        with open(GOLDEN, 'w', encoding='utf-8') as handle:
            json.dump(data, handle, ensure_ascii=False, indent=2, sort_keys=True)
            handle.write('\n')
        print('эталон обновлён: %s (окон %d)' % (GOLDEN, len(data['screens'])))
        return 1 if problems else 0
    if problems:
        print('ПРОВАЛ: нарушены базовые правила раскладки:')
        for problem in problems:
            print('  - ' + problem)
        return 1
    if not os.path.exists(GOLDEN):
        print('ПРОВАЛ: нет эталона %s — создайте его через --update' % GOLDEN)
        return 1
    golden = json.loads(read(GOLDEN))
    lines = diff(golden, data)
    if lines:
        print('ПРОВАЛ: раскладка отличается от эталона (это и есть скриншот-дифф в числах):')
        for line in lines:
            print('  ' + line)
        print('Если изменение осознанное: python3 tools/ui_layout_check.py --update')
        return 1
    not_fitting = data['not_fitting']
    print('ОК: раскладка совпадает с эталоном')
    print('окон в эталоне: %d, экранов в коде: %d, разрешений: %d'
          % (len(data['screens']), len(data['code_sizes']), len(data['resolutions'])))
    for resolution, per_scale in not_fitting.items():
        parts = []
        for scale, names in per_scale.items():
            parts.append('%sx: %s' % (scale, 'все окна' if not names else 'не влезает %d' % len(names)))
        zoom = data.get('not_fitting_with_zoom_50', {}).get(resolution, {})
        bad_zoom = sum(1 for names in zoom.values() if names)
        tail = 'с масштабом окон 50 % — все окна' if bad_zoom == 0 else 'даже с масштабом окон 50 % не влезает в %d случаях' % bad_zoom
        print('  %s — %s (%s)' % (resolution, ', '.join(parts), tail))
    return 0


if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
