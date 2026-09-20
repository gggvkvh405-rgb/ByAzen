package rtx.byazen.api.modules.impl.Visuals.custompet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.ReloadableTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;
import rtx.byazen.api.mods.geckolib.cache.GeckoLibResources;

public final class CustomPetWarmup {
    private static final Identifier TEXTURE = Identifier.of((String)"byazen", (String)"textures/entity/frog/custom_pet.png");
    private static final Identifier MODEL = Identifier.of((String)"byazen", (String)"frog/custom_pet");

    private CustomPetWarmup() {
    }

    public static void warmup() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) {
            return;
        }
        try {
            if (minecraftClient.getTextureManager() != null) {
                minecraftClient.getTextureManager().registerTexture(TEXTURE, (ReloadableTexture)new ResourceTexture(TEXTURE));
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            GeckoLibResources.getBakedModels().getModel(MODEL);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

