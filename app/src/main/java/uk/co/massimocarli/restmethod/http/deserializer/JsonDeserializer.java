package uk.co.massimocarli.restmethod.http.deserializer;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Deserializer for JSON objects.
 * <p/>
 * Created by Simone on 15/01/2014.
 */
public class JsonDeserializer implements Deserializer<JSONObject> {

    /**
     * The Singleton instance
     */
    private static final JsonDeserializer sInstance = new JsonDeserializer();

    /**
     * @return The JsonDeserializer Singleton
     */
    public static JsonDeserializer get() {
        return sInstance;
    }

    /**
     * Private constructor
     */
    private JsonDeserializer() {
    }

    @Override
    public JSONObject realise(InputStream inputStream, Context context) throws IOException {
        final String jsonString = StringDeserializer.getDefault().realise(inputStream, context);
        try {
            final JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
