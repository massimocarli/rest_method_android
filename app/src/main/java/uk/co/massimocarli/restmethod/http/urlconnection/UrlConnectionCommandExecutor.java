package uk.co.massimocarli.restmethod.http.urlconnection;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import uk.co.massimocarli.restmethod.http.deserializer.Deserializer;
import uk.co.massimocarli.restmethod.rest.RestClientConf;
import uk.co.massimocarli.restmethod.rest.RestCommand;
import uk.co.massimocarli.restmethod.rest.RestCommandExecutor;
import uk.co.massimocarli.restmethod.rest.RestCommandResult;
import uk.co.massimocarli.restmethod.stats.TrafficCounterDecorator;
import uk.co.massimocarli.restmethod.stats.TrafficStats;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * This is the RestExecutor implementation that uses Apache UrlConnection
 * <p/>
 * Created by Massimo Carli  on 01/11/2013.
 */
public class UrlConnectionCommandExecutor implements RestCommandExecutor {

    /**
     * The Tag for the log of this class
     */
    private static final String TAG_LOG = UrlConnectionCommandExecutor.class.getName();

    /**
     * The static instance of the Singleton
     */
    private static UrlConnectionCommandExecutor sInstance;

    /**
     * The Configuration for the HttpClient
     */
    private RestClientConf mHttpClientConf;

    /**
     * The private constructor of the Singleton implementation
     */
    private UrlConnectionCommandExecutor(final RestClientConf httpClientConf) {
        this.mHttpClientConf = httpClientConf;
    }

    /**
     * This is the static factory method to access the HttpClientRestCommandExecutor Singleton
     *
     * @param httpClientConf The object that encapsulate the configuration of this  HttpClientRestCommandExecutor
     * @return The HttpClientRestCommandExecutor singleton
     */
    public synchronized static UrlConnectionCommandExecutor get(final RestClientConf httpClientConf) {
        if (sInstance == null) {
            sInstance = new UrlConnectionCommandExecutor(httpClientConf);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
                // Useful for device before Froyo
                System.setProperty("http.keepAlive", "false");
            }
        }
        return sInstance;
    }

    /**
     * This version of the static Factory method returns a new instance at every call
     *
     * @param httpClientConf The object that encapsulate the configuration of this  HttpClientRestCommandExecutor
     * @return A new instance of the HttpClientRestCommandExecutor
     */
    public synchronized static UrlConnectionCommandExecutor create(final RestClientConf httpClientConf) {
        return new UrlConnectionCommandExecutor(httpClientConf);
    }

    /**
     * This version of the static Factory method returns a new instance at every call
     *
     * @return A new instance of the HttpClientRestCommandExecutor with default configuration
     */
    public synchronized static UrlConnectionCommandExecutor create() {
        return new UrlConnectionCommandExecutor(RestClientConf.getDefault());
    }

    @Override
    public <T> RestCommandResult<T> execute(final Context context, RestCommand restCommand,
                                            Deserializer<? extends T> deserializer) throws RestCommandException {
        // The Connection
        HttpURLConnection httpUrlConnection = null;
        // The InputStream to read from
        InputStream inputStream = null;
        // We test if the traffic stats is enabled
        final boolean trafficStatsEnabled = restCommand.isTrafficStatsEnabled();
        TrafficCounterDecorator trafficDeserializer = null;
        if (trafficStatsEnabled) {
            trafficDeserializer = new TrafficCounterDecorator<T>(deserializer);
            deserializer = trafficDeserializer;
        }
        try {
            // We get the Http or Https implementations depending on the protocol. If https
            // the HttpsURLConnection is a specialisation of HTTPUrlConnection
            httpUrlConnection = UrlConnectionUtility.fromRestCommand(context, restCommand);
            // Here we configure it with some specific configuration issues
            if (mHttpClientConf != null) {
                configureUrlConnection(httpUrlConnection);
            }
            // We get the InputStream from the connection
            final int httpResponseCode = httpUrlConnection.getResponseCode();
            if (httpResponseCode >= HttpURLConnection.HTTP_OK && httpResponseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
                // If the code is ok we have to read from the inputStream
                inputStream = httpUrlConnection.getInputStream();
            } else {
                // If the code is ok we have to read from the errorStream
                inputStream = httpUrlConnection.getErrorStream();
            }
            final String httpResponseMessage = httpUrlConnection.getResponseMessage();
            // We parse the stream with the given Deserializer
            final T result = deserializer.realise(inputStream, context);
            // We get the response
            final RestCommandResult restCommandResult = RestCommandResult.get(result, httpResponseCode,
                                                                              httpResponseMessage);
            // If the traffic is enabled we read the data and add to the stats
            if (trafficStatsEnabled) {
                final long dataRead = trafficDeserializer.getDataCount();
                Log.d(TAG_LOG, "Traffic stats enabled and data read: " + dataRead);
                TrafficStats.getInstance(context).addTraffic(dataRead);
                restCommandResult.setTrafficData(dataRead);
            }
            // We return the response
            return restCommandResult;
        } catch (IOException e) {
            e.printStackTrace();
            // In this case we throw the exception up
            Log.e(TAG_LOG, "Error executing httpRequest", e);
            throw new RestCommandException("Error executing httpRequest", e);
        } finally {
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }
        }
    }

    /**
     * This is an utility method that configure the HttpURLConnection with some information we can pass
     * from outside in a some way
     *
     * @param httpURLConnection The HttpURLConnection instance to configure
     */
    private void configureUrlConnection(final HttpURLConnection httpURLConnection) {
        httpURLConnection.setConnectTimeout((int) mHttpClientConf.getConnectionTimeout());
        httpURLConnection.setReadTimeout((int) mHttpClientConf.getSoTimeout());
    }

}
