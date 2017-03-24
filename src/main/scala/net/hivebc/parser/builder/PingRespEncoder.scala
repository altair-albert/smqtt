package net.hivebc.parser.builder

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.hivebc.parser.message.{MessageType, PingRespMessage}

/**
  * Created by albert on 17-3-19.
  */
class PingRespEncoder extends Encoder[PingRespMessage] {
  override def encode(ctx: ChannelHandlerContext, msg: PingRespMessage, out: ByteBuf): Unit = {
    out.writeByte(MessageType.PINGRESP.id << 4).writeByte(0)
  }
}
