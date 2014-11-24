package uk.co.massimocarli.restmethod.http.deserializer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is the implementation of the Deserializer that reads the data from a Stream and returns
 * the related Bitmap
 * <p/>
 * Created by Massimo Carli on 05/11/2013.
 */
public final class BitmapDeserializer implements Deserializer<Bitmap> {

    /*
     * The Tag for the log
     */
    private static final String LOG_TAG = BitmapDeserializer.class.getName();

    /**
     * The name for this Deserializer implementation
     */
    public static final String DESERIALIZER_NAME = "BitmapDeserializer";

    /**
     * We can use always the same instance
     */
    private static BitmapDeserializer sInstance;

    /**
     * The default constructor
     */
    private BitmapDeserializer() {
    }

    /**
     * This is the static factory method for the BitmapDeserializer
     *
     * @return The FileDeserializer instance. It's a singleton
     */
    public synchronized static BitmapDeserializer get() {
        if (sInstance == null) {
            sInstance = new BitmapDeserializer();
        }
        return sInstance;
    }

    @Override
    public Bitmap realise(InputStream inputStream, Context context) throws IOException {
        Bitmap image = BitmapFactory.decodeStream(inputStream);
        return image;
    }

}
