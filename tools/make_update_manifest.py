#!/usr/bin/env python3
"""Генератор манифеста обновлений ByAzen (идеи №206–№208 из IDEAS.md).

Манифест `update.json` описывает свежую сборку: версию, ссылку на jar, контрольную сумму,
размер, дату, требования и короткое «что нового». Клиент читает его при проверке обновлений
(модуль Updater), поэтому манифест должен генерироваться автоматически — руками его
поддерживать бессмысленно, он устареет на первом же релизе.

Использование (так делает CI):

    python3 tools/make_update_manifest.py \
        --jar build/libs/ByAzen-1.9.2.jar \
        --ref arena/01a0bf74-byazen \
        --repo gggvkvh405-rgb/ByAzen

Проверка готового манифеста против собранного jar:

    python3 tools/make_update_manifest.py --check --jar build/libs/ByAzen-1.9.2.jar
"""

from __future__ import annotations

import argparse
import datetime
import hashlib
import json
import os
import re
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MANIFEST = os.path.join(ROOT, "update.json")
GRADLE_PROPERTIES = os.path.join(ROOT, "gradle.properties")
README = os.path.join(ROOT, "README.md")


def prop(name: str) -> str:
    with open(GRADLE_PROPERTIES, encoding="utf-8") as handle:
        match = re.search(rf"^{name}=(\S+)", handle.read(), re.M)
    if not match:
        raise SystemExit(f"в gradle.properties нет {name}")
    return match.group(1)


def file_sha256(path: str) -> str:
    digest = hashlib.sha256()
    with open(path, "rb") as handle:
        for chunk in iter(lambda: handle.read(1 << 20), b""):
            digest.update(chunk)
    return digest.hexdigest()


def notes_for(version: str, limit: int = 240) -> str:
    """Первая строка раздела «Что нового в <версия>» из README — коротко и по делу."""
    try:
        with open(README, encoding="utf-8") as handle:
            readme = handle.read()
    except OSError:
        return ""
    heading = re.search(rf"## Что нового в {re.escape(version)}[^\n]*\n(.+?)\n\n", readme, re.S)
    if not heading:
        return ""
    first = heading.group(1).strip().split("\n")[0].strip()
    first = re.sub(r"^\*+\s*", "", first)
    first = first.replace("**", "").replace("`", "").replace("🔥", "").replace("⭐", "")
    first = re.sub(r"\s+", " ", first).strip()
    if len(first) > limit:
        first = first[: limit - 1].rstrip() + "…"
    return first


def build(jar: str, ref: str, repo: str, version: str | None, minecraft: str | None,
          loader: str | None, released: str | None) -> dict:
    if not os.path.exists(jar):
        raise SystemExit(f"нет собранного jar: {jar}")
    name = os.path.basename(jar)
    version = version or prop("mod_version")
    if f"-{version}." not in name:
        raise SystemExit(f"имя jar «{name}» не совпадает с версией {version}")
    raw = f"https://raw.githubusercontent.com/{repo}/{ref}/{name}"
    return {
        "version": version,
        "jar": name,
        "url": raw,
        "urls": [raw],
        "sha256": file_sha256(jar),
        "size": os.path.getsize(jar),
        "released": released or datetime.date.today().isoformat(),
        "minecraft": minecraft or prop("minecraft_version"),
        "loader": loader or prop("loader_version"),
        "java": 21,
        "notes": notes_for(version),
        "generated": "CI" if os.environ.get("CI") else "локально",
    }


def write_manifest(manifest: dict) -> None:
    with open(MANIFEST, "w", encoding="utf-8") as handle:
        json.dump(manifest, handle, ensure_ascii=False, indent=2)
        handle.write("\n")
    checksum = os.path.join(ROOT, manifest["jar"] + ".sha256")
    with open(checksum, "w", encoding="utf-8") as handle:
        handle.write(f"{manifest['sha256']}  {manifest['jar']}\n")


def check(jar: str) -> int:
    with open(MANIFEST, encoding="utf-8") as handle:
        manifest = json.load(handle)
    version = prop("mod_version")
    problems = []
    if manifest.get("version") != version:
        problems.append(f"версия в манифесте {manifest.get('version')}, в gradle.properties {version}")
    if manifest.get("jar") != f"ByAzen-{version}.jar":
        problems.append(f"файл в манифесте {manifest.get('jar')}, ожидается ByAzen-{version}.jar")
    digest = manifest.get("sha256", "")
    if len(digest) != 64:
        problems.append("sha256 в манифесте не похож на настоящий")
    if not str(manifest.get("url", "")).startswith("https://"):
        problems.append("в манифесте нет https-ссылки на файл")
    if manifest.get("size", 0) < 10000:
        problems.append("размер файла в манифесте подозрительно маленький")
    if jar and os.path.exists(jar):
        real = file_sha256(jar)
        if real != digest:
            problems.append(f"sha256 не совпадает с jar: манифест {digest[:16]}…, файл {real[:16]}…")
        if os.path.getsize(jar) != manifest.get("size"):
            problems.append("размер в манифесте не совпадает с jar")
    if problems:
        print("МАНИФЕСТ: ПРОВАЛ")
        for text in problems:
            print("  - " + text)
        return 1
    print(f"МАНИФЕСТ OK: {manifest['version']} · {manifest['jar']} · "
          f"{manifest['sha256'][:16]}… · {manifest['size']} Б")
    return 0


def main() -> int:
    parser = argparse.ArgumentParser(description="манифест обновлений ByAzen")
    parser.add_argument("--jar", default="")
    parser.add_argument("--ref", default=os.environ.get("GITHUB_REF_NAME", "arena/01a0bf74-byazen"))
    parser.add_argument("--repo", default=os.environ.get("GITHUB_REPOSITORY", "gggvkvh405-rgb/ByAzen"))
    parser.add_argument("--version", default=None)
    parser.add_argument("--minecraft", default=None)
    parser.add_argument("--loader", default=None)
    parser.add_argument("--released", default=None)
    parser.add_argument("--check", action="store_true", help="проверить существующий update.json против jar")
    args = parser.parse_args()

    if args.check:
        jar = args.jar
        if not jar:
            version = prop("mod_version")
            candidate = os.path.join(ROOT, f"ByAzen-{version}.jar")
            jar = candidate if os.path.exists(candidate) else ""
        return check(jar)

    if not args.jar:
        raise SystemExit("нужно указать --jar путь к собранному jar (или --check)")
    manifest = build(args.jar, args.ref, args.repo, args.version, args.minecraft, args.loader, args.released)
    write_manifest(manifest)
    print(f"update.json: {manifest['version']} · {manifest['jar']} · {manifest['sha256'][:16]}… · "
          f"{manifest['size']} Б · {manifest['released']}")
    print(f"что нового: {manifest['notes'] or '(описание не найдено)'}")
    return check(args.jar)


if __name__ == "__main__":
    sys.exit(main())
