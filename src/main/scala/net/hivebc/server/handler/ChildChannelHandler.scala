package net.hivebc.server.handler


import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.logging.{LogLevel, LoggingHandler}
import net.hivebc.broker.metrics.{ByteMetricsCollector, ByteMetricsHandler, MessageMetricsCollector, MessageMetricsHandler}
import net.hivebc.broker.{IdleTimeOutHandler, MQTTHandler}
import net.hivebc.parser.builder.{MQTTDecoder, MQTTEncoder}

/**
  * Created by Albert on 2017/3/10.
  */
class ChildChannelHandler extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel): Unit = {
    ch.pipeline()
      .addFirst("idleEventHandler", new IdleTimeOutHandler)
      .addFirst("bytemetrics", new ByteMetricsHandler(new ByteMetricsCollector))
      .addLast("decoder", new MQTTDecoder)
      .addLast("encoder", new MQTTEncoder)
      .addLast("metrics", new MessageMetricsHandler(new MessageMetricsCollector))
      .addLast("handler", new MQTTHandler)
      .addLast("logger", new LoggingHandler(LogLevel.DEBUG))
  }
}
