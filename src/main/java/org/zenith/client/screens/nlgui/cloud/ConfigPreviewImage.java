package org.zenith.client.screens.nlgui.cloud;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.client.texture.NativeImage;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

public final class ConfigPreviewImage {
   public static final int[] WIDTHS = new int[]{512, 384, 320, 256, 192};
   public static final int MAX_SOURCE_BYTES = 25165824;
   public static final ExecutorService WORKERS = Executors.newSingleThreadExecutor(var0 -> {
      Thread thread = new Thread(var0, "zenith-config-preview");
      thread.setDaemon(true);
      return thread;
   });

   public static CompletableFuture<byte[]> pick() {
      return CompletableFuture.supplyAsync(() -> {
         Path path = openDialog();
         if (path == null) {
            return null;
         }

         try {
            if (Files.size(path) > 25165824L) {
               throw new IllegalArgumentException("Image is larger than 24 MiB");
            } else {
               return encode(Files.readAllBytes(path));
            }
         } catch (IllegalArgumentException illegalargumentexception) {
            throw illegalargumentexception;
         } catch (Exception exception) {
            throw new IllegalArgumentException("Could not read the selected image", exception);
         }
      }, WORKERS);
   }

   public static Path openDialog() {
      MemoryStack memorystack = MemoryStack.stackPush();

      Path path;
      try {
         PointerBuffer pointerbuffer = memorystack.mallocPointer(4);
         pointerbuffer.put(memorystack.UTF8("*.png"));
         pointerbuffer.put(memorystack.UTF8("*.jpg"));
         pointerbuffer.put(memorystack.UTF8("*.jpeg"));
         pointerbuffer.put(memorystack.UTF8("*.bmp"));
         pointerbuffer.flip();
         String s = TinyFileDialogs.tinyfd_openFileDialog("Select a config preview", null, pointerbuffer, "Images (png, jpg, bmp)", false);
         path = s != null && !s.isBlank() ? Path.of(s) : null;
      } catch (Throwable var5) {
         if (memorystack != null) {
            try {
               memorystack.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (memorystack != null) {
         memorystack.close();
      }

      return path;
   }

   public static byte[] encode(byte[] var0) throws Exception {
      NativeImage nativeimage = NativeImage.read(new ByteArrayInputStream(var0));

      byte[] var16;
      label65: {
         byte[] abyte2;
         try {
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            if (i < 1 || j < 1) {
               throw new IllegalArgumentException("Image is empty");
            }

            int k = Math.min(i, j * 16 / 9);
            int l = Math.min(j, i * 9 / 16);
            int i1 = (i - k) / 2;
            int j1 = (j - l) / 2;
            byte[] abyte = null;

            for (int k1 : WIDTHS) {
               int l1 = Math.max(1, k1 * 9 / 16);
               byte[] abyte1 = scaleAndEncode(nativeimage, i1, j1, k, l, k1, l1);
               abyte = abyte1;
               if (abyte1.length <= 262144) {
                  var16 = abyte1;
                  break label65;
               }
            }

            if (abyte == null || abyte.length > 262144) {
               throw new IllegalArgumentException("Image could not be compressed under 256 KiB");
            }

            abyte2 = abyte;
         } catch (Throwable var18) {
            if (nativeimage != null) {
               try {
                  nativeimage.close();
               } catch (Throwable var17) {
                  var18.addSuppressed(var17);
               }
            }

            throw var18;
         }

         if (nativeimage != null) {
            nativeimage.close();
         }

         return abyte2;
      }

      if (nativeimage != null) {
         nativeimage.close();
      }

      return var16;
   }

   public static byte[] scaleAndEncode(NativeImage var0, int var1, int var2, int var3, int var4, int var5, int var6) throws Exception {
      NativeImage nativeimage = new NativeImage(var5, var6, false);

      byte[] abyte;
      try {
         for (int i = 0; i < var6; i++) {
            int j = var2 + i * var4 / var6;
            int k = Math.max(j + 1, var2 + (i + 1) * var4 / var6);

            for (int l = 0; l < var5; l++) {
               int i1 = var1 + l * var3 / var5;
               int j1 = Math.max(i1 + 1, var1 + (l + 1) * var3 / var5);
               nativeimage.setColorArgb(l, i, average(var0, i1, j, j1, k));
            }
         }

         Path path = Files.createTempFile("zenith-preview", ".png");

         try {
            nativeimage.writeTo(path);
            abyte = Files.readAllBytes(path);
         } finally {
            Files.deleteIfExists(path);
         }
      } catch (Throwable var21) {
         try {
            nativeimage.close();
         } catch (Throwable var19) {
            var21.addSuppressed(var19);
         }

         throw var21;
      }

      nativeimage.close();
      return abyte;
   }

   public static int average(NativeImage var0, int var1, int var2, int var3, int var4) {
      long i = 0L;
      long j = 0L;
      long k = 0L;
      long l = 0L;
      int i1 = 0;

      for (int j1 = var2; j1 < var4; j1++) {
         for (int k1 = var1; k1 < var3; k1++) {
            int l1 = var0.getColorArgb(k1, j1);
            i += l1 >>> 24 & 0xFF;
            j += l1 >>> 16 & 0xFF;
            k += l1 >>> 8 & 0xFF;
            l += l1 & 0xFF;
            i1++;
         }
      }

      return i1 == 0 ? -16777216 : (int)(i / i1 << 24) | (int)(j / i1 << 16) | (int)(k / i1 << 8) | (int)(l / i1);
   }
}
