package uk.co.massimocarli.restmethod.http.client;

import android.content.Context;
import android.util.Log;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import uk.co.massimocarli.restmethod.http.deserializer.Deserializer;
import uk.co.massimocarli.restmethod.http.https.HttpsClient;
import uk.co.massimocarli.restmethod.rest.RestClientConf;
import uk.co.massimocarli.restmethod.rest.RestCommand;
import uk.co.massimocarli.restmethod.rest.RestCommandExecutor;
import uk.co.massimocarli.restmethod.rest.RestCommandResult;
import uk.co.massimocarli.restmethod.stats.TrafficCounterDecorator;
import uk.co.massimocarli.restmethod.stats.TrafficStats;

import java.io.IOException;


/**
 * This is the RestExecutor implementation that uses Apache HttpClient
 * <p/>
 * Created by Massimo Carli  on 01/11/2013.
 */
public class HttpClientRestCommandExecutor implements RestCommandExecutor {

    /**
     * The Tag for the log of this class
     */
    private static final String TAG_LOG = HttpClientRestCommandExecutor.class.getName();

    /**
     * The static instance of the Singleton
     */
    private static HttpClientRestCommandExecutor sInstance;

    /**
     * The Configuration for the HttpClient
     */
    private RestClientConf mHttpClientConf;

    /**
     * The private constructor of the Singleton implementation
     */
    private HttpClientRestCommandExecutor(final RestClientConf httpClientConf) {
        this.mHttpClientConf = httpClientConf;
    }

    /**
     * This is the static factory method to access the HttpClientRestCommandExecutor Singleton
     *
     * @param httpClientConf The object that encapsulate the configuration of this  HttpClientRestCommandExecutor
     * @return The HttpClientRestCommandExecutor singleton
     */
    public synchronized static HttpClientRestCommandExecutor get(final RestClientConf httpClientConf) {
        if (sInstance == null) {
            sInstance = new HttpClientRestCommandExecutor(httpClientConf);
        }
        return sInstance;
    }

    /**
     * This version of the static Factory method returns a new instance at every call
     *
     * @param httpClientConf The object that encapsulate the configuration of this  HttpClientRestCommandExecutor
     * @return A new instance of the HttpClientRestCommandExecutor
     */
    public synchronized static HttpClientRestCommandExecutor create(final RestClientConf httpClientConf) {
        return new HttpClientRestCommandExecutor(httpClientConf);
    }

    /**
     * This version of the static Factory method returns a new instance at every call
     *
     * @return A new instance of the HttpClientRestCommandExecutor with default configuration
     */
    public synchronized static HttpClientRestCommandExecutor create() {
        return new HttpClientRestCommandExecutor(RestClientConf.getDefault());
    }


    @Override
    public <T> RestCommandResult<T> execute(Context context, RestCommand restCommand, Deserializer<? extends T> deserializer) throws RestCommandException {
        try {
            // We test if the traffic stats is enabled
            final boolean trafficStatsEnabled = restCommand.isTrafficStatsEnabled();
            TrafficCounterDecorator trafficDeserializer = null;
            if (trafficStatsEnabled) {
                trafficDeserializer = new TrafficCounterDecorator<T>(deserializer);
                deserializer = trafficDeserializer;
            }
            // We get the HttpUriRequest from the RestCommand
            final HttpUriRequest httpRequest = HttpClientUtility.fromRestCommand(restCommand);
            // We instantiate the HttpClient
            HttpClient httpClient = null;
            if (restCommand.getKeyStoreAdapter() != null) {
                httpClient = new HttpsClient(context, restCommand.getKeyStoreAdapter());
            } else {
                httpClient = new DefaultHttpClient();
            }
            // Here we configure it with some specific configuration issues
            if (mHttpClientConf != null) {
                configureHttpClient(httpClient);
            }
            // We create the ResponseHandler to manage the request
            final ResponseHandler<RestCommandResult<T>> responseHandler = HttpClientUtility.createHttpDeserializer(context, deserializer);
            // We execute the command using the client and responseHandler
            final RestCommandResult<T> restCommandResult = httpClient.execute(httpRequest, responseHandler);
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
        }
    }

    /**
     * This is an utility method that configure the HttpClient with some information we can pass
     * from outside in a some way
     *
     * @param httpClient The HttpClient instance to configure
     */
    private void configureHttpClient(final HttpClient httpClient) {
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, (int) mHttpClientConf.getConnectionTimeout());
        HttpConnectionParams.setSoTimeout(params, (int) mHttpClientConf.getSoTimeout());
    }

}
