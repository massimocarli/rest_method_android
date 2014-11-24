package uk.co.massimocarli.restmethod.test;

import android.util.Log;
import uk.co.massimocarli.restmethod.http.deserializer.StringDeserializer;
import uk.co.massimocarli.restmethod.http.urlconnection.UrlConnectionCommandExecutor;
import uk.co.massimocarli.restmethod.rest.RestCommand;
import uk.co.massimocarli.restmethod.rest.RestCommandBuilder;
import uk.co.massimocarli.restmethod.rest.RestCommandExecutor;
import uk.co.massimocarli.restmethod.rest.RestCommandResult;

/**
 * Created by Massimo Carli on 04/11/2013.
 */
public class UrlConnectionGETCommandTest extends JettyServerAndroidTestCaseForGet {

    /**
     * The Tag for the Log
     */
    private static final String TAG_LOG = UrlConnectionGETCommandTest.class.getName();

    /**
     * This method tests a normal GET operation
     */
    public void testSuccessGet() {
        final String url = "http://127.0.0.1:" + SERVER_PORT + GET_TARGET;
        RestCommand getCommand = RestCommandBuilder.get(url).build();
        try {
            RestCommandResult<String> result = UrlConnectionCommandExecutor.create().execute(getContext(), getCommand,
                                                                                             StringDeserializer.getDefault());
            Log.d(TAG_LOG, "Got " + result + " from the server");
            assertEquals(OK_OUTPUT, result.getResult());
            assertEquals(200, result.getStatusCode());
        } catch (RestCommandExecutor.RestCommandException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void test404() {
        final String url = "http://127.0.0.1:" + SERVER_PORT + GET_HTTP_404_ERROR_TARGET;
        RestCommand getCommand = RestCommandBuilder.get(url).build();
        try {
            RestCommandResult<String> result = UrlConnectionCommandExecutor.create().execute(getContext(), getCommand,
                                                                                             StringDeserializer.getDefault());
            Log.d(TAG_LOG, "Got " + result + " from the server");
            assertEquals(404, result.getStatusCode());
        } catch (RestCommandExecutor.RestCommandException e) {
            e.printStackTrace();
        }
    }

}
