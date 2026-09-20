package org.zenith.render;


import java.util.function.Supplier;
import org.zenith.ZenithClient;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.Identifier;

public class ParticleTextures {
   public static final Map<String, ParticleTextures.Texture> map51;

   public static String[] getZClass019() {
      ParticleTextures.Texture[] ai1liiil1l11ii1l1l11liiiil11ll_ii1il11l111ii11iil = ParticleTextures.Texture.values();
      String[] astring = new String[ai1liiil1l11ii1l1l11liiiil11ll_ii1il11l111ii11iil.length];

      for (int i = 0; i < ai1liiil1l11ii1l1l11liiiil11ll_ii1il11l111ii11iil.length; i++) {
         astring[i] = ai1liiil1l11ii1l1l11liiiil11ll_ii1il11l111ii11iil[i].var11916();
      }

      return astring;
   }

   public static Identifier ChatTagParser(String var0) {
      ParticleTextures.Texture i1liiil1l11ii1l1l11liiiil11ll_ii1il11l111ii11iil = map51.get(var0);
      if (i1liiil1l11ii1l1l11liiiil11ll_ii1il11l111ii11iil == null) {
         try {
            i1liiil1l11ii1l1l11liiiil11ll_ii1il11l111ii11iil = ParticleTextures.Texture.valueOf(var0.toUpperCase());
         } catch (IllegalArgumentException var3) {
         }
      }

      return i1liiil1l11ii1l1l11liiiil11ll_ii1il11l111ii11iil != null ? i1liiil1l11ii1l1l11liiiil11ll_ii1il11l111ii11iil.boolean83().get() : null;
   }

   static {
      HashMap hashmap = new HashMap();
      hashmap.put("particle.texture.spaceGlow", ParticleTextures.Texture.call437);
      hashmap.put("particle.texture.spaceStar", ParticleTextures.Texture.call438);
      hashmap.put("particle.texture.star", ParticleTextures.Texture.call461);
      hashmap.put("particle.texture.firefly", ParticleTextures.Texture.call410);
      hashmap.put("particle.texture.snowflake", ParticleTextures.Texture.call460);
      hashmap.put("particle.texture.heart", ParticleTextures.Texture.call462);
      map51 = hashmap;
   }


   public enum Texture {
      call460("particle.texture.snowflake", "particles/snowflake.png"),
      call461("particle.texture.star", "particles/star.png"),
      call462("particle.texture.heart", "particles/heart.png"),
      call410("particle.texture.firefly", "particles/firefly.png"),
      call437("particle.texture.spaceGlow", "particles/space_glow.png"),
      call438("particle.texture.spaceStar", "particles/space_star.png");

      public final String string21;
      public final Supplier<Identifier> supplier;

      Texture(String var3, String var4) {
         this.string21 = var3;
         this.supplier = () -> ZenithClient.on23(var4);
      }

      public String var11916() {
         return this.string21;
      }

      public Supplier<Identifier> boolean83() {
         return this.supplier;
      }
   }
}
