package decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import schema.Message;
import server.netty.ServerAuthHandler;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class FlatBuffersDecoder extends ByteToMessageDecoder {
    private static final Logger LOG = LoggerFactory.getLogger(ServerAuthHandler.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //LOG.warn("testing");
        if (in.readableBytes() <= 0) {
            return;
        }
        int length = in.readableBytes();
        byte[] bytes;
        if (in.hasArray()) {
            bytes = in.array();
        } else {
            bytes = new byte[length];
            in.getBytes(in.readerIndex(), bytes);
        }
        java.nio.ByteBuffer data = java.nio.ByteBuffer.wrap(bytes);
        Message received = Message.getRootAsMessage(data);
        out.add(received);
        in.clear();
    }
}
