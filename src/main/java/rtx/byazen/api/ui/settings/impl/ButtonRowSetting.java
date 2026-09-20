package rtx.byazen.api.ui.settings.impl;

import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.RenderHelper;
import rtx.byazen.utils.render.fonts.Fonts;

public class ButtonRowSetting implements Setting {
    public static final float HEIGHT = 18.0f;
    private final ButtonSetting backend;

    public ButtonRowSetting(ButtonSetting backend) {
        this.backend = backend;
    }

    @Override
    public String name() {
        return this.backend.getName();
    }

    @Override
    public float height() {
        return 18.0f;
    }

    @Override
    public void render(float x, float y, float width, float alpha) {
        String label = this.backend.getLabel();
        if (label == null || label.isEmpty()) {
            label = "Открыть";
        }
        float buttonWidth = Fonts.MONTSERRAT_MEDIUM.width(label, 6.0f) + 10.0f;
        float buttonX = x + width - buttonWidth - 4.0f;
        RenderHelper.drawName(this.backend.getName(), x, y, buttonX - (x + 6.0f) - 4.0f, alpha);
        RenderHelper.drawBtn(buttonX, y + 2.0f, buttonWidth, 12.0f, label, alpha);
    }

    @Override
    public boolean isVisible() {
        return this.backend.isVisible();
    }

    @Override
    public boolean click(float mouseX, float mouseY, float x, float y, float width) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height()) {
            this.backend.click();
            return true;
        }
        return false;
    }

    @Override
    public float preferredWidth() {
        return Fonts.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.0f) + 48.0f;
    }
}
