package com.digirati.taxman.common.rdf.annotation.jsonld;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JsonLdFrame {
    /**
     * Get the path to the JSON file representing the frame on the classpath.
     *
     * @return A path to a JSON-LD frame file.
     */
    String input();

    /**
     * Whether to inject the <code>@id</code> value of the resource being serialized
     * into the frame.
     *
     * @return If the `@id` value should be populated during serialization.
     */
    boolean injectId() default true;
}
