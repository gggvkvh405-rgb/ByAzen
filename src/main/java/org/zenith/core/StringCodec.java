package org.zenith.core;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.imageio.ImageIO;

public final class StringCodec {
   public static final int LocaleEntry = 10000;
   public static final int Translator = 30000;
   public static final long ModuleManager = 800L;
   public static final long CloudApi = 120000L;

   public static String on23(BufferedImage var0, String var1, String var2) throws Exception {
      return on23(var0, var1, var2, 0, 0);
   }

   public static String on23(BufferedImage var0, String var1, String var2, int var3, int var4) throws Exception {
      if (var0 != null && var1 != null && !var1.isBlank()) {
         String s = ItemSpec(var2);
         long i = System.currentTimeMillis();
         String s1 = on23(var0);
         String s2 = "key=" + encode(var1.trim()) + "&method=base64&body=" + encode(s1);
         if (var3 > 0) {
            s2 = s2 + "&min_len=" + var3;
         }

         if (var4 > 0) {
            s2 = s2 + "&max_len=" + var4;
         }

         String s3 = UiAnimation(s + "in.php", s2);
         FileLogger.log("in.php imageBase64=" + s1.length() + " -> " + FileLogger.trim(s3));
         if (s3 != null && s3.startsWith("OK|")) {
            String[] astring = s3.split("\\|", 2);
            if (astring.length >= 2 && !astring[1].isBlank()) {
               String s4 = astring[1].trim();
               String s5 = s + "res.php?key=" + encode(var1.trim()) + "&action=get&id=" + encode(s4);
               long j = i + 120000L;
               int k = 0;

               while (System.currentTimeMillis() < j) {
                  Thread.sleep(800L);
                  String s6 = get(s5);
                  k++;
                  if (s6 != null) {
                     if (s6.startsWith("OK|")) {
                        String[] astring1 = s6.split("\\|", 2);
                        String s7 = astring1.length < 2 ? null : astring1[1].trim();
                        FileLogger.log("res.php answer='" + s7 + "' polls=" + k + " took=" + (System.currentTimeMillis() - i) + "ms");
                        return s7;
                     }

                     if (!s6.contains("CAPCHA_NOT_READY")) {
                        FileLogger.log("res.php error poll=" + k + " -> " + FileLogger.trim(s6));
                        return null;
                     }
                  }
               }

               FileLogger.log("res.php timeout polls=" + k + " took=" + (System.currentTimeMillis() - i) + "ms");
               return null;
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

   public static String ItemSpec(String var0) {
      if (var0 != null && !var0.isBlank()) {
         URI uri = URI.create(var0.trim());
         String s = uri.getScheme();
         if (!"http".equalsIgnoreCase(s) && !"https".equalsIgnoreCase(s)) {
            throw new IllegalArgumentException("Captcha API URL must use HTTP or HTTPS");
         }

         String s1 = uri.toString();
         return s1.endsWith("/") ? s1 : s1 + "/";
      } else {
         throw new IllegalArgumentException("Captcha API URL is empty");
      }
   }

   public static String on23(BufferedImage var0) throws Exception {
      ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
      if (!ImageIO.write(var0, "png", bytearrayoutputstream)) {
         throw new IllegalStateException("PNG encoder is unavailable");
      } else {
         return Base64.getEncoder().encodeToString(bytearrayoutputstream.toByteArray());
      }
   }

   public static String encode(String var0) {
      return URLEncoder.encode(var0, StandardCharsets.UTF_8);
   }

   public static String UiAnimation(String var0, String var1) throws Exception {
      HttpURLConnection httpurlconnection = TextScanner(var0);
      httpurlconnection.setRequestMethod("POST");
      httpurlconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
      httpurlconnection.setDoOutput(true);

      try (OutputStream outputstream = httpurlconnection.getOutputStream()) {
         outputstream.write(var1.getBytes(StandardCharsets.UTF_8));
      }

      return on23(httpurlconnection);
   }

   public static String get(String var0) throws Exception {
      HttpURLConnection httpurlconnection = TextScanner(var0);
      httpurlconnection.setRequestMethod("GET");
      return on23(httpurlconnection);
   }

   public static HttpURLConnection TextScanner(String var0) throws Exception {
      HttpURLConnection httpurlconnection = (HttpURLConnection)URI.create(var0).toURL().openConnection();
      httpurlconnection.setConnectTimeout(10000);
      httpurlconnection.setReadTimeout(30000);
      httpurlconnection.setRequestProperty("Accept", "text/plain");
      return httpurlconnection;
   }

   public static String on23(HttpURLConnection var0) throws Exception {
      Object object;
      try {
         int i = var0.getResponseCode();
         InputStream inputstream = i >= 400 ? var0.getErrorStream() : var0.getInputStream();
         if (inputstream != null) {
            InputStream inputstream1 = inputstream;

            String s1;
            try {
               String s = new String(inputstream1.readAllBytes(), StandardCharsets.UTF_8).trim();
               s1 = i >= 400 ? null : s;
            } catch (Throwable throwable1) {
               if (inputstream != null) {
                  try {
                     inputstream1.close();
                  } catch (Throwable throwable) {
                     throwable1.addSuppressed(throwable);
                  }
               }

               throw throwable1;
            }

            if (inputstream != null) {
               inputstream.close();
            }

            return s1;
         }

         object = null;
      } finally {
         var0.disconnect();
      }

      return (String)object;
   }
}
