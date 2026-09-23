#!/usr/bin/env python3
"""Проверка миксинов ByAzen по официальным подписям ванильных классов.

Почему это нужно: ошибка в миксине не видна ни компилятору, ни CI — игра падает уже в бою, при
загрузке целевого класса. Пример из 1.9.4: обработчик `@Inject` для `breakBlock` (метод возвращает
`boolean`) объявил `CallbackInfo` вместо `CallbackInfoReturnable` — Minecraft падал с
«Invalid descriptor ... CallbackInfoReturnable is required» сразу при входе в мир.

Проверяем по данным tools/vanilla-signatures.txt (подписи из маппингов Yarn, CC0):
  * ошибка: тип Callback* не совпадает с типом возврата целевого метода (падение игры);
  * ошибка: тип у @Accessor/@Shadow не совпадает с ванильным (падение при загрузке класса);
  * внимание: цель не найдена (инъекция молча пропускается из-за require = 0) — вещь не работает;
  * внимание: число параметров обработчика не сходится с целью (инъекция молча пропускается).

Запуск:  python3 tools/mixin_check.py [--all] [--strict]
Обновление индекса подписей — tools/gen_vanilla_index.py.
"""

import gzip
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
SRC = os.path.join(ROOT, 'src', 'main', 'java')
INDEX = os.path.join(ROOT, 'tools', 'vanilla-signatures.txt.gz')

PRIM_TO_CODE = {'void': 'V', 'boolean': 'Z', 'byte': 'B', 'char': 'C', 'short': 'S',
                'int': 'I', 'long': 'J', 'float': 'F', 'double': 'D'}
BOX_TO_CODE = {'Void': 'V', 'Boolean': 'Z', 'Byte': 'B', 'Character': 'C', 'Short': 'S',
               'Integer': 'I', 'Long': 'J', 'Float': 'F', 'Double': 'D'}

INJECTIONS = ['Inject', 'Redirect', 'ModifyArg', 'ModifyVariable', 'ModifyConstant',
              'ModifyReturnValue', 'ModifyExpressionValue', 'WrapOperation', 'WrapWithCondition']
MEMBERS = ['Accessor', 'Invoker', 'Shadow']

MIXIN_ADDED = set()

problems = []
notes = []      # важное: цель не найдена нигде — инъекция точно не работает
inherited = []  # вероятно, метод унаследован — Mixin ищет цель в иерархии
unchecked = []  # целевой класс не попал в индекс — проверить нечем


def problem(text):
    problems.append(text)


def note(text):
    notes.append(text)


def is_inherited_note(text):
    inherited.append(text)


def strip_noise(text):
    text = re.sub(r'//[^\n]*', '', text)
    text = re.sub(r'/\*.*?\*/', '', text, flags=re.S)
    return text


def balanced(text, start):
    """Возвращает (содержимое скобок, индекс закрывающей скобки)."""
    depth = 0
    i = start
    while i < len(text):
        if text[i] == '(':
            depth += 1
        elif text[i] == ')':
            depth -= 1
            if depth == 0:
                return text[start + 1:i], i
        elif text[i] == '"' and depth > 0:
            i = text.index('"', i + 1)
        i += 1
    return '', start


def open_index():
    if os.path.exists(INDEX):
        return gzip.open(INDEX, 'rt', encoding='utf-8')
    plain = INDEX[:-3]
    return open(plain, encoding='utf-8')


def load_index():
    classes = {}
    with open_index() as handle:
        for line in handle:
            parts = line.rstrip('\n').split('\t')
            if parts[0] == 'C':
                classes.setdefault(parts[1], {'methods': [], 'fields': []})
                data = classes[parts[1]]
                BY_PATH[parts[1]] = data
                if len(parts) > 2:
                    BY_PATH[parts[2]] = data
                    inter, yarn = parts[2], parts[1]
                    ALIASES[inter.rsplit('/', 1)[-1].rsplit('$', 1)[-1]] = \
                        yarn.rsplit('/', 1)[-1].rsplit('$', 1)[-1]
            elif parts[0] == 'M' and len(parts) >= 6:
                classes.setdefault(parts[1], {'methods': [], 'fields': []})
                classes[parts[1]]['methods'].append(
                    {'name': parts[2], 'inter': parts[3],
                     'params': [p for p in parts[4].split(',') if p], 'ret': parts[5]})
            elif parts[0] == 'F' and len(parts) >= 5:
                classes.setdefault(parts[1], {'methods': [], 'fields': []})
                classes[parts[1]]['fields'].append(
                    {'name': parts[2], 'inter': parts[3], 'type': parts[4]})
    return classes


BY_PATH = {}
ALIASES = {}  # intermediary-имя класса -> yarn-имя (class_243 -> Vec3d)


def simple_of(path):
    return path.rsplit('/', 1)[-1]


def class_paths(classes):
    """индекс: и yarn-имя, и простое имя -> набор yarn-путей."""
    by_simple = {}
    for path in classes:
        simple = simple_of(path)
        by_simple.setdefault(simple, set()).add(path)
        # @Mixin(targets = "...SuggestionWindow") может ссылаться на вложенный класс по короткому имени
        if '$' in simple:
            by_simple.setdefault(simple.rsplit('$', 1)[-1], set()).add(path)
    return by_simple


def parse_mixin_targets(text, by_simple):
    targets = []
    external = False
    for m in re.finditer(r'@Mixin\s*\(', text):
        args, _ = balanced(text, m.end() - 1)
        if 'remap = false' in args or 'remap=false' in args:
            external = True
        for ref in re.findall(r'([\w\.\$]+)\s*\.class', args):
            ref = ref.strip()
            if '.' in ref and not ref.startswith('net.minecraft'):
                name = ref.rsplit('.', 1)[-1]
            else:
                name = ref.rsplit('.', 1)[-1]
            if name in by_simple:
                targets.extend(sorted(by_simple[name]))
        for block in re.findall(r'targets\s*=\s*\{([^}]*)\}', args):
            for value in re.findall(r'"([^"]+)"', block):
                name = value.rsplit('.', 1)[-1].rsplit('/', 1)[-1]
                targets.extend(sorted(by_simple.get(name, [])))
        for value in re.findall(r'targets\s*=\s*"([^"]+)"', args):
            name = value.rsplit('.', 1)[-1].rsplit('/', 1)[-1]
            targets.extend(sorted(by_simple.get(name, [])))
    return sorted(set(targets)), external


def parse_annotations(text):
    """Возвращает список (имя-аннотации, аргументы, сигнатура-обработчика, строка)."""
    out = []
    for m in re.finditer(r'@(' + '|'.join(INJECTIONS + MEMBERS) + r')\s*\(', text):
        name = m.group(1)
        args, end = balanced(text, m.end() - 1)
        rest = text[end + 1:]
        sig = re.match(r'\s*(?:@\w+(?:\((?:[^()]|\([^()]*\))*\))?\s*)*'
                       r'(?:public|private|protected)\s+([\w<>\[\],\.\? ]+?)\s+(\w+)\s*\(([^;{}]*)\)',
                       rest, re.S)
        if not sig:
            continue
        ret = re.sub(r'\b(public|private|protected|static|final|synchronized|abstract|native|default|strictfp)\b',
                     ' ', sig.group(1)).strip()
        out.append({'type': name, 'args': args,
                    'ret': ret, 'member': sig.group(2),
                    'params': sig.group(3),
                    'line': text[:m.start()].count('\n') + 1})
    return out


def split_params(raw):
    out, depth, cur = [], 0, ''
    for ch in raw:
        if ch in '<([':
            depth += 1
        elif ch in '>)]':
            depth -= 1
        if ch == ',' and depth == 0:
            out.append(cur.strip())
            cur = ''
        else:
            cur += ch
    if cur.strip():
        out.append(cur.strip())
    return out


def param_type(param):
    param = re.sub(r'@\w+(\((?:[^()]|\([^()]*\))*\))?', '', param).strip()
    parts = param.split()
    if len(parts) < 2:
        return None
    return ' '.join(parts[:-1])


def method_strings(args):
    """Все значения method= в аргументах аннотации."""
    values = []
    for m in re.finditer(r'method\s*=\s*(\{[^}]*\}|"[^"]*")', args):
        values.extend(re.findall(r'"([^"]+)"', m.group(1)))
    return values


def at_targets(args):
    return re.findall(r'target\s*=\s*"([^"]+)"', args)


def descriptor_param_count(text):
    """Считает типы в дескрипторной записи параметров: 'Lnet/minecraft/entity/ItemEntity;F' -> 2."""
    count = 0
    i = 0
    while i < len(text):
        if text[i] in 'BCDFIJSZ':
            count += 1
            i += 1
        elif text[i] == 'L':
            count += 1
            i = text.index(';', i) + 1
        elif text[i] == '[':
            i += 1
        else:
            i += 1
    return count


def descriptor_param_types(text):
    """Типы из дескрипторной записи параметров — по простым именам.

    'Ljava/lang/String;ILnet/minecraft/network/listener/ClientLoginPacketListener;' ->
    ['String', 'int', 'ClientLoginPacketListener'].
    """
    out = []
    i = 0
    while i < len(text):
        c = text[i]
        if c == '[':
            i += 1
            continue
        if c in 'BCDFIJSZ':
            out.append(c)  # примитивы держим в том же виде, что даёт norm_type()
            i += 1
        elif c == 'L':
            end = text.index(';', i)
            out.append(norm_type(text[i + 1:end]))
            i = end + 1
        else:
            i += 1
    return out


def name_and_params(method_string):
    """'breakBlock(Lnet/minecraft/class_2338;)Z' -> ('breakBlock', 1, ['BlockPos'])."""
    m = re.match(r'([\w<>$\.\*]+)\s*(?:\((.*)\)(.*))?$', method_string)
    if not m:
        return method_string, None, None
    if m.group(2) is None:
        return m.group(1), None, None
    params = m.group(2)
    if '/' in params or ';' in params or '[' in params:
        return m.group(1), descriptor_param_count(params), descriptor_param_types(params)
    return m.group(1), len(split_params(params)), None


def norm_type(name):
    """Приводит тип к сравнимому виду: убирает дженерики, массивы, пакеты и внешние имена.

    'List<Element>' -> 'List'; 'Biome.Precipitation' -> 'Precipitation';
    'class_1959$class_1963' -> 'class_1963'; 'Boolean' -> 'Z' (как 'boolean').
    """
    name = (name or '').strip()
    name = re.sub(r'<.*', '', name).strip()
    name = name.replace('[]', '').replace('...', '').strip()
    name = name.lstrip('L').rstrip(';') if name.startswith('L') and name.endswith(';') else name
    name = name.replace('/', '.')
    if name in PRIM_TO_CODE:
        return PRIM_TO_CODE[name]
    if name in BOX_TO_CODE:
        return BOX_TO_CODE[name]
    short = name.rsplit('$', 1)[-1].rsplit('.', 1)[-1]
    return ALIASES.get(short, short)


def is_type_variable(name):
    name = (name or '').strip()
    return bool(re.fullmatch(r'[A-Z]', name)) or name in ('?', '')


def find_method_everywhere(classes, method_name, param_count=None):
    """Все методы ванильного jar с таким именем (или intermediary-именем).

    Нужно, чтобы понять: метод унаследован от другого класса (тогда Mixin его найдёт в иерархии)
    или его вообще нет (тогда инъекция молча пропускается).
    """
    found = []
    for path, data in classes.items():
        for m in data['methods']:
            if m['name'] != method_name and m['inter'] != method_name:
                continue
            if param_count is not None and len(m['params']) != param_count:
                continue
            found.append((path, m))
    return found


def check_handler(path, classes, targets, external, ann):
    where = f'{os.path.basename(path)}:{ann["line"]}'
    if external or not targets:
        return
    methods = []
    for target in targets:
        methods.extend(classes.get(target, {}).get('methods', []))
    target_class = targets[0]
    if target_class not in classes:
        return

    params = split_params(ann['params'])
    callback = None
    local_count = 0
    target_params = []
    for param in params:
        ptype = param_type(param)
        if ptype is None:
            continue
        base = ptype.split('<')[0].strip()
        if base == 'CallbackInfo':
            callback = 'CallbackInfo'
        elif base == 'CallbackInfoReturnable':
            callback = 'CallbackInfoReturnable'
            inner = re.search(r'CallbackInfoReturnable\s*<\s*([\w\.]+)', ptype)
            callback = ('CallbackInfoReturnable', inner.group(1) if inner else '?')
        elif '@Local' in param or base == 'Operation':
            local_count += 1
        else:
            target_params.append(base.split('.')[-1])

    if ann['member'].startswith('<'):
        return

    if ann['type'] == 'Shadow':
        member = ann['member']
        found_field = next((f for f in classes[target_class]['fields'] if f['name'] == member or f['inter'] == member), None)
        found_method = next((m for m in methods if m['name'] == member or m['inter'] == member), None)
        if not found_field and not found_method:
            note(f'{where}: @Shadow {member} — в {simple_of(target_class)} не найден '
                 f'(возможно, объявлен в родителе — проверь руками)')
        elif found_field:
            declared = norm_type(ann['ret'])
            if declared != norm_type(found_field['type']) and not is_type_variable(declared):
                problem(f'{where}: @Shadow {member} объявлен как {declared}, '
                        f'а в игре {found_field["type"]} — падение при загрузке класса')
        return

    if ann['type'] in ('Accessor', 'Invoker'):
        member = None
        value = re.search(r'"([^"]+)"', ann['args'])
        member = value.group(1) if value else ann['member']
        found_field = next((f for f in classes[target_class]['fields'] if f['name'] == member or f['inter'] == member), None)
        found_method = next((m for m in methods if m['name'] == member or m['inter'] == member), None)
        if ann['type'] == 'Accessor':
            if not found_field:
                if found_method:
                    problem(f'{where}: @Accessor {member} — в {simple_of(target_class)} это метод, а не поле')
                else:
                    is_inherited_note(f'{where}: @Accessor {member} — в {simple_of(target_class)} '
                                      f'поле не найдено (возможно, объявлено в родителе)')
            else:
                expected = norm_type(found_field['type'])
                if len(params) == 0:
                    actual = norm_type(ann['ret'])
                    if not is_type_variable(actual) and actual != expected:
                        problem(f'{where}: @Accessor {member} возвращает {norm_type(ann["ret"])}, '
                                f'а поле в игре — {norm_type(found_field["type"])}')
                else:
                    first = param_type(params[0]) or ''
                    actual = norm_type(first)
                    if not is_type_variable(actual) and actual != expected:
                        problem(f'{where}: @Accessor {member} принимает {actual}, '
                                f'а поле в игре — {expected}')
                    if norm_type(ann['ret']) != 'V':
                        problem(f'{where}: @Accessor {member} — сеттер должен возвращать void, '
                                f'а объявлен {ann["ret"]}')
        else:
            if not found_method:
                is_inherited_note(f'{where}: @Invoker {member} — в {simple_of(target_class)} '
                                  f'метод не найден (возможно, в родителе)')
        return

    if ann['type'] in ('ModifyArg', 'Redirect'):
        # у этих перехватов тип возврата обработчика жёстко связан с целевым вызовом:
        # @Redirect обязан возвращать то же, что перехваченный метод, иначе Mixin валит класс
        for raw in at_targets(ann['args']):
            parsed = split_target(raw)
            if not parsed:
                continue
            owner, name, types, callee_ret = parsed
            if owner not in BY_PATH:
                continue  # внешняя библиотека (authlib, JDK) — ванильные подписи тут не помогут
            callee = resolve_member(owner, name, types)
            if callee is None:
                note(f'{where}: @{ann["type"]} целится в {simple_of(owner)}.{name} — '
                     f'такого метода в ванильном jar нет, перехват молча пропускается')
                continue
            handler_ret = norm_type(ann['ret'])
            if ann['type'] == 'Redirect':
                if callee_ret and handler_ret != callee_ret and not is_type_variable(ann['ret']):
                    problem(f'{where}: @Redirect возвращает {handler_ret}, а перехваченный '
                            f'{simple_of(owner)}.{name} возвращает {callee_ret} — '
                            f'игра падает при загрузке класса')
            else:
                known = {norm_type(x) for m in callee for x in m['params']}
                if known and handler_ret not in known and not is_type_variable(ann['ret']):
                    note(f'{where}: @ModifyArg возвращает {handler_ret}, а у вызова '
                         f'{simple_of(owner)}.{name} параметры {sorted(known)} — проверь тип аргумента')
    # инъекции: сверяем цель
    mstrings = method_strings(ann['args'])
    if not mstrings:
        return
    for mstring in mstrings:
        name, param_count, param_types = name_and_params(mstring)
        if '*' in name:
            continue  # маска Mixin: точную сверку не делаем
        if name in MIXIN_ADDED or name.startswith('<'):
            continue
        candidates = [m for m in methods if m['name'] == name or m['inter'] == name]
        if param_types:
            # дескриптор задан точно — сверяем не только количество, но и типы параметров
            narrowed = [m for m in candidates
                        if [norm_type(x) for x in m['params']] == param_types]
            if narrowed:
                candidates = narrowed
        elif param_count is not None:
            narrowed = [m for m in candidates if len(m['params']) == param_count]
            if narrowed:
                candidates = narrowed
        if not candidates:
            # В самом классе-цели метода нет: либо он унаследован (Mixin ищет цель в иерархии и
            # найдёт его в родителе), либо его нет вовсе (инъекция молча пропускается).
            global_hits = find_method_everywhere(classes, name, param_count)
            if param_types:
                typed = [(p, m) for p, m in global_hits
                         if [norm_type(x) for x in m['params']] == param_types]
                if typed:
                    global_hits = typed
            owners = sorted({simple_of(p) for p, _m in global_hits})
            candidates = [m for _p, m in global_hits]
            if not candidates:
                note(f'{where}: цель {name} не найдена ни в {simple_of(target_class)}, '
                     f'ни в одном классе ванильного jar — инъекция молча пропускается (require = 0)')
                continue
            if len(owners) > 1:
                is_inherited_note(
                    f'{where}: {name} нет в {simple_of(target_class)}; такой метод есть в '
                    f'{len(owners)} классах ({", ".join(owners[:4])}…) — неоднозначно, проверь руками')
                continue
            is_inherited_note(
                f'{where}: {name} нет в {simple_of(target_class)}, объявлен в {owners[0]} — '
                f'унаследованная цель: инъекция применяется, только если Mixin найдёт метод в иерархии')
        if ann['type'] != 'Inject' or not callback:
            continue
        if callback == 'CallbackInfo':
            if all(m['ret'] != 'void' for m in candidates):
                problem(f'{where}: {name} в игре возвращает {candidates[0]["ret"]}, '
                        f'а обработчик ждёт CallbackInfo. Нужен CallbackInfoReturnable — '
                        f'иначе игра падает при загрузке {simple_of(target_class)}')
            elif any(m['ret'] != 'void' for m in candidates):
                # селектор без дескриптора цепляет сразу все перегрузки: та, что возвращает
                # значение, уронит игру требованием CallbackInfoReturnable
                rets = ', '.join(sorted({m['ret'] for m in candidates}))
                problem(f'{where}: селектор "{mstring}" цепляет перегрузки с разными возвратами '
                        f'({rets}) — для возвращающих нужен CallbackInfoReturnable. '
                        f'Укажи точный дескриптор или отдельный обработчик')
        else:
            inner = norm_type(callback[1])
            if is_type_variable(callback[1]):
                continue
            if all(m['ret'] == 'void' for m in candidates):
                problem(f'{where}: {name} в игре возвращает void, а обработчик объявлен как '
                        f'CallbackInfoReturnable<{callback[1]}> — падение при загрузке класса')
            elif not any(norm_type(m['ret']) == inner for m in candidates):
                rets = ', '.join(sorted({m['ret'] for m in candidates}))
                problem(f'{where}: {name} возвращает {rets}, а обработчик ждёт '
                        f'CallbackInfoReturnable<{callback[1]}> — падение при загрузке класса')
        counts = sorted({len(m['params']) for m in candidates})
        if target_params and len(target_params) not in counts and not local_count:
            note(f'{where}: {name} — обработчик принимает {len(target_params)} параметров цели, '
                 f'а в игре их {counts} — инъекция молча пропускается')


def mixin_added_methods(text):
    """Имена методов, которые сам миксин добавляет в целевой класс (не обработчики @Inject).

    Другие миксины ByAzen могут затем нацеливаться на такой метод — это нормально, поэтому считаем
    его существующим.
    """
    clean = strip_noise(text)
    handlers = {ann['member'] for ann in parse_annotations(clean)}
    added = set()
    for m in re.finditer(r'(?m)^\s{4}(?:public|private|protected)?\s*(?:static\s+)?[\w<>\[\],\.\? ]+\s+(\w+)\s*\(', clean):
        name = m.group(1)
        if name not in handlers:
            added.add(name)
    return added


def split_target(text):
    """'Lnet/minecraft/client/gui/hud/ChatHud;method_1812(Lnet/minecraft/class_2561;)V'
    -> ('net/minecraft/client/gui/hud/ChatHud', 'method_1812', ['Text'], 'void')."""
    m = re.match(r'L([^;]+);([\w<>$]+)(?:\((.*)\)(.+))?$', text)
    if not m:
        return None
    owner, name, params, ret = m.group(1), m.group(2), m.group(3), m.group(4)
    types = descriptor_param_types(params) if params else []
    return owner, name, types, (norm_type(ret) if ret else None)


def resolve_member(owner, name, param_types):
    """Находит метод по цели из @At (и по yarn-, и по intermediary-имени класса/метода)."""
    data = BY_PATH.get(owner)
    if data is None:
        return None
    found = [m for m in data['methods'] if m['name'] == name or m['inter'] == name]
    if param_types:
        narrowed = [m for m in found if [norm_type(x) for x in m['params']] == param_types]
        if narrowed:
            return narrowed
    return found or None


def iterate_java():
    for base, _dirs, names in os.walk(SRC):
        for name in sorted(names):
            if name.endswith('.java'):
                yield os.path.join(base, name)


def collect_mixin_added():
    """Методы, добавленные миксинами ByAzen в целевые классы: их можно потом перехватывать."""
    added = set()
    for path in iterate_java():
        text = open(path, encoding='utf-8', errors='replace').read()
        if '@Mixin' not in text:
            continue
        added |= mixin_added_methods(text)
    return added


def main():
    show_all = '--all' in sys.argv
    strict = '--strict' in sys.argv
    global MIXIN_ADDED
    MIXIN_ADDED = collect_mixin_added()
    classes = load_index()
    by_simple = class_paths(classes)
    checked = 0
    external_files = 0
    for path in iterate_java():
        text = open(path, encoding='utf-8', errors='replace').read()
        if '@Mixin' not in text:
            continue
        clean = strip_noise(text)
        targets, external = parse_mixin_targets(clean, by_simple)
        if external and not targets:
            external_files += 1
            continue
        missing = [t for t in targets if t not in classes]
        if missing:
            unchecked.append(f'{os.path.basename(path)}: {", ".join(simple_of(t) for t in missing)}')
        for ann in parse_annotations(clean):
            checked += 1
            check_handler(path, classes, targets, external, ann)
    print(f'миксины: проверено инъекций и участников — {checked}; '
          f'внешних миксинов (remap = false) — {external_files}')
    if problems:
        print(f'\nОШИБКИ ({len(problems)}):')
        for text in problems:
            print('  ✗ ' + text)
    else:
        print('ошибок нет')
    if notes:
        print(f'\nВНИМАНИЕ ({len(notes)}) — цель инъекции не найдена нигде, вещь не работает:')
        for text in (notes if show_all else notes[:12]):
            print('  ~ ' + text)
        if len(notes) > 12 and not show_all:
            print(f'  ~ … и ещё {len(notes) - 12} (подробнее с --all)')
    if inherited:
        print(f'\nвероятно унаследованные цели ({len(inherited)}) — инъекции применяются не всегда; '
              f'список с --all')
        if show_all:
            for text in inherited:
                print('  · ' + text)
    if unchecked:
        print(f'\nвне индекса подписей ({len(unchecked)}) — цели не проверялись:')
        for text in (unchecked if show_all else unchecked[:6]):
            print('  - ' + text)
        if len(unchecked) > 6 and not show_all:
            print(f'  - … и ещё {len(unchecked) - 6}')
    if not problems and not notes and not inherited:
        print('замечаний нет')
    return 1 if problems or (strict and (notes or inherited)) else 0


if __name__ == '__main__':
    sys.exit(main())
