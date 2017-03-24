package net.hivebc.broker.metrics

import java.util.concurrent.atomic.AtomicLong

/**
  * Created by albert on 17-3-19.
  */
class ByteMetricsCollector {
  private val readBytes = new AtomicLong
  private val wroteBytes = new AtomicLong

  def computeMetrics(): ByteMetrics = {
    val allMetrics = new ByteMetrics
    allMetrics.incrementRead(readBytes.get)
    allMetrics.incrementWrote(wroteBytes.get)
    allMetrics
  }

  def sumReadBytes(count: Long) {
    readBytes.getAndAdd(count)
  }

  def sumWroteBytes(count: Long) {
    wroteBytes.getAndAdd(count)
  }
}
