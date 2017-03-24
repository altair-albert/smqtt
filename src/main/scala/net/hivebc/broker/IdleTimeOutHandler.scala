package net.hivebc.broker

import io.netty.channel.{ChannelHandlerAdapter, ChannelHandlerContext}
import io.netty.handler.timeout.{IdleState, IdleStateEvent}

/**
  * Created by albert on 17-3-18.
  */
class IdleTimeOutHandler extends ChannelHandlerAdapter {
  @throws[Exception]
  override def userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
    evt match {
      case event: IdleStateEvent =>
        if (event.state == IdleState.ALL_IDLE) {
          ctx.fireChannelInactive()
          ctx.close()
        }
      case _ => super.userEventTriggered(ctx, evt)
    }
  }
}
