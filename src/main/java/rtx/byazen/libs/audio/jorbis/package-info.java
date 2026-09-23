/**
 * Декодер Ogg Vorbis, встроенный в клиент (идея №21): JOrbis.
 * <p>
 * Код взят из проекта JOrbis (http://www.jcraft.com/jorbis/) — чистый Java-декодер Ogg Vorbis
 * под лицензией GNU LGPL 2.1 (текст лицензии: docs/third-party/jorbis-LGPL-2.1.txt, копия
 * исходного текста — http://www.jcraft.com/jorbis/). Изменения ByAzen: единственное — пакеты
 * переименованы в {@code rtx.byazen.libs.audio.jorbis}, чтобы декодер не конфликтовал с копией
 * JOrbis, которая уже есть в самом Minecraft. Второе изменение — подавление предупреждения
 * {@code new Integer(…)} в {@code Info#toString()}: логика та же, просто сборка не сыплет предупреждениями
 * на JDK 21.
 * <p>
 * Своя копия нужна для того, чтобы плеер умел играть OGG-потоки (в том числе интернет-радио) и не
 * зависел от того, какие версии библиотек окажутся в конкретной сборке клиента.
 */
package rtx.byazen.libs.audio.jorbis;
