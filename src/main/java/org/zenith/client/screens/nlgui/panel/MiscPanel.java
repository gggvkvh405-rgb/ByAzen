package org.zenith.client.screens.nlgui.panel;

import java.util.ArrayList;
import java.util.List;
import org.zenith.ZenithClient;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.NLMenuScreen_ElementsType;
import org.zenith.client.screens.nlgui.panel.api.Panel;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.MenuScreenId;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class MiscPanel extends Panel {
   public final List<MiscPanel_RenderMisc> renderCategories = new ArrayList<>();

   @Override
   public void render(HudDrawContext var1, int var2, int var3, float var4, float var5, float var6) {
      float f = 96.0F;
      float f1 = 112.0F;
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      var1.drawRoundedRectBatched(
         var5, var6, f, f1, CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()), zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var4)
      );
      var1.flushRoundedRects();
      float f2 = var5 + GuiStyle.PADDING * 2;
      float f3 = var6 + GuiStyle.PADDING * 2;
      var1.drawText(Fonts.NEW_REGULAR.getFont(5.0F), "Misc", f2, f3, zenithstyle.getTextTertiary().getColor().SprintStateEvent(var4));

      try {
         f3 += GuiStyle.PADDING * 2 + 5;

         for (MiscPanel_RenderMisc miscpanel_rendermisc : this.renderCategories) {
            miscpanel_rendermisc.render(var1, var2, var3, f2, f3, var4, miscpanel_rendermisc.type == ZenithClient.on23().NbtEditor().getType());
            f3 += 7.0F;
            f3 += 12.0F;
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public MiscPanel() {
      for (NLMenuScreen_ElementsType nlmenuscreen_elementstype : NLMenuScreen_ElementsType.values()) {
         if (nlmenuscreen_elementstype != NLMenuScreen_ElementsType.CATEGORY) {
            this.renderCategories.add(new MiscPanel_RenderMisc(this, nlmenuscreen_elementstype));
         }
      }
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      for (MiscPanel_RenderMisc miscpanel_rendermisc : this.renderCategories) {
         if (miscpanel_rendermisc.onMouseClicked(var1, var3, var5)) {
            return true;
         }
      }

      return false;
   }
}
