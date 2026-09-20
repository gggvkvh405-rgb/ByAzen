package rtx.byazen.utils.profile;
import fun.shape.profile.Profile;

public final class ProfileIdentity {
    private ProfileIdentity() {
    }

    public static int uid() {
        return Math.max(0, Profile.getUid());
    }

    public static String username(String string) {
        String string2 = Profile.getUsername();
        if (string2 != null && !string2.isBlank() && !"Guest".equalsIgnoreCase(string2)) {
            return string2;
        }
        return string;
    }

    public static String avatarUrl() {
        String string = Profile.getAvatarUrl();
        if (string != null && !string.isBlank() && ProfileIdentity.usable(string)) {
            return string;
        }
        // Old builds fell back to a dead "kimiko" backend address, so the avatar never loaded
        // and the GUI showed an empty/broken square. Better to return nothing - the Discord
        // avatar (which really resolves) is used instead, otherwise a clean placeholder is drawn.
        return null;
    }

    private static boolean usable(String string) {
        try {
            java.net.URI uRI = java.net.URI.create(string.trim());
            String string2 = uRI.getHost();
            String string3 = uRI.getScheme();
            if (string2 == null || string3 == null) {
                return false;
            }
            if (!string3.equalsIgnoreCase("http") && !string3.equalsIgnoreCase("https")) {
                return false;
            }
            // a host name may not contain underscores - such an address can never resolve
            return !string2.contains("_") && string2.contains(".");
        }
        catch (Throwable throwable) {
            return false;
        }
    }
}

