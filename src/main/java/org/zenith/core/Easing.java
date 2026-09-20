package org.zenith.core;

public interface Easing {
   Easing EventMouseScrollHook = new EasingSolver(0.45, 0.49, 1.45, 1.15);
   Easing EventInteractBlock = new EasingSolver(0.45, 0.43, 1.45, 0.91);
   Easing EventTriggerKeyEvent = new EasingSolver(0.1, 0.34, 1.07, 1.04);
   Easing EventInjectHandleInputEvents = new EasingSolver(0.27, 0.49, 1.09, 1.06);
   Easing EventMouseButton = new EasingSolver(0.62, 0.8, -0.16, 0.37);
   Easing EventModifyMouseRotationInput = new EasingSolver(0.25, 0.11, 1.07, 1.1);
   Easing EventMixin_modifySetScreenArg = new EasingSolver(0.42, 0.58, 0.0, 1.0);
   Easing BlockInteractEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing EventClickSlotHook = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing CloseScreenEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing EventDead = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing HotbarInputEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing StopUsingItemEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing RefreshCacheEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing PreventActionEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing ModuleToggleEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing EventMotion = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing EventClick = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing EventEntityCollision = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing EventPushOutOfBlocks = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing EventInjectAddEntity = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing EventHookTickEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing EventHookPacketProcess = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing GuiWalkEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing EventWindowSizeChanged = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing AttackEntityEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing Event18Ext5 = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing Event05 = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing Event37 = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing EventUpdateHealth = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing JumpEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   EaseSineBase PlayerMoveEvent = new EaseInOutQuint();
   EaseSineBase MovementInputEvent = new EaseOutBounce();
   EaseSineBase Event14 = new EaseOutElastic();
   EaseBase HealthUpdateEvent = new EaseLinearStep();
   EaseBase RenderTickEvent = new EaseOutQuad();
   EaseBase Event18Ext = new EaseInOutCubic();
   Easing Event29 = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing RotationUpdateStartEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);
   Easing TargetAcquireEvent = new EasingSolver(0.42, 0.0, 0.58, 1.0);

   static Easing on23(double var0, double var2, double var4, double var6) {
      return new EasingSolver(var0, var4, var2, var6);
   }

   float ease(float var1, float var2, float var3, float var4);
}
