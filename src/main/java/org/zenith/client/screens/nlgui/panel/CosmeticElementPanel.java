package org.zenith.client.screens.nlgui.panel;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.builder.PlayerPreview3D;
import org.zenith.client.screens.nlgui.cosmetics.CosmeticAvatarImageCache;
import org.zenith.client.screens.nlgui.elements.CosmeticElement;
import org.zenith.client.screens.nlgui.elements.CosmeticSettingsElement;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.core.VisualSettingsStore;
import org.zenith.managers.FriendFilter;
import org.zenith.module.render.Menu;
import org.zenith.render.ShapeRenderer;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class CosmeticElementPanel extends ElementPanel {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final float SCROLL_SPEED = 22.0F;
   public static final float SCROLL_SMOOTH = 0.25F;
   public static final int COLUMNS = 3;
   public static final long COSMETIC_SYNC_INTERVAL_MS = 800L;
   public static final String COSMETICS_DIRECTORY = "cosmetics";
   public static final String AVATAR_FILE_NAME = "avatar.json";
   public static final String HEAD_PREFIX = "head-";
   public static final String WEAPON_PREFIX = "weapon-";
   public static final String CATEGORY_FIELD = "category";
   public static final String CATEGORY_WEAPON = "weapon";
   public static final String CATEGORY_WEAPONS = "weapons";
   public static final String WEAPON_WORD = "оружие";
   public static final float RIGHT_PANEL_HEIGHT = 200.0F;
   public static final float RIGHT_HEADER_HEIGHT = 29.0F;
   public final List<CosmeticElement> elements = new ArrayList<>();
   public final List<CosmeticElement> weaponElements = new ArrayList<>();
   public final List<CosmeticElement> petElements = new ArrayList<>();
   public CosmeticSettingsElement settingsElement;
   public final UiAnimation animationChangeCategory = new UiAnimation(200L, 1.0F, Easing.StopUsingItemEvent);
   public CornerRadiusF headTabBounds;
   public CornerRadiusF modelsTabBounds;
   public CornerRadiusF weaponsTabBounds;
   public CornerRadiusF petsTabBounds;
   public CornerRadiusF settingsTabBounds;
   public CosmeticElementPanel_CosmeticCategory currentCategory = CosmeticElementPanel_CosmeticCategory.SETTINGS;
   public CosmeticElementPanel_CosmeticCategory lastCategory;
   public CornerRadiusF scissorBounds;
   public CornerRadiusF rightPreviewBounds;
   public PlayerPreview3D playerPreview3D;
   public float scroll = 0.0F;
   public float scrollTarget = 0.0F;
   public long lastSyncAt;
   public String lastSignature = "";

   @Override
   public void renderHeader(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = var5 + GuiStyle.PADDING.intValue() + GuiStyle.PADDING.intValue() * 2.0F;
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.NEW_ICONS.getFont(6.0F);
         float f1 = Math.min(1.0F, this.animationChangeCategory.on23(1.0F));
         if (this.lastCategory != null) {
            float f2 = -8.0F * f1;
            this.renderHeaderTitle(font, font1, var1, var4, 1.0F - f1, f, var6 + f2);
         }

         if (f1 > 0.0F) {
            float f3 = 8.0F * (1.0F - f1);
            this.renderHeaderTitle(font, font1, var1, var4, f1, f, var6 + f3);
         }

         this.renderHeaderTabs(var1, var4, var5, var6, var7);
      }
   }

   public void renderHeaderTitle(Font var1, Font var2, HudDrawContext var3, float var4, float var5, float var6, float var7) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = var5 * var4;
         String s = ";";
         float f1 = var7 + (23.0F - var1.height()) / 2.0F;
         float f2 = var7 + (23.0F - var2.height()) / 2.0F - 0.1F;
         float f3 = var6 + var2.width(s) + GuiStyle.PADDING.intValue();
         var3.drawText(var1, "Cosmetics", f3, f1, zenithstyle.getTextEnable().getColor().SprintStateEvent(f));
         var3.drawText(var2, s, var6, f2, zenithstyle.getPrimaryColor().getColor().SprintStateEvent(f));
      }
   }

   public void renderHeaderTabs(HudDrawContext var1, float var2, float var3, float var4, float var5) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(4.8F);
         float f = GuiStyle.PADDING.intValue() * 2.0F;
         float f1 = GuiStyle.PADDING.intValue();
         float f2 = var4 + (23.0F - font.height()) / 2.0F;
         float f3 = 0.5F;
         float f4 = 6.0F;
         ArgbColor i11ii1llliilllii1i1 = zenithstyle.getDisableActiveBg().getColor().SprintStateEvent(var2);
         String s = CosmeticElementPanel_CosmeticCategory.SETTINGS.getName();
         String s1 = CosmeticElementPanel_CosmeticCategory.HEAD.getName();
         String s2 = CosmeticElementPanel_CosmeticCategory.MODELS.getName();
         String s3 = CosmeticElementPanel_CosmeticCategory.WEAPONS.getName();
         String s4 = CosmeticElementPanel_CosmeticCategory.PETS.getName();
         float f5 = font.width(s4);
         float f6 = font.width(s3);
         float f7 = font.width(s2);
         float f8 = font.width(s1);
         float f9 = font.width(s);
         float f10 = var3 + var5 - f - f5;
         float f11 = f10 - f1;
         float f12 = f11 - f1 - f6;
         float f13 = f12 - f1;
         float f14 = f13 - f1 - f7;
         float f15 = f14 - f1;
         float f16 = f15 - f1 - f8;
         float f17 = f16 - f1;
         float f18 = f17 - f1 - f9;
         this.settingsTabBounds = new CornerRadiusF(f18 - 2.0F, var4 + 1.0F, f9 + 4.0F, 21.0F);
         this.headTabBounds = new CornerRadiusF(f16 - 2.0F, var4 + 1.0F, f8 + 4.0F, 21.0F);
         this.modelsTabBounds = new CornerRadiusF(f14 - 2.0F, var4 + 1.0F, f7 + 4.0F, 21.0F);
         this.weaponsTabBounds = new CornerRadiusF(f12 - 2.0F, var4 + 1.0F, f6 + 4.0F, 21.0F);
         this.petsTabBounds = new CornerRadiusF(f10 - 2.0F, var4 + 1.0F, f5 + 4.0F, 21.0F);
         boolean flag = ZenithClient.on23().EnchantItemSpec().CameraTweaks();
         boolean flag1 = ZenithClient.on23().ItemServiceBase().TextReplaceUtils();
         float f19 = 0.5F;
         var1.drawText(
            font,
            s,
            f18,
            f2,
            (this.currentCategory == CosmeticElementPanel_CosmeticCategory.SETTINGS
                  ? zenithstyle.getTextEnable().getColor()
                  : zenithstyle.getTextSecondary().getColor())
               .SprintStateEvent(var2)
         );
         var1.drawRect(f17, f2, f3, f4, i11ii1llliilllii1i1);
         var1.drawText(
            font,
            s1,
            f16,
            f2,
            (this.currentCategory == CosmeticElementPanel_CosmeticCategory.HEAD
                  ? zenithstyle.getTextEnable().getColor()
                  : zenithstyle.getTextSecondary().getColor())
               .SprintStateEvent(var2)
         );
         if (flag) {
            var1.drawRect(f16, f2 + font.height() / 2.0F, f8, f19, zenithstyle.getTextSecondary().getColor().SprintStateEvent(var2 * 0.7F));
         }

         var1.drawRect(f15, f2, f3, f4, i11ii1llliilllii1i1);
         var1.drawText(
            font,
            s2,
            f14,
            f2,
            (this.currentCategory == CosmeticElementPanel_CosmeticCategory.MODELS
                  ? zenithstyle.getTextEnable().getColor()
                  : zenithstyle.getTextSecondary().getColor())
               .SprintStateEvent(var2)
         );
         if (flag) {
            var1.drawRect(f14, f2 + font.height() / 2.0F, f7, f19, zenithstyle.getTextSecondary().getColor().SprintStateEvent(var2 * 0.7F));
         }

         var1.drawRect(f13, f2, f3, f4, i11ii1llliilllii1i1);
         var1.drawText(
            font,
            s3,
            f12,
            f2,
            (this.currentCategory == CosmeticElementPanel_CosmeticCategory.WEAPONS
                  ? zenithstyle.getTextEnable().getColor()
                  : zenithstyle.getTextSecondary().getColor())
               .SprintStateEvent(var2)
         );
         if (flag) {
            var1.drawRect(f12, f2 + font.height() / 2.0F, f6, f19, zenithstyle.getTextSecondary().getColor().SprintStateEvent(var2 * 0.7F));
         }

         var1.drawRect(f11, f2, f3, f4, i11ii1llliilllii1i1);
         var1.drawText(
            font,
            s4,
            f10,
            f2,
            (this.currentCategory == CosmeticElementPanel_CosmeticCategory.PETS
                  ? zenithstyle.getTextEnable().getColor()
                  : zenithstyle.getTextSecondary().getColor())
               .SprintStateEvent(var2)
         );
         if (flag1) {
            var1.drawRect(f10, f2 + font.height() / 2.0F, f5, f19, zenithstyle.getTextSecondary().getColor().SprintStateEvent(var2 * 0.7F));
         }
      }
   }

   @Override
   public void render(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      this.syncCosmetics();
      this.scissorBounds = new CornerRadiusF(var5, var6, 376.0F, 297.0F - GuiStyle.PADDING.intValue() * 2.0F);
      this.animationChangeCategory.on23(Menu.menu.int470());
      if (this.settingsElement == null) {
         this.settingsElement = new CosmeticSettingsElement();
      }

      List<CosmeticElement> list = this.currentCategory == CosmeticElementPanel_CosmeticCategory.SETTINGS
         ? Collections.emptyList()
         : this.getFilteredElements(this.currentCategory);
      float f = this.currentCategory == CosmeticElementPanel_CosmeticCategory.SETTINGS
         ? this.settingsElement.getHeight() + GuiStyle.PADDING.intValue()
         : this.getContentHeight(list, GuiStyle.PADDING.intValue());
      this.clampScroll(f + GuiStyle.PADDING.intValue(), this.scissorBounds.height());
      this.scroll = this.scroll + (this.scrollTarget - this.scroll) * 0.25F;
      float f1 = this.animationChangeCategory.on23(1.0F);
      if (this.animationChangeCategory.isDone()) {
         this.lastCategory = null;
      }

      float f2 = (0.5F - f1) / 0.5F;
      if (f2 < 0.0F) {
         f2 = 0.0F;
      }

      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      float f3 = (f1 - 0.3F) / 0.7F;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f3 > 1.0F) {
         f3 = 1.0F;
      }

      var1.enableScissor(var5, var6, var5 + this.scissorBounds.width(), var6 + this.scissorBounds.height());
      ShapeRenderer.boolean184 = false;
      if (this.lastCategory != null && f2 > 0.0F) {
         float f4 = var4 * f2;
         if (this.lastCategory == CosmeticElementPanel_CosmeticCategory.SETTINGS && this.settingsElement != null) {
            this.settingsElement.render(var1, var2, var3, var5 + GuiStyle.PADDING.intValue(), var6 + this.scroll, f4);
         } else {
            this.renderElements(this.getFilteredElements(this.lastCategory), this.lastCategory, var1, var2, var3, f4, var5, var6);
         }
      }

      if (f3 > 0.0F) {
         float f6 = var4 * f3;
         float f5 = 20.0F * (1.0F - f3);
         if (this.currentCategory == CosmeticElementPanel_CosmeticCategory.SETTINGS && this.settingsElement != null) {
            this.settingsElement.render(var1, var2, var3, var5 + GuiStyle.PADDING.intValue(), var6 + this.scroll + f5, f6);
         } else {
            this.renderElements(list, this.currentCategory, var1, var2, var3, f6, var5, var6 + f5);
         }
      }

      var1.disableScissor();
      ShapeRenderer.boolean184 = true;
   }

   @Override
   public void renderPriority(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      if (this.currentCategory == CosmeticElementPanel_CosmeticCategory.SETTINGS && this.settingsElement != null) {
         float f = this.animationChangeCategory.CancellableEvent();
         float f1 = Math.max(0.0F, Math.min(1.0F, (f - 0.3F) / 0.7F));
         if (!(f1 <= 0.0F)) {
            float f2 = var4 * f1;
            float f3 = 20.0F * (1.0F - f1);
            this.settingsElement.renderPriority(var1, var2, var3, var5 + GuiStyle.PADDING.intValue(), var6 + this.scroll + f3, f2);
         }
      }
   }

   public void renderElements(
      List<CosmeticElement> var1, CosmeticElementPanel_CosmeticCategory var2, HudDrawContext var3, int var4, int var5, float var6, float var7, float var8
   ) {
      if (var1.isEmpty()) {
         this.renderEmptyState(var3, var6, var7, var8);
      } else {
         float f = var7 + GuiStyle.PADDING.intValue();
         float f1 = var8 + this.scroll;
         float f2 = 376.0F - GuiStyle.PADDING.intValue() * 2.0F;
         float f3 = GuiStyle.PADDING.intValue();
         float f4 = (f2 - f3 * 2.0F) / 3.0F;
         float[] afloat = new float[3];
         int i = 0;

         for (CosmeticElement cosmeticelement : var1) {
            int j = 0;

            for (int k = 1; k < 3; k++) {
               if (afloat[k] < afloat[j]) {
                  j = k;
               }
            }

            float f6 = f + j * (f4 + f3);
            float f5 = f1 + afloat[j];
            CornerRadiusF l11liliill1iii1 = new CornerRadiusF(f6, f5, cosmeticelement.getWidth(), cosmeticelement.getHeight());
            if (this.scissorBounds.on23(l11liliill1iii1)) {
               cosmeticelement.render(var3, var4, var5, f6, f5, var6, i);
            }

            afloat[j] += cosmeticelement.getHeight() + f3;
            i++;
         }
      }
   }

   public void renderEmptyState(HudDrawContext var1, float var2, float var3, float var4) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
         float f = var3 + 188.0F;
         float f1 = var4 + (297.0F - GuiStyle.PADDING.intValue() * 2.0F) / 2.0F;
         String s = ZenithClient.on23().Easing().translate("panel.cosmetics.empty");
         var1.drawText(font, s, f - font.width(s) / 2.0F, f1 - font.height() / 2.0F, zenithstyle.getTextEnable().getColor().SprintStateEvent(var2));
      }
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.playerPreview3D != null
         && this.rightPreviewBounds != null
         && this.rightPreviewBounds.PotionItemBuilder(var1, var3)
         && this.playerPreview3D.onMouseScrolled(var1, var3, var7)) {
         return true;
      }

      if (this.scissorBounds != null && this.scissorBounds.PotionItemBuilder(var1, var3)) {
         List<CosmeticElement> list = this.getFilteredElements(this.currentCategory);
         if (list.isEmpty()) {
            return false;
         }

         float f = this.scissorBounds.height();
         float f1 = this.getContentHeight(list, GuiStyle.PADDING.intValue()) + GuiStyle.PADDING.intValue();
         if (f1 <= f) {
            return false;
         }

         this.scrollTarget += (float)var7 * 22.0F;
         this.clampScroll(f1, f);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.playerPreview3D != null
         && this.rightPreviewBounds != null
         && this.rightPreviewBounds.PotionItemBuilder(var1, var3)
         && this.playerPreview3D.onMouseClicked(var1, var3, var5.int203())) {
         return true;
      }

      try {
         if (var5 == MenuScreenId.call004) {
            boolean flag = ZenithClient.on23().EnchantItemSpec().CameraTweaks();
            boolean flag1 = ZenithClient.on23().ItemServiceBase().TextReplaceUtils();
            if (this.headTabBounds != null && this.headTabBounds.PotionItemBuilder(var1, var3) && !flag) {
               this.setCategory(CosmeticElementPanel_CosmeticCategory.HEAD);
               return true;
            }

            if (this.modelsTabBounds != null && this.modelsTabBounds.PotionItemBuilder(var1, var3) && !flag) {
               this.setCategory(CosmeticElementPanel_CosmeticCategory.MODELS);
               return true;
            }

            if (this.weaponsTabBounds != null && this.weaponsTabBounds.PotionItemBuilder(var1, var3) && !flag) {
               this.setCategory(CosmeticElementPanel_CosmeticCategory.WEAPONS);
               return true;
            }

            if (this.petsTabBounds != null && this.petsTabBounds.PotionItemBuilder(var1, var3) && !flag1) {
               this.setCategory(CosmeticElementPanel_CosmeticCategory.PETS);
               return true;
            }

            if (this.settingsTabBounds != null && this.settingsTabBounds.PotionItemBuilder(var1, var3)) {
               this.setCategory(CosmeticElementPanel_CosmeticCategory.SETTINGS);
               return true;
            }
         }

         if (!this.animationChangeCategory.isDone()) {
            return false;
         }

         if (var5 != MenuScreenId.call004) {
            return false;
         }

         if (this.scissorBounds == null || !this.scissorBounds.PotionItemBuilder(var1, var3)) {
            return false;
         }

         if (this.currentCategory == CosmeticElementPanel_CosmeticCategory.SETTINGS) {
            if (this.settingsElement != null) {
               if (this.settingsElement.onMousePriorityClicked(var1, var3, var5)) {
                  return true;
               }

               return this.settingsElement.onMouseClicked(var1, var3, var5);
            }

            return false;
         }

         for (CosmeticElement cosmeticelement : this.getFilteredElements(this.currentCategory)) {
            if (cosmeticelement.onMouseClicked(var1, var3, var5)) {
               return true;
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }

      return false;
   }

   @Override
   public boolean onMouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.playerPreview3D == null) {
         return false;
      }

      boolean flag = this.rightPreviewBounds != null && this.rightPreviewBounds.PotionItemBuilder(var1, var3);
      return (this.playerPreview3D.isDragging() || flag) && this.playerPreview3D.onMouseDragged(var1, var3, var5, var6, var8);
   }

   @Override
   public boolean onMouseReleased(double var1, double var3, MenuScreenId var5) {
      if (this.playerPreview3D != null) {
         this.playerPreview3D.onMouseReleased(var1, var3, var5.int203());
      }

      if (this.currentCategory == CosmeticElementPanel_CosmeticCategory.SETTINGS && this.settingsElement != null) {
         this.settingsElement.onMouseReleased(var1, var3, var5);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public List<? extends Element> getElements() {
      return this.getFilteredElements(this.currentCategory);
   }

   @Override
   public void close() {
      CosmeticAvatarImageCache.clear();
      this.playerPreview3D = null;
      this.rightPreviewBounds = null;
      this.animationChangeCategory.UiAnimation(1.0F);
      this.lastCategory = null;
      this.headTabBounds = null;
      this.modelsTabBounds = null;
      this.weaponsTabBounds = null;
      this.petsTabBounds = null;
   }

   @Override
   public void tick() {
      this.syncCosmetics();
   }

   public void setCategory(CosmeticElementPanel_CosmeticCategory var1) {
      this.setCategory(var1, false);
   }

   public void setCategory(CosmeticElementPanel_CosmeticCategory var1, boolean var2) {
      if ((var2 || this.animationChangeCategory.isDone()) && this.currentCategory != var1) {
         this.lastCategory = this.currentCategory;
         this.currentCategory = var1;
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
         this.animationChangeCategory.UiAnimation(0.0F);
         this.animationChangeCategory.Easing(1.0F);
      }
   }

   public void syncCosmetics() {
      long i = System.currentTimeMillis();
      if (i - this.lastSyncAt >= 800L) {
         this.lastSyncAt = i;
         Path path = getCosmeticsDirectory();
         if (path != null) {
            VisualSettingsStore ll1iliil1l1li11li111l = ZenithClient.on23().EnchantItemSpec();
            List<CosmeticElementPanel_CosmeticEntry> list = loadCosmetics(path).stream().filter(var1x -> ll1iliil1l1li11li111l.Easing(var1x.path())).toList();
            String s = list.stream()
               .map(var0 -> var0.path().toAbsolutePath().normalize() + "|" + var0.category())
               .sorted(String.CASE_INSENSITIVE_ORDER)
               .reduce((var0, var1x) -> var0 + "|" + var1x)
               .orElse("");
            if (!Objects.equals(s, this.lastSignature)) {
               this.elements.clear();
               this.weaponElements.clear();
               this.petElements.clear();
               FriendFilter illli1llllii1ii111ili = ZenithClient.on23().ItemServiceBase();

               for (CosmeticElementPanel_CosmeticEntry cosmeticelementpanel_cosmeticentry : list) {
                  if (isWeaponCategory(cosmeticelementpanel_cosmeticentry.category())) {
                     this.weaponElements
                        .add(
                           new CosmeticElement(
                              cosmeticelementpanel_cosmeticentry.name(),
                              cosmeticelementpanel_cosmeticentry.relativePath(),
                              cosmeticelementpanel_cosmeticentry.path(),
                              ll1iliil1l1li11li111l::JumpCircle,
                              ll1iliil1l1li11li111l::UiAnimation
                           )
                        );
                  } else {
                     this.elements
                        .add(
                           new CosmeticElement(
                              cosmeticelementpanel_cosmeticentry.name(),
                              cosmeticelementpanel_cosmeticentry.relativePath(),
                              cosmeticelementpanel_cosmeticentry.path(),
                              ll1iliil1l1li11li111l::HitParticles,
                              ll1iliil1l1li11li111l::on23
                           )
                        );
                     if (!isHeadPath(cosmeticelementpanel_cosmeticentry.relativePath())) {
                        this.petElements
                           .add(
                              new CosmeticElement(
                                 cosmeticelementpanel_cosmeticentry.name(),
                                 cosmeticelementpanel_cosmeticentry.relativePath(),
                                 cosmeticelementpanel_cosmeticentry.path(),
                                 illli1llllii1ii111ili::Rotation,
                                 illli1llllii1ii111ili::ColorAnimator
                              )
                           );
                     }
                  }
               }

               this.moveWeaponOffTheBodySlot(ll1iliil1l1li11li111l);
               this.lastSignature = s;
            }
         }
      }
   }

   public List<CosmeticElement> getFilteredElements(CosmeticElementPanel_CosmeticCategory var1) {
      List<CosmeticElement> object;
      if (var1 == CosmeticElementPanel_CosmeticCategory.PETS) {
         object = new ArrayList<>(this.petElements);
      } else if (var1 == CosmeticElementPanel_CosmeticCategory.HEAD) {
         object = this.elements.stream().filter(var0 -> isHeadPath(var0.getRelativePath())).toList();
      } else if (var1 == CosmeticElementPanel_CosmeticCategory.WEAPONS) {
         object = new ArrayList<>(this.weaponElements);
      } else if (var1 == CosmeticElementPanel_CosmeticCategory.MODELS) {
         object = this.elements.stream().filter(var1x -> !isHeadPath(var1x.getRelativePath()) && !this.isWeaponElement(var1x)).toList();
      } else {
         object = Collections.emptyList();
      }

      String s = ZenithClient.on23().NbtEditor().getSearchValue();
      if (s != null && !s.isBlank()) {
         String s1 = s.trim().toLowerCase(Locale.ROOT);
         return object.stream()
            .filter(var1x -> var1x.getName().toLowerCase(Locale.ROOT).contains(s1) || var1x.getRelativePath().toLowerCase(Locale.ROOT).contains(s1))
            .toList();
      } else {
         return object;
      }
   }

   public float getContentHeight(List<CosmeticElement> var1, float var2) {
      if (var1.isEmpty()) {
         return 0.0F;
      }

      float[] afloat = new float[3];

      for (CosmeticElement cosmeticelement : var1) {
         int i = 0;

         for (int j = 1; j < 3; j++) {
            if (afloat[j] < afloat[i]) {
               i = j;
            }
         }

         afloat[i] += cosmeticelement.getHeight() + var2;
      }

      float f1 = 0.0F;

      for (float f : afloat) {
         if (f > f1) {
            f1 = f;
         }
      }

      return Math.max(0.0F, f1 - var2);
   }

   public void clampScroll(float var1, float var2) {
      if (var1 <= var2) {
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
      } else {
         float f = var2 - var1;
         if (this.scrollTarget < f) {
            this.scrollTarget = f;
         }

         if (this.scrollTarget > 0.0F) {
            this.scrollTarget = 0.0F;
         }

         if (this.scroll < f) {
            this.scroll = f;
         }

         if (this.scroll > 0.0F) {
            this.scroll = 0.0F;
         }
      }
   }

   public static Path getCosmeticsDirectory() {
      try {
         Path path = ZenithClient.ColorAnimator.toPath().resolve("cosmetics");
         Files.createDirectories(path);
         return path;
      } catch (Exception exception) {
         return null;
      }
   }

   public static List<CosmeticElementPanel_CosmeticEntry> loadCosmetics(Path var0) {
      ArrayList<Path> arraylist = new ArrayList<>();
      collectAvatarDirectories(var0, arraylist);
      arraylist.sort(Comparator.comparing(var1x -> displayName(var0, (Path)var1x), String.CASE_INSENSITIVE_ORDER));
      List<CosmeticElementPanel_CosmeticEntry> arraylist1 = new ArrayList<>();

      for (Path path : arraylist) {
         String s = var0.relativize(path).toString().replace('\\', '/');
         arraylist1.add(new CosmeticElementPanel_CosmeticEntry(displayName(var0, path), s, path, readCategory(var0, path)));
      }

      return arraylist1;
   }

   public static void collectAvatarDirectories(Path var0, List<Path> var1) {
      if (Files.isDirectory(var0) && !isIgnoredDirectory(var0)) {
         if (hasAvatarJson(var0)) {
            var1.add(var0);
         } else {
            try (Stream stream = Files.list(var0)) {
               stream.filter(var0x -> Files.isDirectory((Path)var0x)).forEach(var1xx -> collectAvatarDirectories((Path)var1xx, var1));
            } catch (Exception var7) {
            }
         }
      }
   }

   public static boolean isIgnoredDirectory(Path var0) {
      Path path = var0.getFileName();
      return path != null && path.toString().startsWith(".");
   }

   public static boolean hasAvatarJson(Path var0) {
      try (Stream stream = Files.list(var0)) {
         return stream.anyMatch(var0x -> var0x.getClass().toString().equalsIgnoreCase("avatar.json"));
      } catch (Exception exception) {
         return false;
      }
   }

   public static String readCategory(Path var0, Path var1) {
      Path path = var1.resolve("avatar.json");
      String s = var0.relativize(var1).toString().replace('\\', '/');
      String s1 = "";
      String s2 = "";

      try {
         JsonObject jsonobject = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
         String s3 = normalizeCategory(getJsonString(jsonobject, "category"));
         if (!s3.isBlank()) {
            return s3;
         }

         s1 = getJsonString(jsonobject, "name");
         s2 = getJsonString(jsonobject, "description");
      } catch (Exception var8) {
      }

      return inferCategory(s, s1, s2);
   }

   public static String getJsonString(JsonObject var0, String var1) {
      if (var0 != null && var0.has(var1) && !var0.get(var1).isJsonNull()) {
         try {
            return var0.get(var1).getAsString();
         } catch (Exception exception) {
            return "";
         }
      } else {
         return "";
      }
   }

   public static String inferCategory(String var0, String var1, String var2) {
      String s = (var0 + " " + var1 + " " + var2).toLowerCase(Locale.ROOT);
      return !s.startsWith("weapon-")
            && !s.contains("оружие")
            && !s.contains("weapon")
            && !s.contains("sword")
            && !s.contains("greatsword")
            && !s.contains("hammer")
            && !s.contains("halberd")
            && !s.contains("axe")
            && !s.contains("меч")
            && !s.contains("молоток")
         ? ""
         : "weapon";
   }

   public static String normalizeCategory(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String s = var0.trim().toLowerCase(Locale.ROOT);
         return !"weapon".equals(s) && !"weapons".equals(s) && !"оружие".equals(s) ? s : "weapon";
      } else {
         return "";
      }
   }

   public static boolean isHeadPath(String var0) {
      return var0 != null && var0.toLowerCase(Locale.ROOT).startsWith("head-");
   }

   public static boolean isWeaponCategory(String var0) {
      return "weapon".equals(normalizeCategory(var0));
   }

   public void moveWeaponOffTheBodySlot(VisualSettingsStore var1) {
      Path path = var1.HitParticles();
      if (path != null) {
         for (CosmeticElement cosmeticelement : this.weaponElements) {
            if (samePath(cosmeticelement.getPath(), path)) {
               var1.on23(null);
               var1.UiAnimation(path);
               return;
            }
         }
      }
   }

   public boolean isWeaponElement(CosmeticElement var1) {
      for (CosmeticElement cosmeticelement : this.weaponElements) {
         if (samePath(cosmeticelement.getPath(), var1.getPath())) {
            return true;
         }
      }

      return false;
   }

   public static String displayName(Path var0, Path var1) {
      String s;
      if (var1.startsWith(var0)) {
         s = var0.relativize(var1).toString().replace('\\', '/');
      } else {
         Path path = var1.getFileName();
         s = path == null ? "Unknown" : path.toString();
      }

      String s1 = s.toLowerCase(Locale.ROOT);
      if (s1.startsWith("head-")) {
         s = s.substring("head-".length());
      } else if (s1.startsWith("weapon-")) {
         s = s.substring("weapon-".length());
      }

      return s.replace("/", " / ");
   }

   public static String trimToWidth(Font var0, String var1, float var2) {
      if (var1 != null && !var1.isEmpty() && !(var0.width(var1) <= var2)) {
         String s = "...";
         float f = var0.width(s);
         if (f > var2) {
            return "";
         }

         int i = var1.length();

         while (i > 0 && var0.width(var1.substring(0, i)) + f > var2) {
            i--;
         }

         return i <= 0 ? s : var1.substring(0, i) + s;
      } else {
         return var1 == null ? "" : var1;
      }
   }

   public static boolean samePath(Path var0, Path var1) {
      return var0 != null && var1 != null ? var0.toAbsolutePath().normalize().equals(var1.toAbsolutePath().normalize()) : false;
   }

   @Override
   public boolean isRender() {
      return true;
   }

   @Override
   public void renderRightPanel(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = var4 * var7;
         if (!(f <= 0.01F)) {
            float f1 = var7 * var4;
            float f2 = var5 - (128 + GuiStyle.PADDING) * (1.0F - f1);
            float f3 = 171.0F;
            var1.enableScissor(var5 - GuiStyle.PADDING.intValue() * 3.0F, var6, var5 + 128.0F + GuiStyle.PADDING.intValue() * 4.0F, var6 + 200.0F);
            if (ZenithClient.on23().NbtEditor().getBlurPower() != 0.0F) {
               ShapeRenderer.on23(
                  var1.getMatrices(),
                  f2,
                  var6,
                  128.0F,
                  200.0F,
                  ZenithClient.on23().NbtEditor().getBlurPower(),
                  CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
                  ArgbColor.var11934.SprintStateEvent(f),
                  true,
                  false
               );
            }

            var1.drawRoundedRect(
               f2, var6, 128.0F, 29.0F, CornerRadius.Event29(GuiStyle.ROUND.intValue()), zenithstyle.getRightBackground().getColor().SprintStateEvent(f)
            );
            var1.drawRoundedRect(
               f2,
               var6 + 29.0F,
               128.0F,
               f3,
               CornerRadius.RotationUpdateStartEvent(GuiStyle.ROUND.intValue()),
               zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(f)
            );
            Font font = Fonts.NEW_MEDIUM.getFont(5.0F);
            Font font1 = Fonts.NEW_ICONS.getFont(4.5F);
            String s = ";";
            float f4 = f2 + GuiStyle.PADDING.intValue() * 2.0F;
            float f5 = var6 + (29.0F - font.height()) / 2.0F;
            var1.drawText(font1, s, f4, var6 + (29.0F - font1.height()) / 2.0F - 0.1F, zenithstyle.getPrimaryColor().getColor().SprintStateEvent(f));
            String s1 = this.getSelectedName();
            var1.drawText(
               font,
               trimToWidth(font, s1, 128.0F - GuiStyle.PADDING.intValue() * 5.0F - font1.width(s)),
               f4 + font1.width(s) + GuiStyle.PADDING.intValue(),
               f5,
               zenithstyle.getTextEnable().getColor().SprintStateEvent(f)
            );
            float f6 = var6 + 29.0F;
            float f7 = 128.0F;
            this.rightPreviewBounds = new CornerRadiusF(f2, f6, f7, f3);
            if (this.playerPreview3D == null) {
               this.playerPreview3D = new PlayerPreview3D(f2, f6, f7, f3);
            } else {
               this.playerPreview3D.setBounds(f2, f6, f7, f3);
            }

            ClientPlayerEntity clientplayerentity = minecraftClient3.player;
            if (clientplayerentity != null) {
               var1.enableScissor(f2, f6, f2 + f7, f6 + f3);
               this.playerPreview3D.render(var1, clientplayerentity, var2, var3);
               var1.disableScissor();
            } else {
               Font font2 = Fonts.NEW_REGULAR.getFont(5.0F);
               String s2 = "No player";
               var1.drawText(
                  font2,
                  s2,
                  f2 + (f7 - font2.width(s2)) / 2.0F,
                  f6 + (f3 - font2.height()) / 2.0F,
                  zenithstyle.getTextSecondary().getColor().SprintStateEvent(f)
               );
            }

            var1.disableScissor();
         }
      }
   }

   public String getSelectedName() {
      Path path = ZenithClient.on23().EnchantItemSpec().HitParticles();
      if (path == null) {
         return "Cosmetic preview";
      }

      for (CosmeticElement cosmeticelement : this.elements) {
         if (samePath(cosmeticelement.getPath(), path)) {
            return cosmeticelement.getName();
         }
      }

      return "Cosmetic preview";
   }

   @Override
   public CornerRadiusF getRightPanelBlurBounds(float var1, float var2, float var3, float var4) {
      float f = this.getRightPanelBlurProgress(var3, var4);
      if (f <= 0.01F) {
         return null;
      }

      float f1 = var1 - (128 + GuiStyle.PADDING) * (1.0F - f);
      return new CornerRadiusF(f1, var2, 128.0F, 200.0F);
   }

   public CosmeticElementPanel_CosmeticCategory getCurrentCategory() {
      return this.currentCategory;
   }
}
