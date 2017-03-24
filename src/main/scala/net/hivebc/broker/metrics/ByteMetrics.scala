package net.hivebc.broker.metrics

/**
  * Created by albert on 17-3-19.
  */
class ByteMetrics {
  private var m_readBytes: Long = 0
  private var m_wroteBytes: Long = 0

  private[metrics] def incrementRead(numBytes: Long) {
    m_readBytes += numBytes
  }

  private[metrics] def incrementWrote(numBytes: Long) {
    m_wroteBytes += numBytes
  }

  def readBytes: Long = m_readBytes

  def wroteBytes: Long = m_wroteBytes
}
