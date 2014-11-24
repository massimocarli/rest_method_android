package uk.co.massimocarli.restmethod.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utility class to work with {@link android.net.ConnectivityManager}.
 *
 * @author Simone Casagranda - Jul 10, 2013
 */
public class ConnectionUtil {

    /**
     * Singleton for connectivity manager
     */
    private static ConnectivityManager mConnectivityManager;

    /*
     * Private constructor
     */
    private ConnectionUtil() {
        throw new AssertionError("You must use static methods!");
    }

    public static ConnectivityManager getConnectivityManager(Context context) {
        // Checking if we already have an instance
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        return mConnectivityManager;
    }

    /**
     * Check if we are connected to a network
     */
    public static boolean isConnected(Context context) {
        final ConnectivityManager cm = getConnectivityManager(context);
        return isConnected(cm);
    }

    public static boolean isConnected(ConnectivityManager cm) {
        if (cm == null) {
            return false;
        }
        try {
            // Retrieving the connection info
            final NetworkInfo info = cm.getActiveNetworkInfo();
            return info != null && info.isConnected();
        } catch (NullPointerException npe) {
            return false;
        }
    }

    /**
     * Check if there is a Wi-Fi connection available.
     *
     * @param context that is used to retrieve the connectivity service.
     * @return <code>true</code> if there is a WiFi connection, <code>false</code> otherwise.
     */
    public static boolean isConnectedToWifi(Context context) {
        final ConnectivityManager cm = getConnectivityManager(context);
        return isConnectedToWifi(cm);
    }

    /**
     * Check if there is a Wi-Fi connection available.
     *
     * @param cm {@link android.net.ConnectivityManager} used to retrive the state of the connection.
     * @return <code>true</code> if there is a WiFi connection, <code>false</code> otherwise.
     */
    public static boolean isConnectedToWifi(ConnectivityManager cm) {
        // Checking if the ConnectivityManager is available or not
        if (cm == null) {
            return false;
        }
        try {
            // Retrieving the connection info
            final NetworkInfo info = cm.getActiveNetworkInfo();
            // Checking if there is an available connection
            if (info != null && info.isConnected()) {
                final int connectionType = info.getType();
                if (connectionType == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
            }
        } catch (NullPointerException npe) {
            return false;
        }
        return false;
    }

    /**
     * Check if there is a 3G connection available.
     *
     * @param context that is used to retrieve the connectivity service.
     * @return <code>true</code> if there is a 3G connection, <code>false</code> otherwise.
     */
    public static boolean isConnectedTo3G(Context context) {
        final ConnectivityManager cm = getConnectivityManager(context);
        return isConnectedTo3G(cm);
    }

    /**
     * Check if there is a 3G connection available.
     *
     * @param cm {@link android.net.ConnectivityManager} used to retrive the state of the connection.
     * @return <code>true</code> if there is a 3G connection, <code>false</code> otherwise.
     */
    public static boolean isConnectedTo3G(ConnectivityManager cm) {
        // Checking if the ConnectivityManager is available or not
        if (cm == null) {
            return false;
        }
        try {
            // Retrieving the connection info
            final NetworkInfo info = cm.getActiveNetworkInfo();
            // Checking if there is an available connection
            if (info != null && info.isConnected()) {
                final int connectionType = info.getType();
                if (connectionType == ConnectivityManager.TYPE_MOBILE) {
                    return true;
                }
            }
        } catch (NullPointerException npe) {
            return false;
        }
        return false;
    }

    /**
     * Check if there is a connection available (or connecting).
     *
     * @param context that is used to retrieve the connectivity service.
     * @return <code>true</code> if there is a  connection, <code>false</code> otherwise.
     */
    public static boolean isConnectedOrConnecting(Context context) {
        final ConnectivityManager cm = getConnectivityManager(context);
        return isConnectedOrConnecting(cm);
    }

    /**
     * Check if there is a connection available (or connecting).
     *
     * @param cm {@link android.net.ConnectivityManager} used to retrive the state of the connection.
     * @return <code>true</code> if there is a  connection, <code>false</code> otherwise.
     */
    public static boolean isConnectedOrConnecting(ConnectivityManager cm) {
        // Checking if the ConnectivityManager is available or not
        if (cm == null) {
            return false;
        }
        try {
            // Retrieving the connection info
            final NetworkInfo info = cm.getActiveNetworkInfo();
            // Checking if there is an available connection
            if (info != null && info.isConnectedOrConnecting()) {
                return true;
            }
        } catch (NullPointerException npe) {
            return false;
        }
        return false;
    }

    /**
     * Check if there is a Wi-Fi connection available (or connecting).
     *
     * @param context that is used to retrieve the connectivity service.
     * @return <code>true</code> if there is a WiFi connection, <code>false</code> otherwise.
     */
    public static boolean isConnectedOrConnectingToWifi(Context context) {
        final ConnectivityManager cm = getConnectivityManager(context);
        return isConnectedOrConnectingToWifi(cm);
    }

    /**
     * Check if there is a Wi-Fi connection available (or connecting).
     *
     * @param cm {@link android.net.ConnectivityManager} used to retrive the state of the connection.
     * @return <code>true</code> if there is a WiFi connection, <code>false</code> otherwise.
     */
    public static boolean isConnectedOrConnectingToWifi(ConnectivityManager cm) {
        // Checking if the ConnectivityManager is available or not
        if (cm == null) {
            return false;
        }
        try {
            // Retrieving the connection info
            final NetworkInfo info = cm.getActiveNetworkInfo();
            // Checking if there is an available connection
            if (info != null && info.isConnectedOrConnecting()) {
                final int connectionType = info.getType();
                if (connectionType == ConnectivityManager.TYPE_WIFI) {
                    return true;
                }
            }
        } catch (NullPointerException npe) {
            return false;
        }
        return false;
    }

    /**
     * Check if there is a 3G connection available (or connecting).
     *
     * @param context that is used to retrieve the connectivity service.
     * @return <code>true</code> if there is a 3G connection, <code>false</code> otherwise.
     */
    public static boolean isConnectedOrConnectingTo3G(Context context) {
        final ConnectivityManager cm = getConnectivityManager(context);
        return isConnectedOrConnectingTo3G(cm);
    }

    /**
     * Check if there is a 3G connection available (or connecting).
     *
     * @param cm {@link android.net.ConnectivityManager} used to retrieve the state of the connection.
     * @return <code>true</code> if there is a 3G connection, <code>false</code> otherwise.
     */
    public static boolean isConnectedOrConnectingTo3G(ConnectivityManager cm) {
        // Checking if the ConnectivityManager is available or not
        if (cm == null) {
            return false;
        }
        // Retrieving the connection info
        final NetworkInfo info = cm.getActiveNetworkInfo();
        // Checking if there is an available connection
        if (info != null && info.isConnectedOrConnecting()) {
            final int connectionType = info.getType();
            if (connectionType == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }
}
