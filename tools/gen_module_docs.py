#!/usr/bin/env python3
"""Генератор документации по модулям (идея №196 из IDEAS.md).

Сканирует src/main/java/rtx/byazen/api/modules/impl/**/*.java и собирает
docs/MODULES.md: все модули по разделам, с описанием, категорией и настройками.
Запуск:  python3 tools/gen_module_docs.py
"""
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MODULES_DIR = os.path.join(ROOT, 'src', 'main', 'java', 'rtx', 'byazen', 'api', 'modules', 'impl')
OUT = os.path.join(ROOT, 'docs', 'MODULES.md')

SETTING_RE = re.compile(
    r'public\s+final\s+(\w+Setting)\s+(\w+)\s*=\s*this\.register\(\s*new\s+\w+Setting\(\s*'
    r'("(?:[^"\\]|\\.)*")', re.S)
SUPER_RE = re.compile(
    r'super\(\s*("(?:[^"\\]|\\.)*")\s*,\s*("(?:[^"\\]|\\.)*")\s*'
    r'(?:,\s*(?:Category\.)?(\w+))?\s*\)', re.S)
CLASS_RE = re.compile(r'class\s+(\w+)\s+extends\s+(\w+)')
FOLDER_CATEGORY = {'Interface': 'Display', 'Utils': 'Utils', 'Events': 'Events',
                   'Themes': 'Themes', 'Visuals': 'Visuals', 'Display': 'Display'}
CATEGORY_LABEL = {'VISUALS': 'Visuals', 'DISPLAY': 'Display', 'UTILS': 'Utils',
                  'EVENTS': 'Events', 'THEMES': 'Themes'}


def unescape(text):
    """Разворачивает java-строку (включая \\uXXXX) без кавычек."""
    body = text[1:-1]
    body = re.sub(r'\\u([0-9a-fA-F]{4})', lambda m: chr(int(m.group(1), 16)), body)
    body = body.replace('\\"', '"').replace('\\\\', '\\').replace('\\n', ' ').replace('\\t', ' ')
    return re.sub(r'\s+', ' ', body).strip()


def collect_chain():
    """Карта класс -> (родитель, категория из файла) для модулей репозитория."""
    chain = {}
    for base, _dirs, files in os.walk(MODULES_DIR):
        for name in files:
            if not name.endswith('.java'):
                continue
            path = os.path.join(base, name)
            text = open(path, encoding='utf-8', errors='replace').read()
            category = SUPER_RE.search(text)
            for cls, parent in CLASS_RE.findall(text):
                chain[cls] = parent
            if category is not None and category.group(3):
                for cls, _parent in CLASS_RE.findall(text):
                    chain[cls + '#category'] = category.group(3)
    return chain


def resolve_category(cls, chain, visited=None):
    """Ищет раздел по цепочке наследования, пока не найдётся в базовом классе."""
    visited = visited or set()
    if cls in visited or len(visited) > 6:
        return None
    visited.add(cls)
    parent = chain.get(cls)
    if not parent:
        return None
    return chain.get(parent + '#category') or resolve_category(parent, chain, visited)


def find_modules():
    modules = []
    chain = collect_chain()
    for base, _dirs, files in os.walk(MODULES_DIR):
        folder = os.path.basename(base)
        if folder == 'impl':
            continue
        for name in sorted(files):
            if not name.endswith('.java'):
                continue
            path = os.path.join(base, name)
            text = open(path, encoding='utf-8', errors='replace').read()
            match = SUPER_RE.search(text)
            if not match:
                continue
            display = unescape(match.group(1))
            description = unescape(match.group(2))
            settings = []
            for kind, field, label in SETTING_RE.findall(text):
                settings.append((kind.replace('Setting', ''), unescape(label)))
            category = match.group(3)
            if not category:
                cls = None
                found = CLASS_RE.search(text)
                if found:
                    cls = found.group(1)
                category = chain.get(cls + '#category') if cls else None
                category = category or resolve_category(cls, chain) or FOLDER_CATEGORY.get(folder,
                                                                                            folder.upper())
            category = CATEGORY_LABEL.get(category, category)
            modules.append({
                'file': os.path.relpath(path, ROOT).replace(os.sep, '/'),
                'display': display,
                'description': description,
                'category': category,
                'settings': settings,
            })
    modules.sort(key=lambda m: (m['category'], m['display'].lower()))
    return modules


def main():
    modules = find_modules()
    if not modules:
        print('модули не найдены', file=sys.stderr)
        return 1
    lines = ['# Модули ByAzen — документация', '',
             'Файл собран автоматически: `python3 tools/gen_module_docs.py`.',
             'Каждый модуль можно найти в клиенте командой `.find <слово>`, а показать, как он работает, — '
             'командой `.how <модуль>` или кнопкой «Инструкция строки» на вкладке «Гайд» в хабе.', '',
             'Всего модулей: **%d**. Разделы: %s.' % (
                 len(modules),
                 ', '.join('**%s** (%d)' % (cat, sum(1 for m in modules if m['category'] == cat))
                           for cat in sorted({m['category'] for m in modules}))), '']
    current = None
    for module in modules:
        if module['category'] != current:
            current = module['category']
            lines += ['', '## Раздел «%s»' % current, '']
        lines.append('### %s' % module['display'])
        lines.append('')
        lines.append(module['description'] or 'Описание не указано.')
        lines.append('')
        lines.append('* Раздел: `%s`' % current)
        lines.append('* Файл: `%s`' % module['file'])
        if module['settings']:
            names = []
            for kind, label in module['settings']:
                names.append('%s (%s)' % (label, kind))
            lines.append('* Настройки: ' + ', '.join(names))
        else:
            lines.append('* Настройки: переключатель включения и клавиша')
        lines.append('')
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    open(OUT, 'w', encoding='utf-8').write('\n'.join(lines).rstrip() + '\n')
    print('docs/MODULES.md: модулей %d' % len(modules))
    return 0


if __name__ == '__main__':
    sys.exit(main())
