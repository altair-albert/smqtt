package net.hivebc.server.handler

import com.typesafe.scalalogging.Logger
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.channel._


/**
  * Created by Albert on 2017/3/10.
  */
class TimeServerHandler extends ChannelHandlerAdapter {
  private val logger = Logger(classOf[TimeServerHandler])

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    val buf = msg.asInstanceOf[ByteBuf]
    val bs = new Array[Byte](buf.readableBytes())
    buf.readBytes(bs)
    logger.debug(new String(bs))
    ctx.write(Unpooled.copiedBuffer("Hello World\n".getBytes))
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.error(cause.getMessage)
    ctx.close()
  }
}
