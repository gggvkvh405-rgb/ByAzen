package rtx.kimiko.api.modules.impl.Visuals.killeffect;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public final class KillEffectParticleSystem {
    public void clear() {
    }

    public void spawnBurst(Vec3d vec3d, int n, int n2, float f, int n3, float f2) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.world == null || vec3d == null) {
            return;
        }
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        float f3 = f / Math.max((float)n / 50.0f, 1.0f) * 0.7f;
        int n4 = Math.clamp((long)n3, 0, 32);
        for (int i = 0; i < n4; ++i) {
            minecraftClient.world.addParticleClient((ParticleEffect)ParticleTypes.END_ROD, vec3d.x, vec3d.y, vec3d.z, threadLocalRandom.nextDouble(-f3, f3), threadLocalRandom.nextDouble((double)(-f3) * 0.25, (double)f3 * 0.55) - (double)f2, threadLocalRandom.nextDouble(-f3, f3));
        }
    }

    public void tick() {
    }
}

