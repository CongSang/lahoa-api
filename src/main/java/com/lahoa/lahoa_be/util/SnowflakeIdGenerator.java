package com.lahoa.lahoa_be.util;

public class SnowflakeIdGenerator {

    // 1. Định nghĩa các mốc quan trọng
    private static final long EPOCH = 1712912400000L; // Một mốc thời gian cố định
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    // 2. Tính toán các giới hạn (Max values)
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    // 3. Các vị trí dịch bit (Shifts)
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("Worker ID vượt quá giới hạn");
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID vượt quá giới hạn");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    // 4. Hàm tạo ID chính (Thread-safe)
    public synchronized long nextId() {
        long timestamp = timeGen();

        // Xử lý nếu thời gian hệ thống bị lùi lại (Clock move backwards)
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Lỗi clock hệ thống!");
        }

        // Nếu tạo nhiều ID trong cùng 1 mili giây
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 5. Kết hợp các thành phần bằng phép toán Bitwise OR (|) và Dịch bit (<<)
        return ((timestamp - EPOCH) << TIMESTAMP_LEFT_SHIFT) |
                (datacenterId << DATACENTER_ID_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }
}