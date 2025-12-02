package yatwinkle.client.service.module;

public enum Category {
    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    RENDER("Render"),
    MISC("Misc");

    public final String name;

    Category(String name) {
        this.name = name;
    }
}
