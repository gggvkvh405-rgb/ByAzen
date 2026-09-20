package project.weye;

public class Session {
   public static native String getUsername();

   public static native String getRole();

   public static native String getExpirationDate();

   public static native String getUid();

   public static void loadWEye() {
      System.loadLibrary("weye-session");
   }
}
