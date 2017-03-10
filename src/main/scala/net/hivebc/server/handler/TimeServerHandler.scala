package net.hivebc.server.handler

import com.typesafe.scalalogging.Logger
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandler, ChannelInboundHandlerAdapter, SimpleChannelInboundHandler}


/**
  * Created by Albert on 2017/3/10.
  */
class TimeServerHandler extends ChannelInboundHandlerAdapter {
  val LOG = Logger(classOf[TimeServerHandler])

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    val buf = msg.asInstanceOf[ByteBuf]
    val bs = new Array[Byte](buf.readableBytes())
    buf.readBytes(bs)
    LOG.debug(new String(bs))
    buf.writeBytes(Unpooled.copiedBuffer("Hello World".getBytes))
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    ctx.close()
  }
}
