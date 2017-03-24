package net.hivebc.server

import com.typesafe.scalalogging.Logger
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import net.hivebc.server.handler.ChildChannelHandler

/**
  * Created by Albert on 2017/3/10.
  */
class Server {
  def bind(port: Int): Unit = {
    val bossGroup = new NioEventLoopGroup()
    val workerGroup = new NioEventLoopGroup()
    val b = new ServerBootstrap()
    try {
      b.group(bossGroup, workerGroup)
        .channel(classOf[NioServerSocketChannel])
        .option[Integer](ChannelOption.SO_BACKLOG, 1024)
        .childHandler(new ChildChannelHandler)

      val f = b.bind(port).sync()
      f.channel().closeFuture().sync()
    } finally {
      bossGroup.shutdownGracefully()
      workerGroup.shutdownGracefully()
    }
  }
}


object Server {
  private val logger = Logger(classOf[Server])

  def main(args: Array[String]) {
    logger.info("Server Start!")
    val server = new Server
    server.bind(1883)
  }
}