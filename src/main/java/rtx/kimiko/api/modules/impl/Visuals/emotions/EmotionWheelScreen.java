package rtx.kimiko.api.modules.impl.Visuals.emotions;

import java.util.List;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import rtx.kimiko.api.modules.impl.Visuals.Emotions;
import rtx.kimiko.api.ui.BaseScreen;

public final class EmotionWheelScreen extends BaseScreen {
    private final Emotions module;
    private final EmotionWheel wheel;

    public EmotionWheelScreen(Emotions emotions, List<Emotion> list) {
        super(Text.literal("Эмоции"));
        this.module = emotions;
        this.wheel = new EmotionWheel(list);
    }

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        if (this.wheel != null) {
            this.wheel.render(drawContext, true);
        }
        if (this.module != null && !this.module.isMenuKeyDown()) {
            if (this.wheel != null) {
                this.wheel.selectHovered();
            }
            this.close();
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        if (click != null && click.button() == 0 && this.wheel != null) {
            this.wheel.selectHovered();
            this.close();
            return true;
        }
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
