package org.zenith.module.misc;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.zenith.client.screens.autosbor.AutoSborScreen;
import org.zenith.core.HeldItemWatcher;
import org.zenith.core.ItemServiceBase;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.EventHookTickEvent;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.ButtonSetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.ItemCountUtils;

@ModuleInfo(name = "AutoInventory", category = Category.MISC, description = "Автоматически собирает инвентарь")
public class AutoInventory extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoInventory autoInventory = new AutoInventory();
   public static final int int33 = 36;
   public static final long long15 = 1000L;
   public static final long long16 = 300L;
   public static final long long17 = 100L;
   public static final long long18 = 700L;
   public static final int int34 = 45;
   public static final int int35 = 50;
   public static final int int36 = 2;
   public static final int int37 = 3;
   public static final int int38 = 4;
   public static final int int39 = 27;
   public static final int int40 = 0;
   public static final String string5 = "[Кyпить]";
   public static final long long19 = 6000L;
   public static final long long20 = 6000L;
   public static final int int41 = 3;
   public static Pattern pattern2;
   public static Pattern pattern3;
   public static Pattern pattern4;
   public final ItemServiceBase[] val489 = new ItemServiceBase[36];
   public final int[] val490 = new int[36];
   public final long[] val491 = new long[36];
   public final float[] val492 = new float[36];
   public final ItemServiceBase[] val493 = new ItemServiceBase[36];
   public final int[] val494 = new int[36];
   public final long[] val495 = new long[36];
   public final float[] val496 = new float[36];
   public final ModeSetting u0421U0435U0440U0432U0435U0440 = new ModeSetting("Сервер", "Забыли описание", "Funtime 1.21", "HolyWorld");
   public final ButtonSetting u041eU0442U043aU0440U044bU0442U044cU043cU0435U043dU044e = new ButtonSetting("Открыть меню", this::double130);
   public final HeldItemWatcher zClass021 = new HeldItemWatcher();
   public final List<AutoInventory.ArmorCandidate> list8 = new ArrayList<>();
   public final CooldownTimer zClass0678 = new CooldownTimer();
   public final CooldownTimer zClass0679 = new CooldownTimer();
   public AutoInventory.ArmorCandidate autoInventoryVar143;
   public boolean boolean9;
   public boolean boolean10;
   public boolean boolean11;
   public boolean boolean12;
   public int int42 = -1;
   public int int43;
   public int int44;
   public AutoInventory.Service autoInventoryVar159;

   @Override
   public void onEnable() {
      this.int406();
      if (this.list8.isEmpty()) {
         boolean flag = false;

         for (ItemServiceBase i1l11iiliiill1l1li1ii : this.random7()) {
            if (i1l11iiliiill1l1li1ii != null && !i1l11iiliiill1l1li1ii.EventInjectHandleInputEvents().isEmpty()) {
               flag = true;
               break;
            }
         }

         if (!flag) {
            StyledTextBuilder.RotationLegitStrategy("AutoInventory: набор пуст, добавьте предметы через меню");
         } else {
            StyledTextBuilder.RefreshCacheEvent("AutoInventory: все предметы из набора уже есть в инвентаре");
         }
      }

      this.zClass0678.reset();
      this.zClass0679.reset();
      this.boolean10 = false;
      this.boolean11 = false;
      this.boolean12 = false;
      this.int42 = -1;
      this.int43 = 0;
      this.int44 = 0;
      this.autoInventoryVar143 = null;
      this.autoInventoryVar159 = null;
      super.onEnable();
      this.double133();
   }

   @Override
   public void onDisable() {
      this.list8.clear();
      this.boolean9 = false;
      this.boolean10 = false;
      this.boolean11 = false;
      this.boolean12 = false;
      this.int42 = -1;
      this.autoInventoryVar143 = null;
      this.int43 = 0;
      this.int44 = 0;
      this.autoInventoryVar159 = null;
      super.onDisable();
   }

   @EventTarget
   public void UiAnimation(EventHookTickEvent var1) {
      if (this.boolean9) {
         this.setToggled(false);
      } else if (this.boolean10) {
         if (this.zClass0678.EventModifyMouseRotationInput(6000L)) {
            this.boolean10 = false;
            this.double134();
         } else {
            this.double135();
         }
      } else if (this.boolean11) {
         if (this.zClass0678.EventModifyMouseRotationInput(6000L)) {
            this.boolean11 = false;
            this.double134();
         } else {
            this.double136();
         }
      } else if (this.boolean12) {
         this.var2();
      } else if (this.zClass0678.EventModifyMouseRotationInput(1000L)) {
         this.double133();
      }
   }

   public void double130() {
      minecraftClient3.setScreen(new AutoSborScreen(this.random7(), this.var2Var143(), this.int407(), this.double137(), this::double132));
   }

   public HeldItemWatcher double131() {
      return this.zClass021;
   }

   public String double132() {
      return this.u0421U0435U0440U0432U0435U0440.get();
   }

   public void int406() {
      this.list8.clear();
      this.boolean9 = false;
      Map<String, Integer> hashmap = new HashMap<>();
      Map<String, Integer> hashmap1 = new HashMap<>();
      Map<String, Integer> hashmap2 = new HashMap<>();
      ItemServiceBase[] ai1l11iiliiill1l1li1ii = this.random7();

      for (int i = 0; i < ai1l11iiliiill1l1li1ii.length; i++) {
         ItemServiceBase i1l11iiliiill1l1li1ii = ai1l11iiliiill1l1li1ii[i];
         if (i1l11iiliiill1l1li1ii != null && !i1l11iiliiill1l1li1ii.EventInjectHandleInputEvents().isEmpty()) {
            String s = i1l11iiliiill1l1li1ii.EventMouseButton();
            if (s != null && !s.isBlank()) {
               int j = this.on23(i1l11iiliiill1l1li1ii, i);
               float f = this.UiAnimation(i1l11iiliiill1l1li1ii, i);
               long k = this.Event08(i);
               String s1 = this.UiAnimation(i1l11iiliiill1l1li1ii, f);
               int l = hashmap.computeIfAbsent(s1, var3x -> this.on23(i1l11iiliiill1l1li1ii, f));
               int i1 = hashmap1.getOrDefault(s1, 0) + j;
               int j1 = hashmap2.getOrDefault(s1, l);
               hashmap1.put(s1, i1);
               if (j1 >= i1) {
                  hashmap2.put(s1, j1);
               } else {
                  this.list8.add(new AutoInventory.ArmorCandidate(i1l11iiliiill1l1li1ii, s, i1 - j1, i1, f, k));
                  hashmap2.put(s1, i1);
               }
            }
         }
      }
   }

   public void double133() {
      if (this.list8.isEmpty()) {
         this.boolean9 = true;
      } else {
         this.int44 = 0;
         this.on23(this.list8.removeFirst());
      }
   }

   public void on23(AutoInventory.ArmorCandidate var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && minecraftClient3.player.networkHandler != null) {
         this.int408();
         this.autoInventoryVar159 = null;
         this.autoInventoryVar143 = var1;
         var1.int201 = this.on23(var1.var164, var1.float120);
         minecraftClient3.player.networkHandler.sendChatCommand("ah search " + this.autoInventoryVar143.string65);
         this.zClass0678.reset();
         this.zClass0679.reset();
         this.boolean10 = true;
         this.int42 = -1;
      } else {
         this.list8.addFirst(var1);
         this.autoInventoryVar143 = null;
         this.zClass0678.reset();
      }
   }

   public void double134() {
      if (this.autoInventoryVar143 == null) {
         this.zClass0678.reset();
         this.double133();
      } else {
         int i = this.on23(this.autoInventoryVar143.var164, this.autoInventoryVar143.float120);
         if (i > this.autoInventoryVar143.int201) {
            this.var132();
            this.int44 = 0;
         } else {
            this.int44++;
         }

         this.autoInventoryVar159 = null;
         if (i >= this.autoInventoryVar143.int200) {
            this.zClass0678.reset();
         } else if (this.int44 >= 3) {
            StyledTextBuilder.RotationLegitStrategy("AutoInventory: не удалось закупить «" + this.autoInventoryVar143.string65 + "», пропускаю");
            this.autoInventoryVar143 = null;
            this.zClass0678.reset();
            this.double133();
         } else {
            this.on23(
               new AutoInventory.ArmorCandidate(
                  this.autoInventoryVar143.var164,
                  this.autoInventoryVar143.string65,
                  this.autoInventoryVar143.int200 - i,
                  this.autoInventoryVar143.int200,
                  this.autoInventoryVar143.float120,
                  this.autoInventoryVar143.long118
               )
            );
         }
      }
   }

   public void double135() {
      if (minecraftClient3.player != null
         && minecraftClient3.world != null
         && minecraftClient3.interactionManager != null
         && minecraftClient3.currentScreen != null
         && minecraftClient3.player.currentScreenHandler != minecraftClient3.player.playerScreenHandler) {
         ScreenHandler screenhandler = minecraftClient3.player.currentScreenHandler;
         if (ItemCountUtils.ProfileItemBuilder(screenhandler)) {
            if (this.int42 != screenhandler.syncId) {
               this.int42 = screenhandler.syncId;
               this.zClass0679.reset();
            } else if (this.zClass0679.EventModifyMouseRotationInput(300L)) {
               int i = this.on23(screenhandler, this.autoInventoryVar143);
               this.int42 = -1;
               if (i < 0) {
                  if (!this.ItemServiceBase(screenhandler)) {
                     this.boolean10 = false;
                     this.double133();
                  }
               } else if (this.boolean182()) {
                  this.boolean10 = false;
                  this.boolean11 = true;
                  this.int43 = 0;
                  this.int42 = -1;
                  this.zClass0678.reset();
                  this.zClass0679.reset();
               } else {
                  this.boolean10 = false;
                  this.zClass0678.reset();
                  this.boolean11 = true;
                  this.int43 = i;
                  this.zClass0679.reset();
               }
            }
         }
      }
   }

   public int on23(ScreenHandler var1, AutoInventory.ArmorCandidate var2) {
      this.autoInventoryVar159 = null;
      Slot slot = null;
      long i = Long.MAX_VALUE;
      int j = Math.min(45, var1.slots.size());

      for (int k = 0; k < j; k++) {
         Slot slot1 = var1.getSlot(k);
         if (slot1 != null && slot1.hasStack()) {
            ItemStack itemstack = slot1.getStack();
            if (var2.var164.UiAnimation(itemstack) && this.on23(itemstack, var2.float120) && !ItemCountUtils.ModuleToggleEvent(itemstack)) {
               long l = this.FileLogger(itemstack);
               if (l != Long.MAX_VALUE && !this.on23(var2, itemstack, l) && l < i) {
                  slot = slot1;
                  i = l;
               }
            }
         }
      }

      if (slot != null && minecraftClient3.interactionManager != null && minecraftClient3.player != null) {
         int j1 = ItemCountUtils.EventMotion(slot.getStack());
         boolean flag = !this.boolean182() && j1 > var2.int199;
         int k1 = Math.max(1, flag ? var2.int199 : j1);
         String s = slot.getStack().getName().getString();
         this.autoInventoryVar159 = new AutoInventory.Service(slot.getStack(), s != null && !s.isBlank() ? s : var2.string65, k1, this.UiAnimation(i, k1));
         int i1 = flag ? 1 : 0;
         minecraftClient3.interactionManager.clickSlot(var1.syncId, slot.id, i1, SlotActionType.PICKUP, minecraftClient3.player);
         return flag ? Math.max(0, var2.int199 - 1) : 0;
      } else {
         return -1;
      }
   }

   public void double136() {
      if (minecraftClient3.player != null
         && minecraftClient3.world != null
         && minecraftClient3.interactionManager != null
         && minecraftClient3.currentScreen != null
         && minecraftClient3.player.currentScreenHandler != minecraftClient3.player.playerScreenHandler) {
         ScreenHandler screenhandler = minecraftClient3.player.currentScreenHandler;
         if (ItemCountUtils.ProfileItemBuilder(screenhandler)) {
            this.TextScanner(screenhandler);
         } else if (this.boolean182()) {
            this.NbtItemSpec(screenhandler);
         } else if (minecraftClient3.currentScreen.getTitle() != null
            && minecraftClient3.currentScreen.getTitle().getString().contains("Покупка предмета")
            && screenhandler.slots.size() > 4) {
            if (this.int42 != screenhandler.syncId) {
               this.int42 = screenhandler.syncId;
               this.zClass0679.reset();
            } else if (this.zClass0679.EventModifyMouseRotationInput(100L)) {
               if (this.int43 >= 10) {
                  this.ItemSpec(screenhandler, 4);
                  this.int43 -= 10;
                  this.zClass0679.reset();
               } else if (this.int43 > 0) {
                  this.ItemSpec(screenhandler, 3);
                  this.int43--;
                  this.zClass0679.reset();
               } else if (this.NbtEditor(screenhandler)) {
                  this.var132();
                  this.boolean11 = false;
                  this.boolean12 = true;
                  this.int42 = -1;
                  this.zClass0678.reset();
                  this.zClass0679.reset();
               }
            }
         }
      }
   }

   public void TextScanner(ScreenHandler var1) {
      if (this.autoInventoryVar143 == null) {
         this.boolean11 = false;
         this.zClass0678.reset();
      } else if (this.zClass0679.EventModifyMouseRotationInput(300L)) {
         int i = this.on23(var1, this.autoInventoryVar143);
         this.int42 = -1;
         if (i < 0) {
            if (!this.ItemServiceBase(var1)) {
               this.boolean11 = false;
               this.double133();
            }
         } else if (this.boolean182()) {
            this.boolean11 = true;
            this.int43 = 0;
            this.int42 = -1;
            this.zClass0678.reset();
            this.zClass0679.reset();
         } else {
            this.int43 = i;
            this.zClass0678.reset();
            this.zClass0679.reset();
         }
      }
   }

   public void NbtItemSpec(ScreenHandler var1) {
      if (minecraftClient3.currentScreen.getTitle() != null) {
         String s = minecraftClient3.currentScreen.getTitle().getString();
         if (s.contains("Подтверждение покупки")) {
            int i = this.EnchantItemSpec(var1);
            if (i >= 0) {
               if (this.int42 != var1.syncId) {
                  this.int42 = var1.syncId;
                  this.zClass0679.reset();
               } else if (this.zClass0679.EventModifyMouseRotationInput(100L)) {
                  this.ItemSpec(var1, i);
                  this.boolean11 = false;
                  this.boolean12 = true;
                  this.int42 = -1;
                  this.zClass0678.reset();
                  this.zClass0679.reset();
               }
            }
         }
      }
   }

   public int EnchantItemSpec(ScreenHandler var1) {
      int i = this.SimpleItemBuilder(var1);
      if (i == 27) {
         return 0;
      } else {
         return i > 27 ? this.ItemRegistry(var1, i) : -1;
      }
   }

   public int ItemRegistry(ScreenHandler var1, int var2) {
      int i = Math.min(var2, var1.slots.size());

      for (int j = 0; j < i; j++) {
         Slot slot = var1.getSlot(j);
         if (slot != null && slot.hasStack()) {
            ItemStack itemstack = slot.getStack();
            if (itemstack.getItem() == Items.PAPER && "[Кyпить]".equals(itemstack.getName().getString())) {
               return j;
            }
         }
      }

      return -1;
   }

   public int SimpleItemBuilder(ScreenHandler var1) {
      return Math.max(0, var1.slots.size() - 36);
   }

   public boolean ItemServiceBase(ScreenHandler var1) {
      if (var1.slots.size() <= 50) {
         return false;
      }

      if (minecraftClient3.interactionManager != null && minecraftClient3.player != null) {
         Slot slot = var1.getSlot(50);
         if (slot != null && slot.hasStack()) {
            if (slot.getStack().getItem() != Items.LIME_DYE) {
               return false;
            }

            this.autoInventoryVar159 = null;
            minecraftClient3.interactionManager.clickSlot(var1.syncId, slot.id, 0, SlotActionType.PICKUP, minecraftClient3.player);
            this.int42 = -1;
            this.zClass0678.reset();
            this.zClass0679.reset();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean NbtEditor(ScreenHandler var1) {
      if (var1.slots.size() > 2) {
         this.ItemSpec(var1, 2);
         return true;
      } else {
         return false;
      }
   }

   public void ItemSpec(ScreenHandler var1, int var2) {
      if (var2 >= 0 && var2 < var1.slots.size() && minecraftClient3.interactionManager != null && minecraftClient3.player != null) {
         minecraftClient3.interactionManager.clickSlot(var1.syncId, var1.getSlot(var2).id, 0, SlotActionType.PICKUP, minecraftClient3.player);
      }
   }

   public void var132() {
      if (this.autoInventoryVar159 != null) {
         this.zClass021
            .on23(this.autoInventoryVar159.itemStack11, this.autoInventoryVar159.string64, this.autoInventoryVar159.int198, this.autoInventoryVar159.long117);
         this.autoInventoryVar159 = null;
      }
   }

   public void var2() {
      if (this.autoInventoryVar143 == null) {
         this.boolean12 = false;
         this.zClass0678.reset();
      } else {
         int i = this.on23(this.autoInventoryVar143.var164, this.autoInventoryVar143.float120);
         if (i < this.autoInventoryVar143.int200) {
            if (this.zClass0679.EventModifyMouseRotationInput(700L)) {
               this.boolean12 = false;
               this.double134();
            }
         } else {
            this.var132();
            this.boolean12 = false;
            this.zClass0678.reset();
         }
      }
   }

   public long StringCodec(ItemStack var1) {
      LoreComponent lorecomponent = (LoreComponent)var1.get(DataComponentTypes.LORE);
      if (lorecomponent == null) {
         return Long.MAX_VALUE;
      }

      long i = Long.MAX_VALUE;

      for (Text text : lorecomponent.lines()) {
         String s = text.getString().replace(' ', ' ');
         Matcher matcher = pattern2.matcher(s);
         if (matcher.find()) {
            long j = this.StaffList(matcher.group(1));
            if (j != Long.MAX_VALUE) {
               return j;
            }
         } else if (i == Long.MAX_VALUE) {
            Matcher matcher1 = pattern3.matcher(s);
            if (matcher1.find()) {
               i = this.StaffList(matcher1.group(1));
            }
         }
      }

      if (i != Long.MAX_VALUE) {
         return Math.max(1L, i / Math.max(1, ItemCountUtils.EventMotion(var1)));
      }

      long k = this.CloudApiClient(var1);
      return k != Long.MAX_VALUE ? Math.max(1L, k / Math.max(1, ItemCountUtils.EventMotion(var1))) : Long.MAX_VALUE;
   }

   public boolean on23(AutoInventory.ArmorCandidate var1, ItemStack var2, long var3) {
      if (var1.long118 <= 0L) {
         return false;
      }

      long i = this.boolean182() ? Math.max(1L, var3 / Math.max(1, ItemCountUtils.EventMotion(var2))) : var3;
      return i > var1.long118;
   }

   public long FileLogger(ItemStack var1) {
      if (!this.boolean182()) {
         return this.StringCodec(var1);
      }

      long i = this.CloudApiClient(var1);
      if (i != Long.MAX_VALUE) {
         return i;
      }

      long j = this.StringCodec(var1);
      return j == Long.MAX_VALUE ? Long.MAX_VALUE : j * Math.max(1, ItemCountUtils.EventMotion(var1));
   }

   public long CloudApiClient(ItemStack var1) {
      LoreComponent lorecomponent = (LoreComponent)var1.get(DataComponentTypes.LORE);
      if (lorecomponent == null) {
         return Long.MAX_VALUE;
      }

      for (Text text : lorecomponent.lines()) {
         String s = text.getString().replace(' ', ' ');
         Matcher matcher = pattern4.matcher(s);
         if (matcher.find()) {
            long i = this.StaffList(matcher.group(1));
            if (i != Long.MAX_VALUE) {
               return i;
            }
         }
      }

      return Long.MAX_VALUE;
   }

   public long UiAnimation(long var1, int var3) {
      return this.boolean182() ? var1 : var1 * var3;
   }

   public long StaffList(String var1) {
      try {
         return Long.parseLong(var1.replace(" ", "").replace(",", ""));
      } catch (NumberFormatException numberformatexception) {
         return Long.MAX_VALUE;
      }
   }

   public int on23(ItemServiceBase var1, int var2) {
      int[] aint = this.var2Var143();
      if (var2 >= 0 && var2 < aint.length) {
         int i = aint[var2];
         return Math.min(this.getCountMax(var1), i > 0 ? i : this.getDefaultCount(var1));
      } else {
         return this.getDefaultCount(var1);
      }
   }

   public int getDefaultCount(ItemServiceBase var1) {
      return var1 != null && !var1.EventInjectHandleInputEvents().isEmpty() ? Math.max(1, var1.EventInjectHandleInputEvents().getCount()) : 1;
   }

   public int getCountMax(ItemServiceBase var1) {
      if (var1 != null && !var1.EventInjectHandleInputEvents().isEmpty()) {
         return this.isPotion(var1.EventInjectHandleInputEvents().getItem())
            ? 64
            : Math.min(64, Math.max(1, var1.EventInjectHandleInputEvents().getMaxCount()));
      } else {
         return 64;
      }
   }

   public boolean isPotion(Item var1) {
      return var1 == Items.POTION || var1 == Items.SPLASH_POTION || var1 == Items.LINGERING_POTION;
   }

   public int on23(ItemServiceBase var1, float var2) {
      if (minecraftClient3.player == null) {
         return 0;
      }

      int i = 0;

      for (int j = 0; j < minecraftClient3.player.getInventory().size(); j++) {
         ItemStack itemstack = minecraftClient3.player.getInventory().getStack(j);
         if (itemstack != null && !itemstack.isEmpty() && var1.UiAnimation(itemstack) && this.on23(itemstack, var2)) {
            i += Math.max(1, itemstack.getCount());
         }
      }

      return i;
   }

   public String UiAnimation(ItemServiceBase var1, float var2) {
      return var1.EventMouseButton() + "|" + var1.EventInjectHandleInputEvents().getItem() + "|" + var2;
   }

   public float UiAnimation(ItemServiceBase var1, int var2) {
      if (!this.isDurabilityItem(var1)) {
         return 0.0F;
      }

      float[] afloat = this.double137();
      return var2 >= 0 && var2 < afloat.length ? afloat[var2] : 0.0F;
   }

   public ItemServiceBase[] random7() {
      return this.boolean182() ? this.val489 : this.val493;
   }

   public int[] var2Var143() {
      return this.boolean182() ? this.val490 : this.val494;
   }

   public long[] int407() {
      return this.boolean182() ? this.val491 : this.val495;
   }

   public long Event08(int var1) {
      long[] along = this.int407();
      return var1 >= 0 && var1 < along.length ? Math.max(0L, along[var1]) : 0L;
   }

   public float[] double137() {
      return this.boolean182() ? this.val492 : this.val496;
   }

   public boolean boolean182() {
      return "Funtime 1.21".equals(this.double132());
   }

   public boolean on23(ItemStack var1, float var2) {
      if (var2 <= 0.0F) {
         return true;
      }

      int i = var1.getMaxDamage();
      if (i <= 0) {
         return false;
      }

      float f = (float)(i - var1.getDamage()) / i;
      return f >= var2;
   }

   public boolean isDurabilityItem(ItemServiceBase var1) {
      if (var1 != null && !var1.EventInjectHandleInputEvents().isEmpty()) {
         return "Шлем Солнца".equals(var1.EventMouseButton()) ? false : var1.EventInjectHandleInputEvents().getMaxDamage() > 0;
      } else {
         return false;
      }
   }

   public void int408() {
      if (minecraftClient3.player != null && minecraftClient3.currentScreen != null) {
         if (minecraftClient3.player.currentScreenHandler != minecraftClient3.player.playerScreenHandler) {
            minecraftClient3.player.closeHandledScreen();
         }

         minecraftClient3.setScreen(null);
      }
   }


   public static class ArmorCandidate {
      public final ItemServiceBase var164;
      public final String string65;
      public final int int199;
      public final int int200;
      public final float float120;
      public final long long118;
      public int int201;

      public ArmorCandidate(ItemServiceBase var1, String var2, int var3, int var4, float var5, long var6) {
         this.var164 = var1;
         this.string65 = var2;
         this.int199 = var3;
         this.int200 = var4;
         this.float120 = var5;
         this.long118 = var6;
      }
   }

   public static class Service {
      public final ItemStack itemStack11;
      public final String string64;
      public final int int198;
      public final long long117;

      public Service(ItemStack var1, String var2, int var3, long var4) {
         this.itemStack11 = var1.copy();
         this.string64 = var2;
         this.int198 = var3;
         this.long117 = var4;
      }
   }
}
