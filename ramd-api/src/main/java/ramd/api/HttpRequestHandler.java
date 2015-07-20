package ramd.api;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import ramd.RamdException;
import ramd.RamdRequest;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_0;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpRequestHandler extends SimpleChannelInboundHandler<Object> {
    private HttpRequest _request;
    private final StringBuilder _buf = new StringBuilder();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest        request = _request = (HttpRequest) msg;
            HttpMethod         method  = request.method();
            HttpHeaders        headers = request.headers();
            QueryStringDecoder query   = new QueryStringDecoder(request.uri());

            if (HttpHeaderUtil.is100ContinueExpected(request))
                ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));

//            if (headers != null) for (Map.Entry<CharSequence, CharSequence> h : headers) {}

            if (method.equals(HttpMethod.GET)) {
                RamdRequest r = RamdRequest.build(query.path(), query.parameters());
                while (!r.done()) r.handle();
                _buf.append(r.getJson());
            }
        }

        if (msg instanceof HttpContent) {
            ByteBuf bb = ((HttpContent) msg).content();
            if (bb.isReadable()) {
            }
            writeResponse((HttpContent) bb, ctx);
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
