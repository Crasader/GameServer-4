package server.event;

public interface EventHandler {
    void onEvent(EventType event, Object e);
}
