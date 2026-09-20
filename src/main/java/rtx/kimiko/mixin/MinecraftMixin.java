package rtx.kimiko.mixin;

import java.io.File;
import java.io.IOException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.session.Session;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rtx.kimiko.api.events.EventBus;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.mods.acountswiher.IasService;
import rtx.kimiko.api.ui.window.WindowTitleAnimation;
import rtx.kimiko.utils.network.Network;
import rtx.kimiko.utils.session.SessionChanger;

@Mixin(MinecraftClient.class)
public abstract class MinecraftMixin {
    private static final String KIMIKO_DEFAULTS_MARKER = ".kimiko-defaults-applied";
    @Shadow
    @Final
    public GameOptions options;
    @Shadow
    @Final
    public File runDirectory;
    @Shadow
    @Mutable
    private Session session;

    private void kimiko_setSession(Session newSession) {
        this.session = newSession;
    }

    @Inject(method="<init>", at={@At(value="TAIL")}, require = 0)
    private void kimiko_initIas(CallbackInfo ci) {
        SessionChanger.setSessionSetter(this::kimiko_setSession);
        IasService.ensureInitialized();
    }

    @Inject(method="close", at={@At(value="HEAD")}, require = 0)
    private void kimiko_closeIas(CallbackInfo ci) {
        IasService.close();
    }

    @Inject(method="isMultiplayerEnabled", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void kimiko_enableMultiplayer(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
    }

    @Inject(method="tick", at={@At(value="HEAD")}, require = 0)
    private void kimiko_preTickEvent(CallbackInfo ci) {
        Network.tick();
        WindowTitleAnimation.get().tick();
        EventBus.get().post(new TickEvent(TickEvent.Phase.PRE));
    }

    @Inject(method="tick", at={@At(value="RETURN")}, require = 0)
    private void kimiko_postTickEvent(CallbackInfo ci) {
        EventBus.get().post(new TickEvent(TickEvent.Phase.POST));
    }
}
