package org.zenith.core;

import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.zenith.utility.render.display.base.CornerRadius;

public class FillShader extends ShaderWrapper implements ClientWindowProvider {
   public LegacyUniform glUniform6;
   public LegacyUniform glUniform7;
   public LegacyUniform glUniform8;
   public LegacyUniform glUniform9;
   public LegacyUniform glUniform10;
   public LegacyUniform glUniform11;

   public FillShader(Identifier var1) {
      super(var1, VertexFormats.POSITION_COLOR);
   }

   public void on23(float var1, float var2, float var3, float var4, float var5, float var6, float var7, CornerRadius var8) {
      this.glUniform6.set(var1, var2);
      this.glUniform7.set(var3, var4);
      this.glUniform8.set(var8.var14311(), var8.string63(), var8.var14312(), var8.itemStack9());
      this.glUniform9.set(var5);
      this.glUniform10.set(var6 * 0.5F);
      this.glUniform11.set(var7);
   }

   @Override
   protected void float57() {
      this.glUniform6 = this.HudArmorPanel("Size");
      this.glUniform7 = this.HudArmorPanel("ShapeSize");
      this.glUniform8 = this.HudArmorPanel("Radius");
      this.glUniform9 = this.HudArmorPanel("Padding");
      this.glUniform10 = this.HudArmorPanel("Sigma");
      this.glUniform11 = this.HudArmorPanel("Spread");
      super.float57();
   }
}
