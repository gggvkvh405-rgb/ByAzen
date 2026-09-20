package rtx.byazen.mixin;
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
import rtx.byazen.api.modules.impl.Utils.Globals;
import rtx.byazen.api.modules.impl.Visuals.BetterMinecraft;
import rtx.byazen.utils.animations.TabListAnimationAccess;
import rtx.byazen.utils.net.ClientPresence;
import rtx.byazen.utils.render.TabBadgeRenderer;

@Mixin(PlayerListHud.class)
public abstract class PlayerTabOverlayMixin
implements TabListAnimationAccess {
    @Unique
    private static final long BYAZEN_TAB_ANIMATION_MS = 300L;
    @Unique
    private long byazen_animationStart;
    @Unique
    private long byazen_animationDuration;
    @Unique
    private float byazen_animationFrom;
    @Unique
    private float byazen_animationTarget;
    @Unique
    private boolean byazen_visible;
    @Unique
    private boolean byazen_scaledForAnimation;

    @Inject(method="setVisible", at={@At(value="HEAD")}, require = 0)
    private void byazen_trackVisibility(boolean visible, CallbackInfo ci) {
        if (visible != this.byazen_visible) {
            float current = this.byazen_currentScale();
            this.byazen_visible = visible;
            this.byazen_animationFrom = current;
            this.byazen_animationTarget = visible ? 1.0f : 0.0f;
            this.byazen_animationStart = System.currentTimeMillis();
            this.byazen_animationDuration = Math.max(1L, (long)Math.round(300.0f * Math.abs(this.byazen_animationTarget - this.byazen_animationFrom)));
        }
    }

    @Inject(method="render", at={@At(value="HEAD")}, require = 0)
    private void byazen_beginTabAnimation(DrawContext graphics, int windowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        this.byazen_scaledForAnimation = BetterMinecraft.tabAnimationEnabled();
        if (!this.byazen_scaledForAnimation) {
            return;
        }
        float scale = Math.max(0.01f, Math.min(1.15f, this.byazen_currentScale()));
        graphics.getMatrices().pushMatrix();
        graphics.getMatrices().translate((float)windowWidth / 2.0f, 10.0f);
        graphics.getMatrices().scale(scale, scale);
        graphics.getMatrices().translate((float)(-windowWidth) / 2.0f, -10.0f);
    }

    @Inject(method="render", at={@At(value="RETURN")}, require = 0)
    private void byazen_endTabAnimation(DrawContext graphics, int windowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {
        if (this.byazen_scaledForAnimation) {
            graphics.getMatrices().popMatrix();
            this.byazen_scaledForAnimation = false;
        }
    }

    @WrapOperation(method="render", at={@At(value="INVOKE", target="Lnet/minecraft/client/font/TextRenderer;method_27525(Lnet/minecraft/class_5348;)I", ordinal=0)}, require = 0)
    private int byazen_includeBadgeInFullNameWidth(TextRenderer font, StringVisitable fullServerName, Operation<Integer> original, @Local PlayerListEntry playerInfo) {
        int fullServerNameWidth = (Integer)original.call(new Object[]{font, fullServerName});
        if (!Globals.tabBadge()) {
            return fullServerNameWidth;
        }
        return fullServerNameWidth + TabBadgeRenderer.extraWidth();
    }

    @WrapOperation(method="render", at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/DrawContext;method_27535(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;III)V")}, require = 0)
    private void byazen_drawFullTabName(DrawContext graphics, TextRenderer font, Text fullServerName, int x, int y, int color, Operation<Void> original, @Local PlayerListEntry playerInfo) {
        if (!Globals.tabBadge()) {
            original.call(new Object[]{graphics, font, fullServerName, x, y, color});
            return;
        }
        original.call(new Object[]{graphics, font, fullServerName, x + TabBadgeRenderer.extraWidth(), y, color});
        if (PlayerTabOverlayMixin.byazen_hasTabBadge(playerInfo)) {
            TabBadgeRenderer.drawBadge((DrawContext)graphics, (TextRenderer)font, (int)x, (int)y);
        }
    }

    @Unique
    private static boolean byazen_hasTabBadge(PlayerListEntry playerInfo) {
        return playerInfo != null && Globals.tabBadge() && ClientPresence.INSTANCE.isByAzenUser(playerInfo.getProfile().name());
    }

    @Override
    public boolean byazen_shouldRenderClosingTab() {
        return BetterMinecraft.tabAnimationEnabled() && !this.byazen_visible && this.byazen_currentScale() > 0.01f;
    }

    @Unique
    private float byazen_currentScale() {
        long duration = Math.max(1L, this.byazen_animationDuration);
        float progress = Math.min(1.0f, (float)(System.currentTimeMillis() - this.byazen_animationStart) / (float)duration);
        float eased = this.byazen_animationTarget > this.byazen_animationFrom ? this.byazen_easeOutBack(progress) : this.byazen_easeInBack(progress);
        return this.byazen_animationFrom + (this.byazen_animationTarget - this.byazen_animationFrom) * eased;
    }

    @Unique
    private float byazen_easeOutBack(float value) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float t = value - 1.0f;
        return 1.0f + c3 * t * t * t + c1 * t * t;
    }

    @Unique
    private float byazen_easeInBack(float value) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return c3 * value * value * value - c1 * value * value;
    }
}

