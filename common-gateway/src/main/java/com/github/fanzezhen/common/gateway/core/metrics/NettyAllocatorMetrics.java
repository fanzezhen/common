package com.github.fanzezhen.common.gateway.core.metrics;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.netty.buffer.PoolArenaMetric;
import io.netty.buffer.PooledByteBufAllocator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * todo complete all stats
 * @author zezhen.fan
 */
public class NettyAllocatorMetrics implements MeterBinder {


	MetricHolder metricHolder;



	@Override
	public void bindTo(@NotNull MeterRegistry registry) {

		PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;

		List<PoolArenaMetric> poolArenaMetrics = allocator.metric().directArenas();

		metricHolder = new MetricHolder(poolArenaMetrics);

		Gauge.builder("netty.allocator.memory.used", () -> allocator.metric().usedDirectMemory())
				.tags("type", "direct")
				.register(registry);

		Gauge.builder("netty.allocator.allocation.count", () -> metricHolder.numAllocations())
				.tags("type", "direct")
				.register(registry);

		Gauge.builder("netty.allocator.allocation.tiny.count", () -> metricHolder.numTinyAllocations())
				.tags("type", "direct")
				.register(registry);

		Gauge.builder("netty.allocator.allocation.small.count", () -> metricHolder.numSmallAllocations())
				.tags("type", "direct")
				.register(registry);

		Gauge.builder("netty.allocator.allocation.normal.count", () -> metricHolder.numNormalAllocations())
				.tags("type", "direct")
				.register(registry);

		Gauge.builder("netty.allocator.allocation.huge.count", () -> metricHolder.numHugeAllocations())
				.tags("type", "direct")
				.register(registry);


	}

	static class MetricHolder {
		List<PoolArenaMetric> poolArenaMetrics;
		private volatile long lastAllocation = 0;
		private volatile long lastAllocationTiny = 0;
		private volatile long lastAllocationSmall = 0;
		private volatile long lastAllocationNormal = 0;
		private volatile long lastAllocationHuge = 0;

		public MetricHolder(List<PoolArenaMetric> poolArenaMetrics) {
			this.poolArenaMetrics = poolArenaMetrics;
		}

		long numAllocations() {
			long sum = poolArenaMetrics.stream().map(PoolArenaMetric::numAllocations).mapToLong(a -> a).sum();
			long cur = sum - lastAllocation;
			lastAllocation = sum;
			return cur;
		}

		long numTinyAllocations() {
			long sum = 0;
			long cur = sum - lastAllocationTiny;
			lastAllocationTiny = sum;
			return cur;
		}

		long numSmallAllocations() {
			long sum = poolArenaMetrics.stream().map(PoolArenaMetric::numSmallAllocations).mapToLong(a -> a).sum();
			long cur = sum - lastAllocationSmall;
			lastAllocationSmall = sum;
			return cur;
		}

		long numNormalAllocations() {
			long sum = poolArenaMetrics.stream().map(PoolArenaMetric::numNormalAllocations).mapToLong(a -> a).sum();
			long cur = sum - lastAllocationNormal;
			lastAllocationNormal = sum;
			return cur;
		}

		long numHugeAllocations() {
			long sum = poolArenaMetrics.stream().map(PoolArenaMetric::numHugeAllocations).mapToLong(a -> a).sum();
			long cur = sum - lastAllocationHuge;
			lastAllocationHuge = sum;
			return cur;
		}

	}
}
