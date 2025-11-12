package org.ecnumc.voxelflow.annotation;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于 {@code package-info.json} 中注解一个包。
 * <p>
 * 使用该注解的包中的所有类，所有字段默认非空。如果定义了一个字段并赋值为空，或未初始化该字段，则会报出一条警告。
 * <p>
 * 作为特例，如果你希望允许一个受该注解影响的字段是空，请使用 {@code @Nullable} 注解修饰该字段。
 * @author liudongyu
 */

@Documented
@Nonnull
@TypeQualifierDefault(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldsAreNonnullByDefault {
}
