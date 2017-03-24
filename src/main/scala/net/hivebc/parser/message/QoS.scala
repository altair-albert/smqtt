package net.hivebc.parser.message

/**
  * Created by albert on 17-3-12.
  */
object QoS extends Enumeration {
  type QoS = Value
  val MOST_ONE = Value(0x00)
  val LEAST_ONE = Value(0x01)
  val EXACTLY_ONCE = Value(0x02)
  val FAILURE = Value(0x80)
  val RESERVED = Value(0xFF)

  def valueOf(byte: Byte): QoS = {
    byte match {
      case 0x00 => MOST_ONE
      case 0x01 => LEAST_ONE
      case 0x02 => EXACTLY_ONCE
      case 0x80 => FAILURE
      case _ => throw new IllegalArgumentException("Invalid QOS, Excepted 0,1,2,0x80, but Given: " + byte)
    }
  }
}
