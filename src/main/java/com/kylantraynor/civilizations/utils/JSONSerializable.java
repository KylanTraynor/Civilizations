package com.kylantraynor.civilizations.utils;

import org.json.simple.JSONObject;

public interface JSONSerializable {
    JSONObject toJSON();
    void load(JSONObject json);
}
