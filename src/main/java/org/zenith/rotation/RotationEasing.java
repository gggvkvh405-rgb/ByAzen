package org.zenith.rotation;

import org.zenith.core.GameService;
import org.zenith.core.MenuEaseA;
import org.zenith.core.MenuEaseB;
import org.zenith.core.MenuEaseC;
import org.zenith.core.MenuEaseD;
import org.zenith.core.MenuEaseE;
import org.zenith.core.MenuEaseF;
import org.zenith.core.RotationBotStrategy;

public class RotationEasing implements GameService {
   public final RotationSnapStrategy zClass049 = new RotationSnapStrategy();
   public final RotationLegitStrategy zClass054 = new RotationLegitStrategy();
   public final AimPolicyRotationStrategy zClass022 = new AimPolicyRotationStrategy();
   public final MotorIntentRotationStrategy zClass094 = new MotorIntentRotationStrategy();
   public final RotationBotStrategy zClass087 = new RotationBotStrategy();
   public final RotationSmoothStrategy zClass029 = new RotationSmoothStrategy();
   public final RotationBurstStrategy zClass088 = new RotationBurstStrategy();
   public final RotationEasingBase var135 = new MenuEaseF();
   public final RotationEasingBase var1352 = new MenuEaseB();
   public final RotationEasingBase var1353 = new MenuEaseC();
   public final RotationEasingBase var1354 = RoundedRectEasing.call270().call200();
   public final RotationEasingBase var1355 = MenuEaseD.call201().call202();
   public final RotationEasingBase var1356 = MenuEaseA.screen().call457();
   public final RotationEasingBase var1357 = MenuEaseE.call469().call465();

   public Rotation on23(RotationEasingBase var1, Rotation var2) {
      Rotation ililiiili1ll1li11 = switch (var1.call110()) {
         case call415 -> this.zClass049.Easing(var2);
         case call414 -> this.zClass029.Easing(var2);
         case call269 -> this.zClass094.Easing(var2);
         case call416 -> this.zClass094.Easing(var2);
         case call411 -> this.zClass094.Easing(var2);
         case call412 -> this.zClass094.Easing(var2);
         case call441 -> this.zClass088.Easing(var2);
         default -> val003.CloudRouter().LineShader();
      };
      return val002.LineShader().equals(ililiiili1ll1li11)
         ? ililiiili1ll1li11
         : val002.LineShader().on23(val002.LineShader().EmoteManager(ililiiili1ll1li11)).CosmeticManager(val002.LineShader());
   }

   public RotationLegitStrategy ArgbColor() {
      return this.zClass054;
   }

   public MotorIntentRotationStrategy ColorUtils() {
      return this.zClass094;
   }

   public RotationBotStrategy RenderCommandQueue() {
      return this.zClass087;
   }

   public RotationEasingBase HudPreviewItem() {
      return this.var135;
   }

   public RotationEasingBase HudPreviewRenderQueue() {
      return this.var1352;
   }

   public RotationEasingBase RectBatch() {
      return this.var1353;
   }

   public RotationEasingBase RoundedRectBatch() {
      return this.var1354;
   }

   public RotationEasingBase FillShader() {
      return this.var1355;
   }

   public RotationEasingBase ShapeRenderer() {
      return this.var1356;
   }

   public RotationEasingBase ShaderWrapper() {
      return this.var1357;
   }
}
