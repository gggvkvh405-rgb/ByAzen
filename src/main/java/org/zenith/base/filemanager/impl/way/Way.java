package org.zenith.base.filemanager.impl.way;

import com.google.gson.annotations.SerializedName;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.HoverEvent.Action;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class Way {
   @SerializedName("name")
   public final String name;
   @SerializedName("pos")
   public final BlockPos pos;
   @SerializedName("server")
   public final String server;

   public Way(String var1, BlockPos var2, String var3) {
      this.name = var1;
      this.pos = var2;
      this.server = var3;
   }

   public String name() {
      return this.name;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public String server() {
      return this.server;
   }

   public Text toText() {
      String s = this.pos.getX() + " " + this.pos.getY() + " " + this.pos.getZ();
      String s1 = this.name.replace("\"", "\\\"");
      MutableText mutabletext = Text.literal("? " + this.name);
      mutabletext.setStyle(
         Style.EMPTY
            .withBold(true)
            .withColor(TextColor.fromRgb(16765286))
            .withHoverEvent(new HoverEvent.ShowText(Text.literal("Место: " + this.name + "\nСервер: " + this.server)))
      );
      MutableText mutabletext1 = Text.literal("  ⟨" + this.pos.getX() + ", " + this.pos.getY() + ", " + this.pos.getZ() + "⟩");
      mutabletext1.setStyle(
         Style.EMPTY
            .withColor(TextColor.fromRgb(1149618))
            .withHoverEvent(new HoverEvent.ShowText(Text.literal("Нажми, чтобы скопировать координаты")))
            .withClickEvent(new ClickEvent.CopyToClipboard(s))
      );
      MutableText mutabletext2 = Text.literal("  @" + this.server);
      mutabletext2.setStyle(Style.EMPTY.withColor(Formatting.GRAY));
      MutableText mutabletext3 = Text.literal("  [Удалить]");
      mutabletext3.setStyle(
         Style.EMPTY
            .withBold(true)
            .withColor(TextColor.fromRgb(15681391))
            .withHoverEvent(new HoverEvent.ShowText(Text.literal("Подставить команду удаления точки " + this.name)))
            .withClickEvent(new ClickEvent.SuggestCommand(".way remove " + s1))
      );
      return Text.empty().append(mutabletext).append(mutabletext1).append(mutabletext2).append(mutabletext3);
   }
}
