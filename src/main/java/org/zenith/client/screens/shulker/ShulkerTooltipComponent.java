package org.zenith.client.screens.shulker;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.VertexConsumerProvider.Immediate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.joml.Matrix4f;

public final class ShulkerTooltipComponent implements TooltipComponent {
   public final DefaultedList<ItemStack> items;
   public static final Identifier shulkerGuiTexture = Identifier.of("zenith", "textures/container.png");
   public static final int guiWidth = 176;
   public static final int guiHeight = 67;
   public static final int slotOffsetX = 8;
   public static final int slotOffsetY = 7;

   public ShulkerTooltipComponent(DefaultedList<ItemStack> var1) {
      this.items = var1;
   }

   public int getHeight(TextRenderer textRenderer) {
      return 67;
   }

   public int getWidth(TextRenderer textRenderer) {
      return 176;
   }

   public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, Immediate vertexConsumers) {
   }

   public void drawItems(TextRenderer textRenderer, int x, int y, int width, int height, DrawContext context) {
      context.drawTexture(RenderPipelines.GUI_TEXTURED, shulkerGuiTexture, x, y, 0.0F, 0.0F, 176, 67, 176, 67);

      for (int i = 0; i < 27; i++) {
         ItemStack itemstack = (ItemStack)this.items.get(i);
         if (!itemstack.isEmpty()) {
            int j = x + 8 + i % 9 * 18;
            int k = y + 7 + i / 9 * 18;
            context.drawItem(itemstack, j, k);
            context.drawStackOverlay(textRenderer, itemstack, j, k);
         }
      }
   }
}
