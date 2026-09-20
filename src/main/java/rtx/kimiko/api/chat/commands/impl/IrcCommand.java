package rtx.kimiko.api.chat.commands.impl;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import rtx.kimiko.api.chat.commands.Command;
import rtx.kimiko.api.chat.commands.CommandManager;
import rtx.kimiko.api.chat.commands.helpers.CommandDividers;
import rtx.kimiko.api.chat.irc.IrcClient;
import rtx.kimiko.api.chat.irc.IrcPrefix;
import rtx.kimiko.api.modules.impl.Utils.Irc;

public final class IrcCommand
extends Command {
    public IrcCommand() {
        super("irc", "IRC chat: send messages and pick your prefix", new String[0]);
    }

    @Override
    public void execute(String string, String[] stringArray) {
        if (stringArray.length == 0) {
            this.usage();
            return;
        }
        String string2 = stringArray[0].toLowerCase();
        if (string2.equals("prefix")) {
            this.handlePrefix(stringArray);
            return;
        }
        if (string2.equals("block")) {
            this.handleBlock(stringArray);
            return;
        }
        if (string2.equals("unblock")) {
            this.handleUnblock(stringArray);
            return;
        }
        if (string2.equals("blocklist")) {
            this.handleBlocklist();
            return;
        }
        Irc irc = Irc.getInstance();
        if (irc == null || !irc.isEnabled()) {
            this.logDirect("Enable the IRC module first.", Formatting.RED);
            return;
        }
        if (string2.equals("online")) {
            this.logDirect("Online: " + IrcClient.INSTANCE.online() + (IrcClient.INSTANCE.isConnected() ? "" : " (bridge offline)"));
            return;
        }
        if (string2.equals("pm")) {
            this.handlePm(stringArray);
            return;
        }
        String string3 = String.join((CharSequence)" ", stringArray).trim();
        if (string3.isEmpty()) {
            this.logDirect("Nothing to send.", Formatting.RED);
            return;
        }
        IrcClient.INSTANCE.send(string3);
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("IRC chat over the local bridge.", "Usage:", "> irc <message>", "> irc pm <name> <message>", "> irc block <name>", "> irc unblock <name>", "> irc blocklist", "> irc prefix", "> irc prefix list", "> irc prefix <id>", "> irc online");
    }

    @Override
    public Stream<String> tabComplete(String string2, String[] stringArray) {
        if (stringArray.length == 1) {
            return Stream.of("pm", "block", "unblock", "blocklist", "prefix", "online").filter(string -> string.startsWith(stringArray[0].toLowerCase()));
        }
        if (stringArray.length == 2 && stringArray[0].equalsIgnoreCase("prefix")) {
            Stream<String> stream = Stream.concat(Stream.of("list"), Arrays.stream(IrcPrefix.ids()));
            return stream.filter(string -> string.startsWith(stringArray[1].toLowerCase()));
        }
        if (stringArray.length == 2 && stringArray[0].equalsIgnoreCase("unblock")) {
            return IrcClient.INSTANCE.blockedNames().stream().filter(string -> string.startsWith(stringArray[1].toLowerCase()));
        }
        return Stream.empty();
    }

    private void handlePrefix(String[] stringArray) {
        if (stringArray.length == 1) {
            IrcPrefix ircPrefix = IrcClient.INSTANCE.prefix();
            MutableText mutableText = IrcClient.brand().append((Text)Text.literal((String)"Your prefix: ").formatted(Formatting.GRAY));
            mutableText.append((Text)(ircPrefix.isNone() ? Text.literal((String)"none").formatted(Formatting.DARK_GRAY) : ircPrefix.component()));
            this.logDirectRaw(mutableText);
            this.logDirect("Use \"irc prefix list\" to see all, \"irc prefix <id>\" to set one.");
            return;
        }
        if (stringArray[1].equalsIgnoreCase("list")) {
            this.renderPrefixList();
            return;
        }
        IrcPrefix ircPrefix = IrcPrefix.fromId(stringArray[1]);
        if (ircPrefix.isNone() && !stringArray[1].equalsIgnoreCase("none")) {
            this.logDirect("Unknown prefix '" + stringArray[1] + "'. Use irc prefix list.", Formatting.RED);
            return;
        }
        IrcClient.INSTANCE.setPrefix(ircPrefix);
        MutableText mutableText = IrcClient.brand().append((Text)Text.literal((String)"\u0423\u0441\u043f\u0435\u0448\u043d\u043e \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043b\u0435\u043d \u043f\u0440\u0435\u0444\u0438\u043a\u0441 ").formatted(Formatting.WHITE));
        mutableText.append((Text)(ircPrefix.isNone() ? Text.literal((String)"none").formatted(Formatting.DARK_GRAY) : ircPrefix.component()));
        this.logDirectRaw(mutableText);
    }

    private void handleBlock(String[] stringArray) {
        if (stringArray.length < 2) {
            this.logDirect("Usage: irc block <name>", Formatting.RED);
            return;
        }
        String string = stringArray[1].trim();
        if (string.isEmpty()) {
            this.logDirect("Specify who to block.", Formatting.RED);
            return;
        }
        if (string.equalsIgnoreCase(IrcClient.INSTANCE.effectiveName())) {
            this.logDirect("You can't block yourself.", Formatting.RED);
            return;
        }
        if (IrcClient.INSTANCE.block(string)) {
            this.logDirect("Blocked " + string + ". You won't see their messages.");
        } else {
            this.logDirect(string + " is already blocked.", Formatting.YELLOW);
        }
    }

    private void handleBlocklist() {
        List<String> list = IrcClient.INSTANCE.blockedNames();
        if (list.isEmpty()) {
            this.logDirect("Your blocklist is empty.");
            return;
        }
        String string = "IRC BLOCKLIST";
        int n = CommandDividers.calcLineCountForContent((String)"IRC BLOCKLIST", list);
        this.logDirectRaw(CommandDividers.header((String)"IRC BLOCKLIST", (int)n));
        String string2 = CommandManager.get().getPrefix();
        for (String string3 : list) {
            MutableText mutableText = Text.literal((String)string3).styled(style -> style.withColor(Formatting.WHITE).withHoverEvent((HoverEvent)new HoverEvent.ShowText((Text)Text.literal((String)("\u041d\u0430\u0436\u043c\u0438, \u0447\u0442\u043e\u0431\u044b \u0440\u0430\u0437\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u0442\u044c " + string3)).formatted(Formatting.GRAY))).withClickEvent((ClickEvent)new ClickEvent.RunCommand(string2 + "irc unblock " + string3)));
            this.logDirectRaw(mutableText);
        }
        this.logDirectRaw(CommandDividers.footer((String)"IRC BLOCKLIST", (int)n));
        MutableText mutableText = Text.literal((String)"\u041d\u0430\u0436\u043c\u0438 \u043d\u0430 \u043d\u0438\u043a, \u0447\u0442\u043e\u0431\u044b \u0440\u0430\u0437\u0431\u043b\u043e\u043a\u0438\u0440\u043e\u0432\u0430\u0442\u044c").styled(style -> style.withColor(Formatting.GRAY).withItalic(Boolean.valueOf(true)));
        this.logDirectRaw(mutableText);
    }

    private void handleUnblock(String[] stringArray) {
        if (stringArray.length < 2) {
            this.logDirect("Usage: irc unblock <name>", Formatting.RED);
            return;
        }
        String string = stringArray[1].trim();
        if (string.isEmpty()) {
            this.logDirect("Specify who to unblock.", Formatting.RED);
            return;
        }
        if (IrcClient.INSTANCE.unblock(string)) {
            this.logDirect("Unblocked " + string + ".");
        } else {
            this.logDirect(string + " isn't blocked.", Formatting.YELLOW);
        }
    }

    private void renderPrefixList() {
        String string = CommandManager.get().getPrefix();
        String string2 = "IRC PREFIXES";
        List<String> list = Arrays.stream(IrcPrefix.values()).filter(ircPrefix -> !ircPrefix.isNone()).map(IrcPrefix::display).toList();
        int n = CommandDividers.calcLineCountForContent((String)"IRC PREFIXES", list);
        this.logDirectRaw(CommandDividers.header((String)"IRC PREFIXES", (int)n));
        for (IrcPrefix ircPrefix2 : IrcPrefix.values()) {
            if (ircPrefix2.isNone()) continue;
            String string3 = ircPrefix2.display().trim();
            MutableText mutableText = Text.empty().styled(style -> style.withHoverEvent((HoverEvent)new HoverEvent.ShowText((Text)Text.literal((String)("\u041d\u0430\u0436\u043c\u0438, \u0447\u0442\u043e\u0431\u044b \u0432\u044b\u0431\u0440\u0430\u0442\u044c: " + string3)).formatted(Formatting.GRAY))).withClickEvent((ClickEvent)new ClickEvent.RunCommand(string + "irc prefix " + ircPrefix2.id())));
            mutableText.append(ircPrefix2.component());
            this.logDirectRaw(mutableText);
        }
        this.logDirectRaw(CommandDividers.footer((String)"IRC PREFIXES", (int)n));
        MutableText mutableText = Text.literal((String)"\u041d\u0430\u0436\u043c\u0438 \u043d\u0430 \u043f\u0440\u0435\u0444\u0438\u043a\u0441, \u0447\u0442\u043e\u0431\u044b \u0432\u044b\u0431\u0440\u0430\u0442\u044c \u0435\u0433\u043e").styled(style -> style.withColor(Formatting.GRAY).withItalic(Boolean.valueOf(true)));
        this.logDirectRaw(mutableText);
    }

    private void handlePm(String[] stringArray) {
        if (stringArray.length < 3) {
            this.logDirect("Usage: irc pm <name> <message>", Formatting.RED);
            return;
        }
        String string = stringArray[1].trim();
        if (string.isEmpty()) {
            this.logDirect("Specify who to message.", Formatting.RED);
            return;
        }
        if (string.equalsIgnoreCase(IrcClient.INSTANCE.effectiveName())) {
            this.logDirect("You can't private-message yourself.", Formatting.RED);
            return;
        }
        String string2 = String.join((CharSequence)" ", Arrays.copyOfRange(stringArray, 2, stringArray.length)).trim();
        if (string2.isEmpty()) {
            this.logDirect("Nothing to send.", Formatting.RED);
            return;
        }
        IrcClient.INSTANCE.send(string2, string);
    }
}

