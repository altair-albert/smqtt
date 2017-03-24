package net.hivebc.server.handler

import com.typesafe.scalalogging.{Logger, StrictLogging}
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.websocketx._
import io.netty.handler.codec.http.{DefaultHttpRequest, FullHttpRequest, HttpRequest}

/**
  * Created by albert on 17-3-11.
  */
class WebSocketServerHandler extends SimpleChannelInboundHandler[Object] with StrictLogging {

  private var handshaker: WebSocketServerHandshaker = _

  override def messageReceived(ctx: ChannelHandlerContext, msg: Object): Unit = {
    logger.debug("WebSocket")

    msg match {
      case req: FullHttpRequest => handleHttpRequest(ctx, req)
        logger.debug("1")
      case req: WebSocketFrame =>
        handleWebSocketRequest(ctx, req)
        logger.debug("2")
      case _ =>
        logger.debug("3")
        print(msg.toString)
    }

  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }

  private def handleHttpRequest(ctx: ChannelHandlerContext, req: FullHttpRequest): Unit = {
    logger.debug("Http Request")
    val factory = new WebSocketServerHandshakerFactory("ws://localhost:1883", null, true)
    handshaker = factory.newHandshaker(req)
    if (handshaker == null) {
      WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel())
    } else {
      handshaker.handshake(ctx.channel(), req)
    }
  }

  private def handleWebSocketRequest(ctx: ChannelHandlerContext, frame: WebSocketFrame): Unit = {
    logger.debug("WebSocket Request")
    frame match {
      case req: CloseWebSocketFrame => handshaker.close(ctx.channel(), req)
      case req: PingWebSocketFrame => ctx.write(req.content().retain())
      case req: TextWebSocketFrame =>
        logger.debug(req.text())
        ctx.channel().write(new TextWebSocketFrame("Hello World"))

      case _ => None
    }
  }
}
