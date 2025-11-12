package org.ecnumc.voxelflow.annotation;

import javax.annotation.Nullable;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 用于 {@code package-info.json} 中注解一个包。
 * <p>
 * 使用该注解的包中的所有类的方法返回值可空，基本类型除外。
 * <p>
 * 作为特例，如果你希望允许一个受该注解影响的方法返回值是非空，请使用 {@code @Nonnull} 注解修饰该字段。
 * @author liudongyu
 */

@Documented
@Nullable
@TypeQualifierDefault(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodsReturnNullableByDefault {
}
