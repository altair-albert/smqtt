package net.hivebc.broker.metrics

import java.util.concurrent.atomic.AtomicLong

/**
  * Created by albert on 17-3-19.
  */
class MessageMetricsCollector {
  private val readMsgs = new AtomicLong
  private val wroteMsgs = new AtomicLong

  def computeMetrics(): MessageMetrics = {
    val allMetrics = new MessageMetrics
    allMetrics.incrementRead(readMsgs.get)
    allMetrics.incrementWrote(wroteMsgs.get)
    allMetrics
  }

  def sumReadMessages(count: Long) {
    readMsgs.getAndAdd(count)
  }

  def sumWroteMessages(count: Long) {
    wroteMsgs.getAndAdd(count)
  }
}
