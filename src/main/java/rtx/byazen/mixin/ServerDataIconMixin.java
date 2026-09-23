package rtx.byazen.mixin;

import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.utils.network.ServerIconHarvester;

@Mixin(ServerInfo.class)
public class ServerDataIconMixin {
    @Shadow
    public String address;
    @Shadow
    public String name;

    // В 1.21.11 поле аватарки сервера называется favicon: setFavicon(byte[]).
    // Прежнее имя (setIconBytes) не существует — инъекция молча пропускалась, иконки не собирались.
    @Inject(method="setFavicon", at={@At(value="HEAD")}, require = 0)
    private void byazen_harvestIcon(byte[] bytes, CallbackInfo ci) {
        ServerIconHarvester.capture((String)(this.address != null && !this.address.isBlank() ? this.address : this.name), (byte[])bytes);
    }
}
