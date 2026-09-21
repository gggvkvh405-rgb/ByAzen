package rtx.byazen.utils.render;

/**
 * Метка «сейчас рисуются строки чата»: нужна стрим-режиму, чтобы замазывать только чат,
 * а не весь текст интерфейса (идея №47 из IDEAS.md).
 */
public final class ChatMaskScope {

    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private ChatMaskScope() {
    }

    public static void enter() {
        ACTIVE.set(Boolean.TRUE);
    }

    public static void exit() {
        ACTIVE.set(Boolean.FALSE);
    }

    public static boolean active() {
        return ACTIVE.get();
    }
}
