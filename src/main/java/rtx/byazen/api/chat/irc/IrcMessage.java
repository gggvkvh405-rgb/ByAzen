package rtx.byazen.api.chat.irc;

public record IrcMessage(long id, long timestamp, String user, String prefix, String text, String to) {
    public boolean isPrivate() {
        return to != null && !to.isBlank();
    }
}