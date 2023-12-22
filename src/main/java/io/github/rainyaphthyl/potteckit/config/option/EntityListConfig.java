package io.github.rainyaphthyl.potteckit.config.option;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.option.list.ValueListConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class EntityListConfig extends ValueListConfig<Class<? extends Entity>> {
    public static final Function<Class<? extends Entity>, String> TO_STRING_CONVERTER = eClass -> {
        ResourceLocation key = EntityList.getKey(eClass);
        return key == null ? null : key.toString();
    };
    public static final Function<String, Class<? extends Entity>> FROM_STRING_CONVERTER = EntityList::getClassFromName;

    public EntityListConfig(String name, ImmutableList<Class<? extends Entity>> defaultValues) {
        super(name, defaultValues, TO_STRING_CONVERTER, FROM_STRING_CONVERTER);
    }

    public EntityListConfig(String name, ImmutableList<Class<? extends Entity>> defaultValues, String commentTranslationKey, Object... commentArgs) {
        super(name, defaultValues, TO_STRING_CONVERTER, FROM_STRING_CONVERTER, commentTranslationKey, commentArgs);
    }

    public EntityListConfig(String name, ImmutableList<Class<? extends Entity>> defaultValues, Function<Class<? extends Entity>, String> toStringConverter, Function<String, Class<? extends Entity>> fromStringConverter) {
        super(name, defaultValues, toStringConverter, fromStringConverter);
    }

    @Override
    public EntityListConfig copy() {
        EntityListConfig config = new EntityListConfig(name, defaultValue, toStringConverter, fromStringConverter);
        config.copyValuesFrom(this);
        return config;
    }
}
