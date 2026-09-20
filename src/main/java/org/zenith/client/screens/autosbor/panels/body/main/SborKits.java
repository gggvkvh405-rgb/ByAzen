package org.zenith.client.screens.autosbor.panels.body.main;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.item.ItemStack;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.autosbor.AutoSborStyle;
import org.zenith.core.Easing;
import org.zenith.core.ItemStackStore;
import org.zenith.core.ServerConfigStore;
import org.zenith.core.UiAnimation;
import org.zenith.hud.ScrollHandler;
import org.zenith.util.MathUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class SborKits {
   public static final float gridXOffset = 140.0F;
   public static final float countPanelYOffset = 31.0F;
   public static final float countPanelHeight = 30.0F;
   public static final float blockGap = 4.0F;
   public static final long layoutAnimationDuration = 180L;
   public static final long nameMoveDuration = 120L;
   public static final long kitAppearDuration = 180L;
   public static final float gridWidth = 223.0F;
   public static final float panelWidth = 104.0F;
   public static final float panelHeight = 34.0F;
   public static final float panelGap = 4.0F;
   public static final float panelLeftGap = 4.0F;
   public static final float listHeight = 251.0F;
   public static final float scrollOffsetX = 4.0F;
   public static final float scrollWidth = 1.0F;
   public static final float scrollHeightGap = 4.0F;
   public static final float minScrollThumbHeight = 20.0F;
   public static final float textOffsetX = 8.0F;
   public static final float textHoverOffsetX = 2.0F;
   public static final float textYOffset = 9.0F;
   public static final float deleteIconRightGap = 8.0F;
   public static final float deleteIconTextGap = 5.0F;
   public static final float deleteIconHitGap = 2.0F;
   public static final float previewOffsetY = 4.0F;
   public static final float kitAppearOffsetY = 6.0F;
   public static final float previewIconSize = 7.0F;
   public static final float previewIconGap = 2.0F;
   public static final int previewItemLimit = 9;
   public static final CornerRadius panelRadius = CornerRadius.MovementInputEvent(7.0F);
   public static final CornerRadius scrollRadius = CornerRadius.MovementInputEvent(0.5F);
   public static final Font textFont = Fonts.MEDIUM.getFont(6.0F);
   public static final Font deleteIconFont = Fonts.ICONS.getFont(5.0F);
   public static final String deleteIcon = "[";
   public final ScrollHandler scrollHandler = new ScrollHandler();
   public final Map<ServerConfigStore, List<ItemStack>> previewCache = new IdentityHashMap<>();
   public final Map<ServerConfigStore, UiAnimation> nameXAnimations = new IdentityHashMap<>();
   public final Map<ServerConfigStore, UiAnimation> kitAppearAnimations = new IdentityHashMap<>();
   public final UiAnimation layoutAnimation = new UiAnimation(180L, 0.0F, Easing.PreventActionEvent);
   public final Supplier<String> serverSupplier;
   public float listX;
   public float listY;
   public float currentListHeight;
   public float currentScrollHeight;

   public SborKits(Supplier<String> var1) {
      this.serverSupplier = var1;
   }

   public void render(HudDrawContext var1, float var2, float var3, boolean var4, float var5) {
      List<ServerConfigStore> list = this.getKits();
      float f = this.layoutAnimation.on23(var4 ? 1.0F : 0.0F);
      this.listX = var2 + 140.0F + 223.0F + 4.0F;
      this.listY = var3 + this.getListYOffset(f);
      this.currentListHeight = this.getListHeight(f);
      this.currentScrollHeight = this.currentListHeight - 4.0F;
      this.updateScroll(list.size());
      this.cleanUnusedKitData(list);
      float f1 = (float)this.scrollHandler.float260();
      var1.enableScissor((int)this.listX, (int)this.listY, (int)(this.listX + 104.0F), (int)(this.listY + this.currentListHeight));

      for (int i = 0; i < list.size(); i++) {
         ServerConfigStore l1ili1lll = list.get(i);
         float f2 = this.listY + i * 38.0F - f1;
         float f3 = this.getKitAppearProgress(l1ili1lll);
         float f4 = f2 + (1.0F - f3) * 6.0F;
         float f5 = var5 * f3;
         boolean flag = MathUtils.on23(var1.getMouseX(), var1.getMouseY(), this.listX, f4, 104.0, 34.0);
         var1.drawRoundedRect(this.listX, f4, 104.0F, 34.0F, panelRadius, AutoSborStyle.surface().SprintStateEvent(f5));
         this.renderKitName(var1, l1ili1lll, this.listX, f4, flag, f5);
         this.renderDeleteIcon(var1, this.listX, f4, f5);
         this.renderKitPreview(var1, l1ili1lll, this.listX, f4, f5);
      }

      var1.disableScissor();
      this.renderScrollBar(var1, list.size(), var5);
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (!this.isHovered(var1, var3)) {
         return false;
      }

      if (this.scrollHandler.float261() <= 0.0) {
         return false;
      }

      this.scrollHandler.CloudRouter(var5 * 3.0);
      return true;
   }

   public ServerConfigStore getKitAt(double var1, double var3) {
      int i = this.getKitIndexAt(var1, var3);
      return i < 0 ? null : this.getKits().get(i);
   }

   public ServerConfigStore getDeleteKitAt(double var1, double var3) {
      int i = this.getKitIndexAt(var1, var3);
      if (i < 0) {
         return null;
      }

      float f = (float)this.scrollHandler.float260();
      float f1 = this.listY + i * 38.0F - f;
      return !this.isDeleteIconHovered(var1, var3, this.listX, f1) ? null : this.getKits().get(i);
   }

   public void removePreview(ServerConfigStore var1) {
      this.previewCache.remove(var1);
      this.nameXAnimations.remove(var1);
      this.kitAppearAnimations.remove(var1);
   }

   public float getKitAppearProgress(ServerConfigStore var1) {
      UiAnimation l1i1illlili = this.kitAppearAnimations.computeIfAbsent(var1, var0 -> new UiAnimation(180L, 0.0F, Easing.PreventActionEvent));
      return l1i1illlili.on23(1.0F);
   }

   public void cleanUnusedKitData(List<ServerConfigStore> var1) {
      this.previewCache.keySet().removeIf(var2 -> !this.containsKit(var1, var2));
      this.nameXAnimations.keySet().removeIf(var2 -> !this.containsKit(var1, var2));
      this.kitAppearAnimations.keySet().removeIf(var2 -> !this.containsKit(var1, var2));
   }

   public boolean containsKit(List<ServerConfigStore> var1, ServerConfigStore var2) {
      for (ServerConfigStore l1ili1lll : var1) {
         if (l1ili1lll == var2) {
            return true;
         }
      }

      return false;
   }

   public int getKitIndexAt(double var1, double var3) {
      if (!this.isHovered(var1, var3)) {
         return -1;
      } else {
         List<ServerConfigStore> list = this.getKits();
         float f = (float)this.scrollHandler.float260();
         int i = (int)((var3 - this.listY + f) / 38.0);
         if (i >= 0 && i < list.size()) {
            float f1 = this.listY + i * 38.0F - f;
            return !MathUtils.on23(var1, var3, this.listX, f1, 104.0, 34.0) ? -1 : i;
         } else {
            return -1;
         }
      }
   }

   public void renderKitName(HudDrawContext var1, ServerConfigStore var2, float var3, float var4, boolean var5, float var6) {
      String s = var2.getName();
      String s1 = s == null ? "" : s;
      float f = 86.0F - deleteIconFont.width("[") - 5.0F;
      if (textFont.width(s1) > f) {
         while (!s1.isEmpty() && textFont.width(s1 + "...") > f) {
            s1 = s1.substring(0, s1.length() - 1);
         }

         if (!s1.isEmpty()) {
            s1 = s1 + "...";
         }
      }

      UiAnimation l1i1illlili = this.nameXAnimations.computeIfAbsent(var2, var0 -> new UiAnimation(120L, 0.0F, Easing.PreventActionEvent));
      float f1 = var3 + 8.0F + l1i1illlili.on23(var5 ? 2.0F : 0.0F);
      float f2 = var4 + 9.0F;
      var1.drawText(textFont, s1, f1, f2, AutoSborStyle.text().SprintStateEvent(var6));
   }

   public void renderDeleteIcon(HudDrawContext var1, float var2, float var3, float var4) {
      float f = var2 + 104.0F - 8.0F - deleteIconFont.width("[");
      float f1 = var3 + 9.0F + (textFont.height() - deleteIconFont.height()) / 2.0F;
      var1.drawText(
         deleteIconFont,
         "[",
         f,
         f1,
         (this.isDeleteIconHovered(var1.getMouseX(), var1.getMouseY(), var2, var3) ? AutoSborStyle.text() : AutoSborStyle.textTertiary())
            .SprintStateEvent(var4)
      );
   }

   public boolean isDeleteIconHovered(double var1, double var3, float var5, float var6) {
      float f = var5 + 104.0F - 8.0F - deleteIconFont.width("[");
      float f1 = var6 + 9.0F + (textFont.height() - deleteIconFont.height()) / 2.0F;
      return MathUtils.on23(var1, var3, f - 2.0F, f1 - 2.0F, deleteIconFont.width("[") + 4.0F, deleteIconFont.height() + 4.0F);
   }

   public void renderKitPreview(HudDrawContext var1, ServerConfigStore var2, float var3, float var4, float var5) {
      List<ItemStack> list = this.previewCache.computeIfAbsent(var2, this::buildPreviewStacks);
      float f = var4 + 9.0F + textFont.height() + 4.0F;
      float f1 = 0.4375F;

      for (int i = 0; i < list.size(); i++) {
         ItemStack itemstack = list.get(i);
         float f2 = var3 + 8.0F + i * 9.0F;
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(f2, f);
         var1.getMatrices().scale(f1, f1);
         org.zenith.render.LegacyRenderBridge.enableBlend();
         org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, var5);
         var1.drawItem(itemstack, 0, 0);
         org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         var1.getMatrices().popMatrix();
      }
   }

   public List<ItemStack> buildPreviewStacks(ServerConfigStore var1) {
      List<ItemStack> arraylist = new ArrayList<>(9);

      for (ItemStackStore ll1l11l11l1lli1 : var1.WorldTweaks()) {
         if (arraylist.size() >= 9) {
            break;
         }

         ItemStack itemstack = ll1l11l11l1lli1.BlockOverLay();
         if (!itemstack.isEmpty()) {
            arraylist.add(itemstack);
         }
      }

      return arraylist;
   }

   public void updateScroll(int var1) {
      this.scrollHandler.ProtocolMessage(Math.max(0.0F, this.getContentHeight(var1) - this.currentListHeight));
      this.scrollHandler.update();
   }

   public float getContentHeight(int var1) {
      return var1 <= 0 ? 0.0F : var1 * 34.0F + (var1 - 1) * 4.0F;
   }

   public float getListYOffset(float var1) {
      return 31.0F + 34.0F * var1;
   }

   public float getListHeight(float var1) {
      return 251.0F + 34.0F * (1.0F - var1);
   }

   public void renderScrollBar(HudDrawContext var1, int var2, float var3) {
      float f = this.getContentHeight(var2);
      if (!(this.scrollHandler.float261() <= 0.0) && !(f <= this.currentListHeight)) {
         float f1 = this.listX + 104.0F + 4.0F;
         var1.drawRoundedRect(f1, this.listY, 1.0F, this.currentScrollHeight, scrollRadius, AutoSborStyle.textAlpha(10).SprintStateEvent(var3));
         float f2 = Math.max(20.0F, this.currentScrollHeight * (this.currentListHeight / f));
         f2 = Math.min(this.currentScrollHeight, f2);
         float f3 = (float)this.scrollHandler.float261();
         float f4 = Math.max(0.0F, Math.min(1.0F, (float)this.scrollHandler.float260() / f3));
         float f5 = this.listY + (this.currentScrollHeight - f2) * f4;
         var1.drawRoundedRect(f1, f5, 1.0F, f2, scrollRadius, AutoSborStyle.textAlpha(24).SprintStateEvent(var3));
      }
   }

   public boolean isHovered(double var1, double var3) {
      return MathUtils.on23(var1, var3, this.listX, this.listY, 104.0, this.currentListHeight);
   }

   public List<ServerConfigStore> getKits() {
      return ZenithClient.on23().ItemSpec().BlockInteractEvent(this.getServer());
   }

   public String getServer() {
      return this.serverSupplier == null ? "HolyWorld" : this.serverSupplier.get();
   }
}
