package net.hivebc.parser.builder


import io.netty.buffer.ByteBuf
import io.netty.handler.codec.CorruptedFrameException
import io.netty.util.AttributeMap
import net.hivebc.parser.message.{AbstractMessage, MessageType, QoS}

/**
  * Created by albert on 17-3-12.
  */
trait Decoder {
  @throws[Exception]
  def decode(ctx: AttributeMap, in: ByteBuf, out: java.util.List[AnyRef])

  /**
    * Decodes the first 2 bytes of the MQTT packet.
    * The first byte contain the packet operation code and the flags,
    * the second byte and more contains the overall packet length.
    */
  protected def decodeCommonHeader(message: AbstractMessage, in: ByteBuf): Boolean = genericDecodeCommonHeader(message, null, in)

  /**
    * Do the same as the @see#decodeCommonHeader but having a strong validation on the flags values
    */
  protected def decodeCommonHeader(message: AbstractMessage, expectedFlags: Int, in: ByteBuf): Boolean = genericDecodeCommonHeader(message, expectedFlags, in)

  private def genericDecodeCommonHeader(message: AbstractMessage, expectedFlagsOpt: Integer, in: ByteBuf): Boolean = {
    //Common decoding part
    if (in.readableBytes < 2) return false
    val h1: Byte = in.readByte
    val msgType: Byte = ((h1 & 0x00F0) >> 4).toByte
    val flags: Byte = (h1 & 0x0F).toByte
    if (expectedFlagsOpt != null) {
      val expectedFlags: Int = expectedFlagsOpt
      if (expectedFlags.toByte != flags) {
        val hexExpected: String = Integer.toHexString(expectedFlags)
        val hexReceived: String = Integer.toHexString(flags)
        throw new CorruptedFrameException("Received a message with fixed header flags (%s) != expected (%s)".format(hexReceived, hexExpected))
      }
    }
    val dupFlag: Boolean = ((h1 & 0x0008) >> 3).toByte == 1
    val qosLevel: Byte = ((h1 & 0x0006) >> 1).toByte
    val retainFlag: Boolean = (h1 & 0x0001).toByte == 1
    val remainingLength: Int = Utils.decodeRemainingLength(in)
    if (remainingLength == -1) return false
    try {
      message.msgType = MessageType.valueOf(msgType)
    } catch {
      case e: IllegalArgumentException =>
        throw new CorruptedFrameException("Received an invalid QOS: %s".format(e.getMessage), e)
    }
    message.dupFlag = dupFlag
    try {
      message.qos = QoS.valueOf(qosLevel)
    } catch {
      case e: IllegalArgumentException =>
        throw new CorruptedFrameException("Received an invalid QOS: %s".format(e.getMessage), e)
    }
    message.retainFlag = retainFlag
    message.retainLength = remainingLength
    true
  }
}
