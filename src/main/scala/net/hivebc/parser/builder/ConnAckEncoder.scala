package net.hivebc.parser.builder


import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.hivebc.parser.message.{ConnAckMessage, MessageType}

/**
  * Created by albert on 17-3-14.
  */
class ConnAckEncoder extends Encoder[ConnAckMessage] {
  override def encode(chc: ChannelHandlerContext, message: ConnAckMessage, out: ByteBuf) {
    out.writeByte(MessageType.CONNACK.id << 4)
    out.writeBytes(Utils.encodeRemainingLength(2))
    out.writeByte(if (message.sessionPresent) 0x01 else 0x00)
    out.writeByte(message.returnCode)
  }

}
