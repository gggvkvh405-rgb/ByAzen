package org.zenith.core;

import org.joml.Matrix4f;
import org.zenith.render.HudPreviewRenderQueue;
import org.zenith.utility.render.display.base.HudQueuedContext;

public final class HudPreviewItem implements HudPreviewRenderQueue.ExecutableCommand {
   public HudQueuedContext hudQueuedContext;
   public HudPreviewType var13Var159;
   public final Matrix4f matrix4f11 = new Matrix4f();
   public float float226;
   public float float227;
   public float float228;
   public float float229;
   public float float230;
   public float float231;
   public float float232;
   public int int350;
   public int int351;
   public int int352;
   public int int353;
   public int int354;
   public int int355;
   public int int356;
   public int int357;
   public int int358;
   public boolean boolean155;
   public Object object5;
   public Object object6;
   public Object object7;

   @Override
   public void render() {
      this.hudQueuedContext.replayCommand(this);
   }

   public void var1436() {
      this.hudQueuedContext = null;
      this.object5 = null;
      this.object6 = null;
      this.object7 = null;
   }
}
