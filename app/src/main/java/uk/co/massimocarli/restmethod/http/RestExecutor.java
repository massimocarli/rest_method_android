package uk.co.massimocarli.restmethod.http;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import uk.co.massimocarli.restmethod.http.client.HttpClientRestCommandExecutor;
import uk.co.massimocarli.restmethod.http.deserializer.Deserializer;
import uk.co.massimocarli.restmethod.http.urlconnection.UrlConnectionCommandExecutor;
import uk.co.massimocarli.restmethod.rest.RestCommand;
import uk.co.massimocarli.restmethod.rest.RestCommandExecutor;
import uk.co.massimocarli.restmethod.rest.RestCommandResult;

/**
 * This is the RestExecutor that executes the right implementation of the RestCommandExecutor
 * based on the executing version
 * <p/>
 * Created by Massimo Carli on 12/11/2013.
 */
public class RestExecutor implements RestCommandExecutor {

    /**
     * The Tag for the Log
     */
    private static final String TAG_LOG = RestExecutor.class.getName();

    /**
     * The reference to the CommandExecutor depending on the SDK version
     */
    private static final RestCommandExecutor sCommandExecutor;

    static {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) {
            sCommandExecutor = UrlConnectionCommandExecutor.create();
            Log.i(TAG_LOG, "UrlConneection implementation created");
        } else {
            sCommandExecutor = HttpClientRestCommandExecutor.create();
            Log.i(TAG_LOG, "HttpClient implementation created");
        }
    }

    /**
     * The singleton of this RestExecutor
     */
    private static RestExecutor sRestExecutor;

    /**
     * @return The RestExecutor singleton
     */
    public synchronized static RestExecutor get() {
        if (sRestExecutor == null) {
            sRestExecutor = new RestExecutor();
        }
        return sRestExecutor;
    }

    @Override
    public <T> RestCommandResult<T> execute(Context context, RestCommand restCommand,
                                            Deserializer<? extends T> deserializer) throws RestCommandException {
        // We delegate to the right one
        return sCommandExecutor.execute(context, restCommand, deserializer);
    }
}
