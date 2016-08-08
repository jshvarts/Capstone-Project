package com.jshvarts.flatstanley.data.remote;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.jshvarts.flatstanley.model.FlatStanley;
import com.jshvarts.flatstanley.model.FlatStanleyItems;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlatStanleyDeserializer implements JsonDeserializer<FlatStanleyItems> {
    @Override
    public FlatStanleyItems deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        List<FlatStanley> flatStanleys = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            FlatStanley flatStanley = context.deserialize(entry.getValue(), FlatStanley.class);
            flatStanleys.add(flatStanley);
        }
        return new FlatStanleyItems(flatStanleys);

    }
}
