package yatwinkle.client.service.command;

public class CommandException extends RuntimeException {
    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Object... args) {
        super(String.format(message, args));
    }
}
