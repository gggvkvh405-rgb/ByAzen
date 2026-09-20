package org.zenith.core;

import java.util.UUID;

public record CloudUserDto(
   UUID MovementUtils,
   String EffectEngine,
   String TaskScheduler,
   String ScreenUtils,
   long RandomSource,
   String RaycastUtils,
   String ActionSequencePlayer,
   String TickGate,
   long MovementController,
   String TargetSelector,
   long LegitRotationUtils,
   Long Rotation,
   long RotationDelta,
   String RotationMath,
   CloudSessionExtDto ItemCountUtils
) {
   public CloudUserDto {
      RotationMath = RotationMath != null && !RotationMath.isBlank() ? RotationMath : null;
   }

   public CloudUserDto(
      UUID var1,
      String var2,
      String var3,
      String var4,
      long var5,
      String var7,
      String var8,
      String var9,
      long var10,
      String var12,
      long var13,
      Long var15,
      long var16
   ) {
      this(var1, var2, var3, var4, var5, var7, var8, var9, var10, var12, var13, var15, var16, null, null);
   }

   public CloudUserDto(UUID var1, String var2, String var3, long var4, String var6, String var7, String var8, long var9, Long var11) {
      this(var1, var2, var3, "unknown", var4, var6, var7, var8, 1L, var3 + ".zenith", var9, var11, var11 == null ? var9 : var11);
   }

   public UUID PermissionListCodec() {
      return this.MovementUtils;
   }

   public String HudEffectIcons() {
      return this.EffectEngine;
   }

   public String name() {
      return this.TaskScheduler;
   }

   public String RotationLegitStrategy() {
      return this.ScreenUtils;
   }

   public long HudElementMessages() {
      return this.RandomSource;
   }

   public String RotationSnapStrategy() {
      return this.RaycastUtils;
   }

   public String AimPolicyRotationStrategy() {
      return this.ActionSequencePlayer;
   }

   public String ThemeColorCycler() {
      return this.TickGate;
   }

   public long HudScoreboard() {
      return this.MovementController;
   }

   public String RotationSmoothStrategy() {
      return this.TargetSelector;
   }

   public long RenderTickEvent() {
      return this.LegitRotationUtils;
   }

   public Long HudElementMedia() {
      return this.Rotation;
   }

   public long HudTargetPanel() {
      return this.RotationDelta;
   }

   public String description() {
      return this.RotationMath;
   }

   public CloudSessionExtDto HudClockPanel() {
      return this.ItemCountUtils;
   }
}
