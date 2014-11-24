package uk.co.massimocarli.restmethod.rest;

import uk.co.massimocarli.restmethod.http.https.KeyStoreAdapter;

import java.util.Map;

/**
 * The Builder for the Rest requests to send to the Server
 * <p/>
 * Created by Massimo Carli on 30/10/2013.
 */
public final class RestCommandBuilder {

    /**
     * The RestCommand bound to this specific RestCommandBuilder
     */
    private final RestCommand mRestCommand;

    /**
     * Creates a RestCommandBuilder for a given RestCommand
     *
     * @param restCommand The RestCommand to build
     */
    private RestCommandBuilder(final RestCommand restCommand) {
        this.mRestCommand = restCommand;

    }

    /**
     * Get the RestCommandBuilder for a GET
     *
     * @param url The url for the request
     * @return The RestCommandBuilder to manage a GET request
     */
    public static RestCommandBuilder get(final String url) {
        RestCommandBuilder builder = new RestCommandBuilder(new RestCommand(RestCommand.HTTP_METHOD.GET, url));
        return builder;
    }

    /**
     * Get the RestCommandBuilder for a POST
     *
     * @param url The url for the request
     * @return The RestCommandBuilder to manage a POST request
     */
    public static RestCommandBuilder post(final String url) {
        RestCommandBuilder builder = new RestCommandBuilder(new RestCommand(RestCommand.HTTP_METHOD.POST, url));
        return builder;
    }

    /**
     * Get the RestCommandBuilder for a PUT
     *
     * @param url The url for the request
     * @return The RestCommandBuilder to manage a PUT request
     */
    public static RestCommandBuilder put(final String url) {
        RestCommandBuilder builder = new RestCommandBuilder(new RestCommand(RestCommand.HTTP_METHOD.PUT, url));
        return builder;
    }

    /**
     * Get the RestCommandBuilder for a DELETE
     *
     * @param url The url for the request
     * @return The RestCommandBuilder to manage a PUT request
     */
    public static RestCommandBuilder delete(final String url) {
        RestCommandBuilder builder = new RestCommandBuilder(new RestCommand(RestCommand.HTTP_METHOD.DELETE, url));
        return builder;
    }

    /**
     * This method add a param to the request
     *
     * @param name  The name of the param
     * @param value The value for the param
     * @return The RestCommandBuilder itself to manage chaining
     */
    public RestCommandBuilder addParam(final String name, final String value) {
        this.mRestCommand.addParam(name, value);
        return this;
    }

    /**
     * This method add a set of params to the request
     *
     * @param params The name of the param
     * @return The RestCommandBuilder itself to manage chaining
     */
    public RestCommandBuilder addParams(final Map<String, String> params) {
        this.mRestCommand.addParams(params);
        return this;
    }

    /**
     * This method add an header to the request
     *
     * @param name  The name of the header
     * @param value The value for the header
     * @return The RestCommandBuilder itself to manage chaining
     */
    public RestCommandBuilder addHeader(final String name, final String value) {
        this.mRestCommand.addHeader(name, value);
        return this;
    }

    /**
     * This method add a set of headers to the request
     *
     * @param headers The name of the header
     * @return The RestCommandBuilder itself to manage chaining
     */
    public RestCommandBuilder addHeaders(final Map<String, String> headers) {
        this.mRestCommand.addParams(headers);
        return this;
    }

    /**
     * This sets a document of type String as an input
     *
     * @param stringDocument The document of type String to send
     * @return The RestCommandBuilder itself to manage chaining
     */
    public RestCommandBuilder setStringDocument(final String stringDocument) {
        this.mRestCommand.setStringDocument(stringDocument);
        return this;
    }

    /**
     * This sets a binary document  as an input
     *
     * @param binaryDocument The binary document to send
     * @return The RestCommandBuilder itself to manage chaining
     */
    public RestCommandBuilder setStringDocument(final byte[] binaryDocument) {
        this.mRestCommand.setBinaryDocument(binaryDocument);
        return this;
    }

    /**
     * This set the input of the data as a JSon
     *
     * @return The RestCommandBuilder itself to manage chaining
     */
    public RestCommandBuilder isJson() {
        this.mRestCommand.addHeader("Content-Type", "application/json");
        this.mRestCommand.addHeader("Accept", "application/json");
        return this;
    }

    /**
     * This method add the KeyStoreAdapter to manage certificates
     *
     * @param keyStoreAdapter The KeyStoreAdapter for certificate management
     * @return The RestCommandBuilder itself to manage chaining
     */
    public RestCommandBuilder isSecure(final KeyStoreAdapter keyStoreAdapter) {
        this.mRestCommand.setKeyStoreAdapter(keyStoreAdapter);
        return this;
    }

    /**
     * @return The created RestCommand
     */
    public RestCommand build() {
        return this.mRestCommand;
    }

}
