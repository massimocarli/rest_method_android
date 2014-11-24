package uk.co.massimocarli.restmethod.rest;

import android.content.Context;
import uk.co.massimocarli.restmethod.http.deserializer.Deserializer;

import java.io.IOException;

/**
 * This is the interface that abstracts the CommandExecutor for the RestCommand
 * <p/>
 * Created by Massimo Carli on 31/10/2013.
 */
public interface RestCommandExecutor {

    /**
     * This is the operation that a command should implement. It should be able to execute a
     * RestCommand and return an object of type T using the Deserializer
     *
     * @param context      The Context
     * @param restCommand  The Command to execute
     * @param deserializer The Deserializer to use
     * @param <T>          The type of the object to create
     * @return The RestCommandResult that encapsulate the information about the RestMethod
     */
    public abstract <T> RestCommandResult<T> execute(final Context context,
                                                     final RestCommand restCommand,
                                                     final Deserializer<? extends T> deserializer)
            throws RestCommandException;

    /**
     * This is the exception related to the realise operation
     */
    public class RestCommandException extends IOException {

        /**
         * The code for the error if any
         */
        private int mHttpCodeError = 200;

        /**
         * Creates a RestCommandException with a message and the source Exception
         *
         * @param message       The message
         * @param cause         The Exception cause of the error
         * @param httpCodeError The HttpCode for this exception if any
         */
        public RestCommandException(String message, Throwable cause, int httpCodeError) {
            super(message);
            this.mHttpCodeError = httpCodeError;
        }

        /**
         * Creates a RestCommandException with a message and the source Exception
         *
         * @param message The message
         * @param cause   The Exception cause of the error
         */
        public RestCommandException(String message, Throwable cause) {
            super(message);
        }

        /**
         * The HTTP status code
         *
         * @return The status code for the response
         */
        public int getHttpCodeError() {
            return mHttpCodeError;
        }
    }

}
