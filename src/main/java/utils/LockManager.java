package utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LockManager {
    private static final Map<Integer, LockInfo> lockMap = new ConcurrentHashMap<>();
    private static final long LOCK_TIMEOUT = 5 * 60 * 1000;

    public static synchronized boolean acquire(int recordId, int userId, boolean force) {
        LockInfo info = lockMap.get(recordId);

        if (info != null) {
            if (force) {
                lockMap.remove(recordId);
            } else {
                if (System.currentTimeMillis() - info.timestamp > LOCK_TIMEOUT) {
                    lockMap.remove(recordId);
                } else {
                    return false;
                }
            }
        }

        LockInfo newInfo = new LockInfo();
        newInfo.userId = userId;
        newInfo.timestamp = System.currentTimeMillis();
        lockMap.put(recordId, newInfo);
        return true;
    }

    public static synchronized void release(int recordId, int userId) {
        LockInfo info = lockMap.get(recordId);
        if (info != null && info.userId == userId) {
            lockMap.remove(recordId);
        }
    }

    public static boolean isLocked(int recordId) { return lockMap.containsKey(recordId); }
    public static class LockInfo {
        public int userId;
        public long timestamp;
    }
}
