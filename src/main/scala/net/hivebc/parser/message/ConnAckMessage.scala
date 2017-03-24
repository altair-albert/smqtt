package net.hivebc.parser.message

/**
  * Created by albert on 17-3-12.
  */
class ConnAckMessage extends AbstractMessage {
  var returnCode: Byte = _
  var sessionPresent: Boolean = _
  msgType = MessageType.CONNACK
}

object ConnAckMessage {
  val CONNECTION_ACCEPTED: Byte = 0x00
  val UNACCEPTABLE_PROTOCOL_VERSION: Byte = 0x01
  val IDENTIFIER_REJECTED: Byte = 0x02
  val SERVER_UNAVAILABLE: Byte = 0x03
}