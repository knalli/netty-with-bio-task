package de.knallisworld.poc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Main {

	private final int port;

	public Main(final int port) {
		this.port = port;
	}

	public static void main(String... args) throws Exception {
		new Main(3000).run();
	}

	public void run() throws Exception {

		final boolean useExecutorGroup = true;
		final boolean useExecutorInHandler = false;

		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();

		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new TelnetServerInitializer(useExecutorGroup, useExecutorInHandler));

			b.bind(port).sync().channel().closeFuture().sync();
		} finally {
			System.out.println("Shutdown...");
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
			System.out.println("Shutdown!");
		}
	}

}
