package com.haiyunshan.article;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;

public class EntityDeserializer implements JsonDeserializer<ArticleEntry> {

    HashMap<String, Class<? extends ArticleEntry>> map;

    Gson mGson;

    EntityDeserializer() {
        this.mGson = new Gson();

        this.map = new HashMap<>();
        map.put(ParagraphEntry.TYPE, ParagraphEntry.class);
    }

    @Override
    public ArticleEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ArticleEntry entry = null;

        String name = jsonObject.get("type").getAsString();
        Class<? extends ArticleEntry> clz = map.get(name);
        if (clz != null) {
            entry = mGson.fromJson(json, clz);
        }

        if (entry == null) {
            throw new IllegalArgumentException("cannot find entry for " + jsonObject.toString());
        }

        return entry;
    }
}
