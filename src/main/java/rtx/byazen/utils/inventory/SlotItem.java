package rtx.byazen.utils.inventory;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

public enum SlotItem {
    POTION_HLOPUSHKA("\u0425\u043b\u043e\u043f\u0443\u0448\u043a\u0430", 0xFF3232, List.of(SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.SLOWNESS, 200, 9), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.SPEED, 400, 4), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.BLINDNESS, 100, 9), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.GLOWING, 3600, 0))),
    POTION_RADIATION("\u0420\u0430\u0434\u0438\u0430\u0446\u0438\u044f", 0xFF3232, List.of(SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.POISON, 1200, 1), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.WITHER, 1200, 1), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.SLOWNESS, 1800, 2), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.HUNGER, 1200, 4), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.GLOWING, 2400, 0))),
    POTION_SLEEP("\u0421\u043d\u043e\u0442\u0432\u043e\u0440\u043d\u043e\u0435", 0xFF3232, List.of(SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.WEAKNESS, 1800, 1), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.MINING_FATIGUE, 200, 1), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.WITHER, 1800, 2), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.BLINDNESS, 200, 0))),
    POTION_HOLY_WATER("\u0421\u0432\u044f\u0442\u0430\u044f \u0432\u043e\u0434\u0430", 3329330, List.of(SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.REGENERATION, 900, 1), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.INVISIBILITY, 12000, 1), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.INSTANT_HEALTH, 0, 1))),
    POTION_RAGE("\u0413\u043d\u0435\u0432\u0430", 3329330, List.of(SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.STRENGTH, 600, 4), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.SLOWNESS, 600, 3))),
    POTION_PALADIN("\u041f\u0430\u043b\u043b\u0430\u0434\u0438\u043d\u0430", 3329330, List.of(SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.RESISTANCE, 12000, 0), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.FIRE_RESISTANCE, 12000, 0), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.HEALTH_BOOST, 1200, 2), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.INVISIBILITY, 18000, 0))),
    POTION_ASSASSIN("\u0410\u0441\u0441\u0430\u0441\u0438\u043d\u0430", 3329330, List.of(SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.STRENGTH, 1200, 3), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.SPEED, 6000, 2), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.HASTE, 1200, 0), SlotItem.sig((RegistryEntry<StatusEffect>)StatusEffects.INSTANT_DAMAGE, 0, 1)));

    private final String displayName;
    private final int defaultColor;
    private final List<SlotItem.EffectSignature> effectSignatures;

    private SlotItem(String displayName, int defaultColor, List<SlotItem.EffectSignature> effectSignatures) {
        this.displayName = displayName;
        this.defaultColor = defaultColor;
        this.effectSignatures = effectSignatures;
    }

    private static SlotItem.EffectSignature sig(RegistryEntry<StatusEffect> registryEntry, int n, int n2) {
        return new SlotItem.EffectSignature(registryEntry, n, n2);
    }

    public static SlotItem match(ItemStack itemStack, SlotItem.Group group) {
        if (group != SlotItem.Group.POTION || itemStack == null || itemStack.isEmpty()) {
            return null;
        }
        if (itemStack.getItem() != Items.SPLASH_POTION && itemStack.getItem() != Items.LINGERING_POTION) {
            return null;
        }
        if (SlotItem.isPotionSectionButton(itemStack)) {
            return null;
        }
        for (SlotItem slotItem : SlotItem.values()) {
            if (!slotItem.matchesEffects(itemStack)) continue;
            return slotItem;
        }
        return null;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getDefaultColor() {
        return this.defaultColor;
    }

    public SlotItem.Group group() {
        return SlotItem.Group.POTION;
    }

    private static boolean isPotionSectionButton(ItemStack itemStack) {
        LoreComponent loreComponent = (LoreComponent)itemStack.get(DataComponentTypes.LORE);
        if (loreComponent == null || loreComponent.lines().isEmpty()) {
            return false;
        }
        for (Text text : loreComponent.lines()) {
            String string = text.getString();
            if (!string.contains("\u041d\u0430\u0436\u043c\u0438\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u043f\u0435\u0440\u0435\u0439\u0442\u0438") && !string.contains("\u0420\u0430\u0437\u0434\u0435\u043b \u043e\u0442\u043a\u0440\u044b\u0442")) continue;
            return true;
        }
        return false;
    }

    private boolean matchesEffects(ItemStack itemStack) {
        PotionContentsComponent potionContentsComponent = (PotionContentsComponent)itemStack.get(DataComponentTypes.POTION_CONTENTS);
        if (potionContentsComponent == null) {
            return false;
        }
        ArrayList<StatusEffectInstance> arrayList = new ArrayList<StatusEffectInstance>();
        for (StatusEffectInstance object : potionContentsComponent.getEffects()) {
            arrayList.add(object);
        }
        if (arrayList.size() != this.effectSignatures.size()) {
            return false;
        }
        for (SlotItem.EffectSignature effectSignature : this.effectSignatures) {
            boolean bl = false;
            for (StatusEffectInstance statusEffectInstance : arrayList) {
                if (!effectSignature.matches(statusEffectInstance)) continue;
                bl = true;
                break;
            }
            if (bl) continue;
            return false;
        }
        return true;
    }


    public record EffectSignature(RegistryEntry<StatusEffect> effect, int duration, int amplifier) {
        public boolean matches(StatusEffectInstance statusEffectInstance) {
            return statusEffectInstance.getEffectType().equals(this.effect) && statusEffectInstance.getDuration() == this.duration && statusEffectInstance.getAmplifier() == this.amplifier;
        }
    }

    public enum Group {
        POTION
    }
}

