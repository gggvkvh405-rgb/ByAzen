package org.zenith.core;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.apache.commons.lang3.RandomStringUtils;
import org.lwjgl.BufferUtils;

public class AvatarTexture {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final TextureIdFactory var02;
   public int int187 = 0;

   public AvatarTexture(BufferedImage var1) {
      this.var02 = new TextureIdFactory("shadow_" + RandomStringUtils.randomAlphanumeric(8));

      try {
         ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
         ImageIO.write(var1, "png", bytearrayoutputstream);
         byte[] abyte = bytearrayoutputstream.toByteArray();
         ByteBuffer bytebuffer = BufferUtils.createByteBuffer(abyte.length).put(abyte);
         bytebuffer.flip();
         NativeImageBackedTexture nativeimagebackedtexture = new NativeImageBackedTexture(() -> "Zenith avatar", NativeImage.read(bytebuffer));
         ClientProvider.minecraftClient3.getTextureManager().registerTexture(this.var02.var14340(), nativeimagebackedtexture);
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public void reset() {
      this.int187 = 0;
   }

   public boolean call152() {
      return ++this.int187 > 300;
   }

   public void destroy() {
      ClientProvider.minecraftClient3.getTextureManager().destroyTexture(this.var02.var14340());
   }
}
