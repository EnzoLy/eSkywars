package me.enzol.skywars.utils.serialization;

import com.google.gson.*;
import org.bukkit.util.Vector;

import java.lang.reflect.Type;

public final class VectorAdapter implements JsonDeserializer<Vector>, JsonSerializer<Vector> {

    @Override
    public Vector deserialize(JsonElement src, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        return fromJson(src);
    }


    @Override
    public JsonElement serialize(Vector src, Type type, JsonSerializationContext context) {
        return toJson(src);
    }

    public static JsonObject toJson(Vector vector) {
        if (vector == null) return null;

        JsonObject object = new JsonObject();
        object.addProperty("x", vector.getX());
        object.addProperty("y", vector.getY());
        object.addProperty("z", vector.getZ());
        return object;
    }

    public static Vector fromJson(JsonElement element) {
        if (element == null || !element.isJsonObject()) return null;

        JsonObject json = element.getAsJsonObject();
        double x = json.get("x").getAsDouble();
        double y = json.get("y").getAsDouble();
        double z = json.get("z").getAsDouble();
        return new Vector(x, y, z);
    }
}