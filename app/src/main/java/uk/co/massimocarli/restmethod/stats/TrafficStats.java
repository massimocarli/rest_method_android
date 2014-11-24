package uk.co.massimocarli.restmethod.stats;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import uk.co.massimocarli.restmethod.util.ConnectionUtil;

/**
 * This is a Singleton that contains all the information about the content downloaded
 * with the RestMethod.
 *
 * @author Massimo Carli - 11 Jun 2013
 */
public final class TrafficStats {

    /**
     * The Tag for the Log.
     */
    private static final String TAG_LOG = TrafficStats.class.getName();

    /**
     * The number of buffered save ops
     */
    private static final int SKIP_INTERVAL = 50;

    /*
     * The Key we use for the preferences to use
     */
    private static final String PREFS_NAME = "stats.TrafficStats";
    private static final String WIFI_TOTAL_KEY = "WiFiTotalTraffic";
    private static final String MOBILE_TOTAL_KEY = "3GTotalTraffic";

    /**
     * The instance of the Singleton.
     */
    private static TrafficStats sInstance;

    private Context mContext;

    /**
     * The total 3G traffic in bytes since the last reset.
     */
    private long m3GTotalTraffic;

    /**
     * The 3G traffic in the last session. We only control the start of the session.
     */
    private long m3GSessionTraffic;

    /**
     * The total WiFi traffic in bytes since the last reset.
     */
    private long mWiFiTotalTraffic;

    /**
     * The WiFi traffic in the last session. We only control the start of the session.
     */
    private long mWiFiSessionTraffic;

    /**
     * The last time we reset a session.
     */
    private long mLastSessionTime;

    /**
     * The last time we reset all data.
     */
    private long mLastResetTime;

    /**
     * The prefs for the data
     */
    private SharedPreferences mPrefs;

    /**
     * A counter for skipping some save
     */
    private int mSaveCounter;

    /**
     * The Private constructor.
     *
     * @param context The Context for the ConnectivityManager lookup
     */
    private TrafficStats(final Context context) {
        mContext = context;
        mPrefs = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Get the reference to the Singleton for the TrafficStats.
     *
     * @param context The Context we use only the first time to get the ConnectivityManager
     * @return The TrafficStats singleton.
     */
    public static synchronized TrafficStats getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new TrafficStats(context);
            sInstance.mLastResetTime = System.currentTimeMillis();
            sInstance.mLastSessionTime = sInstance.mLastResetTime;
            sInstance.restoreStats();
        }
        return sInstance;
    }

    /**
     * This method adds the traffic and returns the total.
     *
     * @param traffic The traffic to add.
     * @return The total traffic
     */
    public synchronized long addTraffic(final long traffic) {
        // We check for the time of current connection and increment the counters
        // accordingly.
        final boolean isWifi = ConnectionUtil.isConnectedToWifi(mContext);
        if (isWifi) {
            mWiFiTotalTraffic += traffic;
            mWiFiSessionTraffic += traffic;
            Log.d(TAG_LOG, "Added " + traffic + " to the WiFi counter");
            return getTotalTraffic();
        }
        // We check if it's for 3G
        final boolean is3g = ConnectionUtil.isConnectedOrConnectingTo3G(mContext);
        if (is3g) {
            m3GTotalTraffic += traffic;
            m3GSessionTraffic += traffic;
            Log.d(TAG_LOG, "Added " + traffic + " to the 3G counter");
            return getTotalTraffic();
        } else {
            // In this case there's something wrong....
            Log.e(TAG_LOG, "Traffics seems to be neither WiFi nor 3G! ");
        }
        return m3GTotalTraffic;
    }

    public synchronized void saveStats(boolean forced) {
        mSaveCounter = forced ? 0 : mSaveCounter + 1;
        if (mSaveCounter % SKIP_INTERVAL == 0) {
            Log.d(TAG_LOG, "save stats");
            mPrefs.edit()
                    .putLong(WIFI_TOTAL_KEY, mWiFiTotalTraffic)
                    .putLong(MOBILE_TOTAL_KEY, m3GTotalTraffic)
                    .commit();
        }
    }

    public void restoreStats() {
        Log.d(TAG_LOG, "restore stats");
        if (mPrefs.contains(WIFI_TOTAL_KEY)) {
            mWiFiTotalTraffic = mPrefs.getLong(WIFI_TOTAL_KEY, 0);
        }
        if (mPrefs.contains(MOBILE_TOTAL_KEY)) {
            m3GTotalTraffic = mPrefs.getLong(MOBILE_TOTAL_KEY, 0);
        }
    }

    /**
     * @return The session 3G traffic.
     */
    public long get3GSessionTraffic() {
        return m3GSessionTraffic;
    }

    /**
     * @return The 3G total traffic.
     */
    public long get3GTotalTraffic() {
        return m3GTotalTraffic;
    }

    /**
     * @return The session Wifi traffic.
     */
    public long getWiFiSessionTraffic() {
        return mWiFiSessionTraffic;
    }

    /**
     * @return The WiFi total traffic.
     */
    public long getWiFiTotalTraffic() {
        return mWiFiTotalTraffic;
    }

    /**
     * @return The total traffic.
     */
    public long getTotalTraffic() {
        return mWiFiTotalTraffic + m3GTotalTraffic;
    }

    /**
     * Starts a new session.
     */
    public synchronized void startSession() {
        mWiFiSessionTraffic = 0L;
        m3GSessionTraffic = 0L;
        mLastSessionTime = System.currentTimeMillis();
    }

    /**
     * Clear all data.
     */
    public synchronized void reset() {
        startSession();
        mLastResetTime = mLastSessionTime;
        m3GTotalTraffic = 0L;
        mWiFiTotalTraffic = 0L;
        saveStats(true);
    }

    /**
     * @return The last time we reset all items.
     */
    public long getLastResetTime() {
        return mLastResetTime;
    }

    /**
     * @return The last time we start the session.
     */
    public long getLastSessionTime() {
        return mLastSessionTime;
    }

    /**
     * @return The last session data
     */
    public long getLastSessionData() {
        return m3GSessionTraffic + mWiFiSessionTraffic;
    }

    @Override
    public String toString() {
        return "TrafficStats [m3GTotalTraffic=" + m3GTotalTraffic
                + ", m3GSessionTraffic=" + m3GSessionTraffic
                + ", mWiFiTotalTraffic=" + mWiFiTotalTraffic
                + ", mWiFiSessionTraffic=" + mWiFiSessionTraffic
                + ", mLastSessionTime=" + mLastSessionTime
                + ", mLastResetTime=" + mLastResetTime + "]";
    }

}
