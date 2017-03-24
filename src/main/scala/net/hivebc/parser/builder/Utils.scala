package net.hivebc.parser.builder

import java.io.UnsupportedEncodingException

import com.typesafe.scalalogging.{Logger, StrictLogging}
import io.netty.buffer.{ByteBuf, Unpooled}
import io.netty.handler.codec.CorruptedFrameException
import io.netty.util.{Attribute, AttributeMap}
import net.hivebc.parser.message.AbstractMessage

/**
  * Created by albert on 17-3-12.
  */

object Utils extends StrictLogging {
  val MAX_LENGTH_LIMIT: Int = 268435455
  val VERSION_3_1: Byte = 3
  val VERSION_3_1_1: Byte = 4

  def readMessageType(in: ByteBuf): Byte = {
    val h1: Byte = in.readByte
    val messageType: Byte = ((h1 & 0x00F0) >> 4).toByte
    messageType
  }

  def checkHeaderAvailability(in: ByteBuf): Boolean = {
    if (in.readableBytes < 1) return false
    //byte h1 = in.get();
    //byte messageType = (byte) ((h1 & 0x00F0) >> 4);
    in.skipBytes(1)
    //skip the messageType byte
    val remainingLength: Int = Utils.decodeRemainingLength(in)
    if (remainingLength == -1) return false
    //check remaining length
    if (in.readableBytes < remainingLength) return false
    //return messageType == type ? MessageDecoderResult.OK : MessageDecoderResult.NOT_OK;
    true
  }

  /**
    * Decode the variable remaining length as defined in MQTT v3.1 specification
    * (section 2.1).
    *
    * @return the decoded length or -1 if needed more data to decode the length field.
    */
  def decodeRemainingLength(in: ByteBuf): Int = {
    var multiplier: Int = 1
    var value: Int = 0
    var digit: Byte = 0
    do {
      if (in.readableBytes < 1) return -1
      digit = in.readByte
      value += (digit & 0x7F) * multiplier
      multiplier *= 128
    } while ((digit & 0x80) != 0)
    value
  }

  /**
    * Encode the value in the format defined in specification as variable length
    * array.
    *
    * @throws IllegalArgumentException if the value is not in the specification bounds
    *                                  [0..268435455].
    */
  @throws[CorruptedFrameException]
  def encodeRemainingLength(value: Int): ByteBuf = {
    var size = value
    if (size > MAX_LENGTH_LIMIT || size < 0) throw new CorruptedFrameException("Value should in range 0.." + MAX_LENGTH_LIMIT + " found " + size)
    val encoded: ByteBuf = Unpooled.buffer(4)
    var digit: Byte = 0
    do {
      digit = (size % 128).toByte
      size = size / 128
      // if there are more digits to encode, set the top bit of this digit
      if (size > 0) digit = (digit | 0x80).toByte
      encoded.writeByte(digit)
    } while (size > 0)
    encoded
  }

  /**
    * Load a string from the given buffer, reading first the two bytes of len
    * and then the UTF-8 bytes of the string.
    *
    * @return the decoded string or null if NEED_DATA
    */
  @throws[UnsupportedEncodingException]
  def decodeString(in: ByteBuf): String = new String(readFixedLengthContent(in), "UTF-8")

  /**
    * Read a byte array from the buffer, use two bytes as length information followed by length bytes.
    **/
  @throws[UnsupportedEncodingException]
  def readFixedLengthContent(in: ByteBuf): Array[Byte] = {
    if (in.readableBytes < 2) return null
    val strLen: Int = in.readUnsignedShort
    if (in.readableBytes < strLen) return null
    val strRaw: Array[Byte] = new Array[Byte](strLen)
    in.readBytes(strRaw)
    strRaw
  }

  /**
    * Return the IoBuffer with string encoded as MSB, LSB and UTF-8 encoded
    * string content.
    */
  def encodeString(str: String): ByteBuf = {
    var raw: Array[Byte] = null
    try {
      raw = str.getBytes("UTF-8")
    } catch {
      case ex: UnsupportedEncodingException => {
        logger.error(null, ex)
        return null
      }
    }
    encodeFixedLengthContent(raw)
  }

  /**
    * Return the IoBuffer with string encoded as MSB, LSB and bytes array content.
    */
  def encodeFixedLengthContent(content: Array[Byte]): ByteBuf = {
    val out: ByteBuf = Unpooled.buffer(2)
    out.writeShort(content.length)
    out.writeBytes(content)
    out
  }

  /**
    * Return the number of bytes to encode the given remaining length value
    */
  def numBytesToEncode(len: Int): Int = {
    if (0 <= len && len <= 127) return 1
    if (128 <= len && len <= 16383) return 2
    if (16384 <= len && len <= 2097151) return 3
    if (2097152 <= len && len <= 268435455) return 4
    throw new IllegalArgumentException("value shoul be in the range [0..268435455]")
  }

  def encodeFlags(message: AbstractMessage): Byte = {
    var flags = 0
    if (message.dupFlag) flags |= 0x08
    if (message.retainFlag) flags |= 0x01
    flags |= ((message.qos.id & 0x03) << 1)
    flags.toByte
  }

  def isMQTT3_1_1(attrsMap: AttributeMap): Boolean = {
    val versionAttr: Attribute[Integer] = attrsMap.attr(MQTTDecoder.PROTOCOL_VERSION)
    val protocolVersion: Integer = versionAttr.get
    if (protocolVersion == null) {
      return true
    }
    protocolVersion == VERSION_3_1_1
  }
}
