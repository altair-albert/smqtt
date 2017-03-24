package net.hivebc.parser.builder

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.{ByteToMessageDecoder, CorruptedFrameException}
import io.netty.util.AttributeKey
import net.hivebc.parser.message.MessageType
import net.hivebc.parser.message.MessageType.MessageType

import scala.collection.mutable

/**
  * Created by albert on 17-3-13.
  */
class MQTTDecoder extends ByteToMessageDecoder {
  //3 = 3.1, 4 = 3.1.1
  private val decoderMap = new mutable.HashMap[MessageType, Decoder]

  decoderMap.put(MessageType.CONNECT, new ConnectDecoder)
  decoderMap.put(MessageType.CONNACK, new ConnAckDecoder)
  decoderMap.put(MessageType.DISCONNECT, new DisconnectDecoder)
  decoderMap.put(MessageType.PINGREQ, new PingReqDecoder)
  decoderMap.put(MessageType.PINGRESP, new PingRespDecoder)

  @throws[Exception]
  override protected def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: java.util.List[AnyRef]): Unit = {
    in.markReaderIndex()
    if (!Utils.checkHeaderAvailability(in)) {
      in.resetReaderIndex()
      return
    }
    in.resetReaderIndex()
    val msgType = Utils.readMessageType(in)
    val decoder = decoderMap.get(MessageType.valueOf(msgType)).orNull
    if (decoder == null) throw new CorruptedFrameException("Can't find any suitable decoder for message type: " + msgType)
    decoder.decode(ctx, in, out)
  }
}

object MQTTDecoder {
  val PROTOCOL_VERSION: AttributeKey[Integer] = AttributeKey.valueOf("version")
}
