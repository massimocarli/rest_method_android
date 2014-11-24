package uk.co.massimocarli.restmethod.http.https;

import android.content.Context;
import android.util.Log;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;


/**
 * Custom DefaultHttpClient that supports https security.
 * <p/>
 * Created by Simone on 06/03/2014.
 */
public class HttpsClient extends DefaultHttpClient {

    /**
     * The tag for the Log
     */
    private static final String LOG_TAG = HttpsClient.class.getName();

    /**
     * The Context
     */
    private final Context mContext;

    /**
     * The Adapter to use for the keyStore
     */
    private final KeyStoreAdapter mKeyStoreAdapter;

    /**
     * Create the HttpsClient using the KeyStoreAdapter for the certificate issue
     *
     * @param context         The Context
     * @param keyStoreAdapter The KeyStore
     */
    public HttpsClient(final Context context, final KeyStoreAdapter keyStoreAdapter) {
        this.mContext = context;
        this.mKeyStoreAdapter = keyStoreAdapter;
    }

    @Override
    protected ClientConnectionManager createClientConnectionManager() {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", newSslSocketFactory(), 443));
        return new SingleClientConnManager(getParams(), registry);
    }

    private SSLSocketFactory newSslSocketFactory() {
        if (mKeyStoreAdapter != null) {
            try {
                return new SSLSocketFactory(mKeyStoreAdapter.getKeyStore());
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Error getting KeyStore from KeyStoreAdapter", e);
            }
        }
        Log.w(LOG_TAG, "KeyStore from KeyStoreAdapter is null! Are you sire of that?");
        return null;
    }

}
