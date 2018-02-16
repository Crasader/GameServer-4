package server.netty;

import auth.LoginAuth;
import builder.SchemaBuilder;
import com.google.inject.Inject;
import info.UserInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schema.Data;
import schema.ErrorCode;
import schema.JoinRoomCommand;
import schema.Message;
import server.app.Room;
import server.app.RoomManager;
import server.event.impl.EventHandlerFactory;
import server.event.impl.PlayerArriveHandler;
import server.netty.util.NettyUtils;
import server.session.Session;


/**
 * Class used to handle user joining room
 */

public class ServerAuthHandler extends ChannelInboundHandlerAdapter {
    private Channel ch;
    private LoginAuth loginAuth_;
    private RoomManager roomManager;
    private static final Logger LOG = LoggerFactory.getLogger(ServerAuthHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        ch = ctx.channel();
    }

    @Inject
    public ServerAuthHandler(LoginAuth loginAuth) {
        this.loginAuth_ = loginAuth;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgData) throws Exception {
        try {
            Message msg = (Message) msgData;
            if (msg.dataType() != Data.JoinRoomCommand) {
                LOG.warn("User hit ServerAuthHandler with wrong msg dataType");
                return;
            }
            LOG.info("Join Room..." );
            JoinRoomCommand cmd = (JoinRoomCommand) (msg.data(new JoinRoomCommand()));
            String userId = this.loginAuth_.getLoginUserId(cmd.token());
            UserInfo userInfo = this.loginAuth_.getUserRecord(userId);
            LOG.info("roomManager=" + roomManager);
            Room room = roomManager.getRoom(cmd.roomId());
            ByteBuf buf = NettyUtils.getLengthPrependedByteBuf(SchemaBuilder.buildErrorMessage(ErrorCode.Unknown));
            if (!userId.isEmpty() && room!=null) {
                LOG.info("User: '" + userId + "' logged in successfully");
                Channel channel = ctx.channel();
                channel.pipeline().remove(this);
                channel.pipeline().addLast("RoomCommandHandler", new RoomCommandHandler(roomManager));

                ChannelFuture closeFuture = channel.closeFuture();
                closeFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        room.playerLeave(userId);
                    }
                });

                Session newSession = room.playerArrive(userInfo, ctx, new EventHandlerFactory());
                buf = NettyUtils.getLengthPrependedByteBuf(SchemaBuilder.buildRoomInfo(room));
            } else {
                LOG.info("User :" + userId + " failed to login");
                byte errorCode = ErrorCode.Unknown;
                if (userId.isEmpty()) {
                    errorCode = ErrorCode.INVALID_AUTH;
                } else if (room == null) {
                    errorCode = ErrorCode.ROOM_NOT_FOUND;
                }
                buf = NettyUtils.getLengthPrependedByteBuf(SchemaBuilder.buildErrorMessage(errorCode));
            }
            final ChannelFuture f = ctx.writeAndFlush(buf); // (3)
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) {
                    if (!f.isSuccess()) {
                        LOG.info("Send failed" + f.cause());
                        return;
                    }
                }
            });
            return;
        } finally {
            ReferenceCountUtil.release(msgData);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    public void setRoomManager(RoomManager roomManager) {
        this.roomManager = roomManager;
    }


}