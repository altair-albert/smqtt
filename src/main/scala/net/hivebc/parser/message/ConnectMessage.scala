package net.hivebc.parser.message

/**
  * Created by albert on 17-3-12.
  */
class ConnectMessage extends AbstractMessage {
  msgType = MessageType.CONNECT
  qos = QoS.RESERVED

  var protocolName: String = _
  var version: Byte = _
  var clearSession: Boolean = _
  var willFlag: Boolean = _
  var willRetain: Boolean = _
  var keepAlive: Int = _

  var clientId: String = _
  var willTopic: String = _
  var willMsg: Array[Byte] = _

  override def toString: String = {
    var str = "Connect [clientId %s, protocolName %s, version %02X, clean %b]".format(clientId, protocolName, version, clearSession)
    if (willFlag) {
      str += "Will [QoS %d, retain %b]".format(qos.id, retainFlag)
    }
    str
  }
}
