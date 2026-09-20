package rtx.kimiko.api.ui.window;

import net.minecraft.client.MinecraftClient;
import rtx.kimiko.utils.profile.ProfileIdentity;

public final class WindowTitleAnimation {
    private static final long STEP_MS = 45L;
    private static final long HOLD_MS = 4000L;
    private static final int ERASE_PER_STEP = 2;
    private static final WindowTitleAnimation INSTANCE = new WindowTitleAnimation();
    private String visible = "";
    private String target = "";
    private boolean erasing;
    private boolean uidPhase;
    private long nextStepAt;
    private String lastPushed;

    private WindowTitleAnimation() {
    }

    public static WindowTitleAnimation get() {
        return INSTANCE;
    }

    private void push() {
        String title = this.currentTitle();
        if (title.equals(this.lastPushed)) {
            return;
        }
        this.lastPushed = title;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.getWindow() != null) {
            mc.getWindow().setTitle(title);
        }
    }

    public String currentTitle() {
        return "ByAzen v1.1  \u2758  " + this.visible;
    }

    public void tick() {
        long now = System.currentTimeMillis();
        if (this.target.isEmpty()) {
            this.target = phaseText(this.uidPhase);
        }
        if (now < this.nextStepAt) {
            return;
        }
        if (this.erasing) {
            if (this.visible.isEmpty()) {
                this.erasing = false;
                this.uidPhase = !this.uidPhase;
                this.target = phaseText(this.uidPhase);
                this.nextStepAt = now + STEP_MS;
            } else {
                int len = Math.max(0, this.visible.length() - ERASE_PER_STEP);
                this.visible = this.visible.substring(0, len);
                this.nextStepAt = now + STEP_MS;
            }
        } else if (this.visible.equals(this.target)) {
            this.erasing = true;
            this.nextStepAt = now + HOLD_MS;
        } else {
            if (this.visible.length() < this.target.length()) {
                this.visible = this.target.substring(0, this.visible.length() + 1);
            }
            this.nextStepAt = now + STEP_MS;
        }
        this.push();
    }

    private static String profileName() {
        try {
            return ProfileIdentity.username("Guest");
        } catch (Throwable throwable) {
            return "Guest";
        }
    }

    private static String phaseText(boolean bl) {
        if (bl) {
            int n = safeUid();
            return n > 0 ? "UID " + n : profileName() + "'s Profile";
        }
        return profileName() + "'s Profile";
    }

    private static int safeUid() {
        try {
            return ProfileIdentity.uid();
        } catch (Throwable t) {
            return 0;
        }
    }
}
