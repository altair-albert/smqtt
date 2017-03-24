package net.hivebc.parser.builder

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.{CorruptedFrameException, MessageToByteEncoder}
import net.hivebc.parser.message.MessageType.MessageType
import net.hivebc.parser.message.{AbstractMessage, ConnAckMessage, MessageType}

import scala.collection.mutable

/**
  * Created by albert on 17-3-14.
  */
class MQTTEncoder extends MessageToByteEncoder[AbstractMessage] {
  private val encoderMap = new mutable.HashMap[MessageType, Encoder[_ <: AbstractMessage]]
  encoderMap.put(MessageType.CONNECT, new ConnectEncoder)
  encoderMap.put(MessageType.CONNACK, new ConnAckEncoder)
  encoderMap.put(MessageType.DISCONNECT, new DisconnectEncoder)
  encoderMap.put(MessageType.PINGREQ, new PingReqEncoder)
  encoderMap.put(MessageType.PINGRESP, new PingRespEncoder)

  @throws[Exception]
  protected def encode(chc: ChannelHandlerContext, msg: AbstractMessage, out: ByteBuf) {
    val encoder = encoderMap.get(msg.msgType).orNull.asInstanceOf[Encoder[AbstractMessage]]
    if (encoder == null) throw new CorruptedFrameException("Can't find any suitable decoder for message type: " + msg.msgType)
    encoder.encode(chc, msg, out)
  }
}
