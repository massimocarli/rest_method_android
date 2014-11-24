package uk.co.massimocarli.restmethod.http.deserializer;

import android.content.Context;
import android.text.TextUtils;
import uk.co.massimocarli.restmethod.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is the implementation of the Deserializer that reads the value from the Request as
 * a String with a given encoding if any
 * <p/>
 * Created by Massimo Carli on 05/11/2013.
 */
public final class StringDeserializer implements Deserializer<String> {

    /**
     * The name for this Deserializer implementation
     */
    public static final String DESERIALIZER_NAME = "StringDeserializer";
    /**
     * The default encoding is UTF 8
     */
    private static final String DEFAULT_ENCODING = "UTF-8";
    /**
     * This is the singleton instance of the default StringDeserializer
     */
    private static StringDeserializer stringDeserializer;
    /**
     * The encoding of this instance
     */
    private String mEncoding;

    /**
     * Private constructor that creates a StringDeserializer with a given encoding
     *
     * @param encoding The Encoding to use to read the data from the InputStream
     */
    private StringDeserializer(final String encoding) {
        this.mEncoding = encoding;
    }

    /**
     * This static factory method creates and returns a StringDeserializer for a given
     * encoding. This method creates a new instance every time so it's responsability of the
     * client manage its retain
     *
     * @param encoding The encoding to use
     * @return The StringDeserializer for the given encoding
     */
    public static StringDeserializer create(final String encoding) {
        return new StringDeserializer(encoding);
    }

    /**
     * This is the static factory method that returns the singleton of the default
     * StringDeserializer implementation that uses default encoding (UTF-8)
     *
     * @return The default implementation of StringDeserializer
     */
    public synchronized static StringDeserializer getDefault() {
        if (stringDeserializer == null) {
            stringDeserializer = new StringDeserializer(DEFAULT_ENCODING);
        }
        return stringDeserializer;
    }

    @Override
    public String realise(InputStream inputStream, Context context) throws IOException {
        // Here we have to read the data from the inputStream
        // We get the String given the InputStream with the given encoding if
        // present
        String encodedString = null;
        if (TextUtils.isEmpty(mEncoding)) {
            encodedString = IOUtils.toString(inputStream);
        } else {
            encodedString = IOUtils.toString(inputStream, mEncoding);
        }
        IOUtils.closeQuietly(inputStream);
        // We return the given String
        return encodedString;
    }
}
