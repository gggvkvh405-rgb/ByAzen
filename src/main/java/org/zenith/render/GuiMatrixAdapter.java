package org.zenith.render;

import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3x2fc;
import org.joml.Matrix4f;

/** Bridges the 2D GUI matrix stack introduced in 1.21.11 to legacy render code. */
public final class GuiMatrixAdapter {
   private static final ThreadLocal<MatrixStack> REUSABLE_STACK = ThreadLocal.withInitial(MatrixStack::new);
   private static final ThreadLocal<Matrix4f> REUSABLE_MATRIX = ThreadLocal.withInitial(Matrix4f::new);

   private GuiMatrixAdapter() {
   }

   public static Matrix4f toMatrix4f(Matrix3x2fc matrix) {
      return REUSABLE_MATRIX.get().set(
         matrix.m00(), matrix.m01(), 0.0F, 0.0F,
         matrix.m10(), matrix.m11(), 0.0F, 0.0F,
         0.0F, 0.0F, 1.0F, 0.0F,
         matrix.m20(), matrix.m21(), 0.0F, 1.0F
      );
   }

   public static MatrixStack toMatrixStack(Matrix3x2fc matrix) {
      MatrixStack stack = REUSABLE_STACK.get();
      stack.peek().getPositionMatrix().set(toMatrix4f(matrix));
      return stack;
   }
}
