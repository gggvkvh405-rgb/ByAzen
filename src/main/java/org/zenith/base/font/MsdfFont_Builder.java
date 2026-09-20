package org.zenith.base.font;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.zenith.ZenithClient;
import org.zenith.core.ClientProvider;

public class MsdfFont_Builder {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public String name = "?";
   public Identifier dataIdentifer;
   public Identifier atlasIdentifier;

   public MsdfFont_Builder name(String var1) {
      this.name = var1;
      return this;
   }

   public MsdfFont_Builder data(String var1) {
      this.dataIdentifer = ZenithClient.on23("fonts/msdf/" + var1 + ".json");
      return this;
   }

   public MsdfFont_Builder data(Identifier var1) {
      this.dataIdentifer = var1;
      return this;
   }

   public MsdfFont_Builder atlas(String var1) {
      this.atlasIdentifier = ZenithClient.on23("fonts/msdf/" + var1 + ".png");
      return this;
   }

   public MsdfFont_Builder atlas(Identifier var1) {
      this.atlasIdentifier = var1;
      return this;
   }

   public MsdfFont build() {
      FontData fontdata = ResourceProvider.fromJsonToInstance(this.dataIdentifer, FontData.class);
      AbstractTexture abstracttexture = ClientProvider.minecraftClient3.getTextureManager().getTexture(this.atlasIdentifier);
      if (fontdata == null) {
         throw new RuntimeException(
            "Failed to read font data file: " + this.dataIdentifer.toString() + "; Are you sure this is json file? Try to check the correctness of its syntax."
         );
      }

      float f = fontdata.atlas().width();
      float f1 = fontdata.atlas().height();
      Map<Integer, MsdfGlyph> map = fontdata.glyphs().stream().collect(Collectors.toMap(FontData_GlyphData::unicode, var2x -> new MsdfGlyph(var2x, f, f1)));
      HashMap<Integer, Map<Integer, Float>> hashmap = new HashMap<>();
      fontdata.kernings().forEach(var1x -> {
         Map<Integer, Float> map1 = hashmap.computeIfAbsent(var1x.leftChar(), var0x -> new HashMap<>());
         map1.put(var1x.rightChar(), var1x.advance());
      });
      return new MsdfFont(this.name, abstracttexture, fontdata.atlas(), fontdata.metrics(), map, hashmap);
   }
}
