package net.hivebc.broker.metrics

import io.netty.buffer.ByteBuf
import io.netty.channel.{ChannelHandlerAdapter, ChannelHandlerContext, ChannelPromise}
import io.netty.util.AttributeKey

/**
  * Created by albert on 17-3-19.
  */
class ByteMetricsHandler(collector: ByteMetricsCollector) extends ChannelHandlerAdapter {
  private val ATTR_KEY_METRICS: AttributeKey[ByteMetrics] = AttributeKey.valueOf("BytesMetrics")
  private val m_collector = collector

  @throws[Exception]
  override def channelActive(ctx: ChannelHandlerContext) {
    val attr = ctx.attr(ATTR_KEY_METRICS)
    attr.set(new ByteMetrics)
    super.channelActive(ctx)
  }

  @throws[Exception]
  override def channelRead(ctx: ChannelHandlerContext, msg: Any) {
    val metrics = ctx.attr(ATTR_KEY_METRICS).get
    metrics.incrementRead(msg.asInstanceOf[ByteBuf].readableBytes)
    ctx.fireChannelRead(msg)
  }

  @throws[Exception]
  override def write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
    val metrics = ctx.attr(ATTR_KEY_METRICS).get
    metrics.incrementWrote(msg.asInstanceOf[ByteBuf].writableBytes)
    ctx.write(msg, promise)
  }

  @throws[Exception]
  override def close(ctx: ChannelHandlerContext, promise: ChannelPromise) {
    val metrics = ctx.attr(ATTR_KEY_METRICS).get
    m_collector.sumReadBytes(metrics.readBytes)
    m_collector.sumWroteBytes(metrics.wroteBytes)
    super.close(ctx, promise)
  }
}
