package yatwinkle.client.helper;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

public interface MinecraftLogger extends MinecraftInstances {

    Text prefix =
            Text.literal("(").formatted(Formatting.GRAY)
                    .append(Text.literal("yatwinkle").formatted(Formatting.RED))
                    .append(Text.literal(") -> ").formatted(Formatting.GRAY));

    default void chat(String message, Formatting formatting) {
        Text text = prefix.copy().append(Text.literal(message).formatted(formatting));
        Objects.requireNonNull(client.player).sendMessage(text, false);
    }

    default void info(String message) { chat(message, Formatting.WHITE); }

    default void warn(String message) { chat(message, Formatting.YELLOW); }

    default void error(String message) { chat(message, Formatting.RED); }
}
