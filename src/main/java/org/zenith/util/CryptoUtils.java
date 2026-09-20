package org.zenith.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public final class CryptoUtils {
   public static final int int473 = 256;
   public static final int int474 = 32;
   public static final int int475 = 128;
   public static final int int476 = 12;
   public static final SecureRandom secureRandom = new SecureRandom();
   public static final byte byte2 = 1;
   public static final int int477 = 5000;
   public static final int int478 = 12;

   public static String CloudPoller(String var0, String var1) throws Exception {
      if (var0 == null) {
         throw new IllegalArgumentException("plainText == null");
      }

      if (var1 == null) {
         throw new IllegalArgumentException("password == null");
      }

      byte[] abyte = new byte[16];
      secureRandom.nextBytes(abyte);
      int i = RotationPredictiveStrategy(var1);
      byte[] abyte1 = on23(var1.toCharArray(), abyte, i, 64);
      byte[] abyte2 = Arrays.copyOf(abyte1, 32);
      byte[] abyte3 = Arrays.copyOfRange(abyte1, 32, abyte1.length);
      SecretKeySpec secretkeyspec = new SecretKeySpec(abyte2, "AES");
      byte[] abyte4 = new byte[12];
      secureRandom.nextBytes(abyte4);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      GCMParameterSpec gcmparameterspec = new GCMParameterSpec(128, abyte4);
      cipher.init(1, secretkeyspec, gcmparameterspec);
      byte[] abyte5 = cipher.doFinal(var0.getBytes(StandardCharsets.UTF_8));
      byte[] abyte6 = on23(abyte3, abyte4, abyte5);
      ByteBuffer bytebuffer = ByteBuffer.allocate(2 + abyte.length + 1 + abyte4.length + 4 + abyte6.length);
      bytebuffer.order(ByteOrder.BIG_ENDIAN);
      bytebuffer.put((byte)1);
      bytebuffer.put((byte)abyte.length);
      bytebuffer.put(abyte);
      bytebuffer.put((byte)abyte4.length);
      bytebuffer.put(abyte4);
      bytebuffer.putInt(i);
      bytebuffer.put(abyte6);
      byte[] abyte7 = bytebuffer.array();
      return Base64.getEncoder().encodeToString(abyte7);
   }

   public static String EmoteMetadata(String var0, String var1) throws Exception {
      if (var0 == null) {
         throw new IllegalArgumentException("base64Blob == null");
      }

      if (var1 == null) {
         throw new IllegalArgumentException("password == null");
      }

      byte[] abyte = Base64.getDecoder().decode(var0);
      ByteBuffer bytebuffer = ByteBuffer.wrap(abyte).order(ByteOrder.BIG_ENDIAN);
      byte b0 = bytebuffer.get();
      if (b0 != 1) {
         throw new IllegalArgumentException("Unsupported version: " + b0);
      }

      int i = Byte.toUnsignedInt(bytebuffer.get());
      byte[] abyte1 = new byte[i];
      bytebuffer.get(abyte1);
      int j = Byte.toUnsignedInt(bytebuffer.get());
      byte[] abyte2 = new byte[j];
      bytebuffer.get(abyte2);
      int k = bytebuffer.getInt();
      byte[] abyte3 = new byte[bytebuffer.remaining()];
      bytebuffer.get(abyte3);
      byte[] abyte4 = on23(var1.toCharArray(), abyte1, k, 64);
      byte[] abyte5 = Arrays.copyOf(abyte4, 32);
      byte[] abyte6 = Arrays.copyOfRange(abyte4, 32, abyte4.length);
      SecretKeySpec secretkeyspec = new SecretKeySpec(abyte5, "AES");
      byte[] abyte7 = on23(abyte6, abyte2, abyte3);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      GCMParameterSpec gcmparameterspec = new GCMParameterSpec(128, abyte2);
      cipher.init(2, secretkeyspec, gcmparameterspec);
      byte[] abyte8 = cipher.doFinal(abyte7);
      return new String(abyte8, StandardCharsets.UTF_8);
   }

   public static int RotationPredictiveStrategy(String var0) {
      int i = Math.max(1, var0.length());
      int j = 0;
      int k = 0;

      for (int l = 0; l < var0.length(); l++) {
         char c0 = var0.charAt(l);
         j += c0 * (l + 1);
         k ^= c0 << l % 8;
      }

      long j1 = j * 31L ^ k & 255L ^ var0.hashCode();
      int i1 = 20000 + (int)(Math.abs(j1) % 100000L);
      return Math.max(10000, Math.min(200000, i1));
   }

   public static byte[] on23(char[] var0, byte[] var1, int var2, int var3) {
      try {
         PBEKeySpec pbekeyspec = new PBEKeySpec(var0, var1, var2, var3 * 8);
         SecretKeyFactory secretkeyfactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
         SecretKey secretkey = secretkeyfactory.generateSecret(pbekeyspec);
         return secretkey.getEncoded();
      } catch (GeneralSecurityException generalsecurityexception) {
         throw new RuntimeException("PBKDF2 failure", generalsecurityexception);
      }
   }

   public static byte[] on23(byte[] var0, byte[] var1, byte[] var2) {
      try {
         MessageDigest messagedigest = MessageDigest.getInstance("SHA-512");
         byte[] abyte = new byte[var2.length];
         int i = 0;

         for (int j = 0; j < var2.length; i++) {
            messagedigest.reset();
            messagedigest.update(var0);
            messagedigest.update((byte)(i & 0xFF));
            messagedigest.update((byte)(i >> 8 & 0xFF));
            messagedigest.update(var1);
            byte[] abyte1 = messagedigest.digest();
            int k = Math.min(abyte1.length, var2.length - j);

            for (int l = 0; l < k; l++) {
               abyte[j + l] = (byte)(var2[j + l] ^ abyte1[l]);
            }

            j += k;
         }

         return abyte;
      } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
         throw new RuntimeException(nosuchalgorithmexception);
      }
   }

   public static byte[] on23(byte[] var0, String var1) throws Exception {
      byte[] abyte = new byte[16];
      secureRandom.nextBytes(abyte);
      int i = RotationPredictiveStrategy(var1);
      byte[] abyte1 = on23(var1.toCharArray(), abyte, i, 64);
      byte[] abyte2 = Arrays.copyOf(abyte1, 32);
      byte[] abyte3 = Arrays.copyOfRange(abyte1, 32, abyte1.length);
      SecretKeySpec secretkeyspec = new SecretKeySpec(abyte2, "AES");
      byte[] abyte4 = new byte[12];
      secureRandom.nextBytes(abyte4);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(1, secretkeyspec, new GCMParameterSpec(128, abyte4));
      byte[] abyte5 = cipher.doFinal(var0);
      byte[] abyte6 = on23(abyte3, abyte4, abyte5);
      ByteBuffer bytebuffer = ByteBuffer.allocate(2 + abyte.length + 1 + abyte4.length + 4 + abyte6.length).order(ByteOrder.BIG_ENDIAN);
      bytebuffer.put((byte)1);
      bytebuffer.put((byte)abyte.length);
      bytebuffer.put(abyte);
      bytebuffer.put((byte)abyte4.length);
      bytebuffer.put(abyte4);
      bytebuffer.putInt(i);
      bytebuffer.put(abyte6);
      byte[] abyte7 = bytebuffer.array();
      Arrays.fill(abyte1, (byte)0);
      Arrays.fill(abyte2, (byte)0);
      Arrays.fill(abyte3, (byte)0);
      Arrays.fill(var0, (byte)0);
      return Base64.getEncoder().encodeToString(abyte7).getBytes(StandardCharsets.UTF_8);
   }

   public static byte[] UiAnimation(byte[] var0, String var1) throws Exception {
      String s = new String(var0, StandardCharsets.UTF_8);
      byte[] abyte = Base64.getDecoder().decode(s);
      ByteBuffer bytebuffer = ByteBuffer.wrap(abyte).order(ByteOrder.BIG_ENDIAN);
      byte b0 = bytebuffer.get();
      if (b0 != 1) {
         throw new IllegalArgumentException("Unsupported version: " + b0);
      }

      int i = Byte.toUnsignedInt(bytebuffer.get());
      byte[] abyte1 = new byte[i];
      bytebuffer.get(abyte1);
      int j = Byte.toUnsignedInt(bytebuffer.get());
      byte[] abyte2 = new byte[j];
      bytebuffer.get(abyte2);
      int k = bytebuffer.getInt();
      byte[] abyte3 = new byte[bytebuffer.remaining()];
      bytebuffer.get(abyte3);
      byte[] abyte4 = on23(var1.toCharArray(), abyte1, k, 64);
      byte[] abyte5 = Arrays.copyOf(abyte4, 32);
      byte[] abyte6 = Arrays.copyOfRange(abyte4, 32, abyte4.length);
      SecretKeySpec secretkeyspec = new SecretKeySpec(abyte5, "AES");
      byte[] abyte7 = on23(abyte6, abyte2, abyte3);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(2, secretkeyspec, new GCMParameterSpec(128, abyte2));
      byte[] abyte8 = cipher.doFinal(abyte7);
      Arrays.fill(abyte4, (byte)0);
      Arrays.fill(abyte5, (byte)0);
      Arrays.fill(abyte6, (byte)0);
      return abyte8;
   }

   public static byte[] Easing(byte[] var0, String var1) throws Exception {
      byte[] abyte = new byte[12];
      secureRandom.nextBytes(abyte);
      byte[] abyte1 = on23(var1.toCharArray(), abyte, 5000, 32);
      SecretKeySpec secretkeyspec = new SecretKeySpec(abyte1, "AES");
      byte[] abyte2 = new byte[12];
      secureRandom.nextBytes(abyte2);
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(1, secretkeyspec, new GCMParameterSpec(128, abyte2));
      byte[] abyte3 = cipher.doFinal(var0);
      ByteBuffer bytebuffer = ByteBuffer.allocate(abyte.length + abyte2.length + abyte3.length);
      bytebuffer.put(abyte);
      bytebuffer.put(abyte2);
      bytebuffer.put(abyte3);
      Arrays.fill(abyte1, (byte)0);
      return bytebuffer.array();
   }

   public static byte[] ColorAnimator(byte[] var0, String var1) throws Exception {
      ByteBuffer bytebuffer = ByteBuffer.wrap(var0);
      byte[] abyte = new byte[12];
      bytebuffer.get(abyte);
      byte[] abyte1 = new byte[12];
      bytebuffer.get(abyte1);
      byte[] abyte2 = new byte[bytebuffer.remaining()];
      bytebuffer.get(abyte2);
      byte[] abyte3 = on23(var1.toCharArray(), abyte, 5000, 32);
      SecretKeySpec secretkeyspec = new SecretKeySpec(abyte3, "AES");
      Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
      cipher.init(2, secretkeyspec, new GCMParameterSpec(128, abyte1));
      byte[] abyte4 = cipher.doFinal(abyte2);
      Arrays.fill(abyte3, (byte)0);
      return abyte4;
   }
}
