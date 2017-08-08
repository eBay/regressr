package org.ebayopensource.regression.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by asfernando on 5/7/17.
 */
public class JSONCleanser {

    public static String getCleansedJSON(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);
        cleanseJSONObject(jsonObject);
        return jsonObject.toString();
    }

    private static void cleanseJSONArray(JSONArray array) throws JSONException {
        for (int i=0;i<array.length();i++) {
            Object childObject = array.get(i);
            if (childObject instanceof JSONArray) {
                cleanseJSONArray((JSONArray) childObject);
            }
            else if (childObject instanceof JSONObject) {
                cleanseJSONObject((JSONObject) childObject);
            }
            else if (childObject instanceof String) {
                array.put(i, getCleansedPrimitive(childObject));
            }
        }
    }

    private static Object getCleansedPrimitive(Object primitive) {
        if (primitive instanceof String) {
            return ((String)primitive).replaceAll("\"","'");
        }
        return primitive;
    }

    private static void cleanseJSONObject(JSONObject jsonObject) throws JSONException {
        JSONArray names = jsonObject.names();
        if (names == null) {
            return;
        }
        for (int i=0;i<names.length();i++) {
            String key = (String) names.get(i);
            Object childObject = jsonObject.get(key);
            if (childObject instanceof JSONArray) {
                cleanseJSONArray((JSONArray) childObject);
            }
            else if (childObject instanceof JSONObject) {
                cleanseJSONObject((JSONObject) childObject);
            }
            else {
                jsonObject.put(key, getCleansedPrimitive(childObject));
            }
        }
    }
}
