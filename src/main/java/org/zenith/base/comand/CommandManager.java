package org.zenith.base.comand;

import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;
import net.minecraft.command.permission.PermissionPredicate;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.BindsCommand;
import org.zenith.base.comand.impl.CalcCommand;
import org.zenith.base.comand.impl.ClipCommand;
import org.zenith.base.comand.impl.ConfigCommand;
import org.zenith.base.comand.impl.EmoteCommand;
import org.zenith.base.comand.impl.ExchangeCommand;
import org.zenith.base.comand.impl.FriendCommand;
import org.zenith.base.comand.impl.GpsCommand;
import org.zenith.base.comand.impl.HBotCommand;
import org.zenith.base.comand.impl.MacroCommand;
import org.zenith.base.comand.impl.NameProtectCommand;
import org.zenith.base.comand.impl.NeuroCommand;
import org.zenith.base.comand.impl.RCTCommand;
import org.zenith.base.comand.impl.RegionCommand;
import org.zenith.base.comand.impl.RouteCommand;
import org.zenith.base.comand.impl.SortCommand;
import org.zenith.base.comand.impl.StaffCommand;
import org.zenith.base.comand.impl.StreamerModeCommand;
import org.zenith.base.comand.impl.WayCommand;

public class CommandManager {
   public String prefix = ".";
   public final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher();
   public final CommandSource source = new ClientCommandSource(null, MinecraftClient.getInstance(), PermissionPredicate.ALL);
   public final List<CommandAbstract> commands = new ArrayList<>();

   public CommandManager() {
      this.init();
   }

   public void init() {
      this.registerCommand(new FriendCommand());
      this.registerCommand(new MacroCommand());
      this.registerCommand(new ClipCommand());
      this.registerCommand(new ConfigCommand());
      this.registerCommand(new NeuroCommand());
      this.registerCommand(new RCTCommand());
      this.registerCommand(new WayCommand());
      this.registerCommand(new GpsCommand());
      this.registerCommand(new BindsCommand());
      this.registerCommand(new NameProtectCommand());
      this.registerCommand(new StreamerModeCommand());
      this.registerCommand(new RegionCommand());
      this.registerCommand(new StaffCommand());
      this.registerCommand(new CalcCommand());
      this.registerCommand(new ExchangeCommand());
      this.registerCommand(new RouteCommand());
      this.registerCommand(new HBotCommand());
      this.registerCommand(new SortCommand());
      this.registerCommand(new EmoteCommand());
   }

   public void registerCommand(CommandAbstract var1) {
      if (var1 != null) {
         var1.register(this.dispatcher);
         this.commands.add(var1);
      }
   }

   public String getPrefix() {
      return this.prefix;
   }

   public CommandDispatcher<CommandSource> getDispatcher() {
      return this.dispatcher;
   }

   public CommandSource getSource() {
      return this.source;
   }

   public List<CommandAbstract> getCommands() {
      return this.commands;
   }
}
