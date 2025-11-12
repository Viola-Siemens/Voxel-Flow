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
 * 使用该注解的包中的所有类的方法返回值非空，基本类型除外。如果定义了一个方法并尝试返回一个可空的变量或 {@code null}，则会报出一条警告。
 * <p>
 * 作为特例，如果你希望允许一个受该注解影响的方法返回值是空，请使用 {@code @Nullable} 注解修饰该方法。
 * @author liudongyu
 */

@Documented
@Nonnull
@TypeQualifierDefault(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodsReturnNonnullByDefault {
}
