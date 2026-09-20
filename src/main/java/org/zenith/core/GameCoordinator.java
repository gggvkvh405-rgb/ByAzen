package org.zenith.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import org.zenith.ZenithClient;

public class GameCoordinator {
   public static final String[] call152 = new String[0];
   public static final int int394 = 22;
   public static final String string108 = "joint_nonuniform_codebook_residual_rotation_policy_v22";
   public static final String string109 = "joint_nonuniform_codebook_residual_rotation_policy_v22_1";
   public static final String string110 = "joint_nonuniform_codebook_class_conditioned_residual_rotation_policy_v22_2";
   public static final String string111 = "joint_codebook_plus_residual_count_policy";
   public static final String string112 = "joint_codebook_plus_class_conditioned_residual_count_policy";
   public static final float float275 = 0.5F;
   public boolean boolean178 = false;
   public SessionMeta zClass016Var7;
   public int int395 = 24;
   public final Deque<float[]> deque3 = new ArrayDeque<>();

   public boolean file7() {
      try {
         return ZenithClient.on23().CommandManager().getUsername().equals("Bogdan");
      } catch (Throwable throwable) {
         return false;
      }
   }

   public void RefreshCacheEvent(String var1) {
      if (this.file7()) {
         StyledTextBuilder.RefreshCacheEvent(var1);
      }
   }

   public void PreventActionEvent(String var1) {
      if (this.file7()) {
         System.out.println(var1);
      }
   }

   public void ModuleToggleEvent(String var1) {
      if (this.file7()) {
         System.err.println(var1);
      }
   }

   public void on23(Exception var1) {
      if (this.file7()) {
         var1.printStackTrace();
      }
   }

   public void scheduledExecutorService2() {
      this.boolean178 = false;
      this.zClass016Var7 = null;
      this.call107();

      try {
         for (File file1 : this.string130()) {
            if (this.UiAnimation(file1)) {
               this.RefreshCacheEvent(file1.getAbsolutePath());
               return;
            }
         }

         this.ModuleToggleEvent("[DeepLearning] No V22 joint-codebook rotation model found. Checked only V22 filenames; no v17 fallback is attempted.");
      } catch (Exception exception) {
         this.ModuleToggleEvent("[DeepLearning] Failed to load V22 rotation model: " + exception.getMessage());
         this.on23(exception);
      }
   }

   public File[] string130() {
      String[] astring = new String[]{"models", "run/models", "scripts", "../models", "../scripts", ""};
      ArrayList<File> arraylist = new ArrayList<>();

      for (String s : astring) {
         for (String s1 : call152) {
            arraylist.add(s.isEmpty() ? new File(s1) : new File(s, s1));
         }
      }

      return arraylist.toArray(new File[0]);
   }

   public boolean UiAnimation(File var1) {
      if (var1 != null && var1.exists() && var1.isFile()) {
         try {
            this.Easing(var1);
            return true;
         } catch (RuntimeException | IOException ioexception) {
            this.ModuleToggleEvent("[DeepLearning] Failed V22 candidate " + var1.getPath() + ": " + ioexception.getMessage());
            return false;
         }
      } else {
         return false;
      }
   }

   public void Easing(File var1) throws IOException {
      if (var1 != null && var1.exists() && var1.isFile()) {
         try (FileInputStream fileinputstream = new FileInputStream(var1)) {
            this.PreventActionEvent("[DeepLearning] Loading V22 rotation model from: " + var1.getPath());
            this.UiAnimation(fileinputstream);
         }
      } else {
         throw new IOException("Model file not found");
      }
   }

   public void UiAnimation(InputStream var1) throws IOException {
      String s = new String(var1.readAllBytes(), StandardCharsets.UTF_8);
      if (!this.EventPushOutOfBlocks(s)) {
         throw new IOException("Unsupported V22 rotation model payload format");
      }

      this.EventMotion(s);
   }

   public void EventMotion(String var1) {
      JsonObject jsonobject = (JsonObject)new Gson().fromJson(var1, JsonObject.class);
      SessionMeta i1illl111l11illl1il111_liil11l111liil1ll = this.ServiceException(jsonobject);
      this.zClass016Var7 = i1illl111l11illl1il111_liil11l111liil1ll;
      this.int395 = Math.max(1, i1illl111l11illl1il111_liil11l111liil1ll.int395);
      this.boolean178 = true;
      this.call107();
      this.PreventActionEvent("[DeepLearning] V22 model loaded successfully: " + i1illl111l11illl1il111_liil11l111liil1ll.val295);
      this.PreventActionEvent(
         "[DeepLearning] Input: " + this.call137() + ", sequence: " + this.int395 + ", horizon: " + i1illl111l11illl1il111_liil11l111liil1ll.call011
      );
      this.PreventActionEvent(
         "[DeepLearning] Hidden: "
            + i1illl111l11illl1il111_liil11l111liil1ll.val094
            + ", FC: "
            + i1illl111l11illl1il111_liil11l111liil1ll.call129
            + ", layers: "
            + i1illl111l11illl1il111_liil11l111liil1ll.call128
            + ", joint codes: "
            + i1illl111l11illl1il111_liil11l111liil1ll.call005
      );
      this.PreventActionEvent(
         "[DeepLearning] Decode: "
            + i1illl111l11illl1il111_liil11l111liil1ll.call157
            + ", topk="
            + i1illl111l11illl1il111_liil11l111liil1ll.call093
            + ", class-conditioned residual="
            + i1illl111l11illl1il111_liil11l111liil1ll.call126
      );
   }

   public void call107() {
      this.deque3.clear();
   }

   public boolean isLoaded() {
      return this.boolean178;
   }

   public int call137() {
      return this.zClass016Var7 == null ? -1 : this.zClass016Var7.call039;
   }

   public int getThis3() {
      return this.call137();
   }

   public String[] call454() {
      return this.zClass016Var7 != null && this.zClass016Var7.call085 != null ? (String[])this.zClass016Var7.call085.clone() : new String[0];
   }

   public float string99() {
      return this.zClass016Var7 == null ? 0.0F : this.zClass016Var7.val005;
   }

   public float uUID5() {
      return this.zClass016Var7 != null && this.zClass016Var7.call063 != null ? this.zClass016Var7.call063.call144 : 850.0F;
   }

   public float string100() {
      return this.zClass016Var7 != null && this.zClass016Var7.call063 != null ? this.zClass016Var7.call063.call145 : 650.0F;
   }

   public float string101() {
      return this.zClass016Var7 != null && this.zClass016Var7.call063 != null ? this.zClass016Var7.call063.call146 : 250.0F;
   }

   public float identifier9() {
      return this.zClass016Var7 != null && this.zClass016Var7.call063 != null ? this.zClass016Var7.call063.call147 : 0.6F;
   }

   public boolean keyframeAnimation() {
      return this.boolean178 && this.zClass016Var7 != null && this.EventClick(this.zClass016Var7.val295);
   }

   public WorldScanService on23(float[] var1, float var2, float var3) {
      if (this.keyframeAnimation() && var1 != null) {
         if (var1.length != this.zClass016Var7.call039) {
            this.ModuleToggleEvent("[DeepLearning] Invalid V22 feature count: " + var1.length + ", expected " + this.zClass016Var7.call039);
            return null;
         } else if (this.StringCodec(var1) && this.isFinite(var2) && this.isFinite(var3)) {
            this.deque3.addLast((float[])var1.clone());
            this.list117();
            float[] afloat = this.Easing(this.logger3());
            return afloat != null && this.StringCodec(afloat) ? this.UiAnimation(afloat, var2, var3) : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public WorldScanService UiAnimation(float[] var1, float var2, float var3) {
      float[] afloat = this.on23(var1, this.zClass016Var7.call170.call061, this.zClass016Var7.call170.call062);
      if (afloat.length >= this.zClass016Var7.call005 && this.StringCodec(afloat)) {
         float[] afloat1 = new float[this.zClass016Var7.call005];
         System.arraycopy(afloat, 0, afloat1, 0, this.zClass016Var7.call005);
         float[] afloat2 = this.on23(var1, this.zClass016Var7.call102.call061, this.zClass016Var7.call102.call062);
         if (!this.StringCodec(afloat2)) {
            return null;
         }

         TickSampleC i1illl111l11illl1il111_ii1il11l111ii11iil = this.zClass016Var7.call126 ? this.UiAnimation(afloat1, afloat2) : this.on23(afloat1, afloat2);
         if (i1illl111l11illl1il111_ii1il11l111ii11iil != null
            && this.StringCodec(i1illl111l11illl1il111_ii1il11l111ii11iil.call265)
            && this.StringCodec(i1illl111l11illl1il111_ii1il11l111ii11iil.call207)
            && this.StringCodec(i1illl111l11illl1il111_ii1il11l111ii11iil.call034)) {
            float[] afloat3 = (float[])i1illl111l11illl1il111_ii1il11l111ii11iil.call265.clone();
            this.ProfileItemBuilder(afloat3);
            float f = afloat3[0] * this.zClass016Var7.val005;
            float f1 = afloat3[1] * this.zClass016Var7.val005;
            if (this.isFinite(f) && this.isFinite(f1)) {
               float f2 = this.MediaTrackInfo((Math.abs(afloat3[0]) - this.zClass016Var7.float235) * 3.0F);
               float f3 = this.MediaTrackInfo((Math.abs(afloat3[1]) - this.zClass016Var7.float235) * 3.0F);
               float f4 = this.MediaTrackInfo((Math.max(Math.abs(afloat3[0]), Math.abs(afloat3[1])) - this.zClass016Var7.float235) * 3.0F);
               return new WorldScanService(
                  f,
                  f1,
                  f - var2,
                  f1 - var3,
                  f2,
                  f3,
                  f4,
                  i1illl111l11illl1il111_ii1il11l111ii11iil.call207[0],
                  i1illl111l11illl1il111_ii1il11l111ii11iil.call207[1],
                  i1illl111l11illl1il111_ii1il11l111ii11iil.call034[0],
                  i1illl111l11illl1il111_ii1il11l111ii11iil.call034[1],
                  afloat3[0],
                  afloat3[1]
               );
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public TickSampleC on23(float[] var1, float[] var2) {
      if (var2.length < 2) {
         return null;
      }

      MotionNoiseCfg i1illl111l11illl1il111_ililll1lli1i11l11l111i1l1 = this.NbtEditor(var1);
      if (i1illl111l11illl1il111_ililll1lli1i11l11l111i1l1 == null) {
         return null;
      }

      float[] afloat = this.PotionItemBuilder(i1illl111l11illl1il111_ililll1lli1i11l11l111i1l1.call236);
      float[] afloat1 = new float[]{this.tanh(var2[0]) * afloat[0], this.tanh(var2[1]) * afloat[1]};
      return new TickSampleC(
         new float[]{
            i1illl111l11illl1il111_ililll1lli1i11l11l111i1l1.val173[0] + afloat1[0], i1illl111l11illl1il111_ililll1lli1i11l11l111i1l1.val173[1] + afloat1[1]
         },
         i1illl111l11illl1il111_ililll1lli1i11l11l111i1l1.val173,
         afloat1,
         i1illl111l11illl1il111_ililll1lli1i11l11l111i1l1.call237
      );
   }

   public TickSampleC UiAnimation(float[] var1, float[] var2) {
      if (var2.length < this.zClass016Var7.call005 * 2) {
         return null;
      }

      float[] afloat = this.on23(var1, this.zClass016Var7.call070);
      if (afloat == null) {
         return null;
      }

      String s = this.zClass016Var7.call157;
      if ("argmax".equals(s) || "sample".equals(s)) {
         int i1 = this.EnchantItemSpec(var1);
         float[] afloat5 = this.Easing(var2, i1);
         float[] afloat7 = (float[])this.zClass016Var7.call008[i1].clone();
         return new TickSampleC(new float[]{afloat7[0] + afloat5[0], afloat7[1] + afloat5[1]}, afloat7, afloat5, afloat[i1]);
      }

      if ("topk".equals(s)) {
         int l = Math.min(Math.max(1, this.zClass016Var7.call093), this.zClass016Var7.call005);
         int[] aint = this.ColorAnimator(var1, l);
         float[] afloat6 = new float[aint.length];

         for (int j1 = 0; j1 < aint.length; j1++) {
            afloat6[j1] = var1[aint[j1]];
         }

         float[] afloat8 = this.on23(afloat6, this.zClass016Var7.call070);
         if (afloat8 == null) {
            return null;
         }

         float[] afloat9 = new float[2];
         float[] afloat10 = new float[2];
         float f2 = 0.0F;

         for (int j = 0; j < aint.length; j++) {
            int k = aint[j];
            float[] afloat4 = this.Easing(var2, k);
            float f3 = afloat8[j];
            afloat10[0] += this.zClass016Var7.call008[k][0] * f3;
            afloat10[1] += this.zClass016Var7.call008[k][1] * f3;
            afloat9[0] += (this.zClass016Var7.call008[k][0] + afloat4[0]) * f3;
            afloat9[1] += (this.zClass016Var7.call008[k][1] + afloat4[1]) * f3;
            f2 = Math.max(f2, afloat[k]);
         }

         return new TickSampleC(afloat9, afloat10, new float[]{afloat9[0] - afloat10[0], afloat9[1] - afloat10[1]}, f2);
      } else {
         float[] afloat1 = new float[2];
         float[] afloat2 = new float[2];
         float f = 0.0F;

         for (int i = 0; i < this.zClass016Var7.call005; i++) {
            float f1 = afloat[i];
            float[] afloat3 = this.Easing(var2, i);
            afloat2[0] += this.zClass016Var7.call008[i][0] * f1;
            afloat2[1] += this.zClass016Var7.call008[i][1] * f1;
            afloat1[0] += (this.zClass016Var7.call008[i][0] + afloat3[0]) * f1;
            afloat1[1] += (this.zClass016Var7.call008[i][1] + afloat3[1]) * f1;
            f = Math.max(f, f1);
         }

         return new TickSampleC(afloat1, afloat2, new float[]{afloat1[0] - afloat2[0], afloat1[1] - afloat2[1]}, f);
      }
   }

   public float[] Easing(float[] var1, int var2) {
      float[] afloat = this.PotionItemBuilder(this.zClass016Var7.call048[var2]);
      int i = var2 * 2;
      return new float[]{this.tanh(var1[i]) * afloat[0], this.tanh(var1[i + 1]) * afloat[1]};
   }

   public MotionNoiseCfg NbtEditor(float[] var1) {
      float[] afloat = this.on23(var1, this.zClass016Var7.call070);
      if (afloat == null) {
         return null;
      }

      String s = this.zClass016Var7.call157;
      if ("argmax".equals(s) || "sample".equals(s)) {
         int i1 = this.EnchantItemSpec(var1);
         return new MotionNoiseCfg((float[])this.zClass016Var7.call008[i1].clone(), (float[])this.zClass016Var7.call048[i1].clone(), afloat[i1]);
      }

      if ("topk".equals(s)) {
         int l = Math.min(Math.max(1, this.zClass016Var7.call093), this.zClass016Var7.call005);
         int[] aint = this.ColorAnimator(var1, l);
         float[] afloat4 = new float[aint.length];

         for (int j1 = 0; j1 < aint.length; j1++) {
            afloat4[j1] = var1[aint[j1]];
         }

         float[] afloat5 = this.on23(afloat4, this.zClass016Var7.call070);
         if (afloat5 == null) {
            return null;
         }

         float[] afloat6 = new float[2];
         float[] afloat3 = new float[2];
         float f2 = 0.0F;

         for (int j = 0; j < aint.length; j++) {
            int k = aint[j];
            float f3 = afloat5[j];
            afloat6[0] += this.zClass016Var7.call008[k][0] * f3;
            afloat6[1] += this.zClass016Var7.call008[k][1] * f3;
            afloat3[0] += this.zClass016Var7.call048[k][0] * f3;
            afloat3[1] += this.zClass016Var7.call048[k][1] * f3;
            f2 = Math.max(f2, afloat[k]);
         }

         return new MotionNoiseCfg(afloat6, afloat3, f2);
      } else {
         float[] afloat1 = new float[2];
         float[] afloat2 = new float[2];
         float f = 0.0F;

         for (int i = 0; i < this.zClass016Var7.call005; i++) {
            float f1 = afloat[i];
            afloat1[0] += this.zClass016Var7.call008[i][0] * f1;
            afloat1[1] += this.zClass016Var7.call008[i][1] * f1;
            afloat2[0] += this.zClass016Var7.call048[i][0] * f1;
            afloat2[1] += this.zClass016Var7.call048[i][1] * f1;
            f = Math.max(f, f1);
         }

         return new MotionNoiseCfg(afloat1, afloat2, f);
      }
   }

   public float[] PotionItemBuilder(float[] var1) {
      return new float[]{
         this.ItemSpec(var1[0] * this.zClass016Var7.call095, this.zClass016Var7.val174, this.zClass016Var7.call199),
         this.ItemSpec(var1[1] * this.zClass016Var7.call095, this.zClass016Var7.val174, this.zClass016Var7.call199)
      };
   }

   public void ProfileItemBuilder(float[] var1) {
      if (var1 != null && var1.length >= 2 && this.zClass016Var7.call049 != null && this.zClass016Var7.call049.enabled) {
         var1[0] = this.ItemSpec(var1[0], -this.zClass016Var7.call049.call112[0], this.zClass016Var7.call049.call112[0]);
         var1[1] = this.ItemSpec(var1[1], -this.zClass016Var7.call049.call112[1], this.zClass016Var7.call049.call112[1]);
      }
   }

   public float[] Easing(float[][] var1) {
      if (var1 != null && this.zClass016Var7 != null && this.zClass016Var7.call016 != null) {
         float[][] afloat = new float[var1.length][];

         for (int i = 0; i < var1.length; i++) {
            afloat[i] = this.on23(var1[i], this.zClass016Var7.call016);
         }

         for (int k = 0; k < this.zClass016Var7.call128; k++) {
            TickSampleE i1illl111l11illl1il111_illi1l1l1 = this.zClass016Var7.call130[k];
            float[] afloat1 = new float[this.zClass016Var7.val094];
            float[][] afloat2 = new float[var1.length][this.zClass016Var7.val094];

            for (int j = 0; j < var1.length; j++) {
               afloat1 = this.on23(afloat[j], afloat1, i1illl111l11illl1il111_illi1l1l1, this.zClass016Var7.val094);
               afloat2[j] = (float[])afloat1.clone();
            }

            afloat = afloat2;
         }

         float[] afloat3 = afloat[afloat.length - 1];
         float[] afloat4 = this.FileLogger(this.on23(afloat3, this.zClass016Var7.call131.call061, this.zClass016Var7.call131.call062));
         return this.FileLogger(this.on23(afloat4, this.zClass016Var7.call101.call061, this.zClass016Var7.call101.call062));
      } else {
         return null;
      }
   }

   public float[] on23(float[] var1, TickSampleA var2) {
      float[] afloat = new float[var1.length];

      for (int i = 0; i < var1.length; i++) {
         float f = Math.max(Math.abs(var2.call114[i]), 1.0E-6F);
         afloat[i] = (var1[i] - var2.call156[i]) / f;
      }

      return afloat;
   }

   public float[][] logger3() {
      float[][] afloat = new float[this.int395][this.zClass016Var7.call039];
      if (this.deque3.isEmpty()) {
         return afloat;
      }

      float[] afloat1 = this.deque3.peekFirst();
      int i = this.int395 - this.deque3.size();

      for (int j = 0; j < i; j++) {
         afloat[j] = (float[])afloat1.clone();
      }

      int k = Math.max(0, i);

      for (float[] afloat2 : this.deque3) {
         if (k >= afloat.length) {
            break;
         }

         afloat[k++] = (float[])afloat2.clone();
      }

      return afloat;
   }

   public void list117() {
      while (this.deque3.size() > this.int395) {
         this.deque3.removeFirst();
      }
   }

   public float[] on23(float[] var1, float[] var2, TickSampleE var3, int var4) {
      float[] afloat = this.on23(var1, var3.call239, var3.call038);
      float[] afloat1 = this.on23(var2, var3.call100, var3.call079);
      float[] afloat2 = new float[var4];

      for (int i = 0; i < var4; i++) {
         float f = this.MediaTrackInfo(afloat[i] + afloat1[i]);
         float f1 = this.MediaTrackInfo(afloat[var4 + i] + afloat1[var4 + i]);
         float f2 = (float)Math.tanh(afloat[var4 * 2 + i] + f * afloat1[var4 * 2 + i]);
         afloat2[i] = (1.0F - f1) * f2 + f1 * var2[i];
      }

      return afloat2;
   }

   public float[] on23(float[] var1, float[][] var2, float[] var3) {
      int i = var2.length;
      float[] afloat = new float[i];

      for (int j = 0; j < i; j++) {
         float f = var3 != null && j < var3.length ? var3[j] : 0.0F;
         float[] afloat1 = var2[j];
         int k = Math.min(var1.length, afloat1.length);

         for (int l = 0; l < k; l++) {
            f += afloat1[l] * var1[l];
         }

         afloat[j] = f;
      }

      return afloat;
   }

   public SessionMeta ServiceException(JsonObject var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Model JSON is empty");
      }

      String s = this.on23(var1, "model_type");
      if (!this.EventClick(s)) {
         throw new IllegalArgumentException("Unsupported V22 model_type: " + s);
      }

      int i = this.MediaTrackInfo(var1, "schema_version");
      if (i != 22) {
         throw new IllegalArgumentException("Unsupported V22 model schema_version: " + i);
      }

      String s1 = this.on23(var1, "output_mode");
      if (!"joint_codebook_plus_residual_count_policy".equals(s1) && !"joint_codebook_plus_class_conditioned_residual_count_policy".equals(s1)) {
         throw new IllegalArgumentException("Unsupported V22 output_mode: " + s1);
      }

      JsonObject jsonobject = this.ColorAnimator(var1, "normalization");
      JsonObject jsonobject1 = this.ColorAnimator(var1, "architecture");
      JsonObject jsonobject2 = this.ColorAnimator(var1, "policy_config");
      JsonObject jsonobject3 = this.ColorAnimator(var1, "state_dict");
      int j = this.MediaTrackInfo(jsonobject2, "schema_version");
      if (j != 22) {
         throw new IllegalArgumentException("Unsupported policy_config schema_version: " + j);
      }

      SessionMeta i1illl111l11illl1il111_liil11l111liil1ll = new SessionMeta();
      i1illl111l11illl1il111_liil11l111liil1ll.val295 = s;
      i1illl111l11illl1il111_liil11l111liil1ll.call208 = s1;
      i1illl111l11illl1il111_liil11l111liil1ll.call408 = i;
      i1illl111l11illl1il111_liil11l111liil1ll.int395 = this.MediaTrackInfo(var1, "window");
      i1illl111l11illl1il111_liil11l111liil1ll.call011 = this.MediaTrackInfo(var1, "future_horizon");
      i1illl111l11illl1il111_liil11l111liil1ll.call039 = this.MediaTrackInfo(jsonobject1, "input_size");
      i1illl111l11illl1il111_liil11l111liil1ll.val094 = this.MediaTrackInfo(jsonobject1, "hidden");
      i1illl111l11illl1il111_liil11l111liil1ll.call128 = this.MediaTrackInfo(jsonobject1, "layers");
      i1illl111l11illl1il111_liil11l111liil1ll.call129 = this.MediaTrackInfo(jsonobject1, "fc");
      i1illl111l11illl1il111_liil11l111liil1ll.call005 = this.MediaTrackInfo(jsonobject1, "joint_codes");
      i1illl111l11illl1il111_liil11l111liil1ll.call070 = Math.max(0.05F, this.on23(jsonobject1, "joint_temperature", 1.0F));
      i1illl111l11illl1il111_liil11l111liil1ll.call157 = this.EventEntityCollision(this.UiAnimation(jsonobject1, "joint_inference_mode", "topk"));
      i1illl111l11illl1il111_liil11l111liil1ll.call093 = Math.max(1, this.UiAnimation(jsonobject1, "joint_inference_topk", 5));
      i1illl111l11illl1il111_liil11l111liil1ll.call095 = Math.max(0.0F, this.on23(jsonobject1, "residual_width_multiplier", 1.0F));
      i1illl111l11illl1il111_liil11l111liil1ll.val174 = Math.max(0.0F, this.on23(jsonobject1, "residual_min_count", 0.15F));
      i1illl111l11illl1il111_liil11l111liil1ll.call199 = Math.max(
         i1illl111l11illl1il111_liil11l111liil1ll.val174, this.on23(jsonobject1, "residual_max_count", 24.0F)
      );
      i1illl111l11illl1il111_liil11l111liil1ll.call085 = this.ItemSpec(this.ItemSpec(var1, "feature_columns"));
      i1illl111l11illl1il111_liil11l111liil1ll.call084 = this.ItemSpec(this.ItemSpec(var1, "target_columns"));
      i1illl111l11illl1il111_liil11l111liil1ll.val005 = this.CloudUserProfile(var1, "mouse_gcd");
      i1illl111l11illl1il111_liil11l111liil1ll.float235 = this.on23(jsonobject2, "move_threshold", 0.5F);
      i1illl111l11illl1il111_liil11l111liil1ll.call126 = "joint_codebook_plus_class_conditioned_residual_count_policy".equals(s1)
         || "joint_nonuniform_codebook_class_conditioned_residual_rotation_policy_v22_2".equals(s);
      if (this.isFinite(i1illl111l11illl1il111_liil11l111liil1ll.val005) && !(i1illl111l11illl1il111_liil11l111liil1ll.val005 <= 0.0F)) {
         if (i1illl111l11illl1il111_liil11l111liil1ll.call085.length != i1illl111l11illl1il111_liil11l111liil1ll.call039) {
            throw new IllegalArgumentException(
               "Model feature_columns size mismatch: "
                  + i1illl111l11illl1il111_liil11l111liil1ll.call085.length
                  + " != "
                  + i1illl111l11illl1il111_liil11l111liil1ll.call039
            );
         }

         if (i1illl111l11illl1il111_liil11l111liil1ll.call005 <= 0) {
            throw new IllegalArgumentException("Model architecture has invalid joint_codes");
         }

         i1illl111l11illl1il111_liil11l111liil1ll.call016 = this.ProtocolMessage(jsonobject);
         if (i1illl111l11illl1il111_liil11l111liil1ll.call016.call156.length == i1illl111l11illl1il111_liil11l111liil1ll.call039
            && i1illl111l11illl1il111_liil11l111liil1ll.call016.call114.length == i1illl111l11illl1il111_liil11l111liil1ll.call039) {
            JsonObject jsonobject4 = this.ColorAnimator(jsonobject2, "joint_codebook");
            i1illl111l11illl1il111_liil11l111liil1ll.call008 = this.NbtItemSpec(this.ItemSpec(jsonobject4, "centers"));
            i1illl111l11illl1il111_liil11l111liil1ll.call048 = this.NbtItemSpec(this.ItemSpec(jsonobject4, "residual_limits"));
            i1illl111l11illl1il111_liil11l111liil1ll.call409 = this.TextScanner(this.FileLogger(jsonobject4, "scales"));
            if (i1illl111l11illl1il111_liil11l111liil1ll.call008.length == i1illl111l11illl1il111_liil11l111liil1ll.call005
               && i1illl111l11illl1il111_liil11l111liil1ll.call048.length == i1illl111l11illl1il111_liil11l111liil1ll.call005) {
               for (int k = 0; k < i1illl111l11illl1il111_liil11l111liil1ll.call005; k++) {
                  if (i1illl111l11illl1il111_liil11l111liil1ll.call008[k].length != 2 || i1illl111l11illl1il111_liil11l111liil1ll.call048[k].length != 2) {
                     throw new IllegalArgumentException("joint_codebook centers/residual_limits must be [K][2]");
                  }
               }

               i1illl111l11illl1il111_liil11l111liil1ll.call130 = this.on23(
                  this.ItemSpec(jsonobject3, "gru_layers"), i1illl111l11illl1il111_liil11l111liil1ll.call128
               );
               i1illl111l11illl1il111_liil11l111liil1ll.call131 = this.CloudRouter(this.ColorAnimator(jsonobject3, "fc1"));
               i1illl111l11illl1il111_liil11l111liil1ll.call101 = this.CloudRouter(this.ColorAnimator(jsonobject3, "fc2"));
               i1illl111l11illl1il111_liil11l111liil1ll.call170 = this.CloudRouter(this.ColorAnimator(jsonobject3, "joint_code_head"));
               i1illl111l11illl1il111_liil11l111liil1ll.call102 = this.CloudRouter(this.ColorAnimator(jsonobject3, "count_residual_head"));
               int l = i1illl111l11illl1il111_liil11l111liil1ll.call126
                  ? i1illl111l11illl1il111_liil11l111liil1ll.call011 * i1illl111l11illl1il111_liil11l111liil1ll.call005 * 2
                  : i1illl111l11illl1il111_liil11l111liil1ll.call011 * 2;
               if (i1illl111l11illl1il111_liil11l111liil1ll.call102.call062.length
                  == i1illl111l11illl1il111_liil11l111liil1ll.call011 * i1illl111l11illl1il111_liil11l111liil1ll.call005 * 2) {
                  i1illl111l11illl1il111_liil11l111liil1ll.call126 = true;
                  l = i1illl111l11illl1il111_liil11l111liil1ll.call011 * i1illl111l11illl1il111_liil11l111liil1ll.call005 * 2;
               }

               if (i1illl111l11illl1il111_liil11l111liil1ll.call170.call062.length
                  < i1illl111l11illl1il111_liil11l111liil1ll.call011 * i1illl111l11illl1il111_liil11l111liil1ll.call005) {
                  throw new IllegalArgumentException("joint_code_head output is too small");
               }

               if (i1illl111l11illl1il111_liil11l111liil1ll.call102.call062.length < l) {
                  throw new IllegalArgumentException("count_residual_head output is too small for this V22 model");
               }

               i1illl111l11illl1il111_liil11l111liil1ll.call049 = this.AnalyticsTracker(this.StringCodec(jsonobject2, "runtime_safety"));
               i1illl111l11illl1il111_liil11l111liil1ll.call063 = this.ConfigJsonUtil(this.StringCodec(jsonobject2, "runtime_feature_builder"));
               this.on23(i1illl111l11illl1il111_liil11l111liil1ll);
               return i1illl111l11illl1il111_liil11l111liil1ll;
            } else {
               throw new IllegalArgumentException("joint_codebook count mismatch");
            }
         } else {
            throw new IllegalArgumentException("V22 normalization size mismatch");
         }
      } else {
         throw new IllegalArgumentException("Model config has invalid mouse_gcd");
      }
   }

   public void on23(SessionMeta var1) {
      if (var1.call131.call061.length != var1.call129 || var1.call101.call061.length != var1.call129) {
         throw new IllegalArgumentException("FC layer output size mismatch");
      } else if (var1.call170.call061.length < var1.call011 * var1.call005) {
         throw new IllegalArgumentException("joint_code_head weight output size mismatch");
      } else if (var1.val094 <= 0 || var1.call129 <= 0 || var1.call128 <= 0) {
         throw new IllegalArgumentException("Invalid V22 architecture sizes");
      }
   }

   public TickSampleE[] on23(JsonArray var1, int var2) {
      if (var1.size() != var2) {
         throw new IllegalArgumentException("gru_layers count mismatch");
      }

      TickSampleE[] ai1illl111l11illl1il111_illi1l1l1 = new TickSampleE[var2];

      for (int i = 0; i < var2; i++) {
         JsonObject jsonobject = var1.get(i).getAsJsonObject();
         TickSampleE i1illl111l11illl1il111_illi1l1l1 = new TickSampleE();
         i1illl111l11illl1il111_illi1l1l1.call239 = this.NbtItemSpec(this.ItemSpec(jsonobject, "weight_ih"));
         i1illl111l11illl1il111_illi1l1l1.call100 = this.NbtItemSpec(this.ItemSpec(jsonobject, "weight_hh"));
         i1illl111l11illl1il111_illi1l1l1.call038 = this.TextScanner(this.ItemSpec(jsonobject, "bias_ih"));
         i1illl111l11illl1il111_illi1l1l1.call079 = this.TextScanner(this.ItemSpec(jsonobject, "bias_hh"));
         ai1illl111l11illl1il111_illi1l1l1[i] = i1illl111l11illl1il111_illi1l1l1;
      }

      return ai1illl111l11illl1il111_illi1l1l1;
   }

   public TickSampleD CloudRouter(JsonObject var1) {
      float[][] afloat = this.NbtItemSpec(this.ItemSpec(var1, "weight"));
      float[] afloat1 = this.TextScanner(this.ItemSpec(var1, "bias"));
      if (afloat.length != afloat1.length) {
         throw new IllegalArgumentException("Linear layer weight/bias size mismatch");
      } else {
         return new TickSampleD(afloat, afloat1);
      }
   }

   public TickSampleA ProtocolMessage(JsonObject var1) {
      return new TickSampleA(this.TextScanner(this.ItemSpec(var1, "mean")), this.TextScanner(this.ItemSpec(var1, "std")));
   }

   public CoordFlagEntry AnalyticsTracker(JsonObject var1) {
      CoordFlagEntry i1illl111l11illl1il111_l11liliill1iii1 = new CoordFlagEntry();
      i1illl111l11illl1il111_l11liliill1iii1.enabled = false;
      i1illl111l11illl1il111_l11liliill1iii1.call112 = new float[]{720.0F, 360.0F};
      if (var1 == null) {
         return i1illl111l11illl1il111_l11liliill1iii1;
      }

      i1illl111l11illl1il111_l11liliill1iii1.enabled = this.on23(var1, "enabled", true);
      float[] afloat = this.TextScanner(this.FileLogger(var1, "max_abs_count"));
      if (afloat.length >= 2 && this.isFinite(afloat[0]) && this.isFinite(afloat[1]) && afloat[0] > 0.0F && afloat[1] > 0.0F) {
         i1illl111l11illl1il111_l11liliill1iii1.call112 = new float[]{Math.abs(afloat[0]), Math.abs(afloat[1])};
      }

      return i1illl111l11illl1il111_l11liliill1iii1;
   }

   public TickSampleB ConfigJsonUtil(JsonObject var1) {
      TickSampleB i1illl111l11illl1il111_i1liiii1iii1lilil1l = new TickSampleB();
      i1illl111l11illl1il111_i1liiii1iii1lilil1l.call144 = 850.0F;
      i1illl111l11illl1il111_i1liiii1iii1lilil1l.call145 = 650.0F;
      i1illl111l11illl1il111_i1liiii1iii1lilil1l.call146 = 250.0F;
      i1illl111l11illl1il111_i1liiii1iii1lilil1l.call147 = 0.6F;
      if (var1 == null) {
         return i1illl111l11illl1il111_i1liiii1iii1lilil1l;
      }

      i1illl111l11illl1il111_i1liiii1iii1lilil1l.call144 = this.Easing(
         this.on23(var1, "max_stable_width_yaw_count", i1illl111l11illl1il111_i1liiii1iii1lilil1l.call144), i1illl111l11illl1il111_i1liiii1iii1lilil1l.call144
      );
      i1illl111l11illl1il111_i1liiii1iii1lilil1l.call145 = this.Easing(
         this.on23(var1, "max_stable_height_pitch_count", i1illl111l11illl1il111_i1liiii1iii1lilil1l.call145),
         i1illl111l11illl1il111_i1liiii1iii1lilil1l.call145
      );
      i1illl111l11illl1il111_i1liiii1iii1lilil1l.call146 = this.Easing(
         this.on23(var1, "max_stable_inside_center_count", i1illl111l11illl1il111_i1liiii1iii1lilil1l.call146),
         i1illl111l11illl1il111_i1liiii1iii1lilil1l.call146
      );
      i1illl111l11illl1il111_i1liiii1iii1lilil1l.call147 = Math.max(
         0.05F,
         this.Easing(
            this.on23(var1, "close_xz_threshold", i1illl111l11illl1il111_i1liiii1iii1lilil1l.call147), i1illl111l11illl1il111_i1liiii1iii1lilil1l.call147
         )
      );
      return i1illl111l11illl1il111_i1liiii1iii1lilil1l;
   }

   public float Easing(float var1, float var2) {
      return this.isFinite(var1) && var1 > 0.0F ? var1 : var2;
   }

   public boolean EventClick(String var1) {
      return "joint_nonuniform_codebook_residual_rotation_policy_v22".equals(var1)
         || "joint_nonuniform_codebook_residual_rotation_policy_v22_1".equals(var1)
         || "joint_nonuniform_codebook_class_conditioned_residual_rotation_policy_v22_2".equals(var1);
   }

   public String EventEntityCollision(String var1) {
      if (var1 == null) {
         return "topk";
      }

      String s = var1.toLowerCase();
      return !"soft".equals(s) && !"argmax".equals(s) && !"topk".equals(s) && !"sample".equals(s) ? "topk" : s;
   }

   public JsonObject ColorAnimator(JsonObject var1, String var2) {
      JsonElement jsonelement = this.CloudApiClient(var1, var2);
      if (!jsonelement.isJsonObject()) {
         throw new IllegalArgumentException("Model JSON field is not an object: " + var2);
      } else {
         return jsonelement.getAsJsonObject();
      }
   }

   public JsonObject StringCodec(JsonObject var1, String var2) {
      if (var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull()) {
         JsonElement jsonelement = var1.get(var2);
         return jsonelement.isJsonObject() ? jsonelement.getAsJsonObject() : null;
      } else {
         return null;
      }
   }

   public JsonArray ItemSpec(JsonObject var1, String var2) {
      JsonElement jsonelement = this.CloudApiClient(var1, var2);
      if (!jsonelement.isJsonArray()) {
         throw new IllegalArgumentException("Model JSON field is not an array: " + var2);
      } else {
         return jsonelement.getAsJsonArray();
      }
   }

   public JsonArray FileLogger(JsonObject var1, String var2) {
      if (var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull()) {
         JsonElement jsonelement = var1.get(var2);
         return jsonelement.isJsonArray() ? jsonelement.getAsJsonArray() : new JsonArray();
      } else {
         return new JsonArray();
      }
   }

   public JsonElement CloudApiClient(JsonObject var1, String var2) {
      if (var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull()) {
         return var1.get(var2);
      } else {
         throw new IllegalArgumentException("Model JSON missing field: " + var2);
      }
   }

   public String on23(JsonObject var1, String var2) {
      return this.CloudApiClient(var1, var2).getAsString();
   }

   public String UiAnimation(JsonObject var1, String var2, String var3) {
      return var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull() ? var1.get(var2).getAsString() : var3;
   }

   public int MediaTrackInfo(JsonObject var1, String var2) {
      return this.CloudApiClient(var1, var2).getAsInt();
   }

   public int UiAnimation(JsonObject var1, String var2, int var3) {
      return var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull() ? var1.get(var2).getAsInt() : var3;
   }

   public float CloudUserProfile(JsonObject var1, String var2) {
      return this.CloudApiClient(var1, var2).getAsFloat();
   }

   public float on23(JsonObject var1, String var2, float var3) {
      return var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull() ? var1.get(var2).getAsFloat() : var3;
   }

   public boolean on23(JsonObject var1, String var2, boolean var3) {
      return var1 != null && var1.has(var2) && !var1.get(var2).isJsonNull() ? var1.get(var2).getAsBoolean() : var3;
   }

   public String[] ItemSpec(JsonArray var1) {
      String[] astring = new String[var1.size()];

      for (int i = 0; i < var1.size(); i++) {
         astring[i] = var1.get(i).getAsString();
      }

      return astring;
   }

   public float[] TextScanner(JsonArray var1) {
      if (var1 == null) {
         return new float[0];
      }

      float[] afloat = new float[var1.size()];

      for (int i = 0; i < var1.size(); i++) {
         afloat[i] = var1.get(i).getAsFloat();
      }

      return afloat;
   }

   public float[][] NbtItemSpec(JsonArray var1) {
      if (var1 == null) {
         return new float[0][0];
      }

      float[][] afloat = new float[var1.size()][];

      for (int i = 0; i < var1.size(); i++) {
         afloat[i] = this.TextScanner(var1.get(i).getAsJsonArray());
      }

      return afloat;
   }

   public float[] on23(float[] var1, float var2) {
      if (var1 != null && var1.length != 0) {
         float f = Math.max(0.05F, var2);
         float f1 = -Float.MAX_VALUE;

         for (float f2 : var1) {
            float f3 = f2 / f;
            if (this.isFinite(f3)) {
               f1 = Math.max(f1, f3);
            }
         }

         if (!this.isFinite(f1)) {
            return null;
         }

         float f4 = 0.0F;
         float[] afloat = new float[var1.length];

         for (int i = 0; i < var1.length; i++) {
            float f5 = (float)Math.exp(var1[i] / f - f1);
            afloat[i] = f5;
            f4 += f5;
         }

         if (this.isFinite(f4) && !(f4 <= 0.0F)) {
            for (int j = 0; j < afloat.length; j++) {
               afloat[j] /= f4;
            }

            return afloat;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public int EnchantItemSpec(float[] var1) {
      int i = 0;
      float f = -Float.MAX_VALUE;

      for (int j = 0; j < var1.length; j++) {
         if (var1[j] > f) {
            f = var1[j];
            i = j;
         }
      }

      return i;
   }

   public int[] ColorAnimator(float[] var1, int var2) {
      int i = Math.min(Math.max(1, var2), var1.length);
      int[] aint = new int[i];
      float[] afloat = new float[i];

      for (int j = 0; j < i; j++) {
         aint[j] = -1;
         afloat[j] = -Float.MAX_VALUE;
      }

      for (int l = 0; l < var1.length; l++) {
         float f = var1[l];
         if (!(f <= afloat[i - 1])) {
            int k;
            for (k = i - 1; k > 0 && f > afloat[k - 1]; k--) {
               afloat[k] = afloat[k - 1];
               aint[k] = aint[k - 1];
            }

            afloat[k] = f;
            aint[k] = l;
         }
      }

      for (int i1 = 0; i1 < i; i1++) {
         if (aint[i1] < 0) {
            aint[i1] = 0;
         }
      }

      return aint;
   }

   public boolean StringCodec(float[] var1) {
      if (var1 == null) {
         return false;
      }

      for (float f : var1) {
         if (!this.isFinite(f)) {
            return false;
         }
      }

      return true;
   }

   public boolean isFinite(float var1) {
      return !Float.isNaN(var1) && !Float.isInfinite(var1);
   }

   public float ItemSpec(float var1, float var2, float var3) {
      if (var2 > var3) {
         float f = var2;
         var2 = var3;
         var3 = f;
      }

      return Math.max(var2, Math.min(var3, var1));
   }

   public float MediaTrackInfo(float var1) {
      if (var1 >= 0.0F) {
         float f1 = (float)Math.exp(-var1);
         return 1.0F / (1.0F + f1);
      } else {
         float f = (float)Math.exp(var1);
         return f / (1.0F + f);
      }
   }

   public float tanh(float var1) {
      return (float)Math.tanh(var1);
   }

   public float[] FileLogger(float[] var1) {
      float[] afloat = new float[var1.length];

      for (int i = 0; i < var1.length; i++) {
         float f = var1[i];
         afloat[i] = 0.5F * f * (1.0F + (float)Math.tanh(Math.sqrt(0.6366197723675814) * (f + 0.044715F * f * f * f)));
      }

      return afloat;
   }

   public boolean EventPushOutOfBlocks(String var1) {
      if (var1 == null) {
         return false;
      }

      for (int i = 0; i < var1.length(); i++) {
         char c0 = var1.charAt(i);
         if (c0 != '\ufeff' && !Character.isWhitespace(c0)) {
            return c0 == '{';
         }
      }

      return false;
   }
}
