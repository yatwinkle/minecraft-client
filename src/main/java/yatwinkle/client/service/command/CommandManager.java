package yatwinkle.client.service.command;

import yatwinkle.client.feature.command.ToggleCommand;
import yatwinkle.client.feature.event.network.ChatEvent;
import yatwinkle.client.helper.MinecraftLogger;
import yatwinkle.client.service.event.AtomicBus;
import yatwinkle.client.service.event.Listener;

import java.util.*;
import java.util.stream.Collectors;

public final class CommandManager implements MinecraftLogger {

    public static final String PREFIX = ".";
    private static CommandManager instance;

    private final Map<String, Command> commandMap = new HashMap<>();
    private final List<Command> commands = new ArrayList<>();

    private CommandManager() {
        AtomicBus.BUS.subscribe(new Listener<>(ChatEvent.class, this::onChatSend));
    }

    public static void init() {
        if (instance == null) {
            instance = new CommandManager();
            instance.registerDefaults();
        }
    }

    public static CommandManager get() {
        return instance;
    }

    private void registerDefaults() {
        register(new ToggleCommand());
    }

    public void register(Command command) {
        commands.add(command);
        commandMap.put(command.getName().toLowerCase(), command);
        for (String alias : command.getAliases()) {
            commandMap.put(alias.toLowerCase(), command);
        }
    }

    private void onChatSend(ChatEvent event) {
        String message = event.getMessage();
        if (!message.startsWith(PREFIX)) return;

        event.setCancelled(true);

        String[] split = message.substring(PREFIX.length()).trim().split("\\s+");

        if (split.length == 0 || split[0].isEmpty()) return;

        String commandName = split[0].toLowerCase();
        Command command = commandMap.get(commandName);

        if (command == null) {
            error("Command not found: " + commandName + ".");
            return;
        }

        String[] args = Arrays.copyOfRange(split, 1, split.length);

        try {
            command.execute(args);
        } catch (CommandException e) {
            error(e.getMessage());
        }
    }

    public List<String> getSuggestions(String input) {
        if (!input.startsWith(PREFIX)) return Collections.emptyList();

        String raw = input.substring(PREFIX.length());
        String[] split = raw.split(" ", -1);

        if (split.length <= 1) {
            String partialName = split[0].toLowerCase();
            return commands.stream()
                    .map(Command::getName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }

        String commandName = split[0].toLowerCase();
        Command command = commandMap.get(commandName);

        if (command != null) {
            String[] args = Arrays.copyOfRange(split, 1, split.length);
            return command.suggest(args);
        }

        return Collections.emptyList();
    }

    public Collection<Command> getAllCommands() {
        return Collections.unmodifiableList(commands);
    }
}
