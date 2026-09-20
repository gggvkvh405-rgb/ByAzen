package rtx.kimiko.api.modules.impl.Visuals.particles;

import net.minecraft.util.Identifier;

public final class ParticleConstants {
    public static final ParticleTexture[] TEXTURES = new ParticleTexture[]{
        new ParticleTexture("Точка", Identifier.of("kimiko", "textures/features/particles/point.png")),
        new ParticleTexture("Звезда", Identifier.of("kimiko", "textures/features/particles/star.png")),
        new ParticleTexture("Молния", Identifier.of("kimiko", "textures/features/particles/lighting.png")),
        new ParticleTexture("Крест", Identifier.of("kimiko", "textures/features/particles/cross.png")),
        new ParticleTexture("Корона", Identifier.of("kimiko", "textures/features/particles/crown.png")),
        new ParticleTexture("Сердце", Identifier.of("kimiko", "textures/features/particles/heart.png")),
        new ParticleTexture("Линия", Identifier.of("kimiko", "textures/features/particles/line.png")),
        new ParticleTexture("Ромб", Identifier.of("kimiko", "textures/features/particles/rhombus.png")),
        new ParticleTexture("Доллар", Identifier.of("kimiko", "textures/features/particles/dollar.png")),
        new ParticleTexture("Снежинка", Identifier.of("kimiko", "textures/features/particles/snowflake.png")),
        new ParticleTexture("Треугольник", Identifier.of("kimiko", "textures/features/particles/triangle.png"))
    };

    private ParticleConstants() {}
}
