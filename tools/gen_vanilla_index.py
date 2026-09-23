#!/usr/bin/env python3
"""Генерирует tools/vanilla-signatures.txt — компактный индекс подписей ванильных классов,
нужных миксинам ByAzen.

Зачем: миксин «падает» не на компиляции, а в игре — при загрузке целевого класса. Ошибка вида
«Invalid descriptor ... CallbackInfoReturnable is required» убивает игру на входе в мир, а CI её не
видит (CI только компилирует и проверяет jar). Поэтому мы заранее выгружаем из официальных маппингов
Yarn подписи всех методов и полей целевых классов и проверяем миксины статически (tools/mixin_check.py).

Данные: github.com/FabricMC/yarn (лицензия MIT), тег build/refs/heads/<версия>-build-number-<N>,
совпадающий с версией yarn в gradle.properties.

Запуск (нужен клон Yarn, только для разработчика):
    git clone --depth 1 --branch "build/refs/heads/1.21.11-build-number-6" \
        https://github.com/FabricMC/yarn.git /tmp/yarn
    python3 tools/gen_vanilla_index.py /tmp/yarn/mappings > tools/vanilla-signatures.txt
"""

import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, 'src', 'main', 'java')


def simple_of(yarn_path):
    return yarn_path.rsplit('/', 1)[-1]


def load_mappings(mappings_dir):
    """Разбирает .mapping-файлы Yarn.

    Формат: `CLASS <inter> <yarn>` в начале файла, вложенные классы — `\tCLASS <innerInter> <innerYarn>`
    (имена у них простые, без путей), члены вложенного класса идут с двумя табами, члены самого
    класса — с одним.
    """
    classes = {}
    methods = {}
    fields = {}
    inter2yarn = {}
    yarn_set = set()
    for base, _dirs, names in os.walk(mappings_dir):
        for name in names:
            if not name.endswith('.mapping'):
                continue
            path = os.path.join(base, name)
            root_yarn = root_inter = None
            scope_yarn = scope_inter = None
            for raw in open(path, encoding='utf-8'):
                line = raw.rstrip('\n')
                if line.startswith('CLASS '):
                    parts = line.split()
                    root_inter = parts[1]
                    root_yarn = parts[2] if len(parts) > 2 else parts[1]
                    classes[root_yarn] = root_inter
                    inter2yarn[root_inter] = root_yarn
                    yarn_set.add(root_yarn)
                    scope_yarn, scope_inter = root_yarn, root_inter
                elif line.startswith('\tCLASS ') and root_yarn:
                    parts = line.strip().split()
                    inner_inter = parts[1]
                    inner_yarn = parts[2] if len(parts) > 2 else parts[1]
                    if '$' not in inner_inter:
                        inner_inter = root_inter + '$' + inner_inter
                    if '$' not in inner_yarn:
                        inner_yarn = root_yarn + '$' + inner_yarn
                    classes[inner_yarn] = inner_inter
                    inter2yarn[inner_inter] = inner_yarn
                    yarn_set.add(inner_yarn)
                    scope_yarn, scope_inter = inner_yarn, inner_inter
                elif line.startswith('\t\tFIELD ') and scope_yarn:
                    parts = line.strip().split(' ', 3)
                    if len(parts) == 4:
                        fields.setdefault(scope_yarn, []).append((parts[2], parts[1], parts[3]))
                    elif len(parts) == 3:
                        fields.setdefault(scope_yarn, []).append((parts[1], parts[1], parts[2]))
                elif line.startswith('\t\tMETHOD ') and scope_yarn:
                    parts = line.strip().split(' ', 3)
                    if len(parts) == 4:
                        methods.setdefault(scope_yarn, []).append((parts[2], parts[1], parts[3]))
                    elif len(parts) == 3:
                        methods.setdefault(scope_yarn, []).append((parts[1], parts[1], parts[2]))
                elif line.startswith('\tFIELD ') and root_yarn:
                    parts = line.strip().split(' ', 3)
                    if len(parts) == 4:
                        fields.setdefault(root_yarn, []).append((parts[2], parts[1], parts[3]))
                    elif len(parts) == 3:
                        fields.setdefault(root_yarn, []).append((parts[1], parts[1], parts[2]))
                elif line.startswith('\tMETHOD ') and root_yarn:
                    parts = line.strip().split(' ', 3)
                    if len(parts) == 4:
                        methods.setdefault(root_yarn, []).append((parts[2], parts[1], parts[3]))
                    elif len(parts) == 3:
                        methods.setdefault(root_yarn, []).append((parts[1], parts[1], parts[2]))
    return classes, methods, fields, inter2yarn, yarn_set


def parse_type(text, i, inter2yarn):
    """Разбирает один тип дескриптора, начиная с позиции i. Возвращает (имя, новая позиция)."""
    arr = 0
    while i < len(text) and text[i] == '[':
        arr += 1
        i += 1
    c = text[i]
    if c == 'L':
        end = text.index(';', i)
        full = text[i + 1:end]
        i = end + 1
        name = type_name(full, inter2yarn)
    else:
        i += 1
        name = primitive(c)
    return name + '[]' * arr, i


def describe(desc, inter2yarn):
    """'(ILnet/minecraft/class_2338;)Z' -> (['int', 'BlockPos'], 'boolean')."""
    m = re.match(r'\((.*)\)(.+)$', desc)
    if not m:
        return [], '?'
    params, ret = m.group(1), m.group(2)
    out = []
    i = 0
    while i < len(params):
        name, i = parse_type(params, i, inter2yarn)
        out.append(name)
    ret_name, _ = parse_type(ret, 0, inter2yarn)
    return out, ret_name


def primitive(code):
    return {'V': 'void', 'Z': 'boolean', 'B': 'byte', 'C': 'char', 'S': 'short',
            'I': 'int', 'J': 'long', 'F': 'float', 'D': 'double'}.get(code, code)


def type_name(full, inter2yarn):
    yarn = inter2yarn.get(full, full)
    return simple_of(yarn)


def collect_targets():
    """Собирает классы, которые упоминают миксины: @Mixin(...) и @At(target="L...;")."""
    wanted = set()
    for base, _dirs, names in os.walk(SRC):
        for name in sorted(names):
            if not name.endswith('.java'):
                continue
            text = open(os.path.join(base, name), encoding='utf-8', errors='replace').read()
            for m in re.finditer(r'@Mixin\s*\((.*?)\)', text, re.S):
                body = m.group(1)
                for ref in re.findall(r'([\w\.\$]+)\s*\.class', body):
                    wanted.add(ref)
                for tgt in re.findall(r'targets\s*=\s*\{([^}]*)\}', body):
                    for s in re.findall(r'"([^"]+)"', tgt):
                        wanted.add(s)
                for s in re.findall(r'@Mixin\s*\(\s*targets\s*=\s*"([^"]+)"', body):
                    wanted.add(s)
            for m in re.finditer(r'target\s*=\s*"(L?[\w\$]+(?:/[\w\$]+)*)\s*[;:]', text):
                wanted.add(m.group(1))
    return wanted


def to_yarn_path(ref, classes, yarn_set):
    """'BlockPos', 'net.minecraft.util.math.BlockPos', 'net/minecraft/class_2338' -> yarn-путь."""
    ref = ref.strip()
    if ref.startswith('L'):
        ref = ref[1:]
    if ref.startswith('net/minecraft/class_'):
        return None  # разрулим отдельно (intermediary)
    if '/' in ref:
        if ref in yarn_set:
            return ref
        ref = ref.replace('/', '.')
    if '.' in ref:
        return ref.replace('.', '/')
    return ref


def main():
    if len(sys.argv) < 2:
        print(__doc__)
        return 1
    mappings_dir = sys.argv[1]
    classes, methods, fields, inter2yarn, yarn_set = load_mappings(mappings_dir)

    simple2yarn = {}
    for yarn in yarn_set:
        simple2yarn.setdefault(simple_of(yarn), []).append(yarn)

    # Индексируем ВСЕ классы ванильного jar (не только цели миксинов): иначе вывод
    # «метод не найден» ничего не доказывал бы — цель могла быть унаследована от класса,
    # которого в индексе нет.
    wanted_paths = sorted(yarn_set)

    print('# ByAzen · подписи ванильных классов из маппингов Yarn (github.com/FabricMC/yarn, CC0)')
    print('# формат: C<tab>класс(yarn)<tab>класс(intermediary); '
          'M<tab>класс<tab>имя<tab>intermediary<tab>параметры<tab>возврат; '
          'F<tab>класс<tab>имя<tab>intermediary<tab>тип')
    print('# сгенерировано tools/gen_vanilla_index.py — руками не править')
    for yarn in wanted_paths:
        inter = classes.get(yarn, '?')
        print(f'C\t{yarn}\t{inter}')
        for yarn_name, inter_name, desc in sorted(methods.get(yarn, [])):
            params, ret = describe(desc, inter2yarn)
            print(f'M\t{yarn}\t{yarn_name}\t{inter_name}\t{",".join(params)}\t{ret}')
        for yarn_name, inter_name, desc in sorted(fields.get(yarn, [])):
            print(f'F\t{yarn}\t{yarn_name}\t{inter_name}\t{describe("()" + desc, inter2yarn)[1]}')
    print(f'# классов: {len(wanted_paths)}', file=sys.stderr)
    return 0


if __name__ == '__main__':
    sys.exit(main())
