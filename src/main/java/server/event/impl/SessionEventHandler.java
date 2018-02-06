package server.event.impl;

import builder.SchemaBuilder;
import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.events.Event;
import schema.PlayerInfo;
import server.event.EventHandler;
import server.event.EventType;
import server.netty.util.NettyUtils;
import server.session.Session;

public class SessionEventHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SessionEventHandler.class);

    private Session session;
    public SessionEventHandler() {
    }

    @Override
    public void onEvent(EventType event, Object e) {
        assert(session != null);

        if (event == EventType.NEW_PLAYER_ARRIVE) {
            Session session = (Session)e;
            handleNewPlayerArrive(session);
        }
    }

    private void handleNewPlayerArrive(Session newSessionPlayer) {
        ChannelHandlerContext channel = session.getChannel();
        LOG.info("Inform other about new user arrive..............");
        ChannelFuture f = channel.writeAndFlush(NettyUtils.getLengthPrependedByteBuf(SchemaBuilder.buildPlayer(newSessionPlayer)));
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (!f.isSuccess()) {
                    LOG.info("Send failed" + f.cause());
                    return;
                }
            }
        });
    }

    @Override
    public void setSession(Session s) {
        this.session = s;
    }
}
