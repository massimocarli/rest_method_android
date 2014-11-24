package uk.co.massimocarli.restmethod.stats;

import android.content.Context;
import uk.co.massimocarli.restmethod.http.deserializer.Deserializer;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is a decorator for the Deserializer that adds the information related to the
 * data received
 * <p/>
 * Created by Massimo Carli on 15/01/2014.
 */
public class TrafficCounterDecorator<T> implements Deserializer<T> {

    /**
     * The Function to decorate.
     */
    private Deserializer<? extends T> mDecoratee;

    /**
     * The number of bytes read.
     */
    private long mDataCount;

    /**
     * Creates a Deserializer that counts the data read.
     *
     * @param decoratee The Deserializer to decorate
     */
    public TrafficCounterDecorator(final Deserializer<? extends T> decoratee) {
        this.mDecoratee = decoratee;
        mDataCount = 0L;
    }

    @Override
    public T realise(final InputStream inputStream, final Context context) throws IOException {
        return mDecoratee.realise(new InputStream() {

            @Override
            public int read() throws IOException {
                mDataCount++;
                return inputStream.read();
            }

            @Override
            public int read(final byte[] buffer) throws IOException {
                final int dataRead = super.read(buffer);
                mDataCount += dataRead;
                return dataRead;
            }

            @Override
            public int read(final byte[] buffer, final int offset, final int length)
                    throws IOException {
                final int dataRead = super.read(buffer, offset, length);
                mDataCount += dataRead;
                return dataRead;
            }

        }, context);
    }

    /**
     * @return The number of byte read after the last reset.
     */
    public final long getDataCount() {
        return mDataCount;
    }

    /**
     * Resets the counted byte.
     */
    public final void reset() {
        mDataCount = 0;
    }

    /**
     * @return The Deserializer not decorated
     */
    public final Deserializer<? extends T> getDecoratee() {
        return mDecoratee;
    }
}
