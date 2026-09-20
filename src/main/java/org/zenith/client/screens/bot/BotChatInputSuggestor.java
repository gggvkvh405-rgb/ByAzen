package org.zenith.client.screens.bot;

import com.google.common.base.Strings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.command.CommandSource;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.zenith.base.bot.net.BotCommandSource;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.hud.SearchBox;

public final class BotChatInputSuggestor {
   public static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
   public static final Style ERROR_STYLE = Style.EMPTY.withColor(Formatting.RED);
   public static final Style INFO_STYLE = Style.EMPTY.withColor(Formatting.GRAY);
   public static final List<Style> HIGHLIGHT_STYLES = Stream.of(
         Formatting.AQUA, Formatting.YELLOW, Formatting.GREEN, Formatting.LIGHT_PURPLE, Formatting.GOLD
      )
      .<Style>map(Style.EMPTY::withColor)
      .toList();
   public static final int IN_WINDOW_INDEX_OFFSET = 1;
   public static final int MAX_SUGGESTION_SIZE = 10;
   public static final int COLOR = -805306368;
   public final Screen owner;
   public final SearchBox textField;
   public final TextRenderer textRenderer;
   public final Supplier<BotPlayHandler> handlerSupplier;
   public final IntUnaryOperator characterX;
   public final IntSupplier fieldTop;
   public final List<OrderedText> messages = new ArrayList<>();
   public int x;
   public int width;
   public ParseResults<CommandSource> parse;
   public CompletableFuture<Suggestions> pendingSuggestions;
   public BotChatInputSuggestor_SuggestionWindow window;
   public boolean windowActive;
   public boolean completingSuggestions;
   public String suggestion;
   public String lastText = "";
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public BotChatInputSuggestor(Screen var1, SearchBox var2, TextRenderer var3, Supplier<BotPlayHandler> var4, IntUnaryOperator var5, IntSupplier var6) {
      this.owner = var1;
      this.textField = var2;
      this.textRenderer = var3;
      this.handlerSupplier = var4;
      this.characterX = var5;
      this.fieldTop = var6;
   }

   public void setWindowActive(boolean var1) {
      this.windowActive = var1;
      if (!var1) {
         this.window = null;
      }
   }

   public void update(String var1) {
      String s = this.textField.getText();
      if (!s.equals(this.lastText)) {
         this.lastText = s;
         this.setWindowActive(!s.equals(var1));
         this.refresh();
      }
   }

   public void reset(String var1) {
      this.lastText = var1;
      this.parse = null;
      this.pendingSuggestions = null;
      this.suggestion = null;
      this.messages.clear();
      this.setWindowActive(false);
      this.refresh();
   }

   public String getSuggestion() {
      return this.suggestion;
   }

   public boolean isOpen() {
      return this.window != null;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      boolean flag = this.window != null;
      if (flag && this.window.keyPressed(var1)) {
         return true;
      } else if (var1 == 258) {
         this.show();
         return this.window != null;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.window != null && this.window.mouseScrolled(MathHelper.clamp(var1, -1.0, 1.0), var3, var5);
   }

   public boolean mouseClicked(double var1, double var3) {
      return this.window != null && this.window.mouseClicked((int)var1, (int)var3);
   }

   public void show() {
      if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
         Suggestions suggestions = this.pendingSuggestions.join();
         if (!suggestions.isEmpty()) {
            int i = 0;

            for (Suggestion suggestionx : suggestions.getList()) {
               i = Math.max(i, this.textRenderer.getWidth(suggestionx.getText()));
            }

            int j = MathHelper.clamp(this.characterX.applyAsInt(suggestions.getRange().getStart()), 0, Math.max(0, this.owner.width - i));
            this.window = new BotChatInputSuggestor_SuggestionWindow(this, j, i, this.sortSuggestions(suggestions));
         }
      }
   }

   public void clearWindow() {
      this.window = null;
   }

   public List<Suggestion> sortSuggestions(Suggestions var1) {
      String s = this.textField.getText().substring(0, this.textField.var14348());
      int i = getStartOfCurrentWord(s);
      String s1 = s.substring(i).toLowerCase(Locale.ROOT);
      List<Suggestion> arraylist = new ArrayList<>();
      ArrayList arraylist1 = new ArrayList();

      for (Suggestion suggestionx : var1.getList()) {
         if (!suggestionx.getText().startsWith(s1) && !suggestionx.getText().startsWith("minecraft:" + s1)) {
            arraylist1.add(suggestionx);
         } else {
            arraylist.add(suggestionx);
         }
      }

      arraylist.addAll(arraylist1);
      return arraylist;
   }

   public void refresh() {
      BotPlayHandler botplayhandler = this.handlerSupplier.get();
      String s = this.textField.getText();
      if (this.parse != null && !this.parse.getReader().getString().equals(s)) {
         this.parse = null;
      }

      if (!this.completingSuggestions) {
         this.suggestion = null;
         this.window = null;
      }

      this.messages.clear();
      StringReader stringreader = new StringReader(s);
      boolean flag = stringreader.canRead() && stringreader.peek() == '/';
      if (flag) {
         stringreader.skip();
      }

      int i = this.textField.var14348();
      if (flag && botplayhandler != null) {
         CommandDispatcher commanddispatcher = botplayhandler.getCommandDispatcher();
         BotCommandSource botcommandsource = botplayhandler.getCommandSource();
         if (this.parse == null) {
            this.parse = commanddispatcher.parse(stringreader, botcommandsource);
         }

         if (i >= 1 && (this.window == null || !this.completingSuggestions)) {
            this.pendingSuggestions = commanddispatcher.getCompletionSuggestions(this.parse, i);
            this.pendingSuggestions.thenRun(() -> {
               CompletableFuture completablefuture = this.pendingSuggestions;
               if (completablefuture != null && completablefuture.isDone()) {
                  minecraftClient3.execute(() -> {
                     if (this.pendingSuggestions == completablefuture) {
                        this.showCommandSuggestions();
                     }
                  });
               }
            });
         }
      } else {
         String s1 = s.substring(0, i);
         int j = getStartOfCurrentWord(s1);
         Object object = botplayhandler != null ? botplayhandler.getCommandSource().getChatSuggestions() : List.of();
         this.pendingSuggestions = CommandSource.suggestMatching((Iterable)object, new SuggestionsBuilder(s1, j));
      }
   }

   public static int getStartOfCurrentWord(String var0) {
      if (Strings.isNullOrEmpty(var0)) {
         return 0;
      }

      int i = 0;
      Matcher matcher = WHITESPACE_PATTERN.matcher(var0);

      while (matcher.find()) {
         i = matcher.end();
      }

      return i;
   }

   public static OrderedText formatException(CommandSyntaxException var0) {
      if (var0 == null) {
         var0 = CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create();
      }

      Text text = Texts.toText(var0.getRawMessage());
      String s = var0.getContext();
      return s == null ? text.asOrderedText() : Text.translatable("command.context.parse_error", new Object[]{text, var0.getCursor(), s}).asOrderedText();
   }

   public void showCommandSuggestions() {
      if (this.pendingSuggestions != null && this.parse != null) {
         boolean flag = false;
         if (this.textField.var14348() == this.textField.getText().length()) {
            if (this.pendingSuggestions.join().isEmpty() && !this.parse.getExceptions().isEmpty()) {
               int i = 0;

               for (Entry<CommandNode<CommandSource>, CommandSyntaxException> entry : this.parse.getExceptions().entrySet()) {
                  CommandSyntaxException commandsyntaxexception = entry.getValue();
                  if (commandsyntaxexception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                     i++;
                  } else {
                     this.messages.add(formatException(commandsyntaxexception));
                  }
               }

               if (i > 0) {
                  this.messages.add(formatException(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create()));
               }
            } else if (this.parse.getReader().canRead()) {
               flag = true;
            }
         }

         this.x = 0;
         this.width = this.owner.width;
         if (this.messages.isEmpty() && !this.showUsages() && flag) {
            CommandSyntaxException exception = this.parse
               .getExceptions()
               .values()
               .stream()
               .findFirst()
               .orElseGet(() -> CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create());
            this.messages.add(formatException(exception));
         }

         this.window = null;
         if (this.windowActive) {
            this.show();
         }
      }
   }

   public boolean showUsages() {
      BotPlayHandler botplayhandler = this.handlerSupplier.get();
      if (botplayhandler != null && this.parse != null) {
         CommandContextBuilder commandcontextbuilder = this.parse.getContext();
         SuggestionContext suggestioncontext = commandcontextbuilder.findSuggestionContext(this.textField.var14348());
         Map<CommandNode<?>, String> map = botplayhandler.getCommandDispatcher().getSmartUsage(suggestioncontext.parent, botplayhandler.getCommandSource());
         List<OrderedText> arraylist = new ArrayList<>();
         int i = 0;

         for (Entry<CommandNode<?>, String> entry : map.entrySet()) {
            if (!(entry.getKey() instanceof LiteralCommandNode)) {
               arraylist.add(OrderedText.styledForwardsVisitedString(entry.getValue(), INFO_STYLE));
               i = Math.max(i, this.textRenderer.getWidth(entry.getValue()));
            }
         }

         if (!arraylist.isEmpty()) {
            this.messages.addAll(arraylist);
            this.x = MathHelper.clamp(this.characterX.applyAsInt(suggestioncontext.startPos), 0, Math.max(0, this.owner.width - i));
            this.width = i;
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public OrderedText provideRenderText(String var1, int var2) {
      return this.parse != null ? highlight(this.parse, var1, var2) : OrderedText.styledForwardsVisitedString(var1, Style.EMPTY);
   }

   public static String getSuggestionSuffix(String var0, String var1) {
      return var1.startsWith(var0) ? var1.substring(var0.length()) : null;
   }

   public static OrderedText highlight(ParseResults<CommandSource> var0, String var1, int var2) {
      ArrayList arraylist = new ArrayList();
      int i = 0;
      int j = -1;
      CommandContextBuilder commandcontextbuilder = var0.getContext().getLastChild();

      for (ParsedArgument parsedargument : (Iterable<ParsedArgument>)(Iterable<?>)commandcontextbuilder.getArguments().values()) {
         if (++j >= HIGHLIGHT_STYLES.size()) {
            j = 0;
         }

         int k = Math.max(parsedargument.getRange().getStart() - var2, 0);
         if (k >= var1.length()) {
            break;
         }

         int l = Math.min(parsedargument.getRange().getEnd() - var2, var1.length());
         if (l > 0) {
            arraylist.add(OrderedText.styledForwardsVisitedString(var1.substring(i, k), INFO_STYLE));
            arraylist.add(OrderedText.styledForwardsVisitedString(var1.substring(k, l), HIGHLIGHT_STYLES.get(j)));
            i = l;
         }
      }

      if (var0.getReader().canRead()) {
         int i1 = Math.max(var0.getReader().getCursor() - var2, 0);
         if (i1 < var1.length()) {
            int j1 = Math.min(i1 + var0.getReader().getRemainingLength(), var1.length());
            arraylist.add(OrderedText.styledForwardsVisitedString(var1.substring(i, i1), INFO_STYLE));
            arraylist.add(OrderedText.styledForwardsVisitedString(var1.substring(i1, j1), ERROR_STYLE));
            i = j1;
         }
      }

      arraylist.add(OrderedText.styledForwardsVisitedString(var1.substring(i), INFO_STYLE));
      return OrderedText.concat(arraylist);
   }

   public void render(DrawContext var1, int var2, int var3) {
      if (this.window != null) {
         this.window.render(var1, var2, var3);
      } else {
         this.renderMessages(var1);
      }
   }

   public void renderMessages(DrawContext var1) {
      int i = 0;

      for (OrderedText orderedtext : this.messages) {
         int j = this.fieldTop.getAsInt() - 13 - 12 * i;
         var1.fill(this.x - 1, j, this.x + this.width + 1, j + 12, -805306368);
         var1.drawTextWithShadow(this.textRenderer, orderedtext, this.x, j + 2, -1);
         i++;
      }
   }
}
