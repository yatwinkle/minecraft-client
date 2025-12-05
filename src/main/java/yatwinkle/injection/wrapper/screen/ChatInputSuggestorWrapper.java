package yatwinkle.injection.wrapper.screen;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import yatwinkle.client.helper.MinecraftInstances;
import yatwinkle.client.service.command.CommandManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ChatInputSuggestorWrapper implements MinecraftInstances {

    public static boolean onRefresh(String text, Consumer<CompletableFuture<Suggestions>> suggestionSetter, Runnable showMethod) {
        if (text.startsWith(CommandManager.PREFIX)) {
            int lastSpaceIndex = text.lastIndexOf(' ');
            int start = (lastSpaceIndex >= 0) ? lastSpaceIndex + 1 : CommandManager.PREFIX.length();

            List<String> suggestions = CommandManager.get().getSuggestions(text);

            if (!suggestions.isEmpty()) {
                SuggestionsBuilder builder = new SuggestionsBuilder(text, text.toLowerCase(), start);

                for (String suggestion : suggestions) {
                    builder.suggest(suggestion);
                }

                suggestionSetter.accept(builder.buildFuture());
                showMethod.run();
                return true;
            }
        }
        return false;
    }
}
