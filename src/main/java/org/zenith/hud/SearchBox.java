package org.zenith.hud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.Vector2f;
import net.minecraft.util.math.MathHelper;
import org.zenith.base.font.Font;
import org.zenith.core.ClientProvider;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.util.ArgbColor;
import org.zenith.util.MathUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;

public class SearchBox implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Map<Character, Character> map26 = new HashMap<>();
   public String text = "";
   public boolean selected;
   public boolean boolean140;
   public int int296;
   public float float180;
   public Font font;
   public Vector2f vector2f;
   public String emptyText;
   public float width;
   public float height;
   public boolean multiline;
   public long long130 = System.currentTimeMillis();
   public int maxLength = Integer.MAX_VALUE;
   public SearchBox.MatchMode searchBoxVar159 = SearchBox.MatchMode.val178;
   public SearchBox.SearchScope searchBoxVar143 = SearchBox.SearchScope.val389;
   public float scrollOffset = 0.0F;
   public boolean boolean141;
   UiAnimation EventPosHook = new UiAnimation(400L, 0.2F, Easing.StopUsingItemEvent);

   public void on23(CustomDrawContext var1, float var2, float var3, ArgbColor var4, ArgbColor var5) {
      this.vector2f = new Vector2f(var2, var3);
      this.int296 = MathHelper.clamp(this.int296, 0, this.text.length());
      this.float180 = var2;
      boolean flag = this.isEmpty();
      if (this.multiline) {
         this.on23(var1, var2, var3, var4, var5, flag);
      } else {
         String s = this.boolean141 ? "*".repeat(this.text.length()) : this.text;
         float f = this.width;
         int i = 0;

         while (this.font.width(s.substring(i, this.int296)) > f) {
            i++;
         }

         int j = this.int296;

         while (j < s.length() && this.font.width(s.substring(i, j)) < f) {
            j++;
         }

         String s1 = s.substring(i, j);
         if (this.boolean140 && !s1.isEmpty()) {
            var1.drawRect(var2 - 1.0F, var3 - 1.0F, Math.min(this.font.width(s1), f) + 2.0F, this.font.height() + 2.0F, var5.SprintStateEvent(0.5F));
         }

         if (flag) {
            var1.drawText(this.font, this.EmoteRegistry(this.emptyText), var2, var3, var5);
         } else {
            var1.drawText(this.font, s1, var2, var3, var4);
         }

         if (this.selected && System.currentTimeMillis() - this.long130 > 200L) {
            float f1 = flag ? 0.0F : this.font.width(s.substring(i, this.int296));
            float f2 = this.float180 + Math.min(f1, this.width);
            this.EventPosHook.on23(250L);
            var1.drawRect(
               f2,
               var3 - 1.0F,
               1.0F,
               this.font.height() + 2.0F,
               var4.SprintStateEvent(
                  this.EventPosHook
                     .on23(this.EventPosHook.CancellableEvent() == 0.2F ? 1.0F : (this.EventPosHook.CancellableEvent() == 1.0F ? 0.2F : this.EventPosHook.BotDisconnectEvent()))
               )
            );
         }
      }
   }

   public void on23(CustomDrawContext var1, float var2, float var3, ArgbColor var4, ArgbColor var5, boolean var6) {
      String s = this.boolean141 ? "*".repeat(this.text.length()) : this.text;
      if (var6) {
         List<String> list = this.ThemeColorCycler(this.emptyText == null ? "" : this.emptyText);

         for (int i = 0; i < list.size() && (i + 1) * this.font.height() <= this.height; i++) {
            var1.drawText(this.font, list.get(i), var2, var3 + i * this.font.height(), var5);
         }
      } else {
         List<String> list1 = this.ThemeColorCycler(s);
         float f1 = this.font.height();

         for (int j = 0; j < list1.size() && (j + 1) * f1 <= this.height; j++) {
            var1.drawText(this.font, list1.get(j), var2, var3 + j * f1, var4);
         }
      }

      float f = this.font.height();
      if (this.selected && System.currentTimeMillis() - this.long130 > 200L) {
         List<String> list2 = this.ThemeColorCycler(s.substring(0, this.int296));
         int k = Math.max(0, list2.size() - 1);
         if ((k + 1) * f <= this.height) {
            String s1 = list2.isEmpty() ? "" : list2.get(k);
            this.EventPosHook.on23(250L);
            var1.drawRect(
               var2 + this.font.width(s1),
               var3 + k * f - 1.0F,
               1.0F,
               f + 2.0F,
               var4.SprintStateEvent(
                  this.EventPosHook
                     .on23(this.EventPosHook.CancellableEvent() == 0.2F ? 1.0F : (this.EventPosHook.CancellableEvent() == 1.0F ? 0.2F : this.EventPosHook.BotDisconnectEvent()))
               )
            );
         }
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.selected) {
         return false;
      }

      if (var1 == 256) {
         this.selected = false;
         return true;
      }

      if (var1 == 257) {
         if (this.multiline) {
            this.ColorAnimator("\n", this.int296);
            this.int296++;
            this.long130 = System.currentTimeMillis();
         } else {
            this.selected = false;
         }

         return true;
      } else {
         this.long130 = System.currentTimeMillis();
         this.int296 = MathHelper.clamp(this.int296, 0, this.text.length());
         if (InputUtil.isKeyPressed(minecraftClient3.getWindow(), 341)) {
            if (var1 == 86) {
               String s = minecraftClient3.keyboard.getClipboard();
               if (this.boolean140) {
                  this.text = "";
                  this.int296 = 0;
                  this.boolean140 = false;
               }

               this.ColorAnimator(s, this.int296);
               this.int296 = this.int296 + s.length();
               this.boolean140 = false;
            } else if (var1 == 65) {
               this.boolean140 = true;
               this.int296 = this.text.length();
            } else if (var1 == 67 && this.selected && this.boolean140) {
               minecraftClient3.keyboard.setClipboard(this.text);
            }
         } else if (var1 == 261 && !this.text.isEmpty()) {
            this.FovEvent(this.int296 + 1);
            this.boolean140 = false;
         } else if (var1 == 259 && !this.text.isEmpty()) {
            if (this.boolean140) {
               this.text = "";
               this.int296 = 0;
               this.boolean140 = false;
            } else {
               this.FovEvent(this.int296);
               this.int296--;
               if (InputUtil.isKeyPressed(minecraftClient3.getWindow(), 341)) {
                  while (!this.text.isEmpty() && this.int296 > 0) {
                     this.FovEvent(this.int296);
                     this.int296--;
                  }
               }
            }
         } else if (var1 == 262) {
            this.int296++;
            if (InputUtil.isKeyPressed(minecraftClient3.getWindow(), 341)) {
               this.int296 = this.text.length();
            }

            this.boolean140 = false;
         } else if (var1 == 263) {
            this.int296--;
            if (InputUtil.isKeyPressed(minecraftClient3.getWindow(), 341)) {
               this.int296 = 0;
            }

            this.boolean140 = false;
         } else if (var1 == 269) {
            this.int296 = this.text.length();
            this.boolean140 = false;
         } else if (var1 == 268) {
            this.int296 = 0;
            this.boolean140 = false;
         }

         this.int296 = MathHelper.clamp(this.int296, 0, this.text.length());
         return true;
      }
   }

   public void ColorAnimator(String var1, int var2) {
      String s = this.UserdataManager(var1);
      s = this.ArmorHud(s);
      StringBuilder stringbuilder = new StringBuilder();

      for (char c0 : s.toCharArray()) {
         if (this.searchBoxVar159.on23(c0)) {
            stringbuilder.append(c0);
         }
      }

      String s1 = stringbuilder.toString();
      if (this.text.length() + s1.length() > this.maxLength) {
         int i = this.maxLength - this.text.length();
         if (i <= 0) {
            return;
         }

         s1 = s1.substring(0, Math.min(i, s1.length()));
      }

      StringBuilder stringbuilder1 = new StringBuilder();
      boolean flag = false;

      for (int j = 0; j < this.text.length(); j++) {
         if (j == var2) {
            flag = true;
            stringbuilder1.append(s1);
         }

         stringbuilder1.append(this.text.charAt(j));
      }

      if (!flag) {
         stringbuilder1.append(s1);
      }

      this.text = stringbuilder1.toString();
   }

   public void on23(Vector2f var1) {
      this.vector2f = var1;
   }

   public void HudInventoryPanel(String var1) {
      this.emptyText = var1;
   }

   public void setWidth(float var1) {
      this.width = var1;
   }

   public void setHeight(float var1) {
      this.height = var1;
   }

   public void DataChangedEvent(boolean var1) {
      this.multiline = var1;
   }

   public void BlockInteractEvent(long var1) {
      this.long130 = var1;
   }

   public void EventItemRenderHook(int var1) {
      this.maxLength = var1;
   }

   public void on23(SearchBox.MatchMode var1) {
      this.searchBoxVar159 = var1;
   }

   public void on23(SearchBox.SearchScope var1) {
      this.searchBoxVar143 = var1;
   }

   public void PlayerMoveEvent(float var1) {
      this.scrollOffset = var1;
   }

   public void EventInjectPlaced(boolean var1) {
      this.boolean141 = var1;
   }

   public void on23(UiAnimation var1) {
      this.EventPosHook = var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof SearchBox i1lil1lliilli1lli1l)) {
         return false;
      } else {
         if (!i1lil1lliilli1lli1l.canEqual(this)) {
            return false;
         }

         if (this.isSelected() != i1lil1lliilli1lli1l.isSelected()) {
            return false;
         }

         if (this.float263() != i1lil1lliilli1lli1l.float263()) {
            return false;
         }

         if (this.var14348() != i1lil1lliilli1lli1l.var14348()) {
            return false;
         }

         if (Float.compare(this.map49(), i1lil1lliilli1lli1l.map49()) != 0) {
            return false;
         }

         if (Float.compare(this.getWidth(), i1lil1lliilli1lli1l.getWidth()) != 0) {
            return false;
         }

         if (Float.compare(this.getHeight(), i1lil1lliilli1lli1l.getHeight()) != 0) {
            return false;
         }

         if (this.isMultiline() != i1lil1lliilli1lli1l.isMultiline()) {
            return false;
         }

         if (this.float248() != i1lil1lliilli1lli1l.float248()) {
            return false;
         }

         if (this.getMaxLength() != i1lil1lliilli1lli1l.getMaxLength()) {
            return false;
         }

         if (Float.compare(this.getScrollOffset(), i1lil1lliilli1lli1l.getScrollOffset()) != 0) {
            return false;
         }

         if (this.map48() != i1lil1lliilli1lli1l.map48()) {
            return false;
         }

         String s = this.getText();
         String s1 = i1lil1lliilli1lli1l.getText();
         if (s == null ? s1 == null : s.equals(s1)) {
            Font font = this.call050();
            Font font1 = i1lil1lliilli1lli1l.call050();
            if (font == null ? font1 == null : font.equals(font1)) {
               Vector2f vector2f = this.call103();
               Vector2f vector2f1 = i1lil1lliilli1lli1l.call103();
               if (vector2f == null ? vector2f1 == null : vector2f.equals(vector2f1)) {
                  String s2 = this.getEmptyText();
                  String s3 = i1lil1lliilli1lli1l.getEmptyText();
                  if (s2 == null ? s3 == null : s2.equals(s3)) {
                     SearchBox.MatchMode i1lil1lliilli1lli1l_ii1il11l111ii11iil = this.float249();
                     SearchBox.MatchMode i1lil1lliilli1lli1l_ii1il11l111ii11iil1 = i1lil1lliilli1lli1l.float249();
                     if (i1lil1lliilli1lli1l_ii1il11l111ii11iil == null
                        ? i1lil1lliilli1lli1l_ii1il11l111ii11iil1 == null
                        : i1lil1lliilli1lli1l_ii1il11l111ii11iil.equals(i1lil1lliilli1lli1l_ii1il11l111ii11iil1)) {
                        SearchBox.SearchScope i1lil1lliilli1lli1l_l1i1illlili = this.map47();
                        SearchBox.SearchScope i1lil1lliilli1lli1l_l1i1illlili1 = i1lil1lliilli1lli1l.map47();
                        if (i1lil1lliilli1lli1l_l1i1illlili == null
                           ? i1lil1lliilli1lli1l_l1i1illlili1 == null
                           : i1lil1lliilli1lli1l_l1i1illlili.equals(i1lil1lliilli1lli1l_l1i1illlili1)) {
                           UiAnimation l1i1illlilix = this.getEvent11();
                           l1i1illlilix = i1lil1lliilli1lli1l.getEvent11();
                           return l1i1illlilix == null ? l1i1illlilix == null : l1i1illlilix.equals(l1i1illlilix);
                        } else {
                           return false;
                        }
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof SearchBox;
   }

   @Override
   public int hashCode() {
      byte b0 = 59;
      int i = 1;
      i = i * 59 + (this.isSelected() ? 79 : 97);
      i = i * 59 + (this.float263() ? 79 : 97);
      i = i * 59 + this.var14348();
      i = i * 59 + Float.floatToIntBits(this.map49());
      i = i * 59 + Float.floatToIntBits(this.getWidth());
      i = i * 59 + Float.floatToIntBits(this.getHeight());
      i = i * 59 + (this.isMultiline() ? 79 : 97);
      long j = this.float248();
      i = i * 59 + (int)(j >>> 32 ^ j);
      i = i * 59 + this.getMaxLength();
      i = i * 59 + Float.floatToIntBits(this.getScrollOffset());
      i = i * 59 + (this.map48() ? 79 : 97);
      String s = this.getText();
      i = i * 59 + (s == null ? 43 : s.hashCode());
      Font font = this.call050();
      i = i * 59 + (font == null ? 43 : font.hashCode());
      Vector2f vector2f = this.call103();
      i = i * 59 + (vector2f == null ? 43 : vector2f.hashCode());
      String s1 = this.getEmptyText();
      i = i * 59 + (s1 == null ? 43 : s1.hashCode());
      SearchBox.MatchMode i1lil1lliilli1lli1l_ii1il11l111ii11iil = this.float249();
      i = i * 59 + (i1lil1lliilli1lli1l_ii1il11l111ii11iil == null ? 43 : i1lil1lliilli1lli1l_ii1il11l111ii11iil.hashCode());
      SearchBox.SearchScope i1lil1lliilli1lli1l_l1i1illlili = this.map47();
      i = i * 59 + (i1lil1lliilli1lli1l_l1i1illlili == null ? 43 : i1lil1lliilli1lli1l_l1i1illlili.hashCode());
      UiAnimation l1i1illlili = this.getEvent11();
      return i * 59 + (l1i1illlili == null ? 43 : l1i1illlili.hashCode());
   }

   @Override
   public String toString() {
      return "TextBox(text="
         + this.getText()
         + ", selected="
         + this.isSelected()
         + ", selectAll="
         + this.float263()
         + ", cursor="
         + this.var14348()
         + ", posX="
         + this.map49()
         + ", font="
         + this.call050()
         + ", position="
         + this.call103()
         + ", emptyText="
         + this.getEmptyText()
         + ", width="
         + this.getWidth()
         + ", height="
         + this.getHeight()
         + ", multiline="
         + this.isMultiline()
         + ", lastInputTime="
         + this.float248()
         + ", maxLength="
         + this.getMaxLength()
         + ", charFilter="
         + this.float249()
         + ", wordLimit="
         + this.map47()
         + ", scrollOffset="
         + this.getScrollOffset()
         + ", masked="
         + this.map48()
         + ", animation="
         + this.getEvent11()
         + ")";
   }

   public SearchBox(Vector2f var1, Font var2, String var3, float var4) {
      this.font = var2;
      this.emptyText = var3;
      this.width = var4;
      this.vector2f = var1;
   }

   public List<String> ThemeColorCycler(String var1) {
      List<String> arraylist = new ArrayList<>();
      StringBuilder stringbuilder = new StringBuilder();

      for (int i = 0; i < var1.length(); i++) {
         char c0 = var1.charAt(i);
         if (c0 == '\n') {
            arraylist.add(stringbuilder.toString());
            stringbuilder.setLength(0);
         } else {
            if (!stringbuilder.isEmpty() && this.font.width(stringbuilder.toString() + c0) > this.width) {
               arraylist.add(stringbuilder.toString());
               stringbuilder.setLength(0);
            }

            stringbuilder.append(c0);
         }
      }

      arraylist.add(stringbuilder.toString());
      return arraylist;
   }

   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      Vector2f vector2f = this.call103();
      float f = this.multiline ? Math.max(this.font.height(), this.height) : this.font.height();
      this.selected = var5.int203() == 0 && MathUtils.on23(var1, var3, vector2f.x(), vector2f.y() - 3.0F, this.width, f + 6.0F);
      if (this.selected) {
         this.boolean140 = false;
      }

      return this.selected;
   }

   public boolean charTyped(char var1, int var2) {
      if (!this.selected) {
         return false;
      }

      this.long130 = System.currentTimeMillis();
      this.int296 = MathHelper.clamp(this.int296, 0, this.text.length());
      if (this.boolean140) {
         this.text = "";
         this.int296 = 0;
         this.boolean140 = false;
      }

      this.ColorAnimator(Character.toString(var1), this.int296);
      this.int296++;
      this.int296 = MathHelper.clamp(this.int296, 0, this.text.length());
      return true;
   }

   public void FovEvent(int var1) {
      StringBuilder stringbuilder = new StringBuilder();

      for (int i = 0; i < this.text.length(); i++) {
         if (i != var1 - 1) {
            stringbuilder.append(this.text.charAt(i));
         }
      }

      this.text = stringbuilder.toString();
   }

   public boolean isEmpty() {
      return this.text.isEmpty();
   }

   public String EmoteRegistry(String var1) {
      if (var1 != null && !var1.isEmpty() && !(this.width <= 0.0F) && !(this.font.width(var1) <= this.width)) {
         int i = var1.length();

         while (i > 0 && this.font.width(var1.substring(0, i)) > this.width) {
            i--;
         }

         return var1.substring(0, i);
      } else {
         return var1 == null ? "" : var1;
      }
   }

   public String UserdataManager(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         return switch (this.searchBoxVar159) {
            case val179, val129, val296, val180 -> on23(var1, map26);
            default -> var1;
         };
      } else {
         return var1;
      }
   }

   public String ArmorHud(String var1) {
      if (var1 != null && !var1.isEmpty() && this.searchBoxVar143 != SearchBox.SearchScope.val389) {
         StringBuilder stringbuilder = new StringBuilder(var1.length());

         for (int i = 0; i < var1.length(); i++) {
            char c0 = var1.charAt(i);
            if (!Character.isWhitespace(c0)) {
               stringbuilder.append(c0);
            }
         }

         return stringbuilder.toString();
      } else {
         return var1;
      }
   }

   public static String on23(String var0, Map<Character, Character> var1) {
      StringBuilder stringbuilder = new StringBuilder(var0.length());

      for (int i = 0; i < var0.length(); i++) {
         char c0 = var0.charAt(i);
         char c1 = Character.toLowerCase(c0);
         Character character = var1.get(c1);
         if (character == null) {
            stringbuilder.append(c0);
         } else {
            char c2 = Character.isUpperCase(c0) ? Character.toUpperCase(character) : character;
            stringbuilder.append(c2);
         }
      }

      return stringbuilder.toString();
   }

   public String getText() {
      return this.text;
   }

   public boolean isSelected() {
      return this.selected;
   }

   public boolean float263() {
      return this.boolean140;
   }

   public int var14348() {
      return this.int296;
   }

   public float map49() {
      return this.float180;
   }

   public Font call050() {
      return this.font;
   }

   public Vector2f call103() {
      return this.vector2f;
   }

   public String getEmptyText() {
      return this.emptyText;
   }

   public float getWidth() {
      return this.width;
   }

   public float getHeight() {
      return this.height;
   }

   public boolean isMultiline() {
      return this.multiline;
   }

   public long float248() {
      return this.long130;
   }

   public int getMaxLength() {
      return this.maxLength;
   }

   public SearchBox.MatchMode float249() {
      return this.searchBoxVar159;
   }

   public SearchBox.SearchScope map47() {
      return this.searchBoxVar143;
   }

   public float getScrollOffset() {
      return this.scrollOffset;
   }

   public boolean map48() {
      return this.boolean141;
   }

   public UiAnimation getEvent11() {
      return this.EventPosHook;
   }

   public void HudHotbarPanel(String var1) {
      this.text = var1;
   }

   public void VelocityChangeEvent(boolean var1) {
      this.selected = var1;
   }

   public void CrosshairTargetUpdateEvent(boolean var1) {
      this.boolean140 = var1;
   }

   public void EventRender(int var1) {
      this.int296 = var1;
   }

   public void JumpEvent(float var1) {
      this.float180 = var1;
   }

   public void on23(Font var1) {
      this.font = var1;
   }


   public enum SearchScope {
      val389,
      val298;
   }

   public enum MatchMode {
      val178,
      val179,
      val129,
      val296,
      val180,
      val500,
      val297;

      public boolean on23(char var1) {
         return switch (this) {
            case val178 -> true;
            case val179 -> Character.isLetter(var1) && var1 <= 127 && Character.isAlphabetic(var1);
            case val129 -> Character.isLetterOrDigit(var1) && var1 <= 127;
            case val296 -> Character.isLetterOrDigit(var1) && var1 <= 127 || var1 == '_';
            case val180 -> Character.isLetterOrDigit(var1) && var1 <= 127 || var1 == '.' || var1 == ':' || var1 == '-' || var1 == '_';
            case val500 -> String.valueOf(var1).matches("[А-Яа-яЁё]");
            case val297 -> Character.isDigit(var1);
         };
      }
   }
}
