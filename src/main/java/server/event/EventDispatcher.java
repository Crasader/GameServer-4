package server.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventDispatcher {
    private Map<EventType, List<EventHandler>> handlers;

    public EventDispatcher() {
        handlers = new HashMap<EventType, List<EventHandler>>();
    }

    public void addListener(EventType type, EventHandler handler) {
        List<EventHandler> list;
        if (handlers.containsKey(type)) {
            list = handlers.get(type);
        } else {
            list = new ArrayList<>();
            handlers.put(type, list);
        }
        list.add(handler);
    }

    public void removeListener(EventType type, EventHandler handler) {
        if (!handlers.containsKey(type)) {
           return;
        }
        handlers.get(type).remove(handler);
    }

    public void dispatchEvent(Event event, Object e) {
        if (!handlers.containsKey(event.getType())) {
            return;
        }
        List<EventHandler> list = handlers.get(event.getType());
        for(EventHandler handler: list) {
            handler.onEvent(event.getType(), e);
        }
    }
}
