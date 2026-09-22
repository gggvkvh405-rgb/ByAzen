package rtx.byazen.api.modules.impl.Visuals;

import java.util.Random;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.DeathScreenEvent;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.player.AttackEntityEvent;
import rtx.byazen.api.events.impl.player.JumpEvent;
import rtx.byazen.api.events.impl.player.TotemPopEvent;
import rtx.byazen.api.events.impl.render.WorldRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.api.modules.impl.Visuals.custompet.entity.CustomPetEntity;
import rtx.byazen.utils.render.post.bloom.BloomRenderer;
import rtx.byazen.utils.render.world.SimpleParticles;

/**
 * Питомцы 2.0 (идея №67 из IDEAS.md): анимации, звуки, реакции на бой и эмоции.
 * <p>
 * Питомец больше не просто идёт следом: он переживает за хозяина. Атака — восторг с искрами,
 * тотем — радостный визг, смерть — тихая грусть. Эмоции летят над головой мягкими светящимися
 * частицами, а внутри самого питомца на короткое время ускоряется анимация — он будто встряхивается.
 */
public final class PetPlus
extends Module {

    private static final int EMOTION_ATTACK = 0;
    private static final int EMOTION_CHEER = 1;
    private static final int EMOTION_SAD = 2;
    private static final int EMOTION_PLAY = 3;

    private static final int COLOR_ATTACK = 0xFFB36A;
    private static final int COLOR_CHEER = 0x8CFFB0;
    private static final int COLOR_SAD = 0x8FB8FF;
    private static final int COLOR_PLAY = 0xFFD9A0;

    private final SeparatorSetting reactSeparator = this.register(new SeparatorSetting("Реакции"));
    public final BooleanSetting combatReact = this.register(new BooleanSetting("Реакции на бой", "Питомец радуется вашим ударам и прыгает на месте.", true));
    public final BooleanSetting totemReact = this.register(new BooleanSetting("Реакция на тотем", "Радостный визг, когда кто-то уцелел на тотеме.", true));
    public final BooleanSetting deathReact = this.register(new BooleanSetting("Реакция на смерть", "Питомец грустит, когда вы погибли.", true));
    public final BooleanSetting jumpReact = this.register(new BooleanSetting("Реакция на прыжок", "Питомец повторяет прыжки хозяина.", true));

    private final SeparatorSetting moodSeparator = this.register(new SeparatorSetting("Настроение"));
    public final BooleanSetting emotions = this.register(new BooleanSetting("Эмоции", "Показывать эмоции питомца мягкими светящимися частицами.", true));
    public final BooleanSetting idlePlay = this.register(new BooleanSetting("Игры при простое", "Питомец сам затевает игру, когда вы стоите без дела.", true));
    public final SliderSetting moodInterval = this.register(new SliderSetting("Частота, с", "Как часто питомец показывает эмоции сам.", 12.0f, 4.0f, 45.0f, 1.0f)).visible(this.idlePlay::getValue);
    public final SliderSetting sparkLevel = this.register(new SliderSetting("Яркость эмоций, %", "Насколько яркие частицы эмоций.", 70.0f, 20.0f, 150.0f, 5.0f)).visible(this.emotions::getValue);

    private final SeparatorSetting soundSeparator = this.register(new SeparatorSetting("Звуки"));
    public final BooleanSetting sounds = this.register(new BooleanSetting("Звуки питомца", "Питомец попискивает, когда радуется или грустит.", true));
    public final SliderSetting soundVolume = this.register(new SliderSetting("Громкость, %", "Громкость звуков питомца.", 65.0f, 10.0f, 100.0f, 5.0f)).visible(this.sounds::getValue);

    private final SeparatorSetting lookSeparator = this.register(new SeparatorSetting("Вид"));
    public final BooleanSetting accentEmotion = this.register(new BooleanSetting("Акцент в эмоциях", "Подмешивать акцент клиента в частицы эмоций.", true));
    public final BooleanSetting sparkTexture = this.register(new BooleanSetting("Искры вместо свечения", "Для эмоций использовать текстуру искры вместо мягкого свечения.", false));

    private final SimpleParticles particles = new SimpleParticles();
    private final Random random = new Random();
    private int idleTimer;
    private long lastReactAt;
    private int lastEmotion = EMOTION_PLAY;

    public PetPlus() {
        super("PetPlus", "Питомцы 2.0: реакции на бой, эмоции, звуки и живые анимации питомца.", Category.VISUALS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onDisable() {
        this.particles.clear();
        this.idleTimer = 0;
    }

    @EventHandler
    public void onAttack(AttackEntityEvent attackEntityEvent) {
        if (!this.isEnabled() || !this.combatReact.getValue() || attackEntityEvent.getTarget() == null) {
            return;
        }
        if (this.mc.player == null || attackEntityEvent.getTarget() == this.mc.player) {
            return;
        }
        this.react(EMOTION_ATTACK, 1.35, 14, 0.28, 1.25f);
    }

    @EventHandler
    public void onTotem(TotemPopEvent totemPopEvent) {
        if (!this.isEnabled() || !this.totemReact.getValue() || totemPopEvent.getEntity() == null) {
            return;
        }
        if (this.mc.player == null || totemPopEvent.getEntity() != this.mc.player) {
            return;
        }
        this.react(EMOTION_CHEER, 1.7, 22, 0.42, 1.45f);
    }

    @EventHandler
    public void onDeath(DeathScreenEvent deathScreenEvent) {
        if (!this.isEnabled() || !this.deathReact.getValue()) {
            return;
        }
        this.react(EMOTION_SAD, 0.6, 40, 0.0, 0.72f);
    }

    @EventHandler
    public void onJump(JumpEvent jumpEvent) {
        if (!this.isEnabled() || !this.jumpReact.getValue() || this.mc.player == null || jumpEvent.getPlayer() != this.mc.player) {
            return;
        }
        if (this.random.nextFloat() > 0.35f) {
            return;
        }
        this.react(EMOTION_PLAY, 1.1, 12, 0.22, 1.1f);
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        this.particles.step();
        if (!this.isEnabled() || this.mc.player == null || this.mc.world == null) {
            this.idleTimer = 0;
            return;
        }
        this.idleTimer += 50;
        float interval = this.moodInterval.getValue() * 1000.0f;
        if (this.idlePlay.getValue() && (float)this.idleTimer >= interval) {
            this.idleTimer = 0;
            boolean calm = this.mc.player.isOnGround() && this.mc.player.getVelocity().horizontalLengthSquared() < 0.004;
            if (calm) {
                this.react(EMOTION_PLAY, 1.0, 14, 0.18, 1.15f);
            }
        }
    }

    private void react(int emotion, double boost, int ticks, double hop, float pitch) {
        long now = System.currentTimeMillis();
        if (now - this.lastReactAt < 260L) {
            return;
        }
        this.lastReactAt = now;
        this.lastEmotion = emotion;
        CustomPet customPet = CustomPet.getInstance();
        CustomPetEntity pet = customPet == null ? null : customPet.localPet();
        if (pet == null) {
            return;
        }
        pet.requestReaction(boost, ticks, hop);
        if (this.sounds.getValue()) {
            pet.playEmotionSound(pitch);
        }
        if (this.emotions.getValue()) {
            this.emitEmotion(pet, emotion);
        }
    }

    private void emitEmotion(CustomPetEntity pet, int emotion) {
        float strength = this.sparkLevel.getValue() / 100.0f;
        int color = PetPlus.emotionColor(emotion);
        if (this.accentEmotion.getValue()) {
            color = BloomRenderer.mix(color, ClientAccent.accentOpaque() & 0xFFFFFF, 0.35f);
        }
        int count = 6 + (int)(6.0f * strength);
        Vec3d origin = new Vec3d(pet.getX(), pet.getY() + (double)pet.getHeight() + 0.35, pet.getZ());
        for (int i = 0; i < count; ++i) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            double radius = 0.12 + this.random.nextDouble() * 0.28;
            Vec3d at = origin.add(Math.cos(angle) * radius, this.random.nextDouble() * 0.22, Math.sin(angle) * radius);
            Vec3d motion = new Vec3d(Math.cos(angle) * 0.006, 0.012 + this.random.nextDouble() * 0.014, Math.sin(angle) * 0.006);
            int alpha = Math.round((90.0f + 70.0f * strength) * (emotion == EMOTION_SAD ? 0.75f : 1.0f));
            this.particles.spawn(at, motion, 700.0f + this.random.nextFloat() * 500.0f,
                    this.random.nextFloat() * 360.0f,
                    this.sparkTexture.getValue() ? BloomRenderer.sparkTexture() : BloomRenderer.glowTexture(),
                    BloomRenderer.withAlpha(color, alpha), 0.0025f, 0.06f);
        }
    }

    private static int emotionColor(int emotion) {
        switch (emotion) {
            case EMOTION_ATTACK: {
                return COLOR_ATTACK;
            }
            case EMOTION_CHEER: {
                return COLOR_CHEER;
            }
            case EMOTION_SAD: {
                return COLOR_SAD;
            }
            default: {
                return COLOR_PLAY;
            }
        }
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent worldRenderEvent) {
        if (!this.isEnabled() || this.particles.size() == 0 || this.mc.world == null || this.mc.gameRenderer == null) {
            return;
        }
        if (this.mc.gameRenderer.getCamera() == null) {
            return;
        }
        this.particles.render(worldRenderEvent.getStack(), this.mc.gameRenderer.getCamera().getCameraPos(),
                this.mc.gameRenderer.getCamera().getRotation(), (float)worldRenderEvent.getPartialTicks(), 0.22f, true);
    }

    /** Последняя показанная эмоция — для HUD-подсказок и тестов. */
    public int lastEmotion() {
        return this.lastEmotion;
    }

    /** Погладить питомца: короткая радость без боя. */
    public static void pet(CustomPetEntity pet) {
        if (pet == null) {
            return;
        }
        pet.requestReaction(1.25, 16, 0.3);
        pet.playEmotionSound(1.3f);
    }
}
