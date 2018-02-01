package server.event.impl;

import builder.SchemaBuilder;
import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.channel.Channel;
import org.w3c.dom.events.Event;
import server.event.EventHandler;
import server.event.EventType;
import server.session.Session;

public class SessionEventHandler implements EventHandler {

    private Session session;
    public SessionEventHandler() {
    }

    @Override
    public void onEvent(EventType event, Object e) {
        assert(session != null);
        Channel channel = session.getChannel();
        channel.writeAndFlush(SchemaBuilder.buildPlayer(session));
    }

    @Override
    public void setSession(Session s) {
        this.session = s;
    }
}
