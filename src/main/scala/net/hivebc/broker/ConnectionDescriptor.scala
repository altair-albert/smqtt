package net.hivebc.broker

import io.netty.channel.Channel

/**
  * Created by albert on 17-3-15.
  */
class ConnectionDescriptor(val clientId: String, val channel: Channel, val cleanSession: Boolean) {

  override def toString: String = "ConnectionDescriptor {ClientId = %s, cleanSession = %b}".format(clientId, cleanSession)

  override def equals(obj: scala.Any): Boolean = {
    if (obj == this) return true
    if (obj == null || (getClass != obj.getClass)) return false
    val that = obj.asInstanceOf[ConnectionDescriptor]
    if (if (clientId != null) clientId != that.clientId else that.clientId != null) return false
    !(if (channel != null) channel != that.channel else that.channel != null)
  }

  override def hashCode(): Int = {
    var result = if (clientId != null) clientId.hashCode else 0
    result = 31 * result + (if (channel != null) channel.hashCode else 0)
    result
  }
}
