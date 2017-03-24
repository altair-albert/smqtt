package net.hivebc.parser.builder

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import net.hivebc.parser.message.AbstractMessage

/**
  * Created by albert on 17-3-12.
  */
trait Encoder[A <: AbstractMessage] {
  @throws[Exception]
  def encode(ctx: ChannelHandlerContext, msg: A, out: ByteBuf)
}
