package net.hivebc.broker.metrics

import io.netty.channel.{ChannelHandlerAdapter, ChannelHandlerContext, ChannelPromise}
import io.netty.util.AttributeKey

/**
  * Created by albert on 17-3-19.
  */
class MessageMetricsHandler(collector: MessageMetricsCollector) extends ChannelHandlerAdapter {
  private val ATTR_KEY_METRICS: AttributeKey[MessageMetrics] = AttributeKey.valueOf("MessageMetrics")
  private val m_collector = collector

  @throws[Exception]
  override def channelActive(ctx: ChannelHandlerContext) {
    val attr = ctx.attr(ATTR_KEY_METRICS)
    attr.set(new MessageMetrics)
    super.channelActive(ctx)
  }

  @throws[Exception]
  override def channelRead(ctx: ChannelHandlerContext, msg: Any) {
    val metrics = ctx.attr(ATTR_KEY_METRICS).get
    metrics.incrementRead(1)
    ctx.fireChannelRead(msg)
  }

  @throws[Exception]
  override def write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
    val metrics = ctx.attr(ATTR_KEY_METRICS).get
    metrics.incrementWrote(1)
    ctx.write(msg, promise)
  }

  @throws[Exception]
  override def close(ctx: ChannelHandlerContext, promise: ChannelPromise) {
    val metrics = ctx.attr(ATTR_KEY_METRICS).get
    m_collector.sumReadMessages(metrics.messagesRead)
    m_collector.sumWroteMessages(metrics.messagesWrote)
    super.close(ctx, promise)
  }
}
