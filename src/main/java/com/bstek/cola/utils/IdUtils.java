package com.bstek.cola.utils;

import java.util.UUID;

public final class IdUtils {

	//开始时间截 
	private static final long TWEPOCH = 1420041600000L;
	
	//时间截向左移 22 位
	private static final long TIMESTAMP_LEFT_SHIFT = 22L;
	
	//生成序列的掩码，这里为 4095 (0b111111111111=0xfff=4095)
	private static final long SEQUENCE_MASK = -1L ^ (-1L << 12L);
	
	//毫秒内序列(0~4095)
	private static long SEQUENCE = 0L;
	
	//上次生成 ID 的时间截 
	private static long LAST_TIMESTAMP = -1L;
	
	/**
	 * 获取UUID
	 * @return
	 */
	public static String getUUID() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 获取唯一的递增ID
	 * @return
	 */
	public static long getOnlyNumberId() {
		long timestamp = System.currentTimeMillis();
		// 如果当前时间小于上一次 ID 生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
		if (timestamp < LAST_TIMESTAMP) {
			throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", LAST_TIMESTAMP - timestamp));
		}
		// 如果是同一时间生成的，则进行毫秒内序列
		if (LAST_TIMESTAMP == timestamp) {
			SEQUENCE = (SEQUENCE + 1) & SEQUENCE_MASK;
			// 毫秒内序列溢出
			if (SEQUENCE == 0) {
				// 阻塞到下一个毫秒,获得新的时间戳
				timestamp = tilNextMillis(LAST_TIMESTAMP);
			}
		} else {
			SEQUENCE = 0L; // 时间戳改变，毫秒内序列重置
		}
		// 上次生成 ID 的时间截
		LAST_TIMESTAMP = timestamp;
		// 移位并通过或运算拼到一起组成 64 位的 ID
		return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT);
	}

	/** * 
	 * 阻塞到下一个毫秒，直到获得新的时间戳 
	 * @param lastTimestamp 上次生成 ID 的时间截 
	 * @return 当前时间戳 
	 */
	protected static long tilNextMillis(long lastTimestamp) {
		long timestamp = System.currentTimeMillis();
		while (timestamp <= lastTimestamp) {
			timestamp = System.currentTimeMillis();
		}
		return timestamp;
	}

}
