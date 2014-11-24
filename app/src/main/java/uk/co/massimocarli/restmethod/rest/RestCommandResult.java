package uk.co.massimocarli.restmethod.rest;

/**
 * This is the class that describes the object result of a RestCommand execution. We created
 * this object to encapsulate information about the result but also about the HTTP status code
 * and other information (es: traffica data, etc)
 * <p/>
 * Created by Massimo Carli on 06/11/2013.
 */
public final class RestCommandResult<T> {

    /**
     * The Result of the RestCommand
     */
    private final T mResult;

    /**
     * The Http Status Code for the Command
     */
    private final int mStatusCode;

    /**
     * The Http Status code description
     */
    private final String mStatusMessage;

    /**
     * The bytes of this request if available if available
     */
    private long mTrafficData = -1;

    /**
     * Creates a RestCommandResult for a given result, status code and status message
     *
     * @param result        The result of the RestCommand
     * @param statusCode    The http status code
     * @param statusMessage The http status message
     */
    private RestCommandResult(final T result, final int statusCode, final String statusMessage) {
        this.mResult = result;
        this.mStatusCode = statusCode;
        this.mStatusMessage = statusMessage;
    }


    /**
     * Creates a RestCommandResult for a given Result, status code and status message
     *
     * @param result        The result of the RestCommand
     * @param statusCode    The http status code
     * @param statusMessage The http status message
     * @param <T>           The type of the result
     * @return The RestCommandResult with the given data
     */
    public static <T> RestCommandResult<T> get(final T result, final int statusCode, final String statusMessage) {
        return new RestCommandResult(result, statusCode, statusMessage);
    }

    /**
     * @return The HTTP status code
     */
    public int getStatusCode() {
        return mStatusCode;
    }

    /**
     * @return The HTTP status message
     */
    public String getStatusMessage() {
        return mStatusMessage;
    }

    /**
     * @return The result of type T
     */
    public T getResult() {
        return mResult;
    }


    @Override
    public String toString() {
        return mStatusCode + " : " + mStatusMessage + " -> " + mResult;
    }


    /**
     * Set the data received with this request
     *
     * @param trafficData The data in bytes
     */
    public void setTrafficData(long trafficData) {
        this.mTrafficData = trafficData;
    }

    /**
     * @return The number of bytes if any or -1 if not available
     */
    public long getTrafficData() {
        return mTrafficData;
    }
}
