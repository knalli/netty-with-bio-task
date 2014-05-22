package de.knallisworld.poc;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.InetAddress;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@ChannelHandler.Sharable
public class TelnetServerHandler extends SimpleChannelInboundHandler<String> {

	private static final Logger logger = Logger.getLogger(TelnetServerHandler.class.getName());

	private final boolean useExecutor;

	public TelnetServerHandler(boolean useExecutor) {
		super();
		this.useExecutor = useExecutor;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// Send greeting for a new connection.
		ctx.write(
				"Welcome to " + InetAddress.getLocalHost().getHostName() + "!\r\n");
		ctx.write("It is " + new Date() + " now.\r\n");
		ctx.flush();
	}

	@Override
	public void channelRead0(ChannelHandlerContext ctx, String request) throws Exception {

		ChannelFuture future;

		if (useExecutor) {
			future = ctx.executor().submit(new BlockingCall(ctx)).get();
		} else {
			future = new BlockingCall(ctx).call();
		}

		future.addListener(ChannelFutureListener.CLOSE);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.log(
				Level.WARNING,
				"Unexpected exception from downstream.", cause);
		ctx.close();
	}

}
