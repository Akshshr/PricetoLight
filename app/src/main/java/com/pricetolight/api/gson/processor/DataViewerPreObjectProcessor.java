package com.pricetolight.api.gson.processor;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import io.gsonfire.PreProcessor;

public class DataViewerPreObjectProcessor<T> implements PreProcessor<T> {

    private String[] memberNames = new String[0];

    public DataViewerPreObjectProcessor(String... memberNames) {
        if(memberNames != null) {
            this.memberNames = memberNames;
        }
    }

    @Override
    public void preDeserialize(Class<? extends T> clazz, JsonElement src, Gson gson) {
        // data: { me: { ... } } -> { ... }
        if(validateRoot(src)) {
            final JsonObject source = src.getAsJsonObject();
            JsonObject root = src.getAsJsonObject()
                    .getAsJsonObject("data")
                    .getAsJsonObject("viewer");
            for(String memberName : memberNames) {
                root = root.getAsJsonObject(memberName);
            }

            source.remove("data");
            for (Object o : root.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                source.add((String) entry.getKey(), (JsonElement) entry.getValue());
            }
        }
    }

    private boolean validateRoot(JsonElement src) {
        if(!src.isJsonObject()
                || src.getAsJsonObject().getAsJsonObject("data") == null
                || src.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("viewer") == null) {
            return false;
        }
        JsonObject root = src.getAsJsonObject().getAsJsonObject("data").getAsJsonObject("viewer");
        for(String memberName : memberNames) {
            if(!root.get(memberName).isJsonObject()) {
                return false;
            }
            root = root.getAsJsonObject(memberName);
        }
        return true;
    }
}
