package net.hivebc.broker

import java.nio.ByteBuffer
import java.util.concurrent.{ConcurrentHashMap, ConcurrentMap, TimeUnit}

import com.typesafe.scalalogging.StrictLogging
import io.netty.channel.{Channel, ChannelPipeline}
import net.hivebc.broker.metrics.Utils
import net.hivebc.parser.builder.Utils._
import net.hivebc.parser.message.{ConnAckMessage, ConnectMessage, QoS}

/**
  * Created by albert on 17-3-18.
  */
class Processor extends StrictLogging {

  private val clientIds: ConcurrentMap[String, ConnectionDescriptor] = new ConcurrentHashMap()

  def processConnect(channel: Channel, msg: ConnectMessage) {
    logger.debug("CONNECT for client <{}>", msg.clientId)
    if (msg.version != VERSION_3_1 && msg.version != VERSION_3_1_1) {
      val badProto = new ConnAckMessage
      badProto.returnCode = ConnAckMessage.UNACCEPTABLE_PROTOCOL_VERSION
      logger.warn("processConnect sent bad proto ConnAck")
      channel.writeAndFlush(badProto)
      channel.close()
      return
    }
    if (msg.clientId == null || msg.clientId.length == 0) {
      val okResp = new ConnAckMessage
      okResp.returnCode = ConnAckMessage.IDENTIFIER_REJECTED
      channel.writeAndFlush(okResp)
      return
    }
    if (clientIds.containsKey(msg.clientId)) {
      logger.info("Found an existing connection with same client ID <{}>, forcing to close", msg.clientId)
      val oldChannel = clientIds.get(msg.clientId).channel
      oldChannel.close()
      logger.debug("Existing connection with same client ID <{}>, forced to close", msg.clientId)
    }
    val connDescr = new ConnectionDescriptor(msg.clientId, channel, msg.clearSession)
    clientIds.put(msg.clientId, connDescr)
    val keepAlive = msg.keepAlive
    logger.debug("Connect with keepAlive {} s", keepAlive)
    logger.debug("Connect create session <{}>", channel)
    Utils.cleanSession(channel, msg.clearSession)
    Utils.clientId(channel, msg.clientId)
    if (msg.willFlag) {
      val willPayload = msg.willMsg
      val bb = ByteBuffer.allocate(willPayload.length).put(willPayload).flip.asInstanceOf[ByteBuffer]
      logger.info("Session for clientID <{}> with will to topic {}", msg.clientId, msg.willTopic)
    }

    val okResp = new ConnAckMessage
    okResp.returnCode = ConnAckMessage.CONNECTION_ACCEPTED
    channel.writeAndFlush(okResp)
    val flushIntervalMs = 500
    setupAutoFlusher(channel.pipeline, flushIntervalMs)
    logger.info("CONNECT processed")
  }

  private def setupAutoFlusher(pipeline: ChannelPipeline, flushIntervalMs: Int) {
    val autoFlushHandler = new AutoFlushHandler(flushIntervalMs, TimeUnit.MILLISECONDS)
    try {
      pipeline.addAfter("idleEventHandler", "autoFlusher", autoFlushHandler)
    } catch {
      case nseex: NoSuchElementException => pipeline.addFirst("autoFlusher", autoFlushHandler)
    }
  }

  @throws[InterruptedException]
  def processDisconnect(channel: Channel) {
    channel.flush()
    val clientId = Utils.clientId(channel)
    logger.info("DISCONNECT client <{}> with clean session", clientId)
    clientIds.remove(clientId)
    channel.close()
    logger.info("DISCONNECT client <{}> finished", clientId)
  }
}
