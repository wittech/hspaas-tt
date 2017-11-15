package com.huashi.common.util;

import java.security.SecureRandom;

/**
 * 自定义 ID 生成器 ID 生成规则: ID长达 64 bits
 *
 * | 41 bits: Timestamp (毫秒) | 2 bits: 区域（机房） | 10 bits: 机器编号 | 11 bits: 序列号 |
 */
public final class IdGenerator {

	private long twepoch = 1288834974657L; // Thu, 04 Nov 2010 01:42:54 GMT
	// 区域标志位数

	private final static long regionIdBits = 2L;

	// 机器标识位数
	private final static long workerIdBits = 10L;

	// 序列号识位数
	private final static long sequenceBits = 11L;

	// 区域标志ID最大值
	private final static long MAX_REGIONID = -1L ^ (-1L << regionIdBits);

	// 机器ID最大值
	private final static long MAX_WORKERID = -1L ^ (-1L << workerIdBits);

	// 序列号ID最大值
	private final static long SEQUENCE_MASK = -1L ^ (-1L << sequenceBits);

	// 机器ID偏左移10位
	private final static long WORKERID_SHIFT = sequenceBits;

	// 业务ID偏左移20位
	private final static long REGIONID_SHIFT = sequenceBits + workerIdBits;

	// 时间毫秒左移23位
	private final static long TIMESTAMP_LEFTSHIFT = sequenceBits + workerIdBits
			+ regionIdBits;

	private static long lastTimestamp = -1L;

	private long sequence = 0L;

	private final long workerId;

	private final long regionId;

	// public static IdGenerator idGenerator = new
	// IdGenerator(RandomUtils.nextLong(0, 100));

	public IdGenerator(long workerId, long regionId) {

		// 如果超出范围就抛出异常
		if (workerId > MAX_WORKERID || workerId < 0) {
			throw new IllegalArgumentException(
					"worker Id can't be greater than %d or less than 0");
		}
		if (regionId > MAX_REGIONID || regionId < 0) {
			throw new IllegalArgumentException(
					"regionId Id can't be greater than %d or less than 0");
		}

		this.workerId = workerId;
		this.regionId = regionId;
	}

	public IdGenerator(long workerId) {
		// 如果超出范围就抛出异常
		if (workerId > MAX_WORKERID || workerId < 0) {
			throw new IllegalArgumentException(
					"worker Id can't be greater than %d or less than 0");
		}
		this.workerId = workerId;
		this.regionId = 0;
	}

	// public static long getId() {
	// return idGenerator.generate();
	// }

	public long generate() {
		return this.nextId(false, 0);
	}

	public long generate(long busid) {
		return this.nextId(true, busid);
	}

	/**
	 * 实际产生代码的
	 *
	 * @param isPadding
	 * @param busId
	 *
	 * @return
	 */
	private synchronized long nextId(boolean isPadding, long busId) {

		long timestamp = timeGen();
		long paddingnum = regionId;

		if (isPadding) {
			paddingnum = busId;
		}

		if (timestamp < lastTimestamp) {
			try {
				throw new Exception(
						"Clock moved backwards.  Refusing to generate id for "
								+ (lastTimestamp - timestamp) + " milliseconds");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 如果上次生成时间和当前时间相同,在同一毫秒内
		if (lastTimestamp == timestamp) {
			// sequence自增，因为sequence只有10bit，所以和sequenceMask相与一下，去掉高位
			sequence = (sequence + 1) & SEQUENCE_MASK;
			// 判断是否溢出,也就是每毫秒内超过1024，当为1024时，与sequenceMask相与，sequence就等于0
			if (sequence == 0) {
				// 自旋等待到下一毫秒
				timestamp = tailNextMillis(lastTimestamp);
			}
		} else {
			// 如果和上次生成时间不同,重置sequence，就是下一毫秒开始，sequence计数重新从0开始累加,
			// 为了保证尾数随机性更大一些,最后一位设置一个随机数
			sequence = new SecureRandom().nextInt(10);
		}

		lastTimestamp = timestamp;

		return ((timestamp - twepoch) << TIMESTAMP_LEFTSHIFT)
				| (paddingnum << REGIONID_SHIFT) | (workerId << WORKERID_SHIFT)
				| sequence;
	}

	// 防止产生的时间比之前的时间还要小（由于NTP回拨等问题）,保持增量的趋势.
	private long tailNextMillis(final long lastTimestamp) {
		long timestamp = this.timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = this.timeGen();
		}
		return timestamp;
	}

	// 获取当前的时间戳
	protected long timeGen() {
		return System.currentTimeMillis();
	}
	
	/**
	 * 
	   * TODO 生成ID
	   * @return
	 */
	public static long generatex(){
		IdGenerator idGenerator = new IdGenerator(1);
		return idGenerator.generate();
	}

	public static void main(String args[]) {

		IdGenerator idGenerator = new IdGenerator(1);
		
		IdGenerator idGenerator1 = new IdGenerator(1);

		for (int i = 0; i < 1000; i++) {
			System.out.println("#"+idGenerator.generate());
			System.out.println(idGenerator1.generate());
		}

	}

}
