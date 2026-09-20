package rtx.byazen.api.mods.waveycapes;
import net.minecraft.entity.LivingEntity;
import rtx.byazen.api.mods.waveycapes.CapeNodeCollector;
import rtx.byazen.api.mods.waveycapes.CustomCapeRenderer;
import rtx.byazen.api.mods.waveycapes.delegate.PlayerDelegate;
import rtx.byazen.api.mods.waveycapes.support.AnimationSupport;
import rtx.byazen.api.mods.waveycapes.support.SupportManager;
import rtx.byazen.api.mods.waveycapes.versionless.ModBase;
import rtx.byazen.api.mods.waveycapes.versionless.nms.MinecraftPlayer;
import rtx.byazen.api.mods.waveycapes.versionless.util.Vector3;

public abstract class WaveyCapesBase
extends ModBase {
    public static WaveyCapesBase INSTANCE;
    private final CapeNodeCollector capeNodeCollector = new CapeNodeCollector();
    private final CustomCapeRenderer renderer = new CustomCapeRenderer();

    @Override
    public void init() {
        INSTANCE = this;
        super.init();
        this.initSupportHooks();
    }

    public CapeNodeCollector getCapeNodeCollector() {
        return this.capeNodeCollector;
    }

    @Override
    public void initSupportHooks() {
    }

    public CustomCapeRenderer getRenderer() {
        return this.renderer;
    }

    public static WaveyCapesBase getINSTANCE() {
        return INSTANCE;
    }

    @Override
    public Vector3 applyModAnimations(MinecraftPlayer minecraftPlayer, Vector3 vector3) {
        for (AnimationSupport animationSupport : SupportManager.animationSupport) {
            vector3 = animationSupport.applyAnimationChanges((LivingEntity)((PlayerDelegate)minecraftPlayer).getPlayer(), 0.0f, vector3);
        }
        return vector3;
    }
}

