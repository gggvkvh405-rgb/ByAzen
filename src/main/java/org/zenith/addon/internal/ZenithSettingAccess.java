package org.zenith.addon.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.zenith.addon.api.frontend.SettingAccess;
import org.zenith.addon.api.frontend.SettingType;
import org.zenith.module.Module;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.TextSetting;
import org.zenith.setting.SettingGroup;
import org.zenith.setting.ColorSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.StringListSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.setting.Setting;
import org.zenith.setting.ButtonSetting;
import org.zenith.setting.KeySetting;

final class ZenithSettingAccess implements SettingAccess {
   public final Module module;
   public final Setting setting;

   ZenithSettingAccess(Module var1, Setting var2) {
      this.module = Objects.requireNonNull(var1, "module");
      this.setting = Objects.requireNonNull(var2, "setting");
   }

   public String id() {
      return this.module instanceof AddonBackedModule addonbackedmodule ? addonbackedmodule.getSettingId(this.setting) : this.setting.getKey();
   }

   public String name() {
      return this.setting.getName();
   }

   public String description() {
      return this.setting.getDescription();
   }

   public SettingType type() {
      if (this.setting instanceof BooleanSetting) {
         return SettingType.BOOLEAN;
      } else if (this.setting instanceof NumberSetting) {
         return SettingType.NUMBER;
      } else if (this.setting instanceof ModeSetting) {
         return SettingType.CHOICE;
      } else if (this.setting instanceof TextSetting) {
         return SettingType.TEXT;
      } else if (this.setting instanceof ColorSetting) {
         return SettingType.COLOR;
      } else if (this.setting instanceof ButtonSetting) {
         return SettingType.ACTION;
      } else if (this.setting instanceof KeySetting) {
         return SettingType.KEY;
      } else if (this.setting instanceof MultiSelectSetting) {
         return SettingType.MULTI_BOOLEAN;
      } else if (this.setting instanceof StringListSetting) {
         return SettingType.ITEM_LIST;
      } else {
         return this.setting instanceof SettingGroup ? SettingType.GROUP : SettingType.UNKNOWN;
      }
   }

   public boolean visible() {
      try {
         return this.setting.isVisible();
      } catch (RuntimeException runtimeexception) {
         return false;
      }
   }

   public Object value() {
      return switch (this.type()) {
         case BOOLEAN -> ((BooleanSetting)this.setting).isEnabled();
         case NUMBER -> (double)((NumberSetting)this.setting).getCurrent();
         case CHOICE -> ((ModeSetting)this.setting).get();
         case TEXT -> ((TextSetting)this.setting).getValue();
         case COLOR -> ((ColorSetting)this.setting).getIntColor();
         case KEY -> ((KeySetting)this.setting).getKeyCode();
         case MULTI_BOOLEAN -> ((MultiSelectSetting)this.setting).zClass100Var143Var143();
         case ITEM_LIST -> List.copyOf(((StringListSetting)this.setting).queue4());
         case ACTION, GROUP, UNKNOWN -> null;
         default -> throw new MatchException(null, null);
      };
   }

   public boolean value(Object var1) {
      try {
         switch (this.type()) {
            case BOOLEAN:
               if (!(var1 instanceof Boolean obool)) {
                  return false;
               }

               ((BooleanSetting)this.setting).setEnabled(obool);
               break;
            case NUMBER:
               if (!(var1 instanceof Number number2)) {
                  return false;
               }

               float f = number2.floatValue();
               NumberSetting lilliiill11llilll1ll1l = (NumberSetting)this.setting;
               if (!Float.isFinite(f) || f < lilliiill11llilll1ll1l.getMin() || f > lilliiill11llilll1ll1l.getMax()) {
                  return false;
               }

               lilliiill11llilll1ll1l.setCurrent(f);
               break;
            case CHOICE:
               if (!(var1 instanceof String s1) || !this.choices().contains(s1)) {
                  return false;
               }

               ((ModeSetting)this.setting).set(s1);
               break;
            case TEXT:
               if (!(var1 instanceof String s) || !((TextSetting)this.setting).setValueSafe(s)) {
                  return false;
               }
               break;
            case COLOR:
               if (!(var1 instanceof Number number1)) {
                  return false;
               }

               ((ColorSetting)this.setting).setColor(number1.intValue());
               break;
            case KEY:
               if (!(var1 instanceof Number number)) {
                  return false;
               }

               ((KeySetting)this.setting).setKeyCode(number.intValue());
               break;
            case MULTI_BOOLEAN:
               if (!(var1 instanceof List list1)) {
                  return false;
               }

               Set<String> set1 = strings(list1);
               if (set1 == null || !new HashSet<>(this.choices()).containsAll(set1)) {
                  return false;
               }

               for (MultiSelectSetting.Option i1i1lll1liii1il1llll1_ii1il11l111ii11iil : ((MultiSelectSetting)this.setting).int212()) {
                  i1i1lll1liii1il1llll1_ii1il11l111ii11iil.setEnabled(set1.contains(i1i1lll1liii1il1llll1_ii1il11l111ii11iil.getKey()));
               }
               break;
            case ITEM_LIST:
               if (!(var1 instanceof List list)) {
                  return false;
               }

               Set<String> set = strings(list);
               if (set == null) {
                  return false;
               }

               ((StringListSetting)this.setting).ItemRegistry(List.copyOf(set));
               break;
            case ACTION:
               ((ButtonSetting)this.setting).toggle();
               break;
            case GROUP:
            case UNKNOWN:
               return false;
         }

         return true;
      } catch (RuntimeException runtimeexception) {
         return false;
      }
   }

   public double min() {
      return this.setting instanceof NumberSetting lilliiill11llilll1ll1l ? lilliiill11llilll1ll1l.getMin() : SettingAccess.super.min();
   }

   public double max() {
      return this.setting instanceof NumberSetting lilliiill11llilll1ll1l ? lilliiill11llilll1ll1l.getMax() : SettingAccess.super.max();
   }

   public double step() {
      return this.setting instanceof NumberSetting lilliiill11llilll1ll1l ? lilliiill11llilll1ll1l.getIncrement() : SettingAccess.super.step();
   }

   public String suffix() {
      return this.setting instanceof NumberSetting lilliiill11llilll1ll1l ? lilliiill11llilll1ll1l.getSuffix() : SettingAccess.super.suffix();
   }

   public List<String> choices() {
      if (this.setting instanceof ModeSetting ill11ii1ilil1liili1iliil) {
         return ill11ii1ilil1liili1iliil.getValues().stream().map(ModeSetting.Option::getKey).toList();
      } else {
         return this.setting instanceof MultiSelectSetting i1i1lll1liii1il1llll1
            ? i1i1lll1liii1il1llll1.int212().stream().map(MultiSelectSetting.Option::getKey).toList()
            : SettingAccess.super.choices();
      }
   }

   public String placeholder() {
      return this.setting instanceof TextSetting i1ll1llliii11l1 ? i1ll1llliii11l1.getEmptyText() : SettingAccess.super.placeholder();
   }

   public int maxLength() {
      return this.setting instanceof TextSetting i1ll1llliii11l1 ? i1ll1llliii11l1.getValidator().getMaxLength() : SettingAccess.super.maxLength();
   }

   public boolean secret() {
      return this.setting instanceof TextSetting i1ll1llliii11l1 && i1ll1llliii11l1.isSecret();
   }

   public List<? extends SettingAccess> children() {
      return this.setting instanceof SettingGroup l1lili1ii11
         ? l1lili1ii11.getSettings().stream().map(var1x -> new ZenithSettingAccess(this.module, var1x)).toList()
         : SettingAccess.super.children();
   }

   public static Set<String> strings(List<?> var0) {
      Set<String> hashset = new HashSet<>();

      for (Object object : var0) {
         if (!(object instanceof String s)) {
            return null;
         }

         hashset.add(s);
      }

      return hashset;
   }
}
