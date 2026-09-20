package rtx.kimiko.mixin;

import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.kimiko.utils.network.ServerIconHarvester;

@Mixin(ServerInfo.class)
public class ServerDataIconMixin {
    @Shadow
    public String address;
    @Shadow
    public String name;

    @Inject(method="setIconBytes", at={@At(value="HEAD")}, require = 0)
    private void kimiko_harvestIcon(byte[] bytes, CallbackInfo ci) {
        ServerIconHarvester.capture((String)(this.address != null && !this.address.isBlank() ? this.address : this.name), (byte[])bytes);
    }
}
