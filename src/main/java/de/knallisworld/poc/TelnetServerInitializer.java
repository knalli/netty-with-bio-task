package de.knallisworld.poc;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class TelnetServerInitializer extends ChannelInitializer<SocketChannel> {

	private static final StringDecoder DECODER = new StringDecoder();
	private static final StringEncoder ENCODER = new StringEncoder();

	private TelnetServerHandler serverHandler;
	private EventExecutorGroup executorGroup;

	public TelnetServerInitializer(boolean useExecutorGroup, boolean useExecutorInHandler) {

		System.out.println("Using executorGroup for handler: " + useExecutorGroup);
		System.out.println("Using executor in handler: " + useExecutorInHandler);

		if (useExecutorGroup) {
			executorGroup = new DefaultEventExecutorGroup(10);
		}
		serverHandler = new TelnetServerHandler(useExecutorInHandler);
	}

	@Override
	protected void initChannel(final SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();

		// Add the text line codec combination first,
		pipeline.addLast("framer", new DelimiterBasedFrameDecoder(
				8192, Delimiters.lineDelimiter()));
		// the encoder and decoder are static as these are sharable
		pipeline.addLast("decoder", DECODER);
		pipeline.addLast("encoder", ENCODER);

		if (executorGroup != null) {
			pipeline.addLast(executorGroup, "handler", serverHandler);
		} else {
			pipeline.addLast("handler", serverHandler);
		}
	}
}
