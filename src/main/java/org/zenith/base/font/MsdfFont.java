package org.zenith.base.font;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.zenith.core.ClientProvider;
import org.zenith.utility.render.display.base.GradientRadius;

public final class MsdfFont implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int WIDTH_CACHE_LIMIT = 4096;
   public final String name;
   public final AbstractTexture texture;
   public final FontData_AtlasData atlas;
   public final FontData_MetricsData metrics;
   public final Map<Integer, MsdfGlyph> glyphs;
   public final Map<Integer, Map<Integer, Float>> kernings;
   public final Map<String, Float> unitWidthCache = new ConcurrentHashMap<>();

   static char normalizeGlyphChar(char var0) {
      return switch (var0) {
         case 'ǫ', 'ʠ' -> 'Q';
         case 'ɢ' -> 'G';
         case 'ɪ' -> 'I';
         case 'ɴ' -> 'N';
         case 'ʀ' -> 'R';
         case 'ʏ' -> 'Y';
         case 'ʙ' -> 'B';
         case 'ʜ' -> 'H';
         case 'ʟ' -> 'L';
         case 'ᴀ' -> 'A';
         case 'ᴄ' -> 'C';
         case 'ᴅ' -> 'D';
         case 'ᴇ' -> 'E';
         case 'ᴊ' -> 'J';
         case 'ᴋ' -> 'K';
         case 'ᴍ' -> 'M';
         case 'ᴏ' -> 'O';
         case 'ᴘ' -> 'P';
         case 'ᴛ' -> 'T';
         case 'ᴜ' -> 'U';
         case 'ᴠ' -> 'V';
         case 'ᴡ' -> 'W';
         case 'ᴢ' -> 'Z';
         case 'ꜰ' -> 'F';
         case 'ꜱ' -> 'S';
         default -> var0;
      };
   }

   public MsdfFont(
      String var1, AbstractTexture var2, FontData_AtlasData var3, FontData_MetricsData var4, Map<Integer, MsdfGlyph> var5, Map<Integer, Map<Integer, Float>> var6
   ) {
      this.name = var1;
      this.texture = var2;
      this.atlas = var3;
      this.metrics = var4;
      this.glyphs = var5;
      this.kernings = var6;
   }

   public int getTextureId() {
      return System.identityHashCode(this.texture);
   }

   public boolean hasGlyph(int var1) {
      return this.glyphs.containsKey(var1);
   }

   public void applyGlyphs(Matrix4f var1, VertexConsumer var2, String var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10) {
      int i = -1;
      boolean flag = false;

      for (int j = 0; j < var3.length(); j++) {
         char c0 = var3.charAt(j);
         if (flag) {
            flag = false;
         } else if (c0 == 167) {
            flag = true;
         } else {
            MsdfGlyph msdfglyph = this.glyphs.get(Integer.valueOf(c0));
            if (msdfglyph == null) {
               char c1 = normalizeGlyphChar(c0);
               msdfglyph = c1 == c0 ? null : this.glyphs.get(Integer.valueOf(c1));
               if (msdfglyph == null) {
                  continue;
               }

               c0 = c1;
            }

            Map<Integer, Float> map = this.kernings.get(i);
            if (map != null) {
               var7 += map.getOrDefault(Integer.valueOf(c0), 0.0F) * var4;
            }

            var7 += msdfglyph.apply(var1, var2, var4, var7, var8, var9, var10) + var5 + var6;
            i = c0;
         }
      }
   }

   public void applyGlyphs(
      Matrix4f var1, VertexConsumer var2, String var3, float var4, float var5, float var6, float var7, float var8, float var9, GradientRadius var10
   ) {
      int i = -1;
      boolean flag = false;

      for (int j = 0; j < var3.length(); j++) {
         char c0 = var3.charAt(j);
         if (flag) {
            flag = false;
         } else if (c0 == 167) {
            flag = true;
         } else {
            MsdfGlyph msdfglyph = this.glyphs.get(Integer.valueOf(c0));
            if (msdfglyph == null) {
               char c1 = normalizeGlyphChar(c0);
               msdfglyph = c1 == c0 ? null : this.glyphs.get(Integer.valueOf(c1));
               if (msdfglyph == null) {
                  continue;
               }

               c0 = c1;
            }

            Map<Integer, Float> map = this.kernings.get(i);
            if (map != null) {
               var7 += map.getOrDefault(Integer.valueOf(c0), 0.0F) * var4;
            }

            var7 += msdfglyph.apply(var1, var2, var4, var7, var8, var9, var10) + var5 + var6;
            i = c0;
         }
      }
   }

   public float getWidth(String var1, float var2) {
      if (var1 != null && !var1.isEmpty()) {
         Float f = this.unitWidthCache.get(var1);
         if (f == null) {
            f = this.computeUnitWidth(var1);
            if (this.unitWidthCache.size() >= 4096) {
               this.unitWidthCache.clear();
            }

            this.unitWidthCache.put(var1, f);
         }

         return f * var2;
      } else {
         return 0.0F;
      }
   }

   public float computeUnitWidth(String var1) {
      int i = -1;
      float f = 0.0F;
      boolean flag = false;

      for (int j = 0; j < var1.length(); j++) {
         char c0 = var1.charAt(j);
         if (flag) {
            flag = false;
         } else if (c0 == 167) {
            flag = true;
         } else {
            MsdfGlyph msdfglyph = this.glyphs.get(Integer.valueOf(c0));
            if (msdfglyph == null) {
               char c1 = normalizeGlyphChar(c0);
               msdfglyph = c1 == c0 ? null : this.glyphs.get(Integer.valueOf(c1));
               if (msdfglyph == null) {
                  continue;
               }

               c0 = c1;
            }

            Map<Integer, Float> map = this.kernings.get(i);
            if (map != null) {
               f += map.getOrDefault(Integer.valueOf(c0), 0.0F);
            }

            f += msdfglyph.getWidth(1.0F);
            i = c0;
         }
      }

      return f;
   }

   static String normalizeSmallCaps(String var0) {
      int i = var0.length();

      for (int j = 0; j < i; j++) {
         char c0 = var0.charAt(j);
         if (normalizeGlyphChar(c0) != c0) {
            StringBuilder stringbuilder = new StringBuilder(i);
            stringbuilder.append(var0, 0, j);

            for (int k = j; k < i; k++) {
               stringbuilder.append(normalizeGlyphChar(var0.charAt(k)));
            }

            return stringbuilder.toString();
         }
      }

      return var0;
   }

   public float getTextWidth(Text var1, float var2) {
      return this.getTextWidthWithVanillaFallback(var1, var2);
   }

   public float getTextWidthWithVanillaFallback(Text var1, float var2) {
      TextRenderer textrenderer = minecraftClient3.textRenderer;
      float f = 0.0F;

      for (FormattedTextProcessor_TextSegment formattedtextprocessor_textsegment : FormattedTextProcessor.processText(var1, -1)) {
         f += this.getWidthWithVanillaFallback(formattedtextprocessor_textsegment.text(), var2, textrenderer);
      }

      return f;
   }

   public float getWidthWithVanillaFallback(String var1, float var2, TextRenderer var3) {
      var1 = this.normalizeTextForMsdf(var1);
      StringBuilder stringbuilder = new StringBuilder();
      boolean flag = false;
      boolean flag1 = false;
      float f = 0.0F;
      int i = 0;

      while (i < var1.length()) {
         int j = var1.codePointAt(i);
         int k = i + Character.charCount(j);
         if (j == 167) {
            i = this.skipFormattingCode(var1, k);
         } else {
            boolean flag2 = this.canRenderWithMsdf(j);
            if (flag && flag1 != flag2) {
               f += this.getRunWidth(stringbuilder.toString(), flag1, var2, var3);
               stringbuilder.setLength(0);
            }

            stringbuilder.append(var1, i, k);
            flag = true;
            flag1 = flag2;
            i = k;
         }
      }

      if (flag) {
         f += this.getRunWidth(stringbuilder.toString(), flag1, var2, var3);
      }

      return f;
   }

   public float getRunWidth(String var1, boolean var2, float var3, TextRenderer var4) {
      return var2 ? this.getWidth(var1, var3) : var4.getWidth(var1) * this.vanillaTextScale(var4, var3);
   }

   public float vanillaTextScale(TextRenderer var1, float var2) {
      return var2 / Math.max(1.0F, 9.0F);
   }

   public boolean canRenderWithMsdf(int var1) {
      return var1 <= 65535 && this.hasGlyph(var1);
   }

   public int skipFormattingCode(String var1, int var2) {
      return var2 >= var1.length() ? var2 : var2 + Character.charCount(var1.codePointAt(var2));
   }

   public String normalizeTextForMsdf(String var1) {
      return normalizeSmallCaps(var1);
   }

   public Font getFont(float var1) {
      return new Font(this, var1);
   }

   public static MsdfFont_Builder builder() {
      return new MsdfFont_Builder();
   }

   public String getName() {
      return this.name;
   }

   public FontData_AtlasData getAtlas() {
      return this.atlas;
   }

   public FontData_MetricsData getMetrics() {
      return this.metrics;
   }
}
