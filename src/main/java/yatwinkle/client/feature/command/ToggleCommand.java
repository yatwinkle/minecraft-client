package yatwinkle.client.feature.command;

import net.minecraft.util.Formatting;
import yatwinkle.client.service.command.Command;
import yatwinkle.client.service.command.CommandException;
import yatwinkle.client.service.module.Module;
import yatwinkle.client.service.module.Modules;

import java.util.List;
import java.util.stream.Collectors;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("toggle", "Toggles the specified module", "<module>", "t");
    }

    @Override
    public void execute(String[] args) {
        if (args.length < 1) {
            throw new CommandException("Please specify a module name.");
        }

        String moduleName = args[0];
        Module module = Modules.get().getAll().stream()
                .filter(m -> m.getId().equalsIgnoreCase(moduleName) || m.getDisplayName().equalsIgnoreCase(moduleName))
                .findFirst()
                .orElse(null);

        if (module == null) {
            throw new CommandException("Module '%s' not found.", moduleName);
        }

        module.toggle();

        Formatting color = module.isActive() ? Formatting.GREEN : Formatting.RED;
        String status = module.isActive() ? "enabled" : "disabled";

        info("Module " + Formatting.GRAY + "«" + Formatting.WHITE + module.getDisplayName() + Formatting.GRAY + "»" +
                Formatting.RESET + " was " + color + status + Formatting.RESET + ".");
    }

    @Override
    public List<String> suggest(String[] args) {
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            return Modules.get().getAll().stream()
                    .map(Module::getId)
                    .filter(id -> id.toLowerCase().startsWith(partial))
                    .collect(Collectors.toList());
        }
        return super.suggest(args);
    }
}
