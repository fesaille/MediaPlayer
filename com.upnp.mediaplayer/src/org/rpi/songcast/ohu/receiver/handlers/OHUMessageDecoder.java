package org.rpi.songcast.ohu.receiver.handlers;

/**
 * Create a OHUMesage from the ByteBuf
 * Which is then handled by the handlers in the pipeline
 */

import java.nio.charset.Charset;
import java.util.List;

import org.apache.log4j.Logger;
import org.rpi.songcast.common.SongcastMessage;
import org.rpi.songcast.ohu.receiver.OHUChannelInitializer;
import org.rpi.songcast.ohu.receiver.messages.OHUMessageAudio;
import org.rpi.songcast.ohu.receiver.messages.OHUMessageMetaText;
import org.rpi.songcast.ohu.receiver.messages.OHUMessageSlave;
import org.rpi.songcast.ohu.receiver.messages.OHUMessageTrack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;

public class OHUMessageDecoder extends MessageToMessageDecoder<DatagramPacket> {

	private Logger log = Logger.getLogger(this.getClass());
	private OHUChannelInitializer initializer = null;
	private int largestPacketSize = 0;
	private int iCount = 0;

	public OHUMessageDecoder(OHUChannelInitializer initializer) {
		this.initializer = initializer;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
		/*
		if (msg.content().readableBytes() > largestPacketSize) {
			largestPacketSize = msg.content().readableBytes();
			log.debug("LargestPacketSize: " + largestPacketSize);
		}

		if (iCount % 1000 == 0) {
			log.debug("LargestPacketSize: " + largestPacketSize);
			
		}
		iCount++;
		*/
		if (msg instanceof DatagramPacket) {

			ByteBuf buf = msg.content();
			int type = buf.getByte(5) & ~0x80;
			SongcastMessage message = null;
			switch (type) {
			case 3:// Audio
				message = new OHUMessageAudio(buf, initializer.hasSlaves());
				out.add(message);
				break;
			case 4:// Track
				message = new OHUMessageTrack(buf);
				out.add(message);
				break;
			case 5:// MetaText
				message = new OHUMessageMetaText(buf);
				out.add(message);
				break;
			case 6:// Slave
				message = new OHUMessageSlave(buf);
				out.add(message);
				break;
			default:
				log.info("Unknown Message. Type: " + type + " Buffer: " + buf.toString(Charset.forName("utf-8")));
				break;
			}

		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error("Error. OHUMessageDecoder: ",cause);
		ctx.close();
	}

	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		log.debug("Channel Registered: " + ctx.name());
		super.channelRegistered(ctx);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.debug("Channel Actvie: " + ctx.name());
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.debug("Channel Inactive: " + ctx.name());
		super.channelInactive(ctx);
	};

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		log.debug("Channel Unregistered: " + ctx.name());
		super.channelUnregistered(ctx);
	}
}
