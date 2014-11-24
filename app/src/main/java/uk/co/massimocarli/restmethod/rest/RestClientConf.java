package uk.co.massimocarli.restmethod.rest;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * This is a class that contains information to use to configure RestCommand executors
 * <p/>
 * Created by Massimo Carli on 04/11/2013.
 */
public class RestClientConf {

    /**
     * The default connection timeout is 30 seconds
     */
    private static final long DEFAULT_CONNECTION_TIMEOUT = TimeUnit.SECONDS.toMillis(30L);

    /**
     * The default socket timeout is 40 seconds
     */
    private static final long DEFAULT_SOCKET_TIMEOUT = TimeUnit.SECONDS.toMillis(40L);

    /**
     * The Connection timeout in milliseconds
     */
    private long mConnectionTimeout;

    /**
     * The Socket Timeout in milliseconds
     */
    private long mSoTimeout;


    private RestClientConf() {
    }

    /**
     * @return The timeout for the connection
     */
    public long getConnectionTimeout() {
        return mConnectionTimeout;
    }

    /**
     * @return The Socket Timeout
     */
    public long getSoTimeout() {
        return mSoTimeout;
    }

    /**
     * @return The default HttpClientConf with default values
     */
    public static RestClientConf getDefault() {
        final RestClientConf defaultConf = new RestClientConf();
        defaultConf.mConnectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        defaultConf.mSoTimeout = DEFAULT_SOCKET_TIMEOUT;
        return defaultConf;
    }

    /**
     * This is the static factory method that read from an jsonData with configuration data
     *
     * @param jsonData The String with JSonData with configuration issues
     * @return The RestClientConf with the data
     */
    public static RestClientConf fromJsonStream(final String jsonData) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    /**
     * This is the static factory method that read from a Map with configuration data
     *
     * @param confMapData The Map  with configuration issues
     * @return The RestClientConf with the data
     */
    public static RestClientConf fromJsonStream(final Map<String, String> confMapData) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }
}
