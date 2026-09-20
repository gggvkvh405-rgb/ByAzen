package org.zenith.client.screens.bot;

enum BotScreen_BulkAction {
   CONNECT("Z", "module.bot.connect", "module.bot.connect", "module.bot.connectTo"),
   CHAT("d", "module.bot.chat", "module.bot.send", "module.bot.message"),
   RCT("7", "module.bot.rct", "module.bot.rct", "module.bot.anarchy");

   final String icon;
   final String tabKey;
   final String buttonKey;
   final String placeholderKey;

   BotScreen_BulkAction(String var3, String var4, String var5, String var6) {
      this.icon = var3;
      this.tabKey = var4;
      this.buttonKey = var5;
      this.placeholderKey = var6;
   }
}
