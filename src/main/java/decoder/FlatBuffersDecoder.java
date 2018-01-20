package decoder;

import Schema.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class FlatBuffersDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
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
    }
}
