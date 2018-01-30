package server.event;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventDispatcherTest {

    public class TestHandler implements EventHandler {

        int x;
        public TestHandler() {
            x = 0;
        }
        @Override
        public void onEvent(EventType event, Object e) {
            x = (int)e;
        }

        public int getX() {
            return x;
        }
    }

    @Test
    public void testDispatchEventToListeners() {
        EventDispatcher dispatcher = new EventDispatcher();
        TestHandler handler = new TestHandler();
        dispatcher.addListener(EventType.NEW_PLAYER_ARRIVE, handler);

        Event event = new Event(EventType.NEW_PLAYER_ARRIVE);
        dispatcher.dispatchEvent(event, 2);
        assertEquals(handler.getX(), 2);

        dispatcher.dispatchEvent(new Event(EventType.PLAYER_LEFT), 3);
        assertEquals(handler.getX(), 2);

        dispatcher.removeListener(event.getType(), handler);
        dispatcher.dispatchEvent(event, 10);
        assertEquals(handler.getX(), 2);


    }
}
