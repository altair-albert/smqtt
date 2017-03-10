package net.hivebc.server.handler


import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

/**
  * Created by Albert on 2017/3/10.
  */
class ChildChannelHandler extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel): Unit = {
    ch.pipeline().addLast(new TimeServerHandler)
  }
}
