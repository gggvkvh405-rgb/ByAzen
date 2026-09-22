package rtx.byazen.utils.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Шифрование локальных данных (идея №162 из IDEAS.md).
 * <p>
 * Файлы клиента (заметки, музыка, история смертей, профили серверов) можно закрыть ключом: данные
 * укладываются в один контейнер, зашифрованный AES-256-GCM, где каждая запись лежит в обычном zip.
 * Ключ создаётся один раз и хранится отдельно от данных; без него содержимое хранилища не читается.
 * Открыть хранилище обратно можно в любой момент — расшифровка кладёт файлы на место.
 */
public final class SecretBox {

    private static final byte[] MAGIC = {'B', 'Z', 'E', 'N', 'C', '1'};
    private static final int TAG_BITS = 128;
    private static final int IV_BYTES = 12;
    private static final String KEY_FILE = "byazen.key";

    private static SecretKey cached;

    private SecretBox() {
    }

    /** Ключ клиента: создаётся при первом обращении. */
    public static synchronized SecretKey key() {
        if (SecretBox.cached != null) {
            return SecretBox.cached;
        }
        try {
            Path path = SecretBox.keyPath();
            if (Files.exists(path)) {
                byte[] raw = Base64.getDecoder().decode(new String(Files.readAllBytes(path), StandardCharsets.UTF_8).trim());
                SecretBox.cached = new SecretKeySpec(raw, "AES");
                return SecretBox.cached;
            }
            KeyGenerator generator = KeyGenerator.getInstance("AES");
            generator.init(256);
            SecretKey generated = generator.generateKey();
            Files.createDirectories(path.getParent());
            Files.write(path, Base64.getEncoder().encodeToString(generated.getEncoded()).getBytes(StandardCharsets.UTF_8));
            SecretBox.restrictPermissions(path);
            SecretBox.cached = generated;
            return generated;
        }
        catch (Throwable throwable) {
            rtx.byazen.utils.logs.ClientLog.error("ключ шифрования не создался: " + throwable);
            return null;
        }
    }

    public static boolean available() {
        return SecretBox.key() != null;
    }

    private static Path keyPath() {
        return RepositoryStorage.root().resolve(SecretBox.KEY_FILE);
    }

    private static void restrictPermissions(Path path) {
        try {
            java.util.Set<java.nio.file.attribute.PosixFilePermission> permissions = java.util.EnumSet.of(
                    java.nio.file.attribute.PosixFilePermission.OWNER_READ,
                    java.nio.file.attribute.PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(path, permissions);
        }
        catch (Throwable ignored) {
            // не POSIX-система — просто оставляем как есть
        }
    }

    public static boolean isSealed(byte[] data) {
        if (data == null || data.length < SecretBox.MAGIC.length + SecretBox.IV_BYTES) {
            return false;
        }
        for (int i = 0; i < SecretBox.MAGIC.length; ++i) {
            if (data[i] != SecretBox.MAGIC[i]) {
                return false;
            }
        }
        return true;
    }

    /** Шифрует данные: [magic][iv 12][ciphertext+tag]. */
    public static byte[] encrypt(byte[] plain) {
        try {
            SecretKey key = SecretBox.key();
            if (key == null || plain == null) {
                return null;
            }
            byte[] iv = new byte[SecretBox.IV_BYTES];
            new SecureRandom().nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(SecretBox.TAG_BITS, iv));
            byte[] encrypted = cipher.doFinal(plain);
            ByteArrayOutputStream out = new ByteArrayOutputStream(SecretBox.MAGIC.length + iv.length + encrypted.length);
            out.write(SecretBox.MAGIC);
            out.write(iv);
            out.write(encrypted);
            return out.toByteArray();
        }
        catch (Throwable throwable) {
            rtx.byazen.utils.logs.ClientLog.error("шифрование не удалось: " + throwable);
            return null;
        }
    }

    /** Расшифровывает данные. Возвращает null, если ключ не тот или файл повреждён. */
    public static byte[] decrypt(byte[] sealed) {
        try {
            if (!SecretBox.isSealed(sealed)) {
                return null;
            }
            SecretKey key = SecretBox.key();
            if (key == null) {
                return null;
            }
            int offset = SecretBox.MAGIC.length;
            byte[] iv = new byte[SecretBox.IV_BYTES];
            System.arraycopy(sealed, offset, iv, 0, SecretBox.IV_BYTES);
            offset += SecretBox.IV_BYTES;
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(SecretBox.TAG_BITS, iv));
            return cipher.doFinal(sealed, offset, sealed.length - offset);
        }
        catch (Throwable throwable) {
            rtx.byazen.utils.logs.ClientLog.warn("расшифровка не удалась: " + throwable);
            return null;
        }
    }

    /** Дополнительная страховка: расшифровать поток целиком (для проверки хранилища). */
    public static ByteArrayInputStream stream(byte[] sealed) {
        byte[] plain = SecretBox.decrypt(sealed);
        return plain == null ? null : new ByteArrayInputStream(plain);
    }
}
