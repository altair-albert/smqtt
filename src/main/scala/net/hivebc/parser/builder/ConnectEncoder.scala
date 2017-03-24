package net.hivebc.parser.builder

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.hivebc.parser.message.ConnectMessage
import net.hivebc.parser.message.MessageType._

/**
  * Created by albert on 17-3-12.
  */
class ConnectEncoder extends Encoder[ConnectMessage] {
  override def encode(ctx: ChannelHandlerContext, msg: ConnectMessage, out: ByteBuf): Unit = {
    val fixedHeaderBuff: ByteBuf = ctx.alloc.buffer(12)
    val buff: ByteBuf = ctx.alloc.buffer()
    val variableHeaderBuff: ByteBuf = ctx.alloc.buffer(12)
    try {
      fixedHeaderBuff.writeBytes(Utils.encodeString("MQTT"))
      //version
      fixedHeaderBuff.writeByte(0x03)
      var connectionFlags = 0
      if (msg.clearSession) {
        connectionFlags |= 0x02.toByte
      }
      if (msg.willFlag) {
        connectionFlags |= 0x04.toByte
      }
      connectionFlags |= ((msg.qos.id & 0x03) << 3).toByte
      if (msg.willFlag) {
        connectionFlags |= 0x020.toByte
      }
      fixedHeaderBuff.writeByte(connectionFlags)
      fixedHeaderBuff.writeShort(msg.keepAlive)
      if (msg.clientId != null) {
        variableHeaderBuff.writeBytes(Utils.encodeString(msg.clientId))
        if (msg.willFlag) {
          variableHeaderBuff.writeBytes(Utils.encodeString(msg.willTopic))
          variableHeaderBuff.writeBytes(Utils.encodeFixedLengthContent(msg.willMsg))
        }
      }
      val variableHeaderSize: Int = variableHeaderBuff.readableBytes()
      buff.writeByte(CONNECT.id << 4)
      buff.writeBytes(Utils.encodeRemainingLength(12 + variableHeaderSize))
      buff.writeBytes(fixedHeaderBuff).writeBytes(variableHeaderBuff)
      out.writeBytes(buff)
    } finally {
      fixedHeaderBuff.release()
      buff.release()
      variableHeaderBuff.release()
    }
  }
}
