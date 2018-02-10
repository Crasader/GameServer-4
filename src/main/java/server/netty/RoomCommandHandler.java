package server.netty;

import com.google.inject.Inject;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schema.Message;
import server.app.RoomManager;


/**
 * Class used to handle room command from users joined in the room.
 *
 * @author jalbatross (Joey Albano)
 *
 */

public class RoomCommandHandler extends ChannelInboundHandlerAdapter {
    private Channel ch;
    private RoomManager roomManager;
    private static final Logger LOG = LoggerFactory.getLogger(RoomCommandHandler.class);

    @Inject
    public RoomCommandHandler(RoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgData) throws Exception {
        try {
            Message msg = (Message) msgData;
            LOG.info("Command in the room..." );
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
}