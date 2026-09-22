package rtx.byazen.utils.qr;

import java.nio.charset.StandardCharsets;

/**
 * Генератор QR-кодов для обмена конфигурацией (идея №101 из IDEAS.md).
 * <p>
 * Байтовый режим с уровнем коррекции L и всеми версиями от 1 до 40: этого хватает и для профиля
 * модуля, и для всей конфигурации клиента. Матрица строится полностью своим кодом — поисковые узоры,
 * выравнивание, коды Рида-Соломона, маска с наименьшим штрафом, — поэтому картинка рисуется обычными
 * средствами интерфейса и не тянет никаких внешних библиотек.
 */
public final class QrCode {

    private static final int[] ECC_PER_BLOCK_L = {
        7, 10, 15, 20, 26, 18, 20, 24, 30, 18,
        20, 24, 26, 30, 22, 24, 28, 30, 28, 28,
        28, 28, 30, 30, 26, 28, 30, 30, 30, 30,
        30, 30, 30, 30, 30, 30, 30, 30, 30, 30
    };

    private static final int[] BLOCK_COUNT_L = {
        1, 1, 1, 1, 1, 2, 2, 2, 2, 4,
        4, 4, 4, 4, 6, 6, 6, 6, 7, 8,
        8, 9, 9, 10, 12, 12, 12, 13, 14, 15,
        16, 17, 18, 19, 19, 20, 21, 22, 24, 25
    };

    private static final boolean[] FINDER_PATTERN = {
        true, false, true, true, true, false, true, false, false, false, false
    };
    private static final boolean[] FINDER_PATTERN_REVERSED = {
        false, false, false, false, true, false, true, true, true, false, true
    };

    private static final int MIN_VERSION = 1;
    private static final int MAX_VERSION = 40;
    private static final int QUIET_ZONE = 4;

    private QrCode() {
    }

    /** Готовая матрица: {@code modules[y][x]} — тёмный модуль на пересечении строки и столбца. */
    public static final class Symbol {

        public final int version;
        public final int size;
        public final boolean[][] modules;

        Symbol(int version, boolean[][] modules) {
            this.version = version;
            this.modules = modules;
            this.size = modules.length;
        }

        /** Размер с обязательным светлым отступом: именно столько занимает код целиком. */
        public int paddedSize() {
            return this.size + QUIET_ZONE * 2;
        }

        /** Тёмный ли модуль с учётом отступа. */
        public boolean dark(int x, int y) {
            int moduleX = x - QUIET_ZONE;
            int moduleY = y - QUIET_ZONE;
            if (moduleX < 0 || moduleY < 0 || moduleX >= this.size || moduleY >= this.size) {
                return false;
            }
            return this.modules[moduleY][moduleX];
        }

        /** Сколько подряд тёмных модулей идёт в строке — помогает рисовать полосами. */
        public int runLength(int y, int x) {
            int length = 0;
            while (x + length < this.size && this.modules[y][x + length]) {
                ++length;
            }
            return length;
        }
    }

    /** Кодирует текст в UTF-8. Возвращает {@code null}, если данные не помещаются в код. */
    public static Symbol encodeText(String text) {
        if (text == null) {
            return null;
        }
        return QrCode.encode(text.getBytes(StandardCharsets.UTF_8));
    }

    /** Кодирует массив байтов. */
    public static Symbol encode(byte[] data) {
        if (data == null || data.length == 0) {
            return null;
        }
        int version = -1;
        for (int candidate = MIN_VERSION; candidate <= MAX_VERSION; ++candidate) {
            int capacity = QrCode.numDataCodewords(candidate) * 8;
            int needed = 4 + (candidate <= 9 ? 8 : 16) + data.length * 8;
            if (needed <= capacity) {
                version = candidate;
                break;
            }
        }
        if (version < 0) {
            return null;
        }
        int[] codewords = QrCode.codewords(data, version);
        boolean[][] modules = QrCode.buildMatrix(version, QrCode.addEccAndInterleave(codewords, version));
        return new Symbol(version, modules);
    }

    /** Сколько байтов влезает в код указанной версии при уровне коррекции L. */
    public static int capacityBytes(int version) {
        int capacity = QrCode.numDataCodewords(version) * 8;
        int header = 4 + (version <= 9 ? 8 : 16);
        return Math.max(0, (capacity - header) / 8);
    }

    public static int maxVersion() {
        return MAX_VERSION;
    }

    /** Байты данных: режим, длина, содержимое, терминатор и заполнители. */
    private static int[] codewords(byte[] data, int version) {
        int capacity = QrCode.numDataCodewords(version) * 8;
        int[] bits = new int[capacity];
        int index = 0;
        index = QrCode.appendBits(bits, index, 0b0100, 4);
        index = QrCode.appendBits(bits, index, data.length, version <= 9 ? 8 : 16);
        for (byte value : data) {
            index = QrCode.appendBits(bits, index, value & 0xFF, 8);
        }
        index = QrCode.appendBits(bits, index, 0, Math.min(4, capacity - index));
        index = QrCode.appendBits(bits, index, 0, (8 - index % 8) % 8);
        int pad = 0xEC;
        while (index < capacity) {
            index = QrCode.appendBits(bits, index, pad, 8);
            pad ^= 0xEC ^ 0x11;
        }
        int[] words = new int[capacity / 8];
        for (int i = 0; i < words.length; ++i) {
            int value = 0;
            for (int j = 0; j < 8; ++j) {
                value = value << 1 | bits[i * 8 + j];
            }
            words[i] = value;
        }
        return words;
    }

    private static int appendBits(int[] bits, int index, int value, int length) {
        for (int i = length - 1; i >= 0; --i) {
            if (index < bits.length) {
                bits[index] = value >> i & 1;
            }
            ++index;
        }
        return index;
    }

    private static int numRawDataModules(int version) {
        int result = (16 * version + 128) * version + 64;
        if (version >= 2) {
            int numAlign = version / 7 + 2;
            result -= (25 * numAlign - 10) * numAlign - 55;
            if (version >= 7) {
                result -= 36;
            }
        }
        return result;
    }

    private static int numDataCodewords(int version) {
        return QrCode.numRawDataModules(version) / 8 - ECC_PER_BLOCK_L[version - 1] * BLOCK_COUNT_L[version - 1];
    }

    /** Координаты центров выравнивающих узоров. */
    private static int[] alignmentPositions(int version) {
        if (version == 1) {
            return new int[0];
        }
        int numAlign = version / 7 + 2;
        int step = version == 32 ? 26 : (version * 4 + numAlign * 2 + 1) / (numAlign * 2 - 2) * 2;
        int[] result = new int[numAlign];
        result[0] = 6;
        for (int i = 0; i < numAlign - 1; ++i) {
            result[numAlign - 1 - i] = version * 4 + 10 - i * step;
        }
        return result;
    }

    /** Делит данные на блоки, считает коды коррекции и перемежает байты по стандарту. */
    private static int[] addEccAndInterleave(int[] data, int version) {
        int numBlocks = BLOCK_COUNT_L[version - 1];
        int eccLength = ECC_PER_BLOCK_L[version - 1];
        int rawCodewords = QrCode.numRawDataModules(version) / 8;
        int numShortBlocks = numBlocks - rawCodewords % numBlocks;
        int shortBlockLength = rawCodewords / numBlocks;
        int shortDataLength = shortBlockLength - eccLength;
        int[][] blocks = new int[numBlocks][];
        int[][] eccBlocks = new int[numBlocks][];
        int index = 0;
        for (int i = 0; i < numBlocks; ++i) {
            int dataLength = shortDataLength + (i < numShortBlocks ? 0 : 1);
            int[] block = new int[dataLength];
            for (int j = 0; j < dataLength && index + j < data.length; ++j) {
                block[j] = data[index + j] & 0xFF;
            }
            index += dataLength;
            blocks[i] = block;
            eccBlocks[i] = QrCode.reedSolomonRemainder(block, eccLength);
        }
        int[] result = new int[rawCodewords];
        int position = 0;
        for (int i = 0; i < shortDataLength; ++i) {
            for (int j = 0; j < numBlocks; ++j) {
                result[position++] = blocks[j][i];
            }
        }
        for (int j = 0; j < numBlocks; ++j) {
            if (blocks[j].length > shortDataLength) {
                result[position++] = blocks[j][shortDataLength];
            }
        }
        for (int i = 0; i < eccLength; ++i) {
            for (int j = 0; j < numBlocks; ++j) {
                result[position++] = eccBlocks[j][i];
            }
        }
        return result;
    }

    private static int[] reedSolomonRemainder(int[] data, int degree) {
        int[] divisor = QrCode.reedSolomonDivisor(degree);
        int[] remainder = new int[degree];
        for (int value : data) {
            int factor = (value & 0xFF) ^ remainder[0];
            System.arraycopy(remainder, 1, remainder, 0, degree - 1);
            remainder[degree - 1] = 0;
            for (int i = 0; i < degree; ++i) {
                remainder[i] ^= QrCode.galoisMultiply(divisor[i], factor);
            }
        }
        return remainder;
    }

    private static int[] reedSolomonDivisor(int degree) {
        int[] result = new int[degree];
        result[degree - 1] = 1;
        int root = 1;
        for (int i = 0; i < degree; ++i) {
            for (int j = 0; j < degree; ++j) {
                result[j] = QrCode.galoisMultiply(result[j], root);
                if (j + 1 < degree) {
                    result[j] ^= result[j + 1];
                }
            }
            root = QrCode.galoisMultiply(root, 0x02);
        }
        return result;
    }

    private static int galoisMultiply(int left, int right) {
        int result = 0;
        for (int i = 0; i < 8; ++i) {
            if ((right & 1) != 0) {
                result ^= left;
            }
            int high = left & 0x80;
            left = left << 1 & 0xFF;
            if (high != 0) {
                left ^= 0x1D;
            }
            right >>= 1;
        }
        return result;
    }

    private static boolean[][] buildMatrix(int version, int[] codewords) {
        Builder builder = new Builder(version);
        builder.drawFunctionPatterns();
        builder.drawCodewords(codewords);
        int bestMask = 0;
        int bestScore = Integer.MAX_VALUE;
        for (int mask = 0; mask < 8; ++mask) {
            builder.applyMask(mask);
            builder.drawFormat(mask);
            int score = QrCode.penalty(builder.modules);
            builder.applyMask(mask);
            if (score < bestScore) {
                bestScore = score;
                bestMask = mask;
            }
        }
        builder.applyMask(bestMask);
        builder.drawFormat(bestMask);
        return builder.modules;
    }

    private static int bit(int value, int index) {
        return value >> index & 1;
    }

    private static boolean maskBit(int mask, int x, int y) {
        switch (mask) {
            case 0: {
                return (x + y) % 2 == 0;
            }
            case 1: {
                return y % 2 == 0;
            }
            case 2: {
                return x % 3 == 0;
            }
            case 3: {
                return (x + y) % 3 == 0;
            }
            case 4: {
                return (x / 3 + y / 2) % 2 == 0;
            }
            case 5: {
                return x * y % 2 + x * y % 3 == 0;
            }
            case 6: {
                return (x * y % 2 + x * y % 3) % 2 == 0;
            }
            default: {
                return ((x + y) % 2 + x * y % 3) % 2 == 0;
            }
        }
    }

    /** Штраф матрицы по четырём правилам стандарта: чем меньше, тем лучше читается код. */
    private static int penalty(boolean[][] modules) {
        int size = modules.length;
        int result = 0;
        for (int y = 0; y < size; ++y) {
            int run = 1;
            for (int x = 1; x < size; ++x) {
                if (modules[y][x] == modules[y][x - 1]) {
                    ++run;
                    continue;
                }
                if (run >= 5) {
                    result += 3 + (run - 5);
                }
                run = 1;
            }
            if (run >= 5) {
                result += 3 + (run - 5);
            }
        }
        for (int x = 0; x < size; ++x) {
            int run = 1;
            for (int y = 1; y < size; ++y) {
                if (modules[y][x] == modules[y - 1][x]) {
                    ++run;
                    continue;
                }
                if (run >= 5) {
                    result += 3 + (run - 5);
                }
                run = 1;
            }
            if (run >= 5) {
                result += 3 + (run - 5);
            }
        }
        for (int y = 0; y < size - 1; ++y) {
            for (int x = 0; x < size - 1; ++x) {
                boolean color = modules[y][x];
                if (color == modules[y][x + 1] && color == modules[y + 1][x] && color == modules[y + 1][x + 1]) {
                    result += 3;
                }
            }
        }
        for (int y = 0; y < size; ++y) {
            for (int x = 0; x + 11 <= size; ++x) {
                if (QrCode.matchesRow(modules, x, y, false) || QrCode.matchesRow(modules, x, y, true)) {
                    result += 40;
                }
            }
        }
        for (int x = 0; x < size; ++x) {
            for (int y = 0; y + 11 <= size; ++y) {
                if (QrCode.matchesColumn(modules, x, y, false) || QrCode.matchesColumn(modules, x, y, true)) {
                    result += 40;
                }
            }
        }
        int dark = 0;
        for (boolean[] row : modules) {
            for (boolean value : row) {
                if (value) {
                    ++dark;
                }
            }
        }
        int total = size * size;
        result += Math.abs(dark * 20 - total * 10) / total * 10;
        return result;
    }

    private static boolean matchesRow(boolean[][] modules, int x, int y, boolean reversed) {
        for (int i = 0; i < 11; ++i) {
            boolean expected = reversed ? FINDER_PATTERN_REVERSED[i] : FINDER_PATTERN[i];
            if (modules[y][x + i] != expected) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesColumn(boolean[][] modules, int x, int y, boolean reversed) {
        for (int i = 0; i < 11; ++i) {
            boolean expected = reversed ? FINDER_PATTERN_REVERSED[i] : FINDER_PATTERN[i];
            if (modules[y + i][x] != expected) {
                return false;
            }
        }
        return true;
    }

    /** Построитель матрицы: служебные узоры, данные, маска и блоки формата. */
    private static final class Builder {

        private final int size;
        private final boolean[][] modules;
        private final boolean[][] isFunction;

        Builder(int version) {
            this.size = version * 4 + 17;
            this.modules = new boolean[this.size][this.size];
            this.isFunction = new boolean[this.size][this.size];
        }

        void setFunction(int x, int y, boolean dark) {
            if (x < 0 || y < 0 || x >= this.size || y >= this.size) {
                return;
            }
            this.modules[y][x] = dark;
            this.isFunction[y][x] = true;
        }

        /** Синхронизация, три поисковых узора, выравнивание, формат и данные о версии. */
        void drawFunctionPatterns() {
            for (int i = 0; i < this.size; ++i) {
                this.setFunction(6, i, i % 2 == 0);
                this.setFunction(i, 6, i % 2 == 0);
            }
            this.drawFinder(3, 3);
            this.drawFinder(this.size - 4, 3);
            this.drawFinder(3, this.size - 4);
            int version = (this.size - 17) / 4;
            int[] positions = QrCode.alignmentPositions(version);
            int count = positions.length;
            for (int i = 0; i < count; ++i) {
                for (int j = 0; j < count; ++j) {
                    if (i == 0 && j == 0 || i == 0 && j == count - 1 || i == count - 1 && j == 0) {
                        continue;
                    }
                    for (int dy = -2; dy <= 2; ++dy) {
                        for (int dx = -2; dx <= 2; ++dx) {
                            this.setFunction(positions[i] + dx, positions[j] + dy, Math.max(Math.abs(dx), Math.abs(dy)) != 1);
                        }
                    }
                }
            }
            this.drawFormat(0);
            if (version >= 7) {
                int remainder = version;
                for (int i = 0; i < 12; ++i) {
                    remainder = remainder << 1 ^ (remainder >> 11) * 0x1F25;
                }
                int bits = version << 12 | remainder;
                for (int i = 0; i < 18; ++i) {
                    boolean dark = QrCode.bit(bits, i) != 0;
                    int a = this.size - 11 + i % 3;
                    int b = i / 3;
                    this.setFunction(a, b, dark);
                    this.setFunction(b, a, dark);
                }
            }
        }

        private void drawFinder(int centerX, int centerY) {
            for (int dy = -4; dy <= 4; ++dy) {
                for (int dx = -4; dx <= 4; ++dx) {
                    int distance = Math.max(Math.abs(dx), Math.abs(dy));
                    this.setFunction(centerX + dx, centerY + dy, distance != 2 && distance != 4);
                }
            }
        }

        /** Блоки формата: уровень коррекции L и выбранная маска. */
        void drawFormat(int mask) {
            int data = 1 << 3 | mask;
            int remainder = data;
            for (int i = 0; i < 10; ++i) {
                remainder = remainder << 1 ^ (remainder >> 9) * 0x537;
            }
            int bits = (data << 10 | remainder) ^ 0x5412;
            for (int i = 0; i < 6; ++i) {
                this.setFunction(8, i, QrCode.bit(bits, i) != 0);
            }
            this.setFunction(8, 7, QrCode.bit(bits, 6) != 0);
            this.setFunction(8, 8, QrCode.bit(bits, 7) != 0);
            this.setFunction(7, 8, QrCode.bit(bits, 8) != 0);
            for (int i = 9; i < 15; ++i) {
                this.setFunction(14 - i, 8, QrCode.bit(bits, i) != 0);
            }
            for (int i = 0; i < 8; ++i) {
                this.setFunction(this.size - 1 - i, 8, QrCode.bit(bits, i) != 0);
            }
            for (int i = 8; i < 15; ++i) {
                this.setFunction(8, this.size - 15 + i, QrCode.bit(bits, i) != 0);
            }
            this.setFunction(8, this.size - 8, true);
        }

        /** Укладывает байты «змейкой» справа налево, обходя служебные модули. */
        void drawCodewords(int[] codewords) {
            int index = 0;
            int right = this.size - 1;
            while (right >= 1) {
                if (right == 6) {
                    right = 5;
                }
                for (int vertical = 0; vertical < this.size; ++vertical) {
                    for (int j = 0; j < 2; ++j) {
                        int x = right - j;
                        boolean upward = (right + 1 & 2) == 0;
                        int y = upward ? this.size - 1 - vertical : vertical;
                        if (!this.isFunction[y][x] && index < codewords.length * 8) {
                            this.modules[y][x] = QrCode.bit(codewords[index >> 3], 7 - (index & 7)) != 0;
                            ++index;
                        }
                    }
                }
                right -= 2;
            }
        }

        void applyMask(int mask) {
            for (int y = 0; y < this.size; ++y) {
                for (int x = 0; x < this.size; ++x) {
                    if (!this.isFunction[y][x] && QrCode.maskBit(mask, x, y)) {
                        this.modules[y][x] = !this.modules[y][x];
                    }
                }
            }
        }
    }
}
