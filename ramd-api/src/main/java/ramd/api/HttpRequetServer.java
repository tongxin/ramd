package ramd.api;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
/**
 * Uses netty's event-driven http server framework.
 */
public class HttpRequetServer {

    public static void start(int port) throws Exception {
        EventLoopGroup boss_loops = new NioEventLoopGroup(1);
        EventLoopGroup work_loops = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(boss_loops, work_loops)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpServerInitializer());
            Channel ch = server.bind(port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            boss_loops.shutdownGracefully();
            work_loops.shutdownGracefully();
        }

    }

    public static class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline p = ch.pipeline();
            p.addLast(new HttpRequestDecoder());
            // Uncomment the following line if you don't want to handle HttpChunks.
            //p.addLast(new HttpObjectAggregator(1048576));
            p.addLast(new HttpResponseEncoder());
            // Remove the following line if you don't want automatic content compression.
            //p.addLast(new HttpContentCompressor());
            p.addLast(new HttpRequestHandler());
        }
    }


}
