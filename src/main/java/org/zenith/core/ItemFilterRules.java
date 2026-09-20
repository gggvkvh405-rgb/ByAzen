package org.zenith.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.zenith.module.misc.AutoCraft;

public class ItemFilterRules {
   public final String string95;
   public final String string96;
   public String displayName;
   public String string27 = "";
   public String string28 = "";
   public String string97 = "";
   public boolean boolean156;
   public boolean boolean157;
   public boolean boolean109;
   public final List<String> list86 = new ArrayList<>();
   public final List<String> list87 = new ArrayList<>();
   public final Map<String, BlockPosEntry> map36 = new HashMap<>();
   public BlockPosEntry var122 = BlockPosEntry.var12;
   public BlockPosEntry var123 = BlockPosEntry.var12;

   public ItemFilterRules(String var1, String var2, String var3) {
      this.string95 = var1;
      this.string96 = var2;
      this.displayName = var3;

      for (int i = 0; i < 9; i++) {
         this.list86.add("");
         this.list87.add("");
      }
   }

   public boolean string109() {
      return this.list86.stream().allMatch(String::isBlank);
   }

   public List<String> string110() {
      List<String> arraylist = new ArrayList<>();

      for (String s : this.list86) {
         if (!s.isBlank() && !arraylist.contains(s)) {
            arraylist.add(s);
         }
      }

      return arraylist;
   }

   public Map<String, Integer> string111() {
      Map<String, Integer> hashmap = new HashMap<>();

      for (int i = 0; i < this.list86.size(); i++) {
         String s = this.list86.get(i);
         if (!s.isBlank()) {
            String s1 = this.ChatMessageEvent(i);
            hashmap.put(s1, hashmap.getOrDefault(s1, 0) + 1);
         }
      }

      return hashmap;
   }

   public int CloudApi(String var1) {
      return this.string111().getOrDefault(var1, 0);
   }

   public void Easing(int var1, String var2) {
      this.list86.set(var1, var2 == null ? "" : var2);
   }

   public String DataChangedEvent(int var1) {
      return this.list86.get(var1);
   }

   public void ColorAnimator(int var1, String var2) {
      this.list87.set(var1, var2 == null ? "" : var2);
   }

   public String EventInjectPlaced(int var1) {
      return this.list87.get(var1);
   }

   public String ChatMessageEvent(int var1) {
      String s = this.DataChangedEvent(var1);
      String s1 = this.EventInjectPlaced(var1);
      return s1.isBlank() ? s : s + "#" + s1;
   }

   public String FriendFilter(String var1) {
      int i = var1.indexOf(35);
      return i == -1 ? var1 : var1.substring(0, i);
   }

   public String NpcCloneManager(String var1) {
      int i = var1.indexOf(35);
      return i == -1 ? "" : var1.substring(i + 1);
   }

   public String on23(String var1, AutoCraft var2) {
      return var2.ProtocolMessage(this.FriendFilter(var1), this.NpcCloneManager(var1));
   }

   public BlockPosEntry PlayerStateService(String var1) {
      return this.map36.get(var1);
   }

   public boolean on23(ItemStack var1, AutoCraft var2) {
      return var2.on23(var1, this.string27, this.string28);
   }

   public String on23(AutoCraft var1) {
      return var1.ProtocolMessage(this.string27, this.string28);
   }

   public ItemStack UiAnimation(AutoCraft var1) {
      String s = this.string97.isBlank() ? this.string27 : this.string97;
      Item item = var1.GameMessageEvent(s);
      ItemStack itemstack = item == Items.AIR ? ItemStack.EMPTY : item.getDefaultStack();
      if (!itemstack.isEmpty() && this.boolean156) {
         itemstack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
      }

      return itemstack;
   }

   public String getId() {
      return this.string95;
   }

   public String string112() {
      return this.string96;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public void PetManager(String var1) {
      this.displayName = var1 == null ? "" : var1;
   }

   public String float275() {
      return this.string27;
   }

   public void HolyWorldClient(String var1) {
      this.string27 = var1 == null ? "" : var1;
   }

   public String boolean178() {
      return this.string28;
   }

   public void RotationQueue(String var1) {
      this.string28 = var1 == null ? "" : var1;
   }

   public void TaskQueue(String var1) {
      this.string97 = var1 == null ? "" : var1;
   }

   public void EmoteManager(boolean var1) {
      this.boolean156 = var1;
   }

   public boolean zClass016Var7() {
      return this.boolean157;
   }

   public boolean int395() {
      return !this.boolean157;
   }

   public void CosmeticManager(boolean var1) {
      this.boolean157 = var1;
   }

   public boolean deque3() {
      return this.boolean109;
   }

   public void EmotePlayback(boolean var1) {
      this.boolean109 = var1;
   }

   public List<String> call265() {
      return this.list86;
   }

   public List<String> call207() {
      return this.list87;
   }

   public Map<String, BlockPosEntry> call034() {
      return this.map36;
   }

   public BlockPosEntry call241() {
      return this.var122;
   }

   public void on23(BlockPosEntry var1) {
      this.var122 = var1 == null ? BlockPosEntry.var12 : var1;
   }

   public BlockPosEntry call156() {
      return this.var123;
   }

   public void UiAnimation(BlockPosEntry var1) {
      this.var123 = var1 == null ? BlockPosEntry.var12 : var1;
   }
}
