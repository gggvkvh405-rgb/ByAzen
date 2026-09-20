package rtx.kimiko.api.events.funtime;

public final class FunTimeApiException extends Exception {
    private final int status;
    private final String type;

    public FunTimeApiException(int n, String string, String string2) {
        super(string2);
        this.status = n;
        this.type = string == null ? "" : string;
    }

    public String type() {
        return this.type;
    }

    public int status() {
        return this.status;
    }

    public boolean isRateLimited() {
        return this.status == 429;
    }

    public boolean isUnauthorized() {
        return this.status == 401;
    }
}
