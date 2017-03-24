package net.hivebc.parser.builder

import java.util

import io.netty.buffer.ByteBuf
import io.netty.handler.codec.CorruptedFrameException
import io.netty.util.{Attribute, AttributeKey, AttributeMap}
import net.hivebc.parser.message.{AbstractMessage, ConnectMessage, QoS}

/**
  * Created by albert on 17-3-13.
  */
class ConnectDecoder extends Decoder {

  override def decode(ctx: AttributeMap, in: ByteBuf, out: util.List[AnyRef]) {
    in.resetReaderIndex()
    //Common decoding part
    val message = new ConnectMessage
    if (!decodeCommonHeader(message, 0x00, in)) {
      in.resetReaderIndex()
      return
    }
    val remainingLength = message.retainLength
    val start = in.readerIndex()
    val protocolNameLen = in.readUnsignedShort()
    var encProtoName = Array[Byte]()
    var protoName = ""
    val versionAttr: Attribute[Integer] = ctx.attr(MQTTDecoder.PROTOCOL_VERSION)
    protocolNameLen match {
      case 4 =>
        //MQTT version 3.1.1 "MQTT"
        //ProtocolName 6 bytes
        if (in.readableBytes < 8) {
          in.resetReaderIndex
          return
        }
        encProtoName = new Array[Byte](4)
        in.readBytes(encProtoName)
        protoName = new String(encProtoName, "UTF-8")
        if ("MQTT" != protoName) {
          in.resetReaderIndex
          throw new CorruptedFrameException("Invalid protoName: " + protoName)
        }
        message.protocolName = protoName
        versionAttr.set(Utils.VERSION_3_1_1.toInt)
      case _ =>
        //protocol broken
        throw new CorruptedFrameException("Invalid protoName size: " + protocolNameLen)
    }
    //ProtocolVersion 1 byte (value 0x03 for 3.1, 0x04 for 3.1.1)
    message.version = in.readByte
    if (message.version == Utils.VERSION_3_1_1) {
      //if 3.1.1, check the flags (dup, retain and qos == 0)
      if (message.dupFlag || message.retainFlag || (message.qos != QoS.MOST_ONE)) throw new CorruptedFrameException("Received a CONNECT with fixed header flags != 0")
      //check if this is another connect from the same client on the same session
      val connectAttr = ctx.attr(ConnectDecoder.CONNECT_STATUS)
      val alreadyConnected = connectAttr.get()
      if (!alreadyConnected) {
        connectAttr.set(true)
      } else if (alreadyConnected) {
        throw new CorruptedFrameException("Received a second CONNECT on the same network connection")
      }
    }
    //Connection flag
    val connFlags = in.readByte
    if (message.version == Utils.VERSION_3_1_1) if ((connFlags & 0x01) != 0) {
      //bit(0) of connection flags is != 0
      throw new CorruptedFrameException("Received a CONNECT with connectionFlags[0(bit)] != 0")
    }
    val cleanSession = ((connFlags & 0x02) >> 1) == 1
    val willFlag = ((connFlags & 0x04) >> 2) == 1
    val willQos = ((connFlags & 0x18) >> 3).toByte
    if (willQos > 2) {
      in.resetReaderIndex
      throw new CorruptedFrameException("Expected will QoS in range 0..2 but found: " + willQos)
    }
    val willRetain = ((connFlags & 0x20) >> 5) == 1
    val passwordFlag = ((connFlags & 0x40) >> 6) == 1
    val userFlag = ((connFlags & 0x80) >> 7) == 1
    //a password is true iff user is true.
    if (!userFlag && passwordFlag) {
      in.resetReaderIndex
      throw new CorruptedFrameException("Expected password flag to true if the user flag is true but was: " + passwordFlag)
    }
    message.clearSession = cleanSession
    message.willFlag = willFlag
    message.qos = QoS.valueOf(willQos)
    message.willRetain = willRetain
    val keepAlive = in.readUnsignedShort
    message.keepAlive = keepAlive
    if ((remainingLength == 12 && message.version == Utils.VERSION_3_1) || (remainingLength == 10 && message.version == Utils.VERSION_3_1_1)) {
      out.add(message)
      return
    }
    //Decode the ClientID
    val clientID = Utils.decodeString(in)
    if (clientID == null) {
      in.resetReaderIndex
      return
    }
    message.clientId = clientID
    //Decode willTopic
    if (willFlag) {
      val willTopic = Utils.decodeString(in)
      if (willTopic == null) {
        in.resetReaderIndex
        return
      }
      message.willTopic = willTopic
    }
    //Decode willMessage
    if (willFlag) {
      val willMessage = Utils.readFixedLengthContent(in)
      if (willMessage == null) {
        in.resetReaderIndex
        return
      }
      message.willMsg = willMessage
    }
    //Compatibility check with v3.0, remaining length has precedence over
    //the user and password flags
    var readed = in.readerIndex - start
    if (readed == remainingLength) {
      out.add(message)
      return
    }
    readed = in.readerIndex - start
    if (readed == remainingLength) {
      out.add(message)
      return
    }
    out.add(message)
  }
}

object ConnectDecoder {
  val CONNECT_STATUS: AttributeKey[Boolean] = AttributeKey.valueOf("connected")
}