package org.zenith.client.screens.nlgui.style;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import org.zenith.core.ColorAnimator;
import org.zenith.event.EventRenderScreenHook;
import org.zenith.util.ArgbColor;
import org.zenith.util.ColorUtils;
import org.zenith.utility.render.display.base.GradientRadius;

public class StyleManager {
   public final List<ZenithStyle> styles = new ArrayList<>();
   public final ColorAnimator colorCycleIcon;
   public ZenithStyle currentStyle;

   public StyleManager() {
      EventManager.register(this);
      this.colorCycleIcon = new ColorAnimator(1700L);
      this.styles.addAll(this.createDefaultStyles());
      if (this.currentStyle == null && !this.styles.isEmpty()) {
         this.currentStyle = this.styles.getFirst();
      }
   }

   @EventTarget
   public void on474(EventRenderScreenHook var1) {
      this.colorCycleIcon.update();
   }

   public GradientRadius getColorCycleIconGradient() {
      ZenithStyle zenithstyle = this.getCurrentStyle();
      return zenithstyle == null ? GradientRadius.CloudPoller(ArgbColor.var11934) : this.colorCycleIcon.on23(zenithstyle.getPrimaryColor().getColor());
   }

   public ZenithStyle getCurrentStyle() {
      if (this.currentStyle != null) {
         return this.currentStyle;
      } else {
         return this.styles.isEmpty() ? null : this.styles.getFirst();
      }
   }

   public List<ZenithStyle> createDefaultStyles() {
      List<ZenithStyle> arraylist = new ArrayList<>();
      arraylist.add(this.createDefaultStyle("Zenith", "#A6B2FF", "#C4A6FF", "#0E0E107A"));
      arraylist.add(this.createDefaultStyle("Recode", "#FF97A0", "#FFB897", "#100B0C7A"));
      arraylist.add(this.createDefaultStyle("Nebula", "#84EBB4", "#84D0EB", "#1116137A"));
      arraylist.add(this.createMistStyle());
      arraylist.add(this.createDefaultStyle("Obsidian Night", "#A897FF", "#CF97FF", "#06040C7A"));
      arraylist.add(this.createDefaultStyle("Arctic Frost", "#97DCFF", "#97FFEC", "#1416167A"));
      arraylist.add(this.createDefaultStyle("Ultraviolet", "#FFC197", "#FFE197", "#0D06017A"));
      arraylist.add(this.createDefaultStyle("Neon Blood Moon", "#F697FF", "#C197FF", "#1616167A"));
      arraylist.add(this.createDefaultStyle("Toxic Anarchy", "#326077", "#327756", "#0A0F117A"));
      arraylist.add(this.createDefaultStyle("Deep Ocean", "#AA193B", "#6E1950", "#1615157A"));
      arraylist.add(this.createDefaultStyle("Sunset Glow", "#FF6B35", "#FF356B", "#1A0D097A"));
      arraylist.add(this.createDefaultStyle("Cyberpunk 2077", "#FF00FF", "#00D4FF", "#1400147A"));
      return arraylist;
   }

   public ZenithStyle createDefaultStyle(String var1, String var2, String var3, String var4) {
      ZenithStyle zenithstyle = new ZenithStyle(var1);
      zenithstyle.getPrimaryColor().setColor(new ArgbColor(var2));
      zenithstyle.getSecondaryPrimaryColor().setColor(new ArgbColor(var3));
      zenithstyle.setDefaultsFromGuiStyle(new ArgbColor(var4));
      return zenithstyle;
   }

   public ZenithStyle createMistStyle() {
      ZenithStyle zenithstyle = new ZenithStyle("Mist");
      zenithstyle.getPrimaryColor().setColor(new ArgbColor("#7B8FA1"));
      zenithstyle.getSecondaryPrimaryColor().setColor(new ArgbColor("#A5B6C7"));
      zenithstyle.getGlowColor1().setColor(new ArgbColor("#7B8FA17A"));
      zenithstyle.getGlowColor2().setColor(new ArgbColor("#7B8FA11F"));
      zenithstyle.setDefaultsFromGuiStyle();
      zenithstyle.getLeftBackground().setColor(new ArgbColor("#FFFFFF7A").SprintPacketEvent(0.15F));
      zenithstyle.getRightBackground().setColor(new ArgbColor("#FFFFFFB8").SprintPacketEvent(0.15F));
      zenithstyle.getPanelLeftBackground().setColor(new ArgbColor("#FFFFFF7A").SprintPacketEvent(0.15F));
      zenithstyle.getSurfaceEnableBackground().setColor(new ArgbColor("#F5F7FA3D").SprintPacketEvent(0.15F));
      zenithstyle.getHeaderDisableBackground().setColor(new ArgbColor("#EEF2F73D").SprintPacketEvent(0.15F));
      zenithstyle.getSurfaceDisableBackground().setColor(new ArgbColor("#EEF2F73D").SprintPacketEvent(0.15F));
      zenithstyle.getFieldSurfaceBackground().setColor(new ArgbColor("#1E293B05").SprintPacketEvent(0.15F));
      zenithstyle.getFieldBorder().setColor(new ArgbColor("#1E293B0A").SprintPacketEvent(0.15F));
      zenithstyle.getDisableActiveBg().setColor(new ArgbColor("#64748B14").SprintPacketEvent(0.15F));
      zenithstyle.getTextEnable().setColor(new ArgbColor("#1E293B"));
      zenithstyle.getTextSecondary().setColor(new ArgbColor("#0F172AB8"));
      zenithstyle.getTextTertiary().setColor(new ArgbColor("#3341557A"));
      return zenithstyle;
   }

   public JsonObject save() {
      JsonObject jsonobject = new JsonObject();
      JsonArray jsonarray = new JsonArray();

      for (ZenithStyle zenithstyle : this.styles) {
         JsonObject jsonobject1 = new JsonObject();
         zenithstyle.safe(jsonobject1);
         jsonarray.add(jsonobject1);
      }

      jsonobject.add("array", jsonarray);
      jsonobject.addProperty("select", this.currentStyle.getName());
      return jsonobject;
   }

   public void load(JsonObject var1) {
      try {
         if (var1.has("select")) {
            this.currentStyle = this.getStyleByName(var1.get("select").getAsString());
         }

         if (var1.has("array")) {
            for (JsonElement jsonelement : var1.getAsJsonArray("array")) {
               if (jsonelement.isJsonObject()) {
                  ZenithStyle zenithstyle = this.getStyleByName(jsonelement.getAsJsonObject().get("name").getAsString());
                  zenithstyle.load(jsonelement.getAsJsonObject());
               }
            }
         }
      } catch (Exception var5) {
      }
   }

   public ZenithStyle getStyleByName(String var1) {
      if (var1 == null) {
         return null;
      }

      for (ZenithStyle zenithstyle : this.styles) {
         if (var1.equals(zenithstyle.getName())) {
            return zenithstyle;
         }
      }

      return null;
   }

   public GradientRadius getClientColor() {
      return GradientRadius.on23(this.getClientColor(0), this.getClientColor(90), this.getClientColor(180), this.getClientColor(270));
   }

   public ArgbColor getClientColor(int var1) {
      return ColorUtils.on23(4, var1, this.getCurrentStyle().getPrimaryColor().getColor(), this.getCurrentStyle().getSecondaryPrimaryColor().getColor());
   }

   public ArgbColor getGlowColor(int var1) {
      return ColorUtils.on23(4, var1, this.getCurrentStyle().getGlowColor1().getColor(), this.getCurrentStyle().getGlowColor2().getColor());
   }

   public GradientRadius getGlowColor() {
      return GradientRadius.on23(this.getGlowColor(0), this.getGlowColor(90), this.getGlowColor(180), this.getGlowColor(270));
   }

   public List<ZenithStyle> getStyles() {
      return this.styles;
   }

   public ColorAnimator getColorCycleIcon() {
      return this.colorCycleIcon;
   }

   public void setCurrentStyle(ZenithStyle var1) {
      this.currentStyle = var1;
   }
}
