package yatwinkle.client.service.event;

public interface Cancellable {
    boolean isCancelled();
    void setCancelled(boolean cancelled);
}