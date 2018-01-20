import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import Schema.Data;
import Schema.CredentialToken;
import auth.FireBaseAuthModule;
import auth.LoginAuth;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import Schema.Message;


/**
 * Class used to authenticate users. Current implementation should handle
 * FlatBuffers Serialized credentials as ByteBufs. Each serialized Credentials
 * should be prefixed by a 4 byte integer indicating the size in bytes of the
 * serialized FlatBuffer OR a JSON object conforming to the following schema:
 * {username: someUser, password: somePass}
 *
 * Upon successful registration or authentication of the provided
 * username/password pair, ServerAuthHandler removes itself from the pipeline
 * with handlers that are useful for server functionality.
 *
 * @author jalbatross (Joey Albano)
 *
 */

public class ServerAuthHandler extends ChannelInboundHandlerAdapter {
    private Channel ch;
    private LoginAuth loginAuth_;


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
        Message msg = (Message)msgData;
        if (msg.dataType() != Data.CredentialToken) {
            return;
        }
        CredentialToken creds;
        creds = (Schema.CredentialToken)(msg.data(new Schema.CredentialToken()));
        String userId = this.loginAuth_.getLoginUserId(creds.token());
        if (!userId.isEmpty()) {
            System.out.println("User is logged in successfully:" + userId);
        } else {
            System.out.println("Failed to login");
        }
        return;
    }
}