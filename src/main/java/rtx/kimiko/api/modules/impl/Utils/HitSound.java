package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import rtx.kimiko.api.events.impl.player.AttackEntityEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;
import rtx.kimiko.api.modules.settings.impl.ModeSetting;
import rtx.kimiko.api.modules.settings.impl.NumberSetting;
import rtx.kimiko.api.modules.settings.impl.SeparatorSetting;
import rtx.kimiko.utils.sounds.SoundManager;

public class HitSound
extends Module {
    private final SeparatorSetting soundSeparator = this.register(new SeparatorSetting("\u0417\u0432\u0443\u043a"));
    private final ModeSetting soundType = this.register(new ModeSetting("\u0417\u0432\u0443\u043a", "\u0422\u0438\u043f \u0437\u0432\u0443\u043a\u0430 \u043f\u043e\u043f\u0430\u0434\u0430\u043d\u0438\u044f.", "\u0421\u0442\u043e\u043d\u044b", "\u0421\u0442\u043e\u043d\u044b", "\u041c\u0435\u0442\u0430\u043b\u043b", "\u041a\u0440\u0438\u043c\u0438\u043d\u0430\u043b"));
    private final NumberSetting volume = this.register(new NumberSetting("\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c", "\u0413\u0440\u043e\u043c\u043a\u043e\u0441\u0442\u044c \u0437\u0432\u0443\u043a\u0430.", 1.0, 0.1, 2.0, 0.1));

    public HitSound() {
        super("Hit Sound", "\u041f\u0440\u043e\u0438\u0433\u0440\u044b\u0432\u0430\u0435\u0442 \u0437\u0432\u0443\u043a \u043f\u0440\u0438 \u043f\u043e\u043f\u0430\u0434\u0430\u043d\u0438\u0438.", Category.UTILS);
    }

    @EventHandler
    private void onAttack(AttackEntityEvent attackEntityEvent) {
        EntityHitResult entityHitResult;
        if (attackEntityEvent.isSynthetic() || !(attackEntityEvent.getTarget() instanceof LivingEntity)) {
            return;
        }
        HitResult hitResult = this.mc.crosshairTarget;
        if (!(hitResult instanceof EntityHitResult) || (entityHitResult = (EntityHitResult)hitResult).getEntity() != attackEntityEvent.getTarget()) {
            return;
        }
        this.playSelectedSound();
    }

    private void playSelectedSound() {
        float f = this.volume.getFloat();
        if (this.soundType.is("\u041c\u0435\u0442\u0430\u043b\u043b")) {
            SoundManager.playSound(SoundManager.METALLIC, f, 1.0f);
        } else if (this.soundType.is("\u041a\u0440\u0438\u043c\u0438\u043d\u0430\u043b")) {
            SoundManager.playSound(SoundManager.CRIME, f, 1.0f);
        } else {
            switch (ThreadLocalRandom.current().nextInt(4)) {
                case 0: {
                    SoundManager.playSound(SoundManager.MOAN1, f, 1.0f);
                    break;
                }
                case 1: {
                    SoundManager.playSound(SoundManager.MOAN2, f, 1.0f);
                    break;
                }
                case 2: {
                    SoundManager.playSound(SoundManager.MOAN3, f, 1.0f);
                    break;
                }
                default: {
                    SoundManager.playSound(SoundManager.MOAN4, f, 1.0f);
                }
            }
        }
    }
}

