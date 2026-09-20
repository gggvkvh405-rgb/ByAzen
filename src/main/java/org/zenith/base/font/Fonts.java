package org.zenith.base.font;

public final class Fonts {
   public static final MsdfFont BOLD = MsdfFont.builder().atlas("bold").data("bold").build();
   public static final MsdfFont MEDIUM = MsdfFont.builder().atlas("medium").data("medium").build();
   public static final MsdfFont NEW_MEDIUM = MsdfFont.builder().atlas("newmedium").data("newmedium").build();
   public static final MsdfFont NEW_SEMIBOLD = MsdfFont.builder().atlas("newsemibold").data("newsemibold").build();
   public static final MsdfFont NEW_REGULAR = MsdfFont.builder().atlas("newregular").data("newregular").build();
   public static final MsdfFont NEW_ICONS = MsdfFont.builder().atlas("newiconzenith").data("newiconzenith").build();
   public static final MsdfFont REGULAR = MsdfFont.builder().atlas("regular").data("regular").build();
   public static final MsdfFont SEMIBOLD = MsdfFont.builder().atlas("semibold").data("semibold").build();
   public static final MsdfFont ROUND_BOLD = MsdfFont.builder().atlas("roundbold").data("roundbold").build();
   public static final MsdfFont ICONS = MsdfFont.builder().atlas("icons").data("icons").build();

   public Fonts() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
