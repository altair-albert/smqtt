package net.hivebc.parser.message

/**
  * Created by albert on 17-3-12.
  */
abstract class AbstractMessage {
  var qos = QoS.RESERVED
  var msgType = MessageType.RESERVED
  var retainFlag: Boolean = _
  var retainLength: Int = _
  var dupFlag: Boolean = _
}

object MessageType extends Enumeration {
  type MessageType = Value
  val CONNECT = Value(0x01)
  val CONNACK = Value(0x02)
  val PUBLISH = Value(0x03)
  val PUBACK = Value(0x04)
  val PUBREC = Value(0x05)
  val PUBREL = Value(0x06)
  val PUBCOM = Value(0x07)
  val SUBSCRIBE = Value(0x08)
  val SUBBAK = Value(0x09)
  val UNSUBSCRIBE = Value(0x0A)
  val UNSUBACK = Value(0x0B)
  val PINGREQ = Value(0x0C)
  val PINGRESP = Value(0x0D)
  val DISCONNECT = Value(0x0E)
  val RESERVED = Value(0x0F)

  def valueOf(byte: Byte): MessageType = {
    byte match {
      case 0x01 => CONNECT
      case 0x02 => CONNACK
      case 0x03 => PUBLISH
      case 0x04 => PUBACK
      case 0x05 => PUBREC
      case 0x06 => PUBREL
      case 0x07 => PUBCOM
      case 0x08 => SUBSCRIBE
      case 0x09 => SUBBAK
      case 0x0A => UNSUBSCRIBE
      case 0x0B => UNSUBACK
      case 0x0C => PINGREQ
      case 0x0D => PINGRESP
      case 0x0E => DISCONNECT
      case _ => RESERVED
    }
  }
}