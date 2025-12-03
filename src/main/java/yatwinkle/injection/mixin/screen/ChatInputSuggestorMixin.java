package yatwinkle.injection.mixin.screen;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yatwinkle.client.helper.MinecraftInstances;
import yatwinkle.client.service.command.CommandManager;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin implements MinecraftInstances {

    @Shadow
    private CompletableFuture<Suggestions> pendingSuggestions;
    @Shadow public abstract void show(boolean narrate);
    @Shadow @Final
    TextFieldWidget textField;

    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    private void onRefresh(CallbackInfo ci) {
        String text = this.textField.getText();

        if (text.startsWith(CommandManager.PREFIX)) {
            int lastSpaceIndex = text.lastIndexOf(' ');
            int start;

            if (lastSpaceIndex >= 0) {
                start = lastSpaceIndex + 1;
            } else {
                start = CommandManager.PREFIX.length();
            }

            SuggestionsBuilder builder = new SuggestionsBuilder(text, text.toLowerCase(), start);
            var suggestions = CommandManager.get().getSuggestions(text);

            if (!suggestions.isEmpty()) {
                for (String suggestion : suggestions) {
                    builder.suggest(suggestion);
                }

                this.pendingSuggestions = builder.buildFuture();
                this.show(true);
                ci.cancel();
            }
        }
    }

}