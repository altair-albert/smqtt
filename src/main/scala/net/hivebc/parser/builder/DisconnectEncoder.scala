package net.hivebc.parser.builder

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.hivebc.parser.message.{DisconnectMessage, MessageType}

/**
  * Created by albert on 17-3-19.
  */
class DisconnectEncoder extends Encoder[DisconnectMessage] {
  override def encode(ctx: ChannelHandlerContext, msg: DisconnectMessage, out: ByteBuf): Unit = {
    out.writeByte(MessageType.DISCONNECT.id << 4).writeByte(0)
  }
}
