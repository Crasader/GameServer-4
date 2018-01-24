package server.netty.util;

import com.google.flatbuffers.FlatBufferBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyUtils {
    public static ByteBuf getLengthPrependedByteBuf(FlatBufferBuilder flatBufferBuilder) {
        byte[] buf = flatBufferBuilder.sizedByteArray();
        ByteBuf lengthBuffer = Unpooled.buffer(2);
        lengthBuffer.writeShort(buf.length);
        return Unpooled.wrappedBuffer(lengthBuffer,Unpooled.wrappedBuffer(buf));
    }
}
