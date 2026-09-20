package org.zenith.core;

import org.zenith.util.ArgbColor;

public class ServerTheme {
   public final String string86;
   public final String string87;
   public final ThemeColors var111Var159;
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
   public ArgbColor var11933;
   public ArgbColor var11926;
   public ArgbColor var11927;
   public ArgbColor var11928;
   public ArgbColor getModeSetting3;
   public boolean boolean86;
   public boolean boolean87;
   public boolean boolean88;
   public boolean boolean149 = false;
   public static final ServerTheme var111 = ServerThemePalette.var72()
      .CloudApiClient(new ArgbColor(181, 162, 255))
      .MediaTrackInfo(new ArgbColor(255, 203, 162))
      .CloudUserProfile(ArgbColor.var11936)
      .ModuleSnapshotDto(new ArgbColor(181, 162, 255))
      .InventoryUtils(new ArgbColor(255, 203, 162))
      .BotFeatureRegistry(new ArgbColor(88, 87, 93))
      .ServiceException(new ArgbColor(128, 127, 133))
      .CloudRouter(new ArgbColor(32, 31, 37))
      .ProtocolMessage(new ArgbColor(68, 67, 73))
      .AnalyticsTracker(new ArgbColor(48, 47, 53))
      .ConfigJsonUtil(new ArgbColor(38, 37, 43))
      .CloudResponse(new ArgbColor(28, 27, 33))
      .TradeGuardService(new ArgbColor(35, 34, 40))
      .BotFeaturesDto(new ArgbColor(25, 24, 30))
      .CommandManager(new ArgbColor(255, 255, 255))
      .ModuleStateStore(new ArgbColor(23, 22, 28))
      .ServiceException(false)
      .CloudRouter(false)
      .ProtocolMessage(false)
      .MediaTrackInfo("Dark", "8");
   public static final ServerTheme getRotation = ServerThemePalette.var72()
      .CloudApiClient(new ArgbColor(123, 93, 234))
      .MediaTrackInfo(new ArgbColor(255, 192, 121))
      .CloudUserProfile(ArgbColor.var11936)
      .ModuleSnapshotDto(new ArgbColor(123, 93, 234))
      .InventoryUtils(new ArgbColor(255, 192, 121))
      .BotFeatureRegistry(new ArgbColor(138, 137, 143))
      .ServiceException(new ArgbColor(148, 147, 153))
      .CloudRouter(new ArgbColor(236, 236, 236))
      .ProtocolMessage(new ArgbColor(178, 177, 183))
      .AnalyticsTracker(new ArgbColor(188, 187, 193))
      .ConfigJsonUtil(new ArgbColor(229, 229, 229))
      .CloudResponse(new ArgbColor(246, 246, 246))
      .TradeGuardService(new ArgbColor(229, 229, 229))
      .BotFeaturesDto(new ArgbColor(251, 251, 251))
      .CommandManager(new ArgbColor(23, 22, 28))
      .ModuleStateStore(new ArgbColor(255, 255, 255))
      .ServiceException(false)
      .CloudRouter(false)
      .ProtocolMessage(false)
      .MediaTrackInfo("Light", "T");
   public static final ServerTheme var1112 = ServerThemePalette.var72()
      .CloudApiClient(new ArgbColor(181, 162, 255))
      .MediaTrackInfo(new ArgbColor(255, 203, 162))
      .CloudUserProfile(ArgbColor.var11936)
      .ModuleSnapshotDto(new ArgbColor(181, 162, 255))
      .InventoryUtils(new ArgbColor(255, 203, 162))
      .BotFeatureRegistry(new ArgbColor(88, 87, 93))
      .ServiceException(new ArgbColor(128, 127, 133))
      .CloudRouter(new ArgbColor(32, 31, 37))
      .ProtocolMessage(new ArgbColor(68, 67, 73))
      .AnalyticsTracker(new ArgbColor(48, 47, 53))
      .ConfigJsonUtil(new ArgbColor(38, 37, 43))
      .CloudResponse(new ArgbColor(28, 27, 33))
      .TradeGuardService(new ArgbColor(35, 34, 40))
      .BotFeaturesDto(new ArgbColor(25, 24, 30))
      .CommandManager(new ArgbColor(255, 255, 255))
      .ModuleStateStore(new ArgbColor(23, 22, 28))
      .ServiceException(false)
      .CloudRouter(false)
      .ProtocolMessage(false)
      .MediaTrackInfo("Custom", "F");

   public ServerTheme(String var1, String var2, ServerThemePalette var3) {
      this.string86 = var1;
      this.string87 = var2;
      this.color = var3.color;
      this.var11917 = var3.var11917;
      this.getAnarchy = var3.getAnarchy != null ? var3.getAnarchy : ArgbColor.var11936;
      this.var11918 = var3.var11918 != null ? var3.var11918 : this.var11917;
      this.var11919 = var3.var11919 != null ? var3.var11919 : this.var11917;
      this.var11920 = var3.var11920;
      this.var11921 = var3.var11921;
      this.var11922 = var3.var11922;
      this.var11923 = var3.var11923;
      this.var11924 = var3.var11924;
      this.var11925 = var3.var11925;
      this.var11933 = var3.var11912;
      this.var11926 = var3.var11926;
      this.var11927 = var3.var11927;
      this.var11928 = var3.var11928;
      this.getModeSetting3 = var3.var11916;
      this.boolean86 = var3.boolean86;
      this.boolean87 = var3.boolean87;
      this.boolean88 = var3.boolean88;
      this.var111Var159 = new ThemeColors(
         var3.color,
         var3.var11917,
         var3.getAnarchy,
         var3.var11920,
         var3.var11921,
         var3.var11922,
         var3.var11923,
         var3.var11924,
         var3.var11925,
         var3.var11912,
         var3.var11926,
         var3.var11927,
         var3.var11928,
         var3.var11916,
         var3.boolean86,
         var3.boolean87,
         var3.boolean88
      );
   }

   public void reset() {
      this.color = this.var111Var159.var1193();
      this.var11917 = this.var111Var159.var1194();
      this.getAnarchy = this.var111Var159.var1195();
      this.var11920 = this.var111Var159.var1196();
      this.var11921 = this.var111Var159.var1197();
      this.var11922 = this.var111Var159.var1198();
      this.var11923 = this.var111Var159.var1199();
      this.var11924 = this.var111Var159.var11910();
      this.var11925 = this.var111Var159.var11911();
      this.var11933 = this.var111Var159.var11912();
      this.var11926 = this.var111Var159.var11913();
      this.var11927 = this.var111Var159.var11914();
      this.var11928 = this.var111Var159.var11915();
      this.getModeSetting3 = this.var111Var159.var11916();
      this.boolean86 = this.var111Var159.boolean83();
      this.boolean87 = this.var111Var159.boolean84();
      this.boolean88 = this.var111Var159.boolean85();
      this.boolean149 = true;
   }

   public ServerTheme on23(ServerTheme var1, float var2) {
      return ServerThemePalette.var72()
         .CloudApiClient(this.color.Easing(var1.getColor(), var2))
         .MediaTrackInfo(this.var11917.Easing(var1.path11(), var2))
         .CloudUserProfile(this.getAnarchy.Easing(var1.render2(), var2))
         .BotFeatureRegistry(this.var11920.Easing(var1.map39(), var2))
         .ServiceException(this.var11921.Easing(var1.map40(), var2))
         .CloudRouter(this.var11922.Easing(var1.string31(), var2))
         .ProtocolMessage(this.var11923.Easing(var1.int128(), var2))
         .AnalyticsTracker(this.var11924.Easing(var1.string32(), var2))
         .ConfigJsonUtil(this.var11925.Easing(var1.string127(), var2))
         .CloudResponse(this.var11933.Easing(var1.string128(), var2))
         .TradeGuardService(this.var11926.Easing(var1.file6(), var2))
         .BotFeaturesDto(this.var11927.Easing(var1.long157(), var2))
         .CommandManager(this.var11928.Easing(var1.long158(), var2))
         .ModuleStateStore(this.getModeSetting3.Easing(var1.long159(), var2))
         .ServiceException(this.boolean86)
         .CloudRouter(this.boolean87)
         .ProtocolMessage(this.boolean88)
         .MediaTrackInfo(var1.string86, var1.string87);
   }

   public String getName() {
      return this.string86;
   }

   public String getIcon() {
      return this.string87;
   }

   public ThemeColors path10() {
      return this.var111Var159;
   }

   public ArgbColor getColor() {
      return this.color;
   }

   public ArgbColor path11() {
      return this.var11917;
   }

   public ArgbColor render2() {
      return this.getAnarchy;
   }

   public ArgbColor type() {
      return this.var11918;
   }

   public ArgbColor list88() {
      return this.var11919;
   }

   public ArgbColor map39() {
      return this.var11920;
   }

   public ArgbColor map40() {
      return this.var11921;
   }

   public ArgbColor string31() {
      return this.var11922;
   }

   public ArgbColor int128() {
      return this.var11923;
   }

   public ArgbColor string32() {
      return this.var11924;
   }

   public ArgbColor string127() {
      return this.var11925;
   }

   public ArgbColor string128() {
      return this.var11933;
   }

   public ArgbColor file6() {
      return this.var11926;
   }

   public ArgbColor long157() {
      return this.var11927;
   }

   public ArgbColor long158() {
      return this.var11928;
   }

   public ArgbColor long159() {
      return this.getModeSetting3;
   }

   public boolean string129() {
      return this.boolean86;
   }

   public boolean string88() {
      return this.boolean87;
   }

   public boolean isFalse() {
      return this.boolean88;
   }

   public boolean file3() {
      return this.boolean149;
   }

   public void setColor(ArgbColor var1) {
      this.color = var1;
   }

   public void UiAnimation(ArgbColor var1) {
      this.var11917 = var1;
   }

   public void Easing(ArgbColor var1) {
      this.getAnarchy = var1;
   }

   public void ColorAnimator(ArgbColor var1) {
      this.var11918 = var1;
   }

   public void ItemRegistry(ArgbColor var1) {
      this.var11919 = var1;
   }

   public void ItemSpec(ArgbColor var1) {
      this.var11920 = var1;
   }

   public void TextScanner(ArgbColor var1) {
      this.var11921 = var1;
   }

   public void NbtItemSpec(ArgbColor var1) {
      this.var11922 = var1;
   }

   public void EnchantItemSpec(ArgbColor var1) {
      this.var11923 = var1;
   }

   public void SimpleItemBuilder(ArgbColor var1) {
      this.var11924 = var1;
   }

   public void ItemServiceBase(ArgbColor var1) {
      this.var11925 = var1;
   }

   public void NbtEditor(ArgbColor var1) {
      this.var11933 = var1;
   }

   public void PotionItemBuilder(ArgbColor var1) {
      this.var11926 = var1;
   }

   public void ProfileItemBuilder(ArgbColor var1) {
      this.var11927 = var1;
   }

   public void StringCodec(ArgbColor var1) {
      this.var11928 = var1;
   }

   public void FileLogger(ArgbColor var1) {
      this.getModeSetting3 = var1;
   }

   public void CloudUserProfile(boolean var1) {
      this.boolean86 = var1;
   }

   public void ModuleSnapshotDto(boolean var1) {
      this.boolean87 = var1;
   }

   public void InventoryUtils(boolean var1) {
      this.boolean88 = var1;
   }

   public void BotFeatureRegistry(boolean var1) {
      this.boolean149 = var1;
   }
}
