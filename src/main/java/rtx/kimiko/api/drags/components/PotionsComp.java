package rtx.kimiko.api.drags.components;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Formatting;
import rtx.kimiko.api.drags.DragSystem;
import rtx.kimiko.api.drags.components.ListHudComp;
import rtx.kimiko.api.drags.components.ListHudComp.Row;
import rtx.kimiko.api.modules.impl.Interface.PotionsModule;
import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.render.render2d.Render2D;

public final class PotionsComp
extends ListHudComp {
    private static final RegistryEntry<StatusEffect>[] PREVIEW_EFFECTS = new RegistryEntry[]{StatusEffects.SPEED, StatusEffects.JUMP_BOOST, StatusEffects.REGENERATION, StatusEffects.FIRE_RESISTANCE};
    private static final int NEGATIVE_COLOR = ColorUtil.lerpColor(-3355444, -53714, 0.32f);
    private static final float ICON_SCALE = 0.5f;
    private static final float EFFECT_ICON_SIZE = 8.0f;
    private long previewSwitchMs;
    private int previewIndex;
    private static final int PULSE_THRESHOLD_TICKS = 200;
    private static final long PULSE_PERIOD_MS = 900L;
    private static final float PULSE_MIN_ALPHA = 0.2f;

    public PotionsComp() {
        super("potions", "Potions", PotionsModule.class, 104.0f, 33.0f);
    }

    private static boolean isNegative(RegistryEntry<StatusEffect> registryEntry) {
        if (((StatusEffect)registryEntry.value()).getCategory() == StatusEffectCategory.HARMFUL) {
            return true;
        }
        return registryEntry.value() == StatusEffects.SLOW_FALLING.value();
    }

    private static float smooth(float f) {
        f = Math.max(0.0f, Math.min(1.0f, f));
        return f * f * (3.0f - 2.0f * f);
    }

    @Override
    protected String headerIconGlyph() {
        return "s";
    }

    @Override
    protected List<ListHudComp.Row> collectRows() {
        ArrayList<ListHudComp.Row> arrayList = new ArrayList<ListHudComp.Row>();
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.player == null) {
            if (DragSystem.get().isDragModeActive()) {
                arrayList.add(this.previewRow());
            }
            return arrayList;
        }
        for (StatusEffectInstance statusEffectInstance : minecraftClient.player.getStatusEffects()) {
            if (statusEffectInstance == null) continue;
            RegistryEntry registryEntry = statusEffectInstance.getEffectType();
            Object object = Formatting.strip((String)((StatusEffect)registryEntry.value()).getName().getString());
            int n = statusEffectInstance.getAmplifier();
            if (n > 0) {
                object = (String)object + " " + (n + 1);
            }
            int n2 = PotionsComp.isNegative((RegistryEntry<StatusEffect>)registryEntry) ? NEGATIVE_COLOR : 0;
            arrayList.add(new ListHudComp.Row((Object)registryEntry, (String)object, PotionsComp.formatDuration(statusEffectInstance), (drawContext, f, f2, f3, f4) -> PotionsComp.drawEffectIcon((RegistryEntry<StatusEffect>)registryEntry, f, f2, f3, f4), PotionsComp.expiryPulse(statusEffectInstance), n2));
        }
        if (arrayList.isEmpty() && DragSystem.get().isDragModeActive()) {
            arrayList.add(this.previewRow());
        }
        return arrayList;
    }

    private static String formatDuration(StatusEffectInstance statusEffectInstance) {
        if (statusEffectInstance.isInfinite()) {
            return "**:**";
        }
        int n = Math.max(0, statusEffectInstance.getDuration()) / 20;
        int n2 = n / 60;
        return n2 + ":" + String.format(Locale.ROOT, "%02d", n % 60);
    }

    private static void drawEffectIcon(RegistryEntry<StatusEffect> registryEntry, float f, float f2, float f3, float f4) {
        float f5 = (f3 - 8.0f) * 0.5f;
        Render2D.effectIcon(registryEntry, f + f5, f2 + f5, 8.0f, ColorUtil.multAlpha(-1, f4));
    }

    private static ListHudComp.AlphaPulse expiryPulse(StatusEffectInstance statusEffectInstance) {
        if (statusEffectInstance.isInfinite() || statusEffectInstance.getDuration() > 200) {
            return null;
        }
        return () -> {
            int n = Math.max(0, statusEffectInstance.getDuration());
            if (n > 200) {
                return 1.0f;
            }
            float f = 1.0f - (float)n / 200.0f;
            float f2 = PotionsComp.smooth(f) * 0.8f;
            float f3 = (float)(System.currentTimeMillis() % 900L) / 900.0f;
            float f4 = 0.5f - 0.5f * (float)Math.cos((double)f3 * 2.0 * Math.PI);
            return 1.0f - f2 * f4;
        };
    }

    private ListHudComp.Row previewRow() {
        long l = System.currentTimeMillis();
        if (l - this.previewSwitchMs >= 1000L) {
            this.previewIndex = (this.previewIndex + 1) % PREVIEW_EFFECTS.length;
            this.previewSwitchMs = l;
        }
        RegistryEntry<StatusEffect> registryEntry = PREVIEW_EFFECTS[this.previewIndex];
        return new ListHudComp.Row((Object)"preview", "Example effect", "**:**", (drawContext, f, f2, f3, f4) -> PotionsComp.drawEffectIcon(registryEntry, f, f2, f3, f4));
    }
}

