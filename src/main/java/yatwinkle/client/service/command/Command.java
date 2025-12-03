package yatwinkle.client.service.command;

import yatwinkle.client.helper.MinecraftLogger;

import java.util.Collections;
import java.util.List;

public abstract class Command implements MinecraftLogger {

    private final String name;
    private final String description;
    private final String syntax;
    private final String[] aliases;

    public Command(String name, String description, String syntax, String... aliases) {
        this.name = name;
        this.description = description;
        this.syntax = syntax;
        this.aliases = aliases;
    }

    public abstract void execute(String[] args);

    public List<String> suggest(String[] args) {
        return Collections.emptyList();
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getSyntax() { return syntax; }
    public String[] getAliases() { return aliases; }
}
