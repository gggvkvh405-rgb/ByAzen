package rtx.kimiko.api.modules.impl.Visuals.emotions;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import rtx.kimiko.api.modules.impl.Visuals.Emotions;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.render.fonts.Fonts;
import rtx.kimiko.utils.render.render2d.Render2D;

public class EmotionWheel {
    private final List<Emotion> emotions;
    private boolean finished;
    private int selectedIndex = -1;
    private float animAlpha = 0.0f;
    private long openTime = System.currentTimeMillis();

    public EmotionWheel(List<Emotion> emotions) {
        this.emotions = emotions;
    }

    public void close() {
        this.finished = true;
    }

    public void finish() {
        this.finished = true;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void render(DrawContext drawContext, boolean interactive) {
        if (drawContext == null || this.emotions == null || this.emotions.isEmpty()) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getWindow() == null) {
            return;
        }
        int screenWidth = mc.getWindow().getScaledWidth();
        int screenHeight = mc.getWindow().getScaledHeight();
        float cx = screenWidth * 0.5f;
        float cy = screenHeight * 0.5f;

        long elapsed = System.currentTimeMillis() - this.openTime;
        this.animAlpha = MathHelper.clamp(elapsed / 150.0f, 0.0f, 1.0f);

        double mouseX = mc.mouse.getX() * (double) screenWidth / (double) mc.getWindow().getWidth();
        double mouseY = mc.mouse.getY() * (double) screenHeight / (double) mc.getWindow().getHeight();

        float dx = (float) (mouseX - cx);
        float dy = (float) (mouseY - cy);
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        int count = this.emotions.size();
        float sectorAngle = (float) (Math.PI * 2.0 / count);

        this.selectedIndex = -1;
        if (interactive && dist > 25.0f && dist < 120.0f) {
            float mouseAngle = (float) Math.atan2(dy, dx);
            if (mouseAngle < 0) {
                mouseAngle += (float) (Math.PI * 2.0);
            }
            this.selectedIndex = (int) (mouseAngle / sectorAngle) % count;
        }

        // Background center circle
        int centerBg = ColorUtil.rgba(20, 20, 25, (int)(200 * this.animAlpha));
        int centerOutline = ColorUtil.rgba(255, 255, 255, (int)(40 * this.animAlpha));
        Render2D.rect(cx - 24.0f, cy - 24.0f, 48.0f, 48.0f, 24.0f, centerBg);
        Render2D.outline(cx - 24.0f, cy - 24.0f, 48.0f, 48.0f, 24.0f, 1.0f, centerOutline);
        String centerText = "Эмоции";
        float centerW = Fonts.MONTSERRAT_SEMIBOLD.width(centerText, 6.0f);
        Fonts.MONTSERRAT_SEMIBOLD.draw(centerText, cx - centerW * 0.5f, cy - 3.0f, 6.0f, ColorUtil.rgba(220, 220, 220, (int)(255 * this.animAlpha)));

        float radius = 75.0f;
        for (int i = 0; i < count; i++) {
            Emotion emotion = this.emotions.get(i);
            float angle = i * sectorAngle + sectorAngle * 0.5f;
            float itemX = cx + (float) Math.cos(angle) * radius;
            float itemY = cy + (float) Math.sin(angle) * radius;

            boolean isHovered = (i == this.selectedIndex);
            int itemBg = isHovered
                ? ColorUtil.rgba(127, 242, 255, (int)(180 * this.animAlpha))
                : ColorUtil.rgba(30, 30, 38, (int)(180 * this.animAlpha));
            int itemBorder = isHovered
                ? ColorUtil.rgba(255, 255, 255, (int)(230 * this.animAlpha))
                : ColorUtil.rgba(255, 255, 255, (int)(30 * this.animAlpha));
            int textColor = isHovered
                ? ColorUtil.rgba(10, 15, 25, (int)(255 * this.animAlpha))
                : ColorUtil.rgba(230, 230, 230, (int)(255 * this.animAlpha));

            float btnW = 64.0f;
            float btnH = 22.0f;
            Render2D.rect(itemX - btnW * 0.5f, itemY - btnH * 0.5f, btnW, btnH, 6.0f, itemBg);
            Render2D.outline(itemX - btnW * 0.5f, itemY - btnH * 0.5f, btnW, btnH, 6.0f, 1.0f, itemBorder);
            String name = emotion.displayName();
            float textW = Fonts.MONTSERRAT_MEDIUM.width(name, 5.5f);
            Fonts.MONTSERRAT_MEDIUM.draw(name, itemX - textW * 0.5f, itemY - 3.0f, 5.5f, textColor);
        }
    }

    public void selectHovered() {
        if (this.selectedIndex >= 0 && this.selectedIndex < this.emotions.size()) {
            Emotions module = Emotions.getInstance();
            if (module != null) {
                module.playEmotion(this.emotions.get(this.selectedIndex));
            }
        }
    }

    public List<Emotion> getEmotions() {
        return this.emotions;
    }
}
