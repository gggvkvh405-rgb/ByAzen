package rtx.kimiko.mixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.kimiko.api.modules.impl.Utils.Globals;
import rtx.kimiko.api.modules.impl.Visuals.BetterMinecraft;
import rtx.kimiko.utils.animations.TabListAnimationAccess;
import rtx.kimiko.utils.net.ClientPresence;
import rtx.kimiko.utils.render.TabBadgeRenderer;

@Mixin(PlayerListHud.class)
public abstract class PlayerTabOverlayMixin
implements TabListAnimationAccess {
    @Unique
    private static final long KIMIKO_TAB_ANIMATION_MS = 300L;
    @Unique
    private long kimiko_animationStart;
    @Unique
    private long kimiko_animationDuration;
    @Unique
    private float kimiko_animationFrom;
    @Unique
    private float kimiko_animationTarget;
    @Unique
    private boolean kimiko_visible;
    @Unique
    private boolean kimiko_scaledForAnimation;

    @Inject(method="setVisible", at={@At(value="HEAD")}, require = 0)
    private void kimiko_trackVisibility(boolean visible, CallbackInfo ci) {
        if (visible != this.kimiko_visible) {
            float current = this.kimiko_currentScale();
            this.kimiko_visible = visible;
            this.kimiko_animationFrom = current;
            this.kimiko_animationTarget = visible ? 1.0f : 0.0f;
            this.kimiko_animationStart = System.currentTimeMillis();
            this.kimiko_animationDuration = Math.max(1L, (long)Math.round(300.0f * Math.abs(this.kimiko_animationTarget - this.kimiko_animationFrom)));
        }
    }

    @Inject(method="render", at={@At(value="HEAD")}, require = 0)
    private void kimiko_beginTabAnimation(DrawContext graphics, int windowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        this.kimiko_scaledForAnimation = BetterMinecraft.tabAnimationEnabled();
        if (!this.kimiko_scaledForAnimation) {
            return;
        }
        float scale = Math.max(0.01f, Math.min(1.15f, this.kimiko_currentScale()));
        graphics.getMatrices().pushMatrix();
        graphics.getMatrices().translate((float)windowWidth / 2.0f, 10.0f);
        graphics.getMatrices().scale(scale, scale);
        graphics.getMatrices().translate((float)(-windowWidth) / 2.0f, -10.0f);
    }

    @Inject(method="render", at={@At(value="RETURN")}, require = 0)
    private void kimiko_endTabAnimation(DrawContext graphics, int windowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        if (this.kimiko_scaledForAnimation) {
            graphics.getMatrices().popMatrix();
            this.kimiko_scaledForAnimation = false;
        }
    }

    @WrapOperation(method="render", at={@At(value="INVOKE", target="Lnet/minecraft/client/font/TextRenderer;method_27525(Lnet/minecraft/class_5348;)I", ordinal=0)}, require = 0)
    private int kimiko_includeBadgeInFullNameWidth(TextRenderer font, StringVisitable fullServerName, Operation<Integer> original, @Local PlayerListEntry playerInfo) {
        int fullServerNameWidth = (Integer)original.call(new Object[]{font, fullServerName});
        if (!Globals.tabBadge()) {
            return fullServerNameWidth;
        }
        return fullServerNameWidth + TabBadgeRenderer.extraWidth();
    }

    @WrapOperation(method="render", at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/DrawContext;method_27535(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V")}, require = 0)
    private void kimiko_drawFullTabName(DrawContext graphics, TextRenderer font, Text fullServerName, int x, int y, int color, Operation<Void> original, @Local PlayerListEntry playerInfo) {
        if (!Globals.tabBadge()) {
            original.call(new Object[]{graphics, font, fullServerName, x, y, color});
            return;
        }
        original.call(new Object[]{graphics, font, fullServerName, x + TabBadgeRenderer.extraWidth(), y, color});
        if (PlayerTabOverlayMixin.kimiko_hasTabBadge(playerInfo)) {
            TabBadgeRenderer.drawBadge((DrawContext)graphics, (TextRenderer)font, (int)x, (int)y);
        }
    }

    @Unique
    private static boolean kimiko_hasTabBadge(PlayerListEntry playerInfo) {
        return playerInfo != null && Globals.tabBadge() && ClientPresence.INSTANCE.isKimikoUser(playerInfo.getProfile().name());
    }

    @Override
    public boolean kimiko_shouldRenderClosingTab() {
        return BetterMinecraft.tabAnimationEnabled() && !this.kimiko_visible && this.kimiko_currentScale() > 0.01f;
    }

    @Unique
    private float kimiko_currentScale() {
        long duration = Math.max(1L, this.kimiko_animationDuration);
        float progress = Math.min(1.0f, (float)(System.currentTimeMillis() - this.kimiko_animationStart) / (float)duration);
        float eased = this.kimiko_animationTarget > this.kimiko_animationFrom ? this.kimiko_easeOutBack(progress) : this.kimiko_easeInBack(progress);
        return this.kimiko_animationFrom + (this.kimiko_animationTarget - this.kimiko_animationFrom) * eased;
    }

    @Unique
    private float kimiko_easeOutBack(float value) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float t = value - 1.0f;
        return 1.0f + c3 * t * t * t + c1 * t * t;
    }

    @Unique
    private float kimiko_easeInBack(float value) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return c3 * value * value * value - c1 * value * value;
    }
}

