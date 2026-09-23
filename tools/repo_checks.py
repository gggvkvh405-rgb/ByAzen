#!/usr/bin/env python3
"""Preflight-проверки проекта ByAzen (идея №201 из IDEAS.md).

Скрипт запускается локально и в CI ДО сборки: он ловит ровно те классы ошибок,
которые уже дважды ломали сборку (забытый импорт своего класса, вызов нестатического
метода через имя класса), а также проверяет то, что легко забыть руками:
регистрацию модулей, согласованность версий, актуальность документации и раскладки окон.

Использование:
    python3 tools/repo_checks.py            # все проверки
    python3 tools/repo_checks.py --quiet    # только итог
"""

from __future__ import annotations

import os
import re
import hashlib
import json
import subprocess
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
JAVA_ROOT = os.path.join(ROOT, "src", "main", "java")
MODULES_IMPL = os.path.join(JAVA_ROOT, "rtx", "byazen", "api", "modules", "impl")
MODULE_MANAGER = os.path.join(JAVA_ROOT, "rtx", "byazen", "api", "modules", "ModuleManager.java")
GRADLE_PROPERTIES = os.path.join(ROOT, "gradle.properties")
README = os.path.join(ROOT, "README.md")
DOCS_MODULES = os.path.join(ROOT, "docs", "MODULES.md")

SKIP_IMPORT_CHECK = {
    # классы, которые намеренно упоминаются как строки/в отражении
}

problems: list[str] = []
warnings: list[str] = []
checked = 0
# вендорный код (GeckoLib, WaveyCapes, Discord RPC, миксины) не трогаем
VENDOR_MARKERS = ("/api/mods/", "/mixin/", "/utils/discord/", "/api/ui/window/", "/libs/")


def report(kind: str, text: str) -> None:
    problems.append(f"[{kind}] {text}")


def java_files() -> list[str]:
    result = []
    for base, _dirs, names in os.walk(JAVA_ROOT):
        for name in sorted(names):
            if name.endswith(".java"):
                result.append(os.path.join(base, name))
    return result


def strip_noise(text: str) -> str:
    """Убирает строки, текстовые блоки и комментарии — остаётся только код."""
    text = re.sub(r'"""(?:.|\n)*?"""', '""', text)
    text = re.sub(r'"(?:\\.|[^"\\])*"', '""', text)
    text = re.sub(r"'(?:\\.|[^'\\])*'", "''", text)
    text = re.sub(r"/\*.*?\*/", "", text, flags=re.S)
    text = re.sub(r"//[^\n]*", "", text)
    return text


class Sources:
    """Разбор всех исходников: типы, методы (статические и обычные)."""

    def __init__(self) -> None:
        self.package_of: dict[str, str] = {}
        self.packages_by_type: dict[str, set[str]] = {}
        self.static_methods: dict[str, set[str]] = {}
        self.instance_methods: dict[str, set[str]] = {}
        self.own_types: dict[str, set[str]] = {}
        for path in java_files():
            name = os.path.basename(path)[:-5]
            text = open(path, encoding="utf-8").read()
            clean = strip_noise(text)
            package = re.search(r"package\s+([\w.]+);", text)
            self.package_of[name] = package.group(1) if package else ""
            self.packages_by_type.setdefault(name, set()).add(self.package_of[name])
            types = {name}
            types |= set(re.findall(r"\b(?:class|record|enum|interface)\s+(\w+)", clean))
            self.own_types[name] = types
            for match in re.finditer(
                r"(?m)^\s*(public|private|protected)?\s*(static\s+)?[\w<>,\[\]\. ]+\s(\w+)\s*\(", clean
            ):
                method = match.group(3)
                if method == name:
                    continue
                if match.group(2):
                    self.static_methods.setdefault(name, set()).add(method)
                elif match.group(1):
                    self.instance_methods.setdefault(name, set()).add(method)


def check_imports(sources: Sources, quiet: bool) -> None:
    """Свои классы должны быть импортированы: забытый импорт виден только компилятору."""
    global checked
    for path in java_files():
        name = os.path.basename(path)[:-5]
        if name in SKIP_IMPORT_CHECK:
            continue
        if any(marker in path.replace(os.sep, "/") for marker in VENDOR_MARKERS):
            # вендорный код (JOrbis, GeckoLib, WaveyCapes, Discord RPC) не проверяем
            continue
        text = open(path, encoding="utf-8").read()
        clean = strip_noise(text)
        own = re.search(r"package\s+([\w.]+);", text)
        package = own.group(1) if own else ""
        imported = {i.rsplit(".", 1)[1] for i in re.findall(r"import\s+(?:static\s+)?([\w.]+);", text)}
        declared = set(re.findall(r"\b(?:class|record|enum|interface)\s+(\w+)", clean))
        declared |= set(re.findall(r"\brecord\s+(\w+)\s*\(", clean))
        for used in sorted(set(re.findall(r"(?<![\w.])([A-Z]\w+)(?![\w])", clean))):
            if used not in sources.package_of or used in imported or used in declared:
                continue
            if package in sources.packages_by_type.get(used, set()):
                continue
            where = ", ".join(sorted(sources.packages_by_type.get(used, {"?"})))
            report("импорт", f"{os.path.basename(path)}: используется {used} из {where}, импорта нет")
    checked += 1


def check_static_calls(sources: Sources) -> None:
    """ИмяКласса.метод( не должен звать нестатический метод — это ошибка компиляции."""
    for path in java_files():
        name = os.path.basename(path)[:-5]
        clean = strip_noise(open(path, encoding="utf-8").read())
        for match in re.finditer(r"(?<![\w.])([A-Z]\w+)\.([a-zA-Z_]\w*)\s*\(", clean):
            target, method = match.group(1), match.group(2)
            if target == name or method in sources.static_methods.get(target, set()):
                continue
            # ИмяСБольшойБуквы( — это конструктор вложенного типа, а не метод
            if method[:1].isupper():
                continue
            if method in sources.instance_methods.get(target, set()):
                line = clean[: match.start()].count("\n") + 1
                report("статический вызов", f"{os.path.basename(path)}:{line} {target}.{method} — метод нестатический")


def check_module_registration() -> None:
    """Каждый модуль должен быть зарегистрирован в ModuleManager, иначе он просто не существует."""
    manager = open(MODULE_MANAGER, encoding="utf-8").read()
    registered = set(re.findall(r"new\s+(\w+)\s*\(", manager))
    for base, _dirs, names in os.walk(MODULES_IMPL):
        for file in sorted(names):
            if not file.endswith("Module.java"):
                continue
            path = os.path.join(base, file)
            text = open(path, encoding="utf-8").read()
            if "extends Module" not in text or re.search(r"\babstract\s+class", text):
                continue
            cls = file[:-5]
            if cls not in registered:
                report("регистрация", f"{cls} не зарегистрирован в ModuleManager")


def check_get0() -> None:
    """Стилевое правило проекта: в новом коде вместо .get(0) пишем getFirst() (Java 21)."""
    for path in java_files():
        if any(marker in path.replace(os.sep, "/") for marker in VENDOR_MARKERS):
            continue
        clean = strip_noise(open(path, encoding="utf-8").read())
        if ".get(0)" in clean:
            warnings.append(f"стиль: {os.path.basename(path)} использует .get(0) — в новом коде лучше getFirst()")


def check_versions() -> None:
    """Версия в gradle.properties, README и имени jar должны совпадать."""
    props = open(GRADLE_PROPERTIES, encoding="utf-8").read()
    match = re.search(r"mod_version=(\S+)", props)
    if not match:
        report("версия", "в gradle.properties нет mod_version")
        return
    version = match.group(1).strip()
    readme = open(README, encoding="utf-8").read()
    found = re.findall(r"\|\s*Версия\s*\|\s*([\d.]+)\s*\|", readme)
    if not found:
        report("версия", "в README нет строки «| Версия | X |»")
    for value in found:
        if value != version:
            report("версия", f"README: «Версия {value}», а в gradle.properties {version}")
    # исторические упоминания старых jar в README — это нормально, проверяем только актуальные строки
    for pattern, label in (
        (r"\|\s*Готовый мод\s*\|\s*\*\*`ByAzen-([\d.]+)\.jar`", "строка «Готовый мод»"),
        (r"Положите\s+`ByAzen-([\d.]+)\.jar`\s+в", "инструкция установки"),
    ):
        match = re.search(pattern, readme)
        if match and match.group(1) != version:
            report("версия", f"README ({label}): ByAzen-{match.group(1)}.jar, а версия {version}")


def check_manifest() -> None:
    """update.json должен описывать именно тот jar, что лежит в репозитории (идеи №206–№208)."""
    path = os.path.join(ROOT, "update.json")
    if not os.path.exists(path):
        report("манифест", "нет update.json — модуль Updater не найдёт обновлений")
        return
    try:
        with open(path, encoding="utf-8") as handle:
            manifest = json.load(handle)
    except Exception as error:
        report("манифест", f"update.json не читается: {error}")
        return
    version = re.search(r"mod_version=(\S+)", open(GRADLE_PROPERTIES, encoding="utf-8").read()).group(1)
    jar_name = manifest.get("jar", "")
    jar_path = os.path.join(ROOT, jar_name)
    if manifest.get("version") != version:
        warnings.append(f"манифест: update.json описывает {manifest.get('version')}, "
                        f"а в gradle.properties {version} — CI перегенерирует его при сборке")
    if not str(manifest.get("url", "")).startswith("https://"):
        report("манифест", "в update.json нет https-ссылки на файл")
    if len(manifest.get("sha256", "")) != 64:
        report("манифест", "в update.json нет контрольной суммы sha256")
    if not jar_name.endswith(".jar") or not os.path.exists(jar_path):
        report("манифест", f"в репозитории нет файла {jar_name} из манифеста")
        return
    digest = hashlib.sha256()
    with open(jar_path, "rb") as handle:
        for chunk in iter(lambda: handle.read(1 << 20), b""):
            digest.update(chunk)
    real = digest.hexdigest()
    if real != manifest.get("sha256"):
        report("манифест", f"sha256 в update.json не совпадает с {jar_name}")
    if os.path.getsize(jar_path) != manifest.get("size"):
        report("манифест", f"размер в update.json не совпадает с {jar_name}")
    checksum = os.path.join(ROOT, jar_name + ".sha256")
    if not os.path.exists(checksum):
        warnings.append(f"манифест: рядом с {jar_name} нет файла контрольной суммы")
    else:
        text = open(checksum, encoding="utf-8").read().split()
        if not text or text[0] != real:
            report("манифест", f"файл {jar_name}.sha256 не совпадает с самим jar")


def check_docs(quiet: bool) -> None:
    """docs/MODULES.md должен совпадать с тем, что генерирует tools/gen_module_docs.py."""
    script = os.path.join(ROOT, "tools", "gen_module_docs.py")
    path = DOCS_MODULES
    if not os.path.exists(script) or not os.path.exists(path):
        return
    before = open(path, encoding="utf-8").read()
    result = subprocess.run([sys.executable, script], cwd=ROOT, capture_output=True, text=True)
    if result.returncode != 0:
        report("документация", f"gen_module_docs.py упал: {result.stderr.strip()[:160]}")
        return
    after = open(path, encoding="utf-8").read()
    if before != after:
        with open(path, "w", encoding="utf-8") as handle:
            handle.write(before)
        report("документация", "docs/MODULES.md устарел — запустите python3 tools/gen_module_docs.py")
    elif not quiet:
        print("docs/MODULES.md актуален")


def check_layout(quiet: bool) -> None:
    """Эталон раскладки окон должен совпадать с кодом (идея №168)."""
    script = os.path.join(ROOT, "tools", "ui_layout_check.py")
    if not os.path.exists(script):
        return
    result = subprocess.run([sys.executable, script], cwd=ROOT, capture_output=True, text=True)
    output = (result.stdout or "") + (result.stderr or "")
    if "ОК" not in output:
        report("раскладка", "ui_layout_check.py не подтвердил раскладку — смотрите его вывод")
    elif not quiet:
        print("раскладка окон совпадает с эталоном")


def check_mixins(quiet: bool) -> None:
    """Миксины сверяются с подписями ванильных классов (падение в игре ловится здесь, а не у игрока).

    Ошибка в аннотации миксина не видна компилятору: игра падает в бою, при загрузке целевого класса.
    Пример — ByAzen 1.9.4: обработчик breakBlock просил CallbackInfo вместо CallbackInfoReturnable.
    """
    script = os.path.join(ROOT, "tools", "mixin_check.py")
    if not os.path.exists(script):
        return
    result = subprocess.run([sys.executable, script], cwd=ROOT, capture_output=True, text=True)
    output = (result.stdout or "") + (result.stderr or "")
    if result.returncode != 0:
        errors = [line.strip() for line in output.splitlines() if line.strip().startswith("✗")]
        for text in errors[:10]:
            report("миксины", text.lstrip("✗ ").strip())
        if not errors:
            report("миксины", "mixin_check.py нашёл ошибки — смотрите его вывод")
        return
    summary = next((line for line in output.splitlines() if line.startswith("миксины:")), "")
    if not quiet:
        print(summary or "миксины сверены с подписями ванильных классов")
        for line in output.splitlines():
            if line.startswith("ВНИМАНИЕ") or line.startswith("вероятно унаследованные"):
                print("  " + line)


def main() -> int:
    quiet = "--quiet" in sys.argv
    sources = Sources()
    if not quiet:
        print(f"проверяю {len(sources.package_of)} java-файлов")
    check_imports(sources, quiet)
    check_static_calls(sources)
    check_module_registration()
    check_get0()
    check_versions()
    check_manifest()
    check_docs(quiet)
    check_layout(quiet)
    check_mixins(quiet)
    print()
    if warnings:
        print(f"замечания (не блокируют сборку): {len(warnings)}")
        for text in warnings[:10]:
            print("  ~ " + text)
        if len(warnings) > 10:
            print(f"  ~ … и ещё {len(warnings) - 10}")
        print()
    if problems:
        print(f"ПРОВАЛ: проблем {len(problems)}")
        for text in problems:
            print("  - " + text)
        return 1
    print("Preflight: всё чисто (импорты, статические вызовы, модули, версии, доки, раскладка, миксины)")
    return 0


if __name__ == "__main__":
    sys.exit(main())
