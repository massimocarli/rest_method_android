package uk.co.massimocarli.restmethod.test;

import android.app.DownloadManager;
import android.test.AndroidTestCase;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Massimo Carli on 12/11/2013.
 */
public class JettyServerAndroidTestCaseForGet extends AndroidTestCase {

    // The server port we're using
    protected final static int SERVER_PORT = 8080;

    protected final static String GET_METHOD = "GET";

    protected final static String OK_OUTPUT = "OK";

    /**
     * The Url to invoke for the get simple GET test
     */
    protected final static String GET_TARGET = "/testGet";

    /**
     * The Url to invoke for the 404 error in get
     */
    protected final static String GET_HTTP_404_ERROR_TARGET = "/testGetHttpError";

    /**
     * The Jetty WebServer.
     */
    private Server jettyServer;

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        // work-around for Android defect 9431
        System.setProperty("java.net.preferIPv4Stack", "true");
        System.setProperty("java.net.preferIPv6Addresses", "false");
        // Here we create the Jetty web server for every test
        jettyServer = new Server(SERVER_PORT);
        jettyServer.setHandler(new AbstractHandler() {

            @Override
            public void handle(String target, Request request, HttpServletRequest httpServletRequest,
                               HttpServletResponse httpServletResponse) throws IOException, ServletException {
                if (GET_TARGET.equals(target) && GET_METHOD.equals(httpServletRequest.getMethod())) {
                    // Generate a normal response
                    httpServletResponse.setContentType("text/json");
                    httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                    httpServletResponse.getWriter().print(OK_OUTPUT);
                    request.setHandled(true);
                } else if (GET_HTTP_404_ERROR_TARGET.equals(target) && GET_METHOD.equals(
                        httpServletRequest.getMethod())) {
                    // Generate a 404 error code
                    httpServletResponse.setContentType("text/json");
                    httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    request.setHandled(true);
                }
            }
        });
        jettyServer.start();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
        jettyServer.stop();
    }

}
