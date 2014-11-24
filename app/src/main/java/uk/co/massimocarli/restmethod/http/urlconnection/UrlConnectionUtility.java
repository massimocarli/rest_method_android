package uk.co.massimocarli.restmethod.http.urlconnection;

import android.content.Context;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.entity.BufferedHttpEntity;
import uk.co.massimocarli.restmethod.http.deserializer.Deserializer;
import uk.co.massimocarli.restmethod.http.https.KeyStoreAdapter;
import uk.co.massimocarli.restmethod.rest.RestCommand;
import uk.co.massimocarli.restmethod.rest.RestCommandExecutor;
import uk.co.massimocarli.restmethod.rest.RestCommandResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * This is an Utility class that manages the RestCommand using objects from the UrlConnection
 * <p/>
 * Created by Massimo Carli on 12/11/2013.
 */
public final class UrlConnectionUtility {

    /**
     * The Tag for the Log
     */
    private static final String TAG_LOG = UrlConnectionUtility.class.getName();

    /**
     * Private constructor
     */
    private UrlConnectionUtility() {
        throw new AssertionError("Never instantiate me! I'm an Utility class!!!");
    }

    /**
     * This method returns the HttpURLConnection to use for managing the given RestCommand
     *
     * @param restCommand The RestCommand to execute with the URLConnection
     * @return The HttpURLConnection to use for the connection
     * @throws java.io.IOException The Exception in case of error
     */
    public static HttpURLConnection fromRestCommand(final Context context,
                                                    final RestCommand restCommand) throws IOException {
        // We create the URL to connect to
        final URL requestURL = createURLWithParams(restCommand);
        // We create the HttpURLConnection to return
        HttpURLConnection httpURLConnection = (HttpURLConnection) requestURL.openConnection();
        if (httpURLConnection instanceof HttpsURLConnection) {
            // In this case the URL is a HTTPS so we can cast to the HttpsURLConnection to
            // access its methods
            final KeyStoreAdapter keyStoreAdapter = restCommand.getKeyStoreAdapter();
            if (keyStoreAdapter != null) {
                try {
                    KeyStore keyStore = restCommand.getKeyStoreAdapter().getKeyStore();
                    String algorithm = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
                    tmf.init(keyStore);
                    SSLContext sslContext = SSLContext.getInstance("TLS");
                    sslContext.init(null, tmf.getTrustManagers(), null);
                    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
                    final HttpsURLConnection httpsURLConnection = (HttpsURLConnection) httpURLConnection;
                    httpsURLConnection.setSSLSocketFactory(sslSocketFactory);
                    httpURLConnection = httpsURLConnection;
                } catch (Exception e) {
                    Log.e(TAG_LOG, "Error in HTTPS management", e);
                }
            }
        }
        // We set the HTTP Method
        final RestCommand.HTTP_METHOD httpMethod = restCommand.getHttpMethod();
        switch (httpMethod) {
            case GET:
                // We create the HttpRequest for this request
                httpURLConnection.setRequestMethod(RestCommand.HTTP_METHOD.GET.toString());
                break;
            case POST:
                httpURLConnection.setRequestMethod(RestCommand.HTTP_METHOD.POST.toString());
                // We have to enable output
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                break;
            case PUT:
                httpURLConnection.setRequestMethod(RestCommand.HTTP_METHOD.PUT.toString());
                // We have to enable output
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                break;
            case DELETE:
                httpURLConnection.setRequestMethod(RestCommand.HTTP_METHOD.DELETE.toString());
                break;
            default:
                throw new IllegalArgumentException("This method is not valid!!! " + httpMethod);
        }
        // We manage the possible headers
        if (restCommand.hasHeaders()) {
            for (Map.Entry<String, String> header : restCommand.getHeaders()) {
                httpURLConnection.setRequestProperty(header.getKey(), header.getValue());
                Log.d(TAG_LOG, "HTTP header " + header.getKey() + " added with value " + header.getValue());
            }
        }
        // We manage parameters
        if (httpMethod.isDocumentAllowed()) {
            if (restCommand.hasParams()) {
                StringBuilder paramToEncode = new StringBuilder();
                final int paramNumber = restCommand.paramsCount();
                int currentParam = 0;
                for (Map.Entry<String, String> param : restCommand.getParams()) {
                    paramToEncode.append(param.getKey()).append("=").append(
                            URLEncoder.encode(param.getValue(), restCommand.getCharset()));
                    if (currentParam < paramNumber - 1) {
                        paramToEncode.append("&");
                    }
                    currentParam++;
                    Log.d(TAG_LOG, "HTTP param " + param.getKey() + " added with value " + param.getValue());
                }
                final String encondedParams = paramToEncode.toString();
                httpURLConnection.setRequestProperty("Content-Length",
                                                     String.valueOf(encondedParams.getBytes().length));
                final OutputStream output = httpURLConnection.getOutputStream();
                output.write(encondedParams.getBytes());
                output.flush();
                output.close();
                Log.d(TAG_LOG, "String entity added to the request ");
            } else if (restCommand.hasStringDocument()) {
                // We manage String document
                final OutputStream output = httpURLConnection.getOutputStream();
                output.write(restCommand.getStringDocument().getBytes(restCommand.getCharset()));
                Log.d(TAG_LOG, "String entity added to the request ");
            } else if (restCommand.hasBinaryDocument()) {
                // The request is Post or Put so we use another abstraction
                final OutputStream output = httpURLConnection.getOutputStream();
                output.write(restCommand.getBinaryDocument());
                Log.d(TAG_LOG, "Binary entity added to the request ");
            }
        }
        return httpURLConnection;
    }

    /**
     * This method creates the ResponseHandler fom the Deserializer
     *
     * @param context      The Context
     * @param deserializer The Deserializer to user for parsing the HttpResponse
     * @return The responseHandler We can use to manage the type object of type T.
     */
    public static <T> ResponseHandler<RestCommandResult<T>> createHttpDeserializer(final Context context,
                                                                                   final Deserializer<? extends T> deserializer) {
        return new ResponseHandler<RestCommandResult<T>>() {

            @Override
            public RestCommandResult<T> handleResponse(
                    HttpResponse httpResponse) throws RestCommandExecutor.RestCommandException {
                final int httpStatusCode = httpResponse.getStatusLine().getStatusCode();
                final String httpStatusMessage = httpResponse.getStatusLine().getReasonPhrase();
                try {
                    // We check for the httpResponse
                    // We get the proper Entity from the httpResponse
                    final HttpEntity receivedHttpEntity = httpResponse.getEntity();
                    // We create the Buffered Entity from that
                    BufferedHttpEntity bufferedEntity = new BufferedHttpEntity(receivedHttpEntity);
                    // We read the InputStream from the entity
                    InputStream resultInputStream = bufferedEntity.getContent();
                    // Using the Deserializer we get the object T
                    final T result = deserializer.realise(resultInputStream, context);
                    // We return the value from the InputStreamConsumer
                    return RestCommandResult.get(result, httpStatusCode, httpStatusMessage);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    Log.e(TAG_LOG, "Error managing response", ioe);
                    throw new RestCommandExecutor.RestCommandException("Error managing response", ioe, httpStatusCode);
                }
            }
        };
    }

    /**
     * This is an utility method that get the RestCommand and return the URL to invoke. This, if any,
     * adds the GET parameters to the URL. Only DELETE and GET can have parameters but this is
     * managed in the case of RestCommand creation
     *
     * @param restCommand The RestCommand with the request information
     * @return The URL to invoke
     * @throws java.io.IOException The Exception in case of error
     */
    public static URL createURLWithParams(final RestCommand restCommand) throws IOException {
        // We manage the possible parameters
        StringBuilder url = new StringBuilder(restCommand.getUrl());
        if (restCommand.getHttpMethod().isQueryStringSupported() && restCommand.hasParams()) {
            url.append("?");
            final int paramNumber = restCommand.paramsCount();
            int currentParam = 0;
            for (Map.Entry<String, String> param : restCommand.getParams()) {
                url.append(param.getKey()).append("=").append(
                        URLEncoder.encode(param.getValue(), restCommand.getCharset()));
                currentParam++;
                if (currentParam < paramNumber - 1) {
                    url.append("&");
                }
                Log.d(TAG_LOG, "HTTP param " + param.getKey() + " added with value " + param.getValue());
            }
        }
        // We create the URL to connect to
        final URL requestURL = new URL(url.toString());
        return requestURL;
    }

}
