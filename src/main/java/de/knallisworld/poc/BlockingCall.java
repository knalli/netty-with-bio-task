package de.knallisworld.poc;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

import java.util.Date;
import java.util.concurrent.Callable;

public class BlockingCall implements Callable<ChannelFuture> {

	private final ChannelHandlerContext ctx;

	public BlockingCall(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public ChannelFuture call() throws Exception {
		final String threadName = Thread.currentThread().getName();
		System.out.println(threadName + ": Starting...");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			System.err.println(threadName + ": Failed: " + e.getMessage());
		}
		System.out.println(threadName + ": Finished");
		return ctx.write("Task finished at " + new Date());
	}
}
