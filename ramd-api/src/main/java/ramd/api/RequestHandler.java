package ramd.api;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class RequestHandler extends SimpleChannelInboundHandler<Object> {
    private HttpRequest _request;
    private final StringBuilder _buf = new StringBuilder();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof HttpRequest) {
            HttpRequest request = _request = (HttpRequest) msg;
            HttpHeaders headers = request.headers();
            QueryStringDecoder query = new QueryStringDecoder(request.uri());

            if (HttpHeaderUtil.is100ContinueExpected(request))
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));

            if (headers != null) for (Map.Entry<CharSequence, CharSequence> h : headers) {}

            parsePathSequence(query.path());
        }

        if (msg instanceof HttpContent) {
            ByteBuf bb = ((HttpContent) msg).content();
            System.out.println("----httpcontent-----");
            if (bb.isReadable()) {
                System.out.println(bb.toString(CharsetUtil.UTF_8));
            }
            _buf.append("Done.");
            writeResponse((HttpContent) msg, ctx);
        }
    }

    private void writeResponse(HttpObject msg, ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1,
                msg.decoderResult().isSuccess()? OK : BAD_REQUEST,
                Unpooled.copiedBuffer(_buf.toString(), CharsetUtil.UTF_8));
        if (HttpHeaderUtil.isKeepAlive(_request)) {
            response.headers().set(CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
