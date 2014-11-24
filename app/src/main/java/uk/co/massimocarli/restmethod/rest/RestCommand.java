package uk.co.massimocarli.restmethod.rest;

import uk.co.massimocarli.restmethod.http.https.KeyStoreAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the abstraction of a HTTP Request of a specific type
 * <p/>
 * Created by Massimo Carli on 30/10/2013.
 */
public class RestCommand {

    /**
     * This is the default charset to use for the params encoding
     */
    public static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * The Enum for the type of RestCommand
     */
    public static enum HTTP_METHOD {

        GET(false, true), PUT(true, false), POST(true, false), DELETE(false, true);

        private final boolean mDocumentAllowed;

        private final boolean mQueryStringSupported;

        /**
         * Private parameter to check if the document is allowed
         *
         * @param documentAllowed If true the Method supports the document into the request
         */
        private HTTP_METHOD(final boolean documentAllowed, final boolean queryStringSupported) {
            mDocumentAllowed = documentAllowed;
            mQueryStringSupported = queryStringSupported;
        }

        /**
         * @return True if the method allow the document into request and false otherwise
         */
        public boolean isDocumentAllowed() {
            return mDocumentAllowed;
        }

        /**
         * @return True if the parameters are into the URL
         */
        public boolean isQueryStringSupported() {
            return mQueryStringSupported;
        }

    }

    /**
     * This is the HTTP Method of this RestCommand
     */
    private HTTP_METHOD mHttpMethod;

    /**
     * This is the end point Url for this RestCommand
     */
    private final String mUrl;

    /**
     * The Map for the params of the request
     */
    private final Map<String, String> mParams = new HashMap<String, String>();

    /**
     * The Map for the headers of the request
     */
    private final Map<String, String> mHeaders = new HashMap<String, String>();

    /**
     * This is document of type String we can add to the request.
     */
    private String mStringDocument;

    /**
     * This is document of type byte[] we can add to the request.
     */
    private byte[] mBinaryDocument;

    /**
     * The Charset fot this request
     */
    private String mCharset = DEFAULT_CHARSET;

    /**
     * If true the stats for traffic are enabled. The default is true
     */
    private boolean mTrafficStatsEnabled = true;

    /**
     * This is the Adapter for the optional certificate
     */
    private KeyStoreAdapter mKeyStoreAdapter;

    /**
     * Creates a RestCommand for a given HttpMethod and url
     *
     * @param httpMethod The method http to use for this request
     * @param url        The url to send the request to
     */
    RestCommand(final HTTP_METHOD httpMethod, final String url) {
        this.mHttpMethod = httpMethod;
        this.mUrl = url;
    }

    /**
     * This method adds a param to the request
     *
     * @param name  The name of the param to send
     * @param value The value for the param
     */
    void addParam(final String name, final String value) {
        mParams.put(name, value);
    }

    /**
     * This method adds a set of params to the request
     *
     * @param params The params to add to the request
     */
    void addParams(final Map<String, String> params) {
        mParams.putAll(params);
    }

    /**
     * This set a String document to the request
     *
     * @param stringDocument The String document to add ot the request
     */
    void setStringDocument(final String stringDocument) {
        if (!this.mHttpMethod.isDocumentAllowed()) {
            throw new IllegalStateException(
                    "HTTP Method " + this.mHttpMethod + " doesn't support document into the request!");
        }
        if (mBinaryDocument != null) {
            throw new IllegalStateException("You cannot add a String document if a binary one is already present");
        }
        this.mStringDocument = stringDocument;
    }

    /**
     * This set a binary document to the request
     *
     * @param binaryDocument The binary document to add ot the request
     */
    void setBinaryDocument(final byte[] binaryDocument) {
        if (!this.mHttpMethod.isDocumentAllowed()) {
            throw new IllegalStateException(
                    "HTTP Method " + this.mHttpMethod + " doesn't support document into the request!");
        }
        if (mStringDocument != null) {
            throw new IllegalStateException("You cannot add a binary document if a string one is already present");
        }
        this.mBinaryDocument = binaryDocument;
    }

    /**
     * This method adds a set of params to the request
     *
     * @param headers The headers to add to the request
     */
    void addHeaders(final Map<String, String> headers) {
        mHeaders.putAll(headers);
    }

    /**
     * This method adds a Header to the request
     *
     * @param name  The name of the header to send
     * @param value The value for the header
     */
    void addHeader(final String name, final String value) {
        mHeaders.put(name, value);
    }

    /**
     * This method set the charset to use for the parameter encoding
     *
     * @param charset The charset to use for parameters encoding
     */
    void withCharset(final String charset) {
        this.mCharset = charset;
    }

    /**
     * @return True if the request has params and false otherwise
     */
    public boolean hasParams() {
        return !mParams.isEmpty();
    }

    /**
     * @return The number of params
     */
    public int paramsCount() {
        return mParams.size();
    }

    /**
     * @return The number of headers
     */
    public int headersCount() {
        return mHeaders.size();
    }

    /**
     * @return The Charset for the request
     */
    public String getCharset() {
        return mCharset;
    }

    /**
     * @return The Iterable object for the Params
     */
    public Iterable<Map.Entry<String, String>> getParams() {
        return mParams.entrySet();
    }

    /**
     * @return True if the request has headers and false otherwise
     */
    public boolean hasHeaders() {
        return !mHeaders.isEmpty();
    }

    /**
     * @return The Iterator for the Headers
     */
    public Iterable<Map.Entry<String, String>> getHeaders() {
        return mHeaders.entrySet();
    }

    /**
     * @return The HTTP Method for this RestCommand
     */
    public HTTP_METHOD getHttpMethod() {
        return mHttpMethod;
    }

    /**
     * @return The Url for this RestCommand
     */
    public String getUrl() {
        return mUrl;
    }

    /**
     * Set the KeyStoreAdapter to use for https connection
     *
     * @param keyStoreAdapter The Adapter to use for connection
     */
    public void setKeyStoreAdapter(final KeyStoreAdapter keyStoreAdapter) {
        this.mKeyStoreAdapter = keyStoreAdapter;
    }

    /**
     * @return The KeyStoreAdapter to use
     */
    public KeyStoreAdapter getKeyStoreAdapter() {
        return mKeyStoreAdapter;
    }

    /**
     * @return The String document if any
     */
    public String getStringDocument() {
        return mStringDocument;
    }

    /**
     * @return True if the request has a String document
     */
    public boolean hasStringDocument() {
        return mStringDocument != null;
    }

    /**
     * @return The binary document if any
     */
    public byte[] getBinaryDocument() {
        return mBinaryDocument;
    }

    /**
     * @return True if the request has a binary document
     */
    public boolean hasBinaryDocument() {
        return mBinaryDocument != null;
    }

    /**
     * Enabled or disable traffic stats
     *
     * @param trafficStatsEnabled If true the traffic are enabled (default).
     */
    public void setTrafficStatsEnabled(final boolean trafficStatsEnabled) {
        this.mTrafficStatsEnabled = trafficStatsEnabled;
    }

    /**
     * @return If true the statistics for traffic are enabled
     */
    public boolean isTrafficStatsEnabled() {
        return mTrafficStatsEnabled;
    }

}
