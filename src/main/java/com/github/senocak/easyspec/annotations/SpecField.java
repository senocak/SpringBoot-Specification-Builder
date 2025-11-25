package com.github.senocak.easyspec.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Optional annotation to mark fields that can be used in Specifications.
 * This annotation is for documentation purposes and can be used by
 * code generators or validation tools.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * public class User {
 *     @SpecField
 *     private String email;
 *
 *     @SpecField(description = "User's full name")
 *     private String name;
 * }
 * }</pre>
 *
 * @author Spring Boot Easy Specification
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SpecField {
    /**
     * Optional description of the field for documentation.
     *
     * @return the description
     */
    String description() default "";
}

