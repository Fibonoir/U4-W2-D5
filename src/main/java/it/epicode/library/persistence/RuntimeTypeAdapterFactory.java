package it.epicode.library.persistence;


import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory {
    private final Class<?> baseType;
    private final String typeFieldName;
    private final Map<String, Class<?>> labelToSubtype = new LinkedHashMap<>();
    private final Map<Class<?>, String> subtypeToLabel = new LinkedHashMap<>();

    private RuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName) {
        if (typeFieldName == null || baseType == null) {
            throw new NullPointerException();
        }
        this.baseType = baseType;
        this.typeFieldName = typeFieldName;
    }

    public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName) {
        return new RuntimeTypeAdapterFactory<T>(baseType, typeFieldName);
    }

    public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type, String label) {
        if (type == null || label == null) {
            throw new NullPointerException();
        }
        if (subtypeToLabel.containsKey(type) || labelToSubtype.containsKey(label)) {
            throw new IllegalArgumentException("Types and labels must be unique.");
        }
        labelToSubtype.put(label, type);
        subtypeToLabel.put(type, label);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type) {
        if (!baseType.isAssignableFrom(type.getRawType())) {
            return null;
        }

        final Map<String, TypeAdapter<?>> labelToDelegate = new LinkedHashMap<>();
        final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new LinkedHashMap<>();

        for (Map.Entry<String, Class<?>> entry : labelToSubtype.entrySet()) {
            TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
            labelToDelegate.put(entry.getKey(), delegate);
            subtypeToDelegate.put(entry.getValue(), delegate);
        }

        return new TypeAdapter<R>() {
            @Override
            public void write(JsonWriter out, R value) throws IOException {
                Class<?> srcType = value.getClass();
                String label = subtypeToLabel.get(srcType);
                if (label == null) {
                    throw new JsonParseException("Cannot serialize " + srcType.getName() + "; did you forget to register a subtype?");
                }

                @SuppressWarnings("unchecked")
                TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);
                JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();

                JsonObject clone = new JsonObject();
                clone.addProperty(typeFieldName, label);
                for (Map.Entry<String, JsonElement> e : jsonObject.entrySet()) {
                    clone.add(e.getKey(), e.getValue());
                }

                Streams.write(clone, out);
            }

            @Override
            public R read(JsonReader in) throws IOException {
                JsonObject jsonObject = Streams.parse(in).getAsJsonObject();
                JsonElement labelJsonElement = jsonObject.remove(typeFieldName);
                if (labelJsonElement == null) {
                    throw new JsonParseException("Cannot deserialize " + baseType + " because it does not define a field named " + typeFieldName);
                }
                String label = labelJsonElement.getAsString();

                @SuppressWarnings("unchecked")
                TypeAdapter<R> delegate = (TypeAdapter<R>) labelToDelegate.get(label);
                if (delegate == null) {
                    throw new JsonParseException("Cannot deserialize " + baseType + " subtype named " + label + "; did you forget to register a subtype?");
                }
                return delegate.fromJsonTree(jsonObject);
            }
        }.nullSafe();
    }
}

