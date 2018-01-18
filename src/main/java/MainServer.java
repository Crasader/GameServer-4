import auth.FireBaseAuthModule;
import auth.LocalAuthModule;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

import java.io.FileInputStream;

public class MainServer {
    private int port;
    public MainServer(int port) {
        this.port = port;
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
        new MainServer(port).run();
    }

    public void run() throws Exception {
        setUpFireBase();

        final SslContext sslCtx;
        sslCtx = null;
        System.out.println("Running server on port " + this.port);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //final Injector authInjector = Guice.createInjector(new FireBaseAuthModule());
        final Injector authInjector = Guice.createInjector(new LocalAuthModule());
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
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