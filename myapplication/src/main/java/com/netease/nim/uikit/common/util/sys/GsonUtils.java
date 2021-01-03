package com.netease.nim.uikit.common.util.sys;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Created by xfdream
 */
public class GsonUtils {
    private static volatile Gson instance;

    private GsonUtils() {
    }

    public static Gson getInstance() {
        if (instance == null) {
            synchronized (GsonUtils.class) {
                if (instance == null) {
                    GsonBuilder builder = new GsonBuilder();
                    setGsonBuilder(builder);
                    instance = builder.create();
                }
            }
        }
        return instance;
    }

    /**
     * 设置gson
     *
     * @param builder
     */
    public static void setGsonBuilder(GsonBuilder builder) {
        builder.registerTypeAdapter(String.class, STRING_TYPEADAPTER);
        builder.registerTypeAdapter(BigDecimal.class, BIGDECIMAL_TYPEADAPTER);
        // 支持Map的key为复杂对象的形式
        builder.enableComplexMapKeySerialization();
        builder.serializeNulls();
        builder.registerTypeAdapter(Integer.class, new IntegerDefault0Adapter());
        builder.registerTypeAdapter(int.class, new IntegerDefault0Adapter());
        builder.registerTypeAdapter(Double.class, new DoubleDefault0Adapter());
        builder.registerTypeAdapter(double.class, new DoubleDefault0Adapter());
        builder.registerTypeAdapter(Long.class, new LongDefault0Adapter());
        builder.registerTypeAdapter(long.class, new LongDefault0Adapter());


        // 时间转化为特定格式
        // builder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        // 不导出实体中没有用@Expose注解的属性
        //builder.excludeFieldsWithoutExposeAnnotation();
        // 会把字段首字母大写,注:对于实体上使用了@SerializedName注解的不会生效.
        // .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
        // 对json结果格式化
        // builder.setPrettyPrinting();
        // 有的字段不是一开始就有的,会随着版本的升级添加进来,那么在进行序列化和返序列化的时候就会根据版本号来选择是否要序列化.
        // @Since(版本号)能完美地实现这个功能.还的字段可能,随着版本的升级而删除,那么
        // @Until(版本号)也能实现这个功能,GsonBuilder.setVersion(double)方法需要调用.
        // builder.setVersion(1.0);
    }

    /**
     * 获取跳过属性的Gson
     *
     * @param keys
     * @return
     */
    public static Gson getGsonSkipField(String[] keys) {
        return new GsonBuilder().setExclusionStrategies(new JsonKit(keys)).create();
    }

    /**
     * 转成json
     *
     * @param object
     * @return
     */
    public static String toJson(Object object) {
        return getInstance().toJson(object);
    }

    /**
     * 转成bean
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> T fromJson(String gsonString, Class<T> cls) {
        return getInstance().fromJson(gsonString, cls);
    }


    /**
     * 转成list
     *
     * @param gsonString
     * @param cls
     * @return
     */
    public static <T> List<T> fromJsonByList(String gsonString, Class<T> cls) {
        return getInstance().fromJson(gsonString, new ListParameterizedType(cls));
    }

    /**
     * 转成map的
     *
     * @param gsonString
     * @return
     */
    public static <T> Map<String, T> fromJsonByMaps(String gsonString) {
        return getInstance().fromJson(gsonString, new TypeToken<Map<String, T>>() {
        }.getType());
    }

    private static class ListParameterizedType implements ParameterizedType {
        private Type type;

        private ListParameterizedType(Type type) {
            this.type = type;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return new Type[]{type};
        }

        @Override
        public Type getRawType() {
            return List.class;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

    private static class JsonKit implements ExclusionStrategy {
        private String[] keys;

        public JsonKit(String[] keys) {
            this.keys = keys;
        }

        @Override
        public boolean shouldSkipClass(Class<?> arg0) {
            return false;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes arg0) {
            if (keys != null) {
                for (String key : keys) {
                    if (key.equals(arg0.getName())) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    //读取String值为null转换成""
    public static final TypeAdapter<String> STRING_TYPEADAPTER = new TypeAdapter<String>() {

        @Override
        public String read(JsonReader reader) {
            try {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return "";
                }
                return reader.nextString();
            } catch (Exception e) {
            }
            return "";
        }

        @Override
        public void write(JsonWriter writer, String value) {
            try {
                if (value == null) {
                    writer.nullValue();
                    return;
                }
                writer.value(value);
            } catch (Exception e) {
            }
        }
    };

    //读取BigDecimal值自定义转换，防止精度丢失
    public static final TypeAdapter<BigDecimal> BIGDECIMAL_TYPEADAPTER = new TypeAdapter<BigDecimal>() {

        @Override
        public BigDecimal read(JsonReader reader) {
            try {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return BigDecimal.ZERO;
                }
                return BigDecimal.valueOf(reader.nextDouble());
            } catch (Exception e) {
            }
            return BigDecimal.ZERO;
        }

        @Override
        public void write(JsonWriter writer, BigDecimal value) {
            try {
                if (value == null) {
                    writer.value(0);
                } else {
                    writer.value(value);
                }
            } catch (Exception e) {
            }
        }
    };

    public static class IntegerDefault0Adapter implements JsonSerializer<Integer>, JsonDeserializer<Integer> {
        @Override
        public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为int类型,如果后台返回""或者null,则返回0
                    return 0;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsInt();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }

    public static class DoubleDefault0Adapter implements JsonSerializer<Double>, JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为double类型,如果后台返回""或者null,则返回0.00
                    return 0.00;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsDouble();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }

    public static class LongDefault0Adapter implements JsonSerializer<Long>, JsonDeserializer<Long> {
        @Override
        public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            try {
                if (json.getAsString().equals("") || json.getAsString().equals("null")) {//定义为long类型,如果后台返回""或者null,则返回0
                    return 0l;
                }
            } catch (Exception ignore) {
            }
            try {
                return json.getAsLong();
            } catch (NumberFormatException e) {
                throw new JsonSyntaxException(e);
            }
        }

        @Override
        public JsonElement serialize(Long src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src);
        }
    }

}
