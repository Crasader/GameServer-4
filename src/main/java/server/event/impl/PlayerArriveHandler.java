package server.event.impl;

import builder.SchemaBuilder;
import io.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.event.EventHandler;
import server.event.EventType;
import server.netty.util.NettyUtils;
import server.session.Session;

public class PlayerArriveHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PlayerArriveHandler.class);

    private Session session;
    public PlayerArriveHandler(Session s) {
        this.session = s;
    }

    @Override
    public void onEvent(EventType event, Object e) {
        assert(session != null);

        if (event == EventType.NEW_PLAYER_ARRIVE) {
            Session session = (Session)e;
            handleNewPlayerArrive(session);
            return;
        }
        if (event == EventType.PLAYER_LEFT) {
            Session session = (Session)e;
            handlePlayerLeft(session);
            return;
        }
    }

    private void handlePlayerLeft(Session session) {
        ChannelHandlerContext channel = session.getChannel();
        LOG.info("Inform other about new user arrive..............");
        ChannelFuture f = channel.writeAndFlush(
                NettyUtils.getLengthPrependedByteBuf(SchemaBuilder.buildPlayerLeft(session)));
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

    private void handleNewPlayerArrive(Session newSessionPlayer) {
        ChannelHandlerContext channel = session.getChannel();
        LOG.info("Inform other about new user arrive..............");
        ChannelFuture f = channel.writeAndFlush(
                NettyUtils.getLengthPrependedByteBuf(SchemaBuilder.buildPlayerArrive(newSessionPlayer)));
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
