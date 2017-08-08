package org.ebayopensource.regression.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;

/**
 * Created by asfernando on 5/3/17.
 */
public class CustomJsonAssert {

    public static void assertEquals(String prev, String newOne, boolean compareValues) throws JSONException {
        JSONAssert.assertEquals(prev, newOne, new RegressrJSONComparator(compareValues, true));
    }

    public static void assertEquals(String prev, String newOne, boolean compareValues, boolean compareJSONArrays) throws JSONException {
        JSONAssert.assertEquals(prev, newOne, new RegressrJSONComparator(compareValues, compareJSONArrays));
    }
}

class RegressrJSONComparator extends DefaultComparator {

    private boolean compareValues;

    private boolean compareJSONArrays;

    public RegressrJSONComparator(boolean compareValues, boolean compareJSONArrays) {
        super(JSONCompareMode.NON_EXTENSIBLE);
        this.compareValues = compareValues;
        this.compareJSONArrays = compareJSONArrays;
    }

    @Override
    public void compareJSON(String prefix, JSONObject expected, JSONObject actual, JSONCompareResult result) throws JSONException {
        super.compareJSON(prefix, expected, actual, result);
    }

    private boolean isValue(Object value) {
        if ( (value instanceof Number) || (value instanceof String) ) {
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result) throws JSONException {
        if (!compareValues && isValue(expectedValue) && isValue(actualValue)) {
            return;
        }
        super.compareValues(prefix, expectedValue, actualValue, result);
    }

    @Override
    public void compareJSONArray(String prefix, JSONArray expected, JSONArray actual, JSONCompareResult result) throws JSONException {
        if (!compareJSONArrays) {
            return;
        }
        super.compareJSONArray(prefix, expected, actual, result);
    }
}
