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

public class PlayerLeftHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerLeftHandler.class);

    private Session session;
    public PlayerLeftHandler(Session s) {
        this.session = s;
    }

    @Override
    public void onEvent(EventType event, Object e) {
        assert(session != null);
        ChannelHandlerContext channel = session.getChannel();
        LOG.info("Inform other about player left the room..............");
        ChannelFuture f = channel.writeAndFlush(
                NettyUtils.getLengthPrependedByteBuf(SchemaBuilder.buildPlayerLeft((Session)e)));
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


}
