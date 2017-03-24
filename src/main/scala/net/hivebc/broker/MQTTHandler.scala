package net.hivebc.broker

import com.typesafe.scalalogging.StrictLogging
import io.netty.channel.{ChannelHandlerAdapter, ChannelHandlerContext}
import net.hivebc.broker.metrics.Utils
import net.hivebc.parser.message._

/**
  * Created by albert on 17-3-14.
  */
class MQTTHandler extends ChannelHandlerAdapter with StrictLogging {
  private val processor: Processor = new Processor

  override def channelActive(ctx: ChannelHandlerContext): Unit = {
    logger.debug("Connect coming")
  }

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    msg match {
      case mqtt: ConnectMessage => logger.debug(mqtt.toString); processor.processConnect(ctx.channel(), mqtt)
      case mqtt: DisconnectMessage => processor.processDisconnect(ctx.channel())
      case mqtt: PingReqMessage =>
        logger.debug("Client <{}> Ping", Utils.clientId(ctx.channel()))
        val pingResp = new PingRespMessage
        ctx.writeAndFlush(pingResp)
      case _ => logger.error("Unknown Type")
    }
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = super.channelReadComplete(ctx)
}
