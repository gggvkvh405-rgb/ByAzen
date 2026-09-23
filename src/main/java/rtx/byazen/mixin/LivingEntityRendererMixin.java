package rtx.byazen.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rtx.byazen.api.modules.impl.Utils.Globals;
import rtx.byazen.api.modules.impl.Utils.StreamerMode;
import rtx.byazen.api.modules.impl.Visuals.HitColor;
import rtx.byazen.api.modules.impl.Visuals.NameTags;
import rtx.byazen.api.modules.impl.Visuals.SelfTag;
import rtx.byazen.utils.net.ClientPresence;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {
    @Inject(method="updateRenderState*", at={@At(value="RETURN")}, cancellable=true, require = 0)
    private void byazen_onUpdateRenderState(T entity, S state, float tickDelta, CallbackInfo ci) {
        boolean nameHidden = NameTags.hidesNameTagFor(entity);
        if (nameHidden) {
            state.displayName = null;
        }
        if (!nameHidden && SelfTag.active()) {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (entity == mc.player && state.displayName == null && !mc.options.getPerspective().isFirstPerson()) {
                state.displayName = entity.getDisplayName();
                state.nameLabelPos = entity.getAttachments().getPointNullable(EntityAttachmentType.NAME_TAG, 0, entity.getYaw());
            }
        }
        MinecraftClient mcRef = MinecraftClient.getInstance();
        if (state.displayName != null && entity == mcRef.player) {
            state.displayName = StreamerMode.applySelfRank(state.displayName);
        }
        if (state.displayName != null && entity instanceof PlayerEntity badgePlayer) {
            if (Globals.tagsBadge() && ClientPresence.INSTANCE.isByAzenUser(badgePlayer.getGameProfile().name())) {
                state.displayName = Text.empty().append(Text.literal("\ue000").setStyle(Style.EMPTY.withFont(StyleSpriteSource.DEFAULT).withColor(9081843))).append(Text.literal(" ").append(state.displayName)).append(Text.literal("  "));
            }
        }
        HitColor.captureTint(state, entity);
        if (HitColor.shouldTint(entity)) {
            state.hurt = false;
        }
    }

    // В 1.21.11 итоговый цвет модели (в том числе оттенок урона) считает getMixColor,
    // а не getHurtColor — прежняя цель не существовала, свой цвет урона не применялся.
    @Inject(method="getMixColor", at={@At(value="RETURN")}, cancellable=true, require = 0)
    private void byazen_customHurtTint(S state, CallbackInfoReturnable<Integer> cir) {
        Integer tint = HitColor.tintFor(state);
        if (tint != null) {
            cir.setReturnValue(tint);
        }
    }
}
