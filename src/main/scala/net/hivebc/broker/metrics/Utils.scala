package net.hivebc.broker.metrics

import io.netty.channel.{Channel, ChannelHandlerContext}
import io.netty.util.AttributeKey

/**
  * Created by albert on 17-3-19.
  */
object Utils {
  val ATTR_USERNAME = "username"
  val ATTR_SESSION_STOLEN = "sessionStolen"
  val ATTR_CLIENT_ID = "ClientId"
  val CLEAN_SESSION = "cleanSession"
  val KEEP_ALIVE = "keepAlive"
  val MAX_MESSAGE_QUEUE = 1024
  private val ATTR_KEY_KEEPALIVE: AttributeKey[Integer] = AttributeKey.valueOf(KEEP_ALIVE)
  private val ATTR_KEY_CLEANSESSION: AttributeKey[Boolean] = AttributeKey.valueOf(CLEAN_SESSION)
  private val ATTR_KEY_CLIENTID: AttributeKey[String] = AttributeKey.valueOf(ATTR_CLIENT_ID)
  private val ATTR_KEY_USERNAME: AttributeKey[String] = AttributeKey.valueOf(ATTR_USERNAME)
  private val ATTR_KEY_SESSION_STOLEN: AttributeKey[Boolean] = AttributeKey.valueOf(ATTR_SESSION_STOLEN)

  def getAttribute(ctx: ChannelHandlerContext, key: AttributeKey[AnyRef]): Any = {
    val attr = ctx.channel.attr(key)
    attr.get
  }

  def keepAlive(channel: Channel, keepAlive: Int) {
    channel.attr(ATTR_KEY_KEEPALIVE).set(keepAlive)
  }

  def cleanSession(channel: Channel, cleanSession: Boolean) {
    channel.attr(ATTR_KEY_CLEANSESSION).set(cleanSession)
  }

  def cleanSession(channel: Channel): Boolean = channel.attr(ATTR_KEY_CLEANSESSION).get()

  def clientId(channel: Channel, clientID: String) {
    channel.attr(ATTR_KEY_CLIENTID).set(clientID)
  }

  def clientId(channel: Channel): String = channel.attr(ATTR_KEY_CLIENTID).get()

  def userName(channel: Channel, username: String) {
    channel.attr(ATTR_KEY_USERNAME).set(username)
  }

  def userName(channel: Channel): String = channel.attr(ATTR_KEY_USERNAME).get()

  def sessionStolen(channel: Channel, value: Boolean) {
    channel.attr(ATTR_KEY_SESSION_STOLEN).set(value)
  }

  def sessionStolen(channel: Channel): Boolean = channel.attr(ATTR_KEY_SESSION_STOLEN).get()

}
