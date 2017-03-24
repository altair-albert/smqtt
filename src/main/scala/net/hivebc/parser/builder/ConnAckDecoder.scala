package net.hivebc.parser.builder

import java.util

import io.netty.buffer.ByteBuf
import io.netty.util.AttributeMap
import net.hivebc.parser.message.{AbstractMessage, ConnAckMessage}

/**
  * Created by albert on 17-3-14.
  */
class ConnAckDecoder extends Decoder {
  override def decode(ctx: AttributeMap, in: ByteBuf, out: util.List[AnyRef]): Unit = {
    in.resetReaderIndex
    //Common decoding part
    val message: ConnAckMessage = new ConnAckMessage
    if (!decodeCommonHeader(message, 0x00, in)) {
      in.resetReaderIndex()
      return
    }

    in.skipBytes(1)

    message.returnCode = in.readByte
    out.add(message)
  }
}
