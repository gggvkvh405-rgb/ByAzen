package org.zenith.client.screens.bot;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestion;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.Texts;
import net.minecraft.util.math.MathHelper;

final class BotChatInputSuggestor_SuggestionWindow {
   public final BotChatInputSuggestor this_0;
   public final Rect2i area;
   public final String typedText;
   public final List<Suggestion> suggestions;
   public int inWindowIndex;
   public int selection;
   public double lastMouseX;
   public double lastMouseY;
   public boolean completed;

   void render(DrawContext var1, int var2, int var3) {
      int i = Math.min(this.suggestions.size(), 10);
      boolean flag = this.inWindowIndex > 0;
      boolean flag1 = this.suggestions.size() > this.inWindowIndex + i;
      boolean flag2 = flag || flag1;
      boolean flag3 = this.lastMouseX != var2 || this.lastMouseY != var3;
      if (flag3) {
         this.lastMouseX = var2;
         this.lastMouseY = var3;
      }

      if (flag2) {
         var1.fill(
            this.area.getX(), this.area.getY() - 1, this.area.getX() + this.area.getWidth(), this.area.getY(), -805306368
         );
         var1.fill(
            this.area.getX(),
            this.area.getY() + this.area.getHeight(),
            this.area.getX() + this.area.getWidth(),
            this.area.getY() + this.area.getHeight() + 1,
            -805306368
         );
         if (flag) {
            for (int j = 0; j < this.area.getWidth(); j++) {
               if (j % 2 == 0) {
                  var1.fill(this.area.getX() + j, this.area.getY() - 1, this.area.getX() + j + 1, this.area.getY(), -1);
               }
            }
         }

         if (flag1) {
            for (int l = 0; l < this.area.getWidth(); l++) {
               if (l % 2 == 0) {
                  var1.fill(
                     this.area.getX() + l,
                     this.area.getY() + this.area.getHeight(),
                     this.area.getX() + l + 1,
                     this.area.getY() + this.area.getHeight() + 1,
                     -1
                  );
               }
            }
         }
      }

      boolean flag4 = false;

      for (int k = 0; k < i; k++) {
         Suggestion suggestion = this.suggestions.get(k + this.inWindowIndex);
         var1.fill(
            this.area.getX(),
            this.area.getY() + 12 * k,
            this.area.getX() + this.area.getWidth(),
            this.area.getY() + 12 * k + 12,
            -805306368
         );
         if (var2 > this.area.getX()
            && var2 < this.area.getX() + this.area.getWidth()
            && var3 > this.area.getY() + 12 * k
            && var3 < this.area.getY() + 12 * k + 12) {
            if (flag3) {
               this.select(k + this.inWindowIndex);
            }

            flag4 = true;
         }

         var1.drawTextWithShadow(
            this.this_0.textRenderer,
            suggestion.getText(),
            this.area.getX() + 1,
            this.area.getY() + 2 + 12 * k,
            k + this.inWindowIndex == this.selection ? 65280 : -5592406
         );
      }

      if (flag4) {
         Message message = this.suggestions.get(this.selection).getTooltip();
         if (message != null) {
            var1.drawTooltip(this.this_0.textRenderer, Texts.toText(message), var2, var3);
         }
      }
   }

   BotChatInputSuggestor_SuggestionWindow(BotChatInputSuggestor var1, int var2, int var3, List<Suggestion> var4) {
      this.this_0 = var1;
      int i = var1.fieldTop.getAsInt() - 3 - Math.min(var4.size(), 10) * 12;
      this.area = new Rect2i(var2 - 1, i, var3 + 1, Math.min(var4.size(), 10) * 12);
      this.typedText = var1.textField.getText();
      this.suggestions = var4;
      this.select(0);
   }

   boolean mouseClicked(int var1, int var2) {
      if (!this.area.contains(var1, var2)) {
         return false;
      }

      int i = (var2 - this.area.getY()) / 12 + this.inWindowIndex;
      if (i >= 0 && i < this.suggestions.size()) {
         this.select(i);
         this.complete();
      }

      return true;
   }

   boolean mouseScrolled(double var1, double var3, double var5) {
      if (this.area.contains((int)var3, (int)var5)) {
         this.inWindowIndex = MathHelper.clamp((int)(this.inWindowIndex - var1), 0, Math.max(this.suggestions.size() - 10, 0));
         return true;
      } else {
         return false;
      }
   }

   boolean keyPressed(int var1) {
      if (var1 == 265) {
         this.scroll(-1);
         this.completed = false;
         return true;
      }

      if (var1 == 264) {
         this.scroll(1);
         this.completed = false;
         return true;
      }

      if (var1 == 258) {
         if (this.completed) {
            this.scroll(net.minecraft.client.util.InputUtil.isKeyPressed(
               net.minecraft.client.MinecraftClient.getInstance().getWindow(), org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT
            ) ? -1 : 1);
         }

         this.complete();
         return true;
      } else if (var1 == 256) {
         this.this_0.clearWindow();
         this.this_0.suggestion = null;
         return true;
      } else {
         return false;
      }
   }

   void scroll(int var1) {
      this.select(this.selection + var1);
      int i = this.inWindowIndex;
      int j = this.inWindowIndex + 10 - 1;
      if (this.selection < i) {
         this.inWindowIndex = MathHelper.clamp(this.selection, 0, Math.max(this.suggestions.size() - 10, 0));
      } else if (this.selection > j) {
         this.inWindowIndex = MathHelper.clamp(this.selection + 1 - 10, 0, Math.max(this.suggestions.size() - 10, 0));
      }
   }

   void select(int var1) {
      this.selection = var1;
      if (this.selection < 0) {
         this.selection = this.selection + this.suggestions.size();
      }

      if (this.selection >= this.suggestions.size()) {
         this.selection = this.selection - this.suggestions.size();
      }

      Suggestion suggestion = this.suggestions.get(this.selection);
      this.this_0.suggestion = BotChatInputSuggestor.getSuggestionSuffix(this.this_0.textField.getText(), suggestion.apply(this.typedText));
   }

   void complete() {
      Suggestion suggestion = this.suggestions.get(this.selection);
      this.this_0.completingSuggestions = true;
      String s = suggestion.apply(this.typedText);
      this.this_0.textField.HudHotbarPanel(s);
      this.this_0.lastText = s;
      int i = suggestion.getRange().getStart() + suggestion.getText().length();
      this.this_0.textField.EventRender(i);
      this.this_0.windowActive = true;
      this.this_0.refresh();
      this.select(this.selection);
      this.this_0.completingSuggestions = false;
      this.completed = true;
   }
}
