package net.hivebc.parser.builder

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.hivebc.parser.message.{MessageType, PingReqMessage}

/**
  * Created by albert on 17-3-19.
  */
class PingReqEncoder extends Encoder[PingReqMessage] {
  override def encode(ctx: ChannelHandlerContext, msg: PingReqMessage, out: ByteBuf): Unit = {
    out.writeByte(MessageType.PINGREQ.id << 4).writeByte(0)
  }
}
