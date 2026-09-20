package rtx.kimiko.api.ui.module;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import rtx.kimiko.IMinecraft;
import rtx.kimiko.api.ui.theme.ClientAccent;
import rtx.kimiko.utils.render.fonts.Fonts;
import rtx.kimiko.utils.render.render2d.Render2D;
import rtx.kimiko.utils.sounds.Sounds;

public final class SearchField {
    private static final String FONT = "montserrat-medium";
    private static final float SIZE = 7.0f;
    private static final float PAD_X = 5.0f;
    private String text = "";
    private String placeholder = "\u041f\u043e\u0438\u0441\u043a...";
    private String iconGlyph = "q";
    private boolean typing;
    private boolean dragging;
    private int cursorPosition;
    private int selectionStart = -1;
    private int selectionEnd = -1;
    private long lastClickTime;
    private float xOffset;
    private float bx;
    private float by;
    private float bw;
    private float bh;
    private float textStartX;
    private float textVisibleW;
    private float hoverT;
    private float focusT;
    private float animCursorX;
    private boolean cursorSnap = true;
    private final List<Float> charAnim = new ArrayList<Float>();
    private static final float REVEAL_SPEED = 3.6f;
    private static final float REVEAL_STAGGER = 0.14f;

    private static int clamp(int n, int n2, int n3) {
        return Math.max(n2, Math.min(n3, n));
    }

    private static float w(String string) {
        return Render2D.textWidth(FONT, string, 7.0f);
    }

    public String getText() {
        return this.text;
    }

    private static int col(int n, int n2, int n3, float f) {
        int n4 = Math.max(0, Math.min(255, Math.round(f)));
        if (n4 <= 0) {
            return 0;
        }
        return new Color(n, n2, n3, n4).getRGB();
    }

    public void setText(String string) {
        this.text = string == null ? "" : string;
        this.cursorPosition = Math.min(this.cursorPosition, this.text.length());
        this.clearSelection();
        this.dragging = false;
        this.cursorSnap = true;
        this.charAnim.clear();
        for (int i = 0; i < this.text.length(); ++i) {
            this.charAnim.add(Float.valueOf(1.0f));
        }
    }

    private static int withAlpha(Color color, float f) {
        int n = SearchField.clampA((float)color.getAlpha() * f);
        return n << 24 | color.getRGB() & 0xFFFFFF;
    }

    private static float clamp01(float f) {
        return f < 0.0f ? 0.0f : (f > 1.0f ? 1.0f : f);
    }

    public void render(DrawContext drawContext, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8) {
        float f9;
        float f10;
        int n;
        float f11;
        int n2;
        float f12;
        this.bx = f;
        this.by = f2;
        this.bw = f3;
        this.bh = f4;
        this.cursorPosition = SearchField.clamp(this.cursorPosition, 0, this.text.length());
        boolean bl = f6 >= f && f6 <= f + f3 && f7 >= f2 && f7 <= f2 + f4;
        float f13 = 1.0f - (float)Math.exp(-f8 * 16.0f);
        float f14 = 1.0f - (float)Math.exp(-f8 * 11.0f);
        this.hoverT += ((bl ? 1.0f : 0.0f) - this.hoverT) * f13;
        this.focusT += ((this.typing ? 1.0f : 0.0f) - this.focusT) * f14;
        float f15 = Math.max(this.hoverT, this.focusT);
        int n3 = ClientAccent.accent(255.0f) & 0xFFFFFF;
        Render2D.rect(f, f2, f3, f4, 4.0f, SearchField.col(0, 0, 0, (40.0f + 12.0f * f15) * f5));
        Color color = new Color(9, 9, 9, 0);
        Color color2 = new Color(9, 12, 14, 0);
        int n4 = SearchField.withAlpha(color, f5);
        int n5 = SearchField.withAlpha(color2, f5);
        Render2D.outline(f, f2, f3, f4, 4.0f, 0.8f, n4, n5, n5, n4);
        float f16 = 7.5f;
        float f17 = f + 5.0f;
        if (this.iconGlyph.isEmpty()) {
            this.textStartX = f + 5.0f + 1.0f;
        } else {
            f12 = f2 + (f4 - f16) * 0.5f + 0.3f;
            int n6 = SearchField.mixRgb(0xB4B4B4, n3, f15);
            int n7 = SearchField.clampA((125.0f + 95.0f * f15) * f5);
            Fonts.KIMIKO.msdf(this.iconGlyph, f17, f12, f16, n7 << 24 | n6);
            this.textStartX = f17 + f16 + 4.0f;
        }
        f12 = f + f3 - 5.0f;
        this.textVisibleW = Math.max(4.0f, f12 - this.textStartX);
        this.updateXOffset();
        float f18 = this.textStartX - this.xOffset;
        float f19 = f2 + (f4 - 7.0f) * 0.5f - 0.5f;
        Render2D.pushScissor(drawContext, this.textStartX - 2.0f, f2, this.textVisibleW + 2.0f, f4);
        if (this.typing && this.hasSelection()) {
            int n8 = this.getStartOfSelection();
            n2 = this.getEndOfSelection();
            float f20 = f18 + SearchField.w(this.text.substring(0, n8));
            f11 = f18 + SearchField.w(this.text.substring(0, n2));
            Render2D.rect(f20, f2 + 2.5f, f11 - f20, f4 - 5.0f, 1.5f, SearchField.col(60, 128, 240, 165.0f * f5));
        }
        float f21 = f8 * 3.6f;
        n2 = 0;
        for (n = 0; n < this.charAnim.size(); ++n) {
            f11 = this.charAnim.get(n).floatValue();
            if (!(f11 < 1.0f)) continue;
            this.charAnim.set(n, Float.valueOf(Math.min(1.0f, f11 + f21)));
            n2 = 1;
        }
        if (!this.text.isEmpty()) {
            n = SearchField.col(255, 255, 255, 235.0f * f5);
            if (n2 == 0) {
                Render2D.text(FONT, this.text, f18, f19, 7.0f, n);
            } else {
                f11 = f19 + 3.5f;
                for (int i = 0; i < this.text.length(); ++i) {
                    f10 = SearchField.clamp01(this.charProgress(i));
                    if (f10 <= 0.0f) continue;
                    f9 = f18 + SearchField.w(this.text.substring(0, i));
                    String string = String.valueOf(this.text.charAt(i));
                    int n9 = SearchField.clampA(235.0f * f5 * f10);
                    if (n9 <= 0) continue;
                    int n10 = n9 << 24 | 0xFFFFFF;
                    if (f10 >= 1.0f) {
                        Render2D.text(FONT, string, f9, f19, 7.0f, n10);
                        continue;
                    }
                    float f22 = SearchField.easeOutBack(f10);
                    float f23 = f9 + SearchField.w(string) * 0.5f;
                    drawContext.getMatrices().pushMatrix();
                    drawContext.getMatrices().translate(f23, f11);
                    drawContext.getMatrices().scale(f22, f22);
                    drawContext.getMatrices().translate(-f23, -f11);
                    Render2D.text(FONT, string, f9, f19, 7.0f, n10);
                    drawContext.getMatrices().popMatrix();
                }
            }
        } else {
            float f24 = 1.0f - this.focusT;
            if (f24 > 0.01f && !this.placeholder.isEmpty()) {
                Render2D.text(FONT, this.placeholder, f18, f19, 7.0f, SearchField.col(255, 255, 255, 95.0f * f5 * f24));
            }
        }
        Render2D.popScissor(drawContext);
        float f25 = SearchField.w(this.text.substring(0, this.cursorPosition));
        if (this.cursorSnap || !this.typing) {
            this.animCursorX = f25;
            this.cursorSnap = false;
        } else {
            this.animCursorX += (f25 - this.animCursorX) * (1.0f - (float)Math.exp(-f8 * 20.0f));
        }
        long l = System.currentTimeMillis();
        if (this.focusT > 0.01f && !this.hasSelection()) {
            f10 = (float)(Math.sin((double)l / 200.0) * 0.5 + 0.5);
            f9 = f18 + this.animCursorX;
            Render2D.rect(f9, f2 + 2.5f, 0.6f, f4 - 5.0f, 0.0f, SearchField.col(255, 255, 255, (70.0f + 185.0f * f10) * this.focusT * f5));
        }
        if (this.dragging) {
            int n11 = this.cursorIndexAt(f6);
            if (this.selectionStart == -1) {
                this.selectionStart = this.cursorPosition;
            }
            this.selectionEnd = this.cursorPosition = n11;
            if (this.selectionStart == this.selectionEnd) {
                this.clearSelection();
            }
        }
    }

    public void blur() {
        this.typing = false;
        this.dragging = false;
        this.clearSelection();
    }

    public boolean hasText() {
        return !this.text.isEmpty();
    }

    public boolean isTyping() {
        return this.typing;
    }

    public boolean charTyped(CharInput charInput) {
        if (!this.typing) {
            return false;
        }
        int n = charInput.codepoint();
        if (Character.isISOControl(n)) {
            return false;
        }
        this.deleteSelectedText();
        this.replaceText(this.cursorPosition, this.cursorPosition, charInput.asString());
        Sounds.play("search_typing");
        return true;
    }

    public boolean keyPressed(KeyInput keyInput) {
        if (!this.typing) {
            return false;
        }
        int n = keyInput.key();
        int n2 = keyInput.modifiers();
        boolean bl = SearchField.ctrlDown(n2);
        boolean bl2 = SearchField.shiftDown(n2);
        if (bl) {
            switch (n) {
                case 65: {
                    this.selectAllText();
                    return true;
                }
                case 67: {
                    this.copyToClipboard();
                    return true;
                }
                case 86: {
                    this.pasteFromClipboard();
                    return true;
                }
                case 88: {
                    this.cutToClipboard();
                    return true;
                }
                case 263: {
                    this.moveCursorTo(this.findPreviousWordBoundary(this.cursorPosition), bl2);
                    return true;
                }
                case 262: {
                    this.moveCursorTo(this.findNextWordBoundary(this.cursorPosition), bl2);
                    return true;
                }
                case 259: {
                    this.deletePrevious(true);
                    return true;
                }
                case 261: {
                    this.deleteNext(true);
                    return true;
                }
            }
            return false;
        }
        switch (n) {
            case 263: {
                if (!bl2 && this.hasSelection()) {
                    this.moveCursorTo(this.getStartOfSelection(), false);
                } else {
                    this.moveCursorTo(this.cursorPosition - 1, bl2);
                }
                return true;
            }
            case 262: {
                if (!bl2 && this.hasSelection()) {
                    this.moveCursorTo(this.getEndOfSelection(), false);
                } else {
                    this.moveCursorTo(this.cursorPosition + 1, bl2);
                }
                return true;
            }
            case 268: {
                this.moveCursorTo(0, bl2);
                return true;
            }
            case 269: {
                this.moveCursorTo(this.text.length(), bl2);
                return true;
            }
            case 259: {
                this.deletePrevious(false);
                return true;
            }
            case 261: {
                this.deleteNext(false);
                return true;
            }
            case 257: {
                this.blur();
                return true;
            }
            case 256: {
                if (!this.text.isEmpty()) {
                    this.setText("");
                } else {
                    this.blur();
                }
                return true;
            }
        }
        return false;
    }

    public SearchField icon(String string) {
        this.iconGlyph = string == null ? "" : string;
        return this;
    }

    private void replaceText(int n, int n2, String string) {
        int n3;
        int n4;
        int n5 = SearchField.clamp(n, 0, this.text.length());
        if (n5 > (n4 = SearchField.clamp(n2, 0, this.text.length()))) {
            n3 = n5;
            n5 = n4;
            n4 = n3;
        }
        this.text = this.text.substring(0, n5) + string + this.text.substring(n4);
        this.cursorPosition = n5 + string.length();
        for (n3 = Math.min(n4, this.charAnim.size()) - 1; n3 >= n5; --n3) {
            this.charAnim.remove(n3);
        }
        for (n3 = 0; n3 < string.length(); ++n3) {
            this.charAnim.add(n5 + n3, Float.valueOf(-((float)n3 * 0.14f)));
        }
        this.clearSelection();
    }

    private boolean isWordCharacter(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    public SearchField placeholder(String string) {
        this.placeholder = string == null ? "" : string;
        return this;
    }

    public boolean mouseClicked(double d, double d2, int n) {
        boolean bl;
        boolean bl2 = bl = d >= (double)this.bx && d <= (double)(this.bx + this.bw) && d2 >= (double)this.by && d2 <= (double)(this.by + this.bh);
        if (bl && n == 0) {
            this.cursorSnap = true;
            long l = System.currentTimeMillis();
            int n2 = this.cursorIndexAt((float)d);
            if (l - this.lastClickTime < 250L) {
                this.typing = true;
                this.dragging = false;
                this.selectAllText();
            } else {
                this.typing = true;
                this.dragging = true;
                this.selectionStart = this.cursorPosition = n2;
                this.selectionEnd = this.cursorPosition;
            }
            this.lastClickTime = l;
            return true;
        }
        if (!bl && n == 0) {
            this.blur();
        }
        return false;
    }

    public void mouseReleased(int n) {
        if (n == 0) {
            this.dragging = false;
        }
    }

    private void clearSelection() {
        this.selectionStart = -1;
        this.selectionEnd = -1;
    }

    private int cursorIndexAt(double d) {
        int n;
        float f = (float)d - this.textStartX + this.xOffset;
        for (n = 0; n < this.text.length(); ++n) {
            float f2 = SearchField.w(this.text.substring(n, n + 1));
            float f3 = SearchField.w(this.text.substring(0, n));
            if (f3 + f2 / 2.0f > f) break;
        }
        return SearchField.clamp(n, 0, this.text.length());
    }

    private int findNextWordBoundary(int n) {
        int n2;
        for (n2 = SearchField.clamp(n, 0, this.text.length()); n2 < this.text.length() && !this.isWordCharacter(this.text.charAt(n2)); ++n2) {
        }
        while (n2 < this.text.length() && this.isWordCharacter(this.text.charAt(n2))) {
            ++n2;
        }
        return n2;
    }

    private void selectAllText() {
        if (this.text.isEmpty()) {
            this.clearSelection();
            this.cursorPosition = 0;
            return;
        }
        this.selectionStart = 0;
        this.selectionEnd = this.text.length();
        this.cursorPosition = this.text.length();
    }

    private void copyToClipboard() {
        if (this.hasSelection()) {
            IMinecraft.mc.keyboard.setClipboard(this.getSelectedText());
        }
    }

    private int getStartOfSelection() {
        return Math.min(this.selectionStart, this.selectionEnd);
    }

    private boolean hasSelection() {
        return this.selectionStart != -1 && this.selectionEnd != -1 && this.selectionStart != this.selectionEnd;
    }

    private float charProgress(int n) {
        return n >= 0 && n < this.charAnim.size() ? this.charAnim.get(n).floatValue() : 1.0f;
    }

    private void deleteSelectedText() {
        if (this.hasSelection()) {
            this.replaceText(this.getStartOfSelection(), this.getEndOfSelection(), "");
        }
    }

    private void cutToClipboard() {
        if (!this.hasSelection()) {
            return;
        }
        IMinecraft.mc.keyboard.setClipboard(this.getSelectedText());
        this.deleteSelectedText();
    }

    private String getSelectedText() {
        return this.text.substring(this.getStartOfSelection(), this.getEndOfSelection());
    }

    private void pasteFromClipboard() {
        String string = IMinecraft.mc.keyboard.getClipboard();
        if (string == null || string.isEmpty()) {
            return;
        }
        String string2 = string.replace("\r", "").replace("\n", " ");
        this.deleteSelectedText();
        this.replaceText(this.cursorPosition, this.cursorPosition, string2);
    }

    private void deletePrevious(boolean bl) {
        if (this.hasSelection()) {
            this.deleteSelectedText();
            return;
        }
        if (this.cursorPosition <= 0) {
            return;
        }
        int n = bl ? this.findPreviousWordBoundary(this.cursorPosition) : this.cursorPosition - 1;
        this.replaceText(n, this.cursorPosition, "");
    }

    private int getEndOfSelection() {
        return Math.max(this.selectionStart, this.selectionEnd);
    }

    private void moveCursorTo(int n, boolean bl) {
        int n2 = SearchField.clamp(n, 0, this.text.length());
        if (bl) {
            if (this.selectionStart == -1) {
                this.selectionStart = this.cursorPosition;
            }
            this.selectionEnd = this.cursorPosition = n2;
            if (this.selectionStart == this.selectionEnd) {
                this.clearSelection();
            }
        } else {
            this.cursorPosition = n2;
            this.clearSelection();
        }
    }

    private int findPreviousWordBoundary(int n) {
        int n2;
        for (n2 = SearchField.clamp(n, 0, this.text.length()); n2 > 0 && !this.isWordCharacter(this.text.charAt(n2 - 1)); --n2) {
        }
        while (n2 > 0 && this.isWordCharacter(this.text.charAt(n2 - 1))) {
            --n2;
        }
        return n2;
    }

    private void updateXOffset() {
        float f;
        float f2 = SearchField.w(this.text.substring(0, Math.min(this.cursorPosition, this.text.length())));
        if (f2 < this.xOffset) {
            this.xOffset = Math.max(0.0f, f2 - 6.0f);
        } else if (f2 - this.xOffset > this.textVisibleW - 4.0f) {
            this.xOffset = f2 - (this.textVisibleW - 4.0f) + 6.0f;
        }
        if (this.xOffset < 0.0f) {
            this.xOffset = 0.0f;
        }
        if (this.xOffset > (f = Math.max(0.0f, SearchField.w(this.text) - this.textVisibleW + 4.0f))) {
            this.xOffset = f;
        }
    }

    private static float easeOutBack(float f) {
        float f2 = 3.6f;
        float f3 = f2 + 1.0f;
        float f4 = f - 1.0f;
        return 1.0f + f3 * f4 * f4 * f4 + f2 * f4 * f4;
    }

    private static int mixRgb(int n, int n2, float f) {
        f = f < 0.0f ? 0.0f : (f > 1.0f ? 1.0f : f);
        int n3 = n >> 16 & 0xFF;
        int n4 = n >> 8 & 0xFF;
        int n5 = n & 0xFF;
        int n6 = n2 >> 16 & 0xFF;
        int n7 = n2 >> 8 & 0xFF;
        int n8 = n2 & 0xFF;
        int n9 = Math.round((float)n3 + (float)(n6 - n3) * f);
        int n10 = Math.round((float)n4 + (float)(n7 - n4) * f);
        int n11 = Math.round((float)n5 + (float)(n8 - n5) * f);
        return n9 << 16 | n10 << 8 | n11;
    }

    private static boolean shiftDown(int n) {
        if ((n & 1) != 0) {
            return true;
        }
        long l = IMinecraft.mc.getWindow().getHandle();
        return GLFW.glfwGetKey((long)l, (int)340) == 1 || GLFW.glfwGetKey((long)l, (int)344) == 1;
    }

    private static boolean ctrlDown(int n) {
        if ((n & 2) != 0) {
            return true;
        }
        long l = IMinecraft.mc.getWindow().getHandle();
        return GLFW.glfwGetKey((long)l, (int)341) == 1 || GLFW.glfwGetKey((long)l, (int)345) == 1;
    }

    private static int clampA(float f) {
        return Math.max(0, Math.min(255, Math.round(f)));
    }

    private void deleteNext(boolean bl) {
        if (this.hasSelection()) {
            this.deleteSelectedText();
            return;
        }
        if (this.cursorPosition >= this.text.length()) {
            return;
        }
        int n = bl ? this.findNextWordBoundary(this.cursorPosition) : this.cursorPosition + 1;
        this.replaceText(this.cursorPosition, n, "");
    }

    public void focus() {
        this.typing = true;
        this.dragging = false;
        this.cursorPosition = this.text.length();
        this.clearSelection();
        this.cursorSnap = true;
    }
}

