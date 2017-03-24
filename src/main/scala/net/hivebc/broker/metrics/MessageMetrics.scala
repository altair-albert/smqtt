package net.hivebc.broker.metrics

/**
  * Created by albert on 17-3-19.
  */
class MessageMetrics {
  private var m_messagesRead: Long = 0
  private var m_messageWrote: Long = 0

  def incrementRead(numMessages: Long) {
    m_messagesRead += numMessages
  }

  def incrementWrote(numMessages: Long) {
    m_messageWrote += numMessages
  }

  def messagesRead: Long = m_messagesRead

  def messagesWrote: Long = m_messageWrote

}
