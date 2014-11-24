package uk.co.massimocarli.restmethod.http.deserializer;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is an interface that abstract the objects that read from an InpuutStream and create
 * an object of type E
 * <p/>
 * Created by Massimo Carli on 30/10/2013.
 */
public interface Deserializer<E> {

    /**
     * This method reads data from an inputStream and creates, if possible, an object or
     * type E
     * <p/>
     * E The type of the object to create
     *
     * @param inputStream The stream to read from
     * @param context     The Context
     * @return The object of type E
     * @throws java.io.IOException The exception in case unable to create the object of type E
     */
    E realise(InputStream inputStream, Context context) throws IOException;

}
