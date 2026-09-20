package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.zenith.ZenithClient;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.StyledTextBuilder;
import org.zenith.module.Module;
import org.zenith.setting.KeySetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.ScoreboardUtils;

public class BindsCommand extends CommandAbstract {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public BindsCommand() {
      super("binds");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var1x -> {
         this.sendHelp();
         return 1;
      });
      var1.then(
         literal("list")
            .executes(
               var1x -> {
                  boolean flag = false;
                  int i = 0;

                  for (Module lii1lll1l1li1ii1iiillii : ZenithClient.on23().ColorAnimator().PacketDispatcher()) {
                     List<KeySetting> list = lii1lll1l1li1ii1iiillii.getSettings()
                        .stream()
                        .filter(var0 -> var0 instanceof KeySetting)
                        .map(var0 -> (KeySetting)var0)
                        .filter(var1xx -> this.isBound(var1xx.getKeyCode()))
                        .toList();
                     boolean flag1 = this.isBound(lii1lll1l1li1ii1iiillii.getKeyCode());
                     if (flag1 || !list.isEmpty()) {
                        if (!flag) {
                           this.sendTitle("Назначенные бинды");
                           flag = true;
                        }

                        if (flag1) {
                           this.sendBindLine(lii1lll1l1li1ii1iiillii.getName(), ScoreboardUtils.EventPosHook(lii1lll1l1li1ii1iiillii.getKeyCode()), false);
                           i++;
                        } else {
                           this.sendModuleLine(lii1lll1l1li1ii1iiillii.getName());
                        }

                        for (KeySetting l1ll111iiil : list) {
                           this.sendBindLine(l1ll111iiil.getName(), ScoreboardUtils.EventPosHook(l1ll111iiil.getKeyCode()), true);
                           i++;
                        }
                     }
                  }

                  if (!flag) {
                     StyledTextBuilder.RefreshCacheEvent("Список биндов пуст!");
                  } else {
                     this.sendFooter("Всего биндов: " + i);
                  }

                  return 1;
               }
            )
      );
      var1.then(literal("clear").executes(var1x -> {
         int i = this.clearBinds();
         if (i == 0) {
            StyledTextBuilder.RefreshCacheEvent("Бинды уже пусты!");
         } else {
            this.sendTitle("Бинды очищены");
            this.sendFooter("Сброшено биндов: " + i);
         }

         return 1;
      }));
      var1.then(literal("help").executes(var1x -> {
         this.sendHelp();
         return 1;
      }));
   }

   public boolean isBound(int var1) {
      return var1 != ScoreboardUtils.call065.int396 && !ScoreboardUtils.EventPosHook(var1).isEmpty();
   }

   public int clearBinds() {
      int i = 0;

      for (Module lii1lll1l1li1ii1iiillii : ZenithClient.on23().ColorAnimator().PacketDispatcher()) {
         if (this.isBound(lii1lll1l1li1ii1iiillii.getKeyCode())) {
            lii1lll1l1li1ii1iiillii.setKeyCode(ScoreboardUtils.call065.int396);
            i++;
         }

         for (KeySetting l1ll111iiil : this.getKeySettings(lii1lll1l1li1ii1iiillii)) {
            if (this.isBound(l1ll111iiil.getKeyCode())) {
               l1ll111iiil.setKeyCode(ScoreboardUtils.call065.int396);
               i++;
            }
         }
      }

      return i;
   }

   public List<KeySetting> getKeySettings(Module var1) {
      return var1.getSettings().stream().filter(var0 -> var0 instanceof KeySetting).map(var0 -> (KeySetting)var0).toList();
   }

   public void sendHelp() {
      this.sendTitle("Binds");
      this.sendDescription();
      this.sendUsage(".binds list", "вывести все назначенные бинды");
      this.sendUsage(".binds clear", "очистить все бинды");
   }

   public void sendTitle(String var1) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      this.sendStyled(this.part(var1, zenithstyle.getPrimaryColor().getColor(), true));
   }

   public void sendDescription() {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      this.sendStyled(
         this.part("Показывает назначенные клавиши модулей и KeySetting, а также может очистить их.", zenithstyle.getTextEnable().getColor(), false)
      );
   }

   public void sendUsage(String var1, String var2) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      MutableText mutabletext = Text.empty()
         .append(this.part(var1, zenithstyle.getPrimaryColor().getColor(), true))
         .append(this.part(" - ", zenithstyle.getTextSecondary().getColor(), false))
         .append(this.part(var2, zenithstyle.getTextEnable().getColor(), false));
      this.sendStyled(mutabletext);
   }

   public void sendModuleLine(String var1) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      this.sendStyled(this.part(var1, zenithstyle.getTextEnable().getColor(), true));
   }

   public void sendBindLine(String var1, String var2, boolean var3) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      MutableText mutabletext = Text.empty();
      if (var3) {
         mutabletext.append(this.part("  - ", zenithstyle.getTextSecondary().getColor(), false));
      }

      mutabletext.append(this.part(var1, var3 ? zenithstyle.getTextSecondary().getColor() : zenithstyle.getTextEnable().getColor(), !var3))
         .append(this.part(" -> ", zenithstyle.getTextSecondary().getColor(), false))
         .append(this.part(var2, zenithstyle.getPrimaryColor().getColor(), true));
      this.sendStyled(mutabletext);
   }

   public void sendFooter(String var1) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      this.sendStyled(this.part(var1, zenithstyle.getTextSecondary().getColor(), false));
   }

   public void sendStyled(MutableText var1) {
      if (minecraftClient3.player != null) {
         minecraftClient3.player.sendMessage(this.buildPrefix().append(Text.literal(" ")).append(var1), false);
      }
   }

   public MutableText buildPrefix() {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      return Text.empty()
         .append(this.part("[ ", zenithstyle.getTextSecondary().getColor(), false))
         .append(this.part("Zenith", zenithstyle.getPrimaryColor().getColor(), true))
         .append(this.part(" ]", zenithstyle.getTextSecondary().getColor(), false));
   }

   public MutableText part(String var1, ArgbColor var2, boolean var3) {
      return Text.literal(var1).setStyle(Style.EMPTY.withColor(var2.call001()).withBold(var3));
   }
}
