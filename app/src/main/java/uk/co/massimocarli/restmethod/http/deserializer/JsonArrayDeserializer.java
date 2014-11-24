package uk.co.massimocarli.restmethod.http.deserializer;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Deserializer for JSON objects when it's an array and so we have to return JSONArray
 * <p/>
 * Created by Simone on 15/01/2014.
 */
public class JsonArrayDeserializer implements Deserializer<JSONArray> {

    /**
     * The Singleton instance
     */
    private static final JsonArrayDeserializer sInstance = new JsonArrayDeserializer();

    /**
     * @return The JsonDeserializer Singleton
     */
    public static JsonArrayDeserializer get() {
        return sInstance;
    }

    /**
     * Private constructor
     */
    private JsonArrayDeserializer() {
    }

    @Override
    public JSONArray realise(InputStream inputStream, Context context) throws IOException {
        final String jsonString = StringDeserializer.getDefault().realise(inputStream, context);
        try {
            final JSONArray jsonArray = new JSONArray(jsonString);
            return jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
