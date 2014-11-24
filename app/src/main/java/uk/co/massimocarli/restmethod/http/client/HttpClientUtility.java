package uk.co.massimocarli.restmethod.http.client;

import android.content.Context;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import uk.co.massimocarli.restmethod.http.deserializer.Deserializer;
import uk.co.massimocarli.restmethod.rest.RestCommand;
import uk.co.massimocarli.restmethod.rest.RestCommandExecutor;
import uk.co.massimocarli.restmethod.rest.RestCommandResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This is an Utility class that manages the RestCommand using objects from the Apache HttpClient
 * framework
 * <p/>
 * Created by Massimo Carli on 01/11/2013.
 */
public final class HttpClientUtility {

    /**
     * The Tag for the Log
     */
    private static final String TAG_LOG = HttpClientUtility.class.getName();

    /**
     * Private constructor
     */
    private HttpClientUtility() {
        throw new AssertionError("Never instantiate me! I'm an Utility class!!!");
    }

    /**
     * This method receives a RestCommand and creates a HttpUriRequest from that using
     * headers, parameters and other stuffs
     *
     * @param restCommand The RestCommand to get the information from
     * @return The HttpUriRequest to execute with the HttpClient
     */
    public static HttpUriRequest fromRestCommand(final RestCommand restCommand) throws IOException {
        final RestCommand.HTTP_METHOD httpMethod = restCommand.getHttpMethod();
        HttpUriRequest httpUriRequest = null;
        switch (httpMethod) {
            case GET:
                // We create the HttpRequest for this request
                httpUriRequest = new HttpGet(buildQueryString(restCommand));
                break;
            case POST:
                httpUriRequest = new HttpPost(restCommand.getUrl());
                break;
            case PUT:
                httpUriRequest = new HttpPut(restCommand.getUrl());
                break;
            case DELETE:
                httpUriRequest = new HttpDelete(buildQueryString(restCommand));
                break;
            default:
                throw new IllegalArgumentException("This method is not valid!!! " + httpMethod);
        }
        // We manage the possible headers
        if (restCommand.hasHeaders()) {
            for (Map.Entry<String, String> header : restCommand.getHeaders()) {
                httpUriRequest.addHeader(header.getKey(), header.getValue());
                Log.d(TAG_LOG, "HTTP header " + header.getKey() + " added with value " + header.getValue());
            }
        }
        if (httpMethod.isDocumentAllowed()) {
            // We manage the possible parameters
            if (restCommand.hasParams()) {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(restCommand.paramsCount());
                for (Map.Entry<String, String> param : restCommand.getParams()) {
                    nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                    try {
                        ((HttpEntityEnclosingRequestBase) httpUriRequest).setEntity(
                                new UrlEncodedFormEntity(nameValuePairs, restCommand.getCharset()));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Log.e(TAG_LOG, "Encoding not supported ", e);
                    }
                }
            } else if (restCommand.hasStringDocument()) {
                // The request is Post or Put so we use another abstraction
                final HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = (HttpEntityEnclosingRequestBase) httpUriRequest;
                final StringEntity stringEntity = new StringEntity(restCommand.getStringDocument());
                httpEntityEnclosingRequestBase.setEntity(stringEntity);
                Log.d(TAG_LOG, "String entity added to the request ");
            } else if (restCommand.hasBinaryDocument()) {
                // The request is Post or Put so we use another abstraction
                final HttpEntityEnclosingRequestBase httpEntityEnclosingRequestBase = (HttpEntityEnclosingRequestBase) httpUriRequest;
                final ByteArrayEntity byteArrayEntity = new ByteArrayEntity(restCommand.getBinaryDocument());
                httpEntityEnclosingRequestBase.setEntity(byteArrayEntity);
                Log.d(TAG_LOG, "Binary entity added to the request ");
            }
        }
        // We return the request to execute
        return httpUriRequest;
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
     * This build the querystring for the params when supported
     *
     * @param restCommand The command with the data
     * @return The QueryString to append to the url
     * @throws java.io.IOException In case of error
     */
    private static String buildQueryString(final RestCommand restCommand) throws IOException {
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
        return url.toString();
    }

}
