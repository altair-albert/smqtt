package net.hivebc.broker

import java.util.concurrent.{ScheduledFuture, TimeUnit}

import io.netty.channel.{ChannelHandlerAdapter, ChannelHandlerContext}

/**
  * Created by albert on 17-3-18.
  */
class AutoFlushHandler(writerIdleTime: Long, unit: TimeUnit) extends ChannelHandlerAdapter {
  private val MIN_TIMEOUT_NANOS = TimeUnit.MILLISECONDS.toNanos(1)
  private val writerIdleTimeNanos = Math.max(unit.toNanos(writerIdleTime), MIN_TIMEOUT_NANOS)
  @volatile private var writerIdleTimeout: ScheduledFuture[_] = _
  @volatile private var lastWriteTime = 0L
  @volatile private var state = 0


  @throws[Exception]
  override def handlerAdded(ctx: ChannelHandlerContext) {
    if (ctx.channel.isActive && ctx.channel.isRegistered) {
      // channelActive() event has been fired already, which means this.channelActive() will
      // not be invoked. We have to initialize here instead.
      initialize(ctx)
    } else {
      // channelActive() event has not been fired yet.  this.channelActive() will be invoked
      // and initialization will occur there.
    }
  }

  @throws[Exception]
  override def handlerRemoved(ctx: ChannelHandlerContext) {
    destroy()
  }

  @throws[Exception]
  override def channelRegistered(ctx: ChannelHandlerContext) {
    if (ctx.channel.isActive) initialize(ctx)
    super.channelRegistered(ctx)
  }

  @throws[Exception]
  override def channelActive(ctx: ChannelHandlerContext) {
    initialize(ctx)
    super.channelActive(ctx)
  }

  @throws[Exception]
  override def channelInactive(ctx: ChannelHandlerContext) {
    destroy()
    super.channelInactive(ctx)
  }

  private def initialize(ctx: ChannelHandlerContext) {
    state match {
      case 1 =>
      case 2 =>
        return
    }
    state = 1
    val loop = ctx.executor
    lastWriteTime = System.nanoTime
    writerIdleTimeout = loop.schedule(new WriterIdleTimeoutTask(ctx), writerIdleTimeNanos, TimeUnit.NANOSECONDS)
  }

  private def destroy() {
    state = 2
    if (writerIdleTimeout != null) {
      writerIdleTimeout.cancel(false)
      writerIdleTimeout = null
    }
  }

  /**
    * Is called when the write timeout expire.
    */
  @throws[Exception]
  protected def channelIdle(ctx: ChannelHandlerContext) {
    ctx.channel.flush()
  }

  final private class WriterIdleTimeoutTask private[broker](val ctx: ChannelHandlerContext) extends Runnable {
    override def run() {
      if (!ctx.channel.isOpen) return
      val nextDelay = writerIdleTimeNanos - (System.nanoTime - lastWriteTime)
      if (nextDelay <= 0) {
        // Writer is idle - set a new timeout and notify the callback.
        writerIdleTimeout = ctx.executor.schedule(this, writerIdleTimeNanos, TimeUnit.NANOSECONDS)
        try {
          channelIdle(ctx)
        } catch {
          case t: Throwable => ctx.fireExceptionCaught(t)
        }
      } else {
        // Write occurred before the timeout - set a new timeout with shorter delay.
        writerIdleTimeout = ctx.executor.schedule(this, nextDelay, TimeUnit.NANOSECONDS)
      }
    }
  }

}
