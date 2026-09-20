package org.zenith.module.render;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;


import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.zenith.ZenithClient;
import org.zenith.render.HandShaderManager;
import org.zenith.render.RawShaderProgram;
import org.zenith.render.RenderPassSetup;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ColorSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.ColorUtils;

@ModuleInfo(name = "Chams", category = Category.RENDER, description = "module.chams.desc")
public final class Chams extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Chams chams = new Chams();
   public static final RenderLayer renderLayer = boolean139();
   public static final Object object4 = new Object();
   public static final int[] val522 = new int[4];
   public static SimpleFramebuffer simpleFramebuffer;
   public static SimpleFramebuffer simpleFramebuffer2;
   public static long long80 = Long.MIN_VALUE;
   public static RawShaderProgram var05;
   public static RawShaderProgram var052;
   public static boolean boolean38;
   public static boolean boolean39;
   public static boolean boolean40;
   private static GpuBuffer chamsUniformBuffer;
   private static final int CHAMS_UNIFORM_BUFFER_SIZE = calculateChamsUniformBufferSize();
   public final MultiSelectSetting targets = new MultiSelectSetting(
      "module.chams.targets",
      "module.chams.targets.desc",
      MultiSelectSetting.Option.UiAnimation("module.chams.players", true),
      MultiSelectSetting.Option.UiAnimation("module.chams.self", false),
      MultiSelectSetting.Option.UiAnimation("module.chams.friends", true)
   );
   public final ModeSetting colorMode = new ModeSetting(
      "module.chams.colorMode", "module.chams.colorMode.desc", "module.chams.color.rainbow", "module.chams.color.client", "module.chams.color.custom"
   );
   public final BooleanSetting secondColor = new BooleanSetting("module.chams.secondColor", "module.chams.secondColor.desc", false, this::float161);
   public final ColorSetting customColor = new ColorSetting("module.chams.customColor", "module.chams.customColor.desc", ArgbColor.var11934, this::float161);
   public final ColorSetting customSecondColor = new ColorSetting(
      "module.chams.customSecondColor",
      "module.chams.customSecondColor.desc",
      new ArgbColor(170, 120, 255),
      () -> this.float161() && this.secondColor.isEnabled()
   );
   public final NumberSetting colorTransfer = new NumberSetting("module.chams.colorTransfer", 0.3F, 0.0F, 1.0F, 0.01F, "module.chams.colorTransfer.desc", "");
   public final BooleanSetting blur = new BooleanSetting("module.chams.blur", "module.chams.blur.desc", true);
   public final NumberSetting blurIterations = new NumberSetting(
      "module.chams.blurIterations", 4.0F, 1.0F, 8.0F, 1.0F, "module.chams.blurIterations.desc", "", this.blur::isEnabled, null
   );
   public final NumberSetting blurOffset = new NumberSetting(
      "module.chams.blurOffset", 2.0F, 0.2F, 8.0F, 0.1F, "module.chams.blurOffset.desc", "", this.blur::isEnabled, null
   );
   public final NumberSetting blurStrength = new NumberSetting(
      "module.chams.blurStrength", 1.25F, 0.0F, 20.0F, 0.05F, "module.chams.blurStrength.desc", "", this.blur::isEnabled, null
   );
   public final NumberSetting brightness = new NumberSetting("module.chams.brightness", 1.0F, 1.0F, 3.0F, 0.05F, "module.chams.brightness.desc", "");
   public final BooleanSetting mirror = new BooleanSetting("module.chams.mirror", "module.chams.mirror.desc", false);
   public final BooleanSetting throughWalls = new BooleanSetting("module.chams.throughWalls", "module.chams.throughWalls.desc", false);
   public final NumberSetting alpha = new NumberSetting("module.chams.alpha", 1.0F, 0.98F, 1.0F, 0.01F, "module.chams.alpha.desc", "");
   public final NumberSetting speed = new NumberSetting("module.chams.speed", 1.15F, 0.2F, 3.0F, 0.05F, "module.chams.speed.desc", "");
   public final NumberSetting fresnel = new NumberSetting("module.chams.fresnel", 1.8F, 0.2F, 4.0F, 0.1F, "module.chams.fresnel.desc", "");

   @Override
   public void onDisable() {
      super.onDisable();
      synchronized (object4) {
         UiAnimation(simpleFramebuffer);
         UiAnimation(simpleFramebuffer2);
         simpleFramebuffer = null;
         simpleFramebuffer2 = null;
         long80 = Long.MIN_VALUE;
      }

      float172();
   }

   public void int399() {
      this.float170();
   }

   public boolean on23(LivingEntityRenderState var1) {
      if (this.isEnabled() && minecraftClient3.player != null && minecraftClient3.world != null && var1 instanceof PlayerEntityRenderState playerentityrenderstate
         )
       {
         return minecraftClient3.world.getEntityById(playerentityrenderstate.id) instanceof AbstractClientPlayerEntity abstractclientplayerentity
            ? this.ItemServiceBase(abstractclientplayerentity)
            : false;
      } else {
         return false;
      }
   }

   public RenderLayer int400() {
      return renderLayer;
   }

   public boolean int401() {
      return this.float284() && this.float285();
   }

   public int PreventActionEvent(int var1) {
      int i = Math.clamp(Math.round(this.alpha.getCurrent() * 255.0F), 0, 255);
      return i << 24 | var1 & 16777215;
   }

   public int ModuleToggleEvent(int var1) {
      int[] aint = this.zClass022Var159();
      int i = aint.length > 0 ? aint[0] : var1;
      return this.PreventActionEvent(i);
   }

   public boolean int402() {
      return this.throughWalls.isEnabled();
   }

   public static Chams.ColorState getChamsVar159() {
      return Chams.ColorState.call094();
   }

   public boolean float284() {
      return true;
   }

   public boolean float285() {
      synchronized (object4) {
         if (long80 == Long.MIN_VALUE) {
            return false;
         }

         return simpleFramebuffer != null && simpleFramebuffer.getColorAttachmentView() != null;
      }
   }

   public boolean ItemServiceBase(AbstractClientPlayerEntity var1) {
      if (!var1.isAlive() || var1.isSpectator()) {
         return false;
      } else if (var1 == minecraftClient3.player) {
         return this.call405() && minecraftClient3.options.getPerspective() != Perspective.FIRST_PERSON;
      } else if (!this.float286()) {
         return false;
      } else {
         return !this.float160() && ZenithClient.on23().MediaTrackInfo().UiAnimation(var1)
            ? false
            : !AntiBot.antiBot.isEnabled() || !AntiBot.antiBot.ItemSpec(var1);
      }
   }

   public boolean float286() {
      return this.targets.ConfigJsonUtil(0);
   }

   public boolean call405() {
      return this.targets.ConfigJsonUtil(1);
   }

   public boolean float160() {
      return this.targets.ConfigJsonUtil(2);
   }

   public boolean float161() {
      return this.colorMode.is(2);
   }

   public static RenderLayer boolean139() {
      RenderPipeline pipeline = RenderPipelines.register(
         RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
            .withLocation(Identifier.of("zenith", "pipeline/chams_entity"))
            .withVertexShader(Identifier.of("zenith", "core/chams_entity/vertex"))
            .withFragmentShader(Identifier.of("zenith", "core/chams_entity/fragment"))
            .withSampler("Sampler0")
            .withUniform("ZenithChamsData", UniformType.UNIFORM_BUFFER)
            .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL, DrawMode.QUADS)
            .withCull(false)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .withDepthWrite(true)
            .withBlend(BlendFunction.TRANSLUCENT)
            .build()
      );
      RenderLayer layer = RenderLayer.of(
         "zenith_chams_glass",
         RenderSetup.builder(pipeline).useLightmap().useOverlay().translucent().build()
      );
      return ((RenderPassSetup)(Object)layer).zenith$withRenderPassSetup(pass -> {
         pass.setUniform("ZenithChamsData", updateChamsUniforms().slice());
         GpuTextureView texture = chams.float171();
         if (texture != null) {
            pass.bindTexture("Sampler0", texture, RenderSystem.getSamplerCache().get(FilterMode.LINEAR));
         }
      });
   }

   private static int calculateChamsUniformBufferSize() {
      Std140SizeCalculator size = new Std140SizeCalculator();
      size.putFloat();
      size.putVec3();
      size.putVec3();
      size.putFloat();
      size.putFloat();
      size.putFloat();
      size.putFloat();
      size.putFloat();
      size.putFloat();
      size.putVec2();
      return size.get();
   }

   private static GpuBuffer updateChamsUniforms() {
      int[] colors = chams.zClass022Var159();
      int primary = colors.length > 0 ? colors[0] : 0xFFFFFFFF;
      int secondary = colors.length > 1 ? colors[1] : primary;
      SimpleFramebuffer framebuffer = simpleFramebuffer;
      float width = framebuffer == null ? 1.0F : Math.max(1, framebuffer.textureWidth);
      float height = framebuffer == null ? 1.0F : Math.max(1, framebuffer.textureHeight);

      try (MemoryStack stack = MemoryStack.stackPush()) {
         Std140Builder data = Std140Builder.onStack(stack, CHAMS_UNIFORM_BUFFER_SIZE);
         data.putFloat(chams.float174());
         data.putVec3(rgb(primary));
         data.putVec3(rgb(secondary));
         data.putFloat(chams.alpha.getCurrent());
         data.putFloat(chams.fresnel.getCurrent());
         data.putFloat(chams.float175());
         data.putFloat(chams.colorTransfer.getCurrent());
         data.putFloat(chams.brightness.getCurrent());
         data.putFloat(chams.mirror.isEnabled() ? 1.0F : 0.0F);
         data.putVec2(new Vector2f(width, height));

         if (chamsUniformBuffer == null) {
            chamsUniformBuffer = RenderSystem.getDevice().createBuffer(
               () -> "Zenith chams uniforms",
               GpuBuffer.USAGE_UNIFORM | GpuBuffer.USAGE_MAP_WRITE,
               data.get()
            );
         } else {
            RenderSystem.getDevice().createCommandEncoder().writeToBuffer(chamsUniformBuffer.slice(), data.get());
         }
      }
      return chamsUniformBuffer;
   }

   private static Vector3f rgb(int color) {
      return new Vector3f(
         (color >> 16 & 0xFF) / 255.0F,
         (color >> 8 & 0xFF) / 255.0F,
         (color & 0xFF) / 255.0F
      );
   }

   public void float170() {
      if (minecraftClient3.world != null && minecraftClient3.getWindow() != null && minecraftClient3.getFramebuffer() != null) {
         synchronized (object4) {
            Chams.ColorState l1ill111l1ll1illlil11_ii1il11l111ii11iil = getChamsVar159();

            try {
               int j = Math.max(1, minecraftClient3.getFramebuffer().textureWidth);
               int k = Math.max(1, minecraftClient3.getFramebuffer().textureHeight);
               this.EnchantItemSpec(j, k);
               if (simpleFramebuffer == null) {
                  return;
               }

               long l = this.float173();
               if (long80 != l) {
                  if (simpleFramebuffer.getColorAttachment() != null) {
                     org.zenith.render.LegacyRenderBridge.copyColor(minecraftClient3.getFramebuffer(), simpleFramebuffer);
                     if (this.blur.isEnabled()) {
                        this.int294();
                     }

                     long80 = l;
                     return;
                  }

                  return;
               }
            } finally {
               l1ill111l1ll1illlil11_ii1il11l111ii11iil.vec3d16();
            }
         }
      }
   }

   public GpuTextureView float171() {
      synchronized (object4) {
         return simpleFramebuffer == null ? null : simpleFramebuffer.getColorAttachmentView();
      }
   }

   public void EnchantItemSpec(int var1, int var2) {
      if (simpleFramebuffer == null
         || simpleFramebuffer2 == null
         || simpleFramebuffer.textureWidth != var1
         || simpleFramebuffer.textureHeight != var2
         || simpleFramebuffer2.textureWidth != var1
         || simpleFramebuffer2.textureHeight != var2) {
         UiAnimation(simpleFramebuffer);
         UiAnimation(simpleFramebuffer2);
         simpleFramebuffer = new SimpleFramebuffer("Zenith chams", var1, var2, false);
         simpleFramebuffer2 = new SimpleFramebuffer("Zenith chams blur", var1, var2, false);
         on23(simpleFramebuffer);
         on23(simpleFramebuffer2);
         long80 = Long.MIN_VALUE;
      }
   }

   public static void on23(SimpleFramebuffer var0) {
      // Sampling is selected explicitly when the framebuffer texture is bound.
   }

   public void int294() {
      if (simpleFramebuffer != null && simpleFramebuffer2 != null && int295()) {
         int i = Math.clamp(Math.round(this.blurIterations.getCurrent()), 1, 8);
         float f = Math.max(0.0F, this.blurStrength.getCurrent());
         float f1 = Math.max(0.05F, this.blurOffset.getCurrent()) * (0.35F + f * 0.08F);
         SimpleFramebuffer simpleframebuffer = simpleFramebuffer;
         SimpleFramebuffer simpleframebuffer1 = simpleFramebuffer2;

         for (int j = 0; j < i; j++) {
            this.on23(var05, simpleframebuffer, simpleframebuffer1, f1);
            SimpleFramebuffer simpleframebuffer2 = simpleframebuffer;
            simpleframebuffer = simpleframebuffer1;
            simpleframebuffer1 = simpleframebuffer2;
         }

         for (int k = 0; k < i; k++) {
            this.on23(var052, simpleframebuffer, simpleframebuffer1, f1);
            SimpleFramebuffer simpleframebuffer3 = simpleframebuffer;
            simpleframebuffer = simpleframebuffer1;
            simpleframebuffer1 = simpleframebuffer3;
         }

         if (simpleframebuffer != simpleFramebuffer) {
            this.on23(var052, simpleframebuffer, simpleFramebuffer, 0.0F);
         }
      }
   }

   public void on23(RawShaderProgram var1, SimpleFramebuffer var2, SimpleFramebuffer var3, float var4) {
      if (var1 != null && var2 != null && var3 != null) {
         org.zenith.render.LegacyRenderBridge.setOutput(var3);
         var1.bind();
         var1.ItemSpec("image", 0);
         var1.on23("offset", var4);
         var1.on23("resolution", 1.0F / Math.max(1.0F, var3.textureWidth), 1.0F / Math.max(1.0F, var3.textureHeight));
         org.zenith.render.LegacyRenderBridge.activeTexture(33984);
         org.zenith.render.LegacyRenderBridge.bindTexture(var2.getColorAttachmentView());
         HandShaderManager.var14336();
         var1.unbind();
      }
   }

   public static boolean int295() {
      if (var05 != null && var052 != null) {
         return true;
      }

      if (boolean38) {
         return false;
      }

      try {
         var05 = new RawShaderProgram("chams_kawase", "kawase_down", "kawase");
         var052 = new RawShaderProgram("chams_kawase", "kawase_up", "kawase");
         return true;
      } catch (RuntimeException runtimeexception) {
         float172();
         boolean38 = true;
         return false;
      }
   }

   public static void float172() {
      if (var05 != null) {
         var05.delete();
         var05 = null;
      }

      if (var052 != null) {
         var052.delete();
         var052 = null;
      }

      boolean38 = false;
   }

   public long float173() {
      if (minecraftClient3.world == null) {
         return Long.MIN_VALUE;
      }

      long i = minecraftClient3.world.getTime();
      int j = Float.floatToIntBits(minecraftClient3.getRenderTickCounter().getTickProgress(false));
      return i << 32 ^ j & 4294967295L;
   }

   public float float174() {
      if (minecraftClient3.world == null) {
         return 0.0F;
      }

      float f = minecraftClient3.getRenderTickCounter().getTickProgress(false);
      return ((float)minecraftClient3.world.getTime() + f) * 0.05F * this.speed.getCurrent();
   }

   public float float175() {
      return 0.0018F + this.speed.getCurrent() * 0.0025F;
   }

   public int[] zClass022Var159() {
      if (this.colorMode.is(0)) {
         long j1 = System.currentTimeMillis() / 8L;
         int k = CosmeticManager(j1 % 360L);
         int l = CosmeticManager((j1 + 90L) % 360L);
         return new int[]{k, l};
      } else if (this.colorMode.is(1)) {
         int i1 = ZenithClient.on23().TextScanner().getClientColor(0).call001();
         int k1 = ZenithClient.on23().TextScanner().getClientColor(180).call001();
         return new int[]{i1, k1};
      } else {
         int i = this.customColor.getIntColor();
         int j = this.secondColor.isEnabled() ? this.customSecondColor.getIntColor() : i;
         return new int[]{i, j};
      }
   }

   public static int CosmeticManager(long var0) {
      float f = (float)((var0 % 360L + 360L) % 360L) / 360.0F;
      return ArgbColor.FileLogger(f, 1.0F, 1.0F).call001();
   }

   public static void UiAnimation(SimpleFramebuffer var0) {
      if (var0 != null) {
         var0.delete();
      }
   }


   public static final class ColorState {
      public void vec3d16() {
      }

      public static ColorState call094() {
         return new ColorState();
      }
   }
}
