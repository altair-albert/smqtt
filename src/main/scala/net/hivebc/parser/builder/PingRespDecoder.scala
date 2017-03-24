package net.hivebc.parser.builder

import java.util

import io.netty.buffer.ByteBuf
import io.netty.util.AttributeMap
import net.hivebc.parser.message.PingRespMessage

/**
  * Created by albert on 17-3-19.
  */
class PingRespDecoder extends Decoder {
  override def decode(ctx: AttributeMap, in: ByteBuf, out: util.List[AnyRef]): Unit = {
    in.resetReaderIndex()
    val message = new PingRespMessage
    if (!decodeCommonHeader(message, 0x00, in)) {
      in.resetReaderIndex()
      return
    }
    out.add(message)
  }
}
