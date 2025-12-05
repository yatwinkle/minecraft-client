package yatwinkle.injection.mixin.screen;

import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yatwinkle.injection.wrapper.screen.ChatInputSuggestorWrapper;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public abstract class ChatInputSuggestorMixin {

    @Shadow private CompletableFuture<Suggestions> pendingSuggestions;
    @Shadow public abstract void show(boolean narrate);
    @Shadow @Final TextFieldWidget textField;

    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    private void onRefresh(CallbackInfo ci) {
        boolean handled = ChatInputSuggestorWrapper.onRefresh(
                this.textField.getText(),
                (future) -> this.pendingSuggestions = future,
                () -> this.show(true)
        );

        if (handled) {
            ci.cancel();
        }
    }
}
