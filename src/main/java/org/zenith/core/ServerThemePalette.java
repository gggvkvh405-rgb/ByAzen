package org.zenith.core;

import org.zenith.util.ArgbColor;

public class ServerThemePalette {
   public ArgbColor color;
   public ArgbColor var11917;
   public ArgbColor getAnarchy;
   public ArgbColor var11918;
   public ArgbColor var11919;
   public ArgbColor var11920;
   public ArgbColor var11921;
   public ArgbColor var11922;
   public ArgbColor var11923;
   public ArgbColor var11924;
   public ArgbColor var11925;
   public ArgbColor var11912;
   public ArgbColor var11926;
   public ArgbColor var11927;
   public ArgbColor var11928;
   public ArgbColor var11916;
   public boolean boolean86 = false;
   public boolean boolean87 = false;
   public boolean boolean88 = false;

   public static ServerThemePalette var72() {
      return new ServerThemePalette();
   }

   public ServerThemePalette CloudApiClient(ArgbColor var1) {
      this.color = var1;
      return this;
   }

   public ServerThemePalette MediaTrackInfo(ArgbColor var1) {
      this.var11917 = var1;
      return this;
   }

   public ServerThemePalette CloudUserProfile(ArgbColor var1) {
      this.getAnarchy = var1;
      return this;
   }

   public ServerThemePalette ModuleSnapshotDto(ArgbColor var1) {
      this.var11918 = var1;
      return this;
   }

   public ServerThemePalette InventoryUtils(ArgbColor var1) {
      this.var11919 = var1;
      return this;
   }

   public ServerThemePalette BotFeatureRegistry(ArgbColor var1) {
      this.var11920 = var1;
      return this;
   }

   public ServerThemePalette ServiceException(ArgbColor var1) {
      this.var11921 = var1;
      return this;
   }

   public ServerThemePalette CloudRouter(ArgbColor var1) {
      this.var11922 = var1;
      return this;
   }

   public ServerThemePalette ProtocolMessage(ArgbColor var1) {
      this.var11923 = var1;
      return this;
   }

   public ServerThemePalette AnalyticsTracker(ArgbColor var1) {
      this.var11924 = var1;
      return this;
   }

   public ServerThemePalette ConfigJsonUtil(ArgbColor var1) {
      this.var11925 = var1;
      return this;
   }

   public ServerThemePalette CloudResponse(ArgbColor var1) {
      this.var11912 = var1;
      return this;
   }

   public ServerThemePalette TradeGuardService(ArgbColor var1) {
      this.var11926 = var1;
      return this;
   }

   public ServerThemePalette BotFeaturesDto(ArgbColor var1) {
      this.var11927 = var1;
      return this;
   }

   public ServerThemePalette CommandManager(ArgbColor var1) {
      this.var11928 = var1;
      return this;
   }

   public ServerThemePalette ModuleStateStore(ArgbColor var1) {
      this.var11916 = var1;
      return this;
   }

   public ServerThemePalette ServiceException(boolean var1) {
      this.boolean86 = var1;
      return this;
   }

   public ServerThemePalette CloudRouter(boolean var1) {
      this.boolean87 = var1;
      return this;
   }

   public ServerThemePalette ProtocolMessage(boolean var1) {
      this.boolean88 = var1;
      return this;
   }

   public ServerTheme MediaTrackInfo(String var1, String var2) {
      return new ServerTheme(var1, var2, this);
   }
}
