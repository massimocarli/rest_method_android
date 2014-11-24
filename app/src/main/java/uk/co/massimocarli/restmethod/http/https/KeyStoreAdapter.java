package uk.co.massimocarli.restmethod.http.https;

import java.security.KeyStore;

/**
 * Created by Massimo Carli on 10/03/2014.
 */
public interface KeyStoreAdapter {

    /**
     * @return The KeyStore to use
     */
    KeyStore getKeyStore();

}

