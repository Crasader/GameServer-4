package server;

import auth.FireBaseAuthModule;
import auth.LocalAuthModule;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.inject.Guice;
import com.google.inject.Injector;
import decoder.FlatBuffersDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import server.netty.ServerAuthHandler;

import java.io.FileInputStream;

public class MainServer {

    public enum Stage {
        DEV, PROD
    }

    private int port;
    private Stage stage;
    public MainServer(int port, Stage stage) {
        this.port = port;
        this.stage = stage;
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        port = 8080;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            }
            catch (Exception e) {
                System.out.println("WARNING : port parsing failed so default port 8080 is used");
            }
        }
        new MainServer(port, Stage.DEV).run();
    }

    public void run() throws Exception {
        setUpFireBase();

        final SslContext sslCtx;
        sslCtx = null;
        System.out.println("Running server on port " + this.port);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final Injector authInjector = createAuthInjector();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("lengthFieldBasedFrameDecoder", createLengthBasedFrameDecoder());
                            ch.pipeline().addLast("FlatBuffersDecoder", createFlatBuffersDecoder());
                            ch.pipeline().addLast("authHandler", authInjector.getInstance(ServerAuthHandler.class));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();

        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private ChannelHandler createFlatBuffersDecoder() {
        return new FlatBuffersDecoder();
    }

    private ChannelHandler createLengthBasedFrameDecoder()
    {
        return new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 2, 0, 2);
    }

    private Injector createAuthInjector() {
        if (stage == Stage.PROD) {
            return Guice.createInjector(new FireBaseAuthModule());
        }
        return Guice.createInjector(new LocalAuthModule());
    }

    private void setUpFireBase() throws Exception {
        FileInputStream serviceAccount =
                new FileInputStream("/Users/nghiaround/Desktop/key.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl("https://pokerg-bf08c.firebaseio.com")
                .build();
        FirebaseApp.initializeApp(options);
    }
}