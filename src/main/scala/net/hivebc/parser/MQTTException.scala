package net.hivebc.parser

/**
  * Created by albert on 17-3-12.
  */
class MQTTException(msg: String, cause: Throwable) extends RuntimeException(msg, cause) {
  def this(msg: String) {
    this(msg, null)
  }

  def this(cause: Throwable) {
    this("MQTTException", cause)
  }

  def this() {
    this("MQTTException", null)
  }
}
