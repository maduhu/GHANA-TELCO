package org.motechproject.ghana.telco.domain.builder;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public abstract class Builder<T> {

    private T classToBuild;

    protected Builder(T classToBuild) {
        this.classToBuild = classToBuild;
    }

    public T build() {
        Map<String, Field> fields = getFields(classToBuild.getClass());
        Map<String, Field> fieldsWithValue = getFields(this.getClass());
        try {
            copyFieldValues(fields, fieldsWithValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return classToBuild;
    }

    private void copyFieldValues(Map<String, Field> fields, Map<String, Field> fieldsWithValue) throws IllegalAccessException {
        for (String fieldName : fieldsWithValue.keySet()) {
            Field field = fields.get(fieldName);
            Field value = fieldsWithValue.get(fieldName);

            field.setAccessible(true);
            value.setAccessible(true);

            field.set(classToBuild, value.get(this));
        }
    }

    private Map<String, Field> getFields(Class<? extends Object> clazz) {
        Map<String, Field> fieldMap = new HashMap<String, Field>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldMap.put(field.getName(), field);
        }
        return fieldMap;
    }
}
