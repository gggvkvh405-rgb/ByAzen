package rtx.byazen.api.modules.impl.Visuals.particles;

import net.minecraft.util.Identifier;

public final class ParticleConstants {
    public static final ParticleTexture[] TEXTURES = new ParticleTexture[]{
        new ParticleTexture("Точка", Identifier.of("byazen", "textures/features/particles/point.png")),
        new ParticleTexture("Звезда", Identifier.of("byazen", "textures/features/particles/star.png")),
        new ParticleTexture("Молния", Identifier.of("byazen", "textures/features/particles/lighting.png")),
        new ParticleTexture("Крест", Identifier.of("byazen", "textures/features/particles/cross.png")),
        new ParticleTexture("Корона", Identifier.of("byazen", "textures/features/particles/crown.png")),
        new ParticleTexture("Сердце", Identifier.of("byazen", "textures/features/particles/heart.png")),
        new ParticleTexture("Линия", Identifier.of("byazen", "textures/features/particles/line.png")),
        new ParticleTexture("Ромб", Identifier.of("byazen", "textures/features/particles/rhombus.png")),
        new ParticleTexture("Доллар", Identifier.of("byazen", "textures/features/particles/dollar.png")),
        new ParticleTexture("Снежинка", Identifier.of("byazen", "textures/features/particles/snowflake.png")),
        new ParticleTexture("Треугольник", Identifier.of("byazen", "textures/features/particles/triangle.png")),
        new ParticleTexture("Свечение", Identifier.of("byazen", "textures/features/particles/glow.png")),
        new ParticleTexture("Искра", Identifier.of("byazen", "textures/features/particles/spark.png"))
    };

    private ParticleConstants() {}
}
