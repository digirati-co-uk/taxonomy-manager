package com.digirati.taxman.common.rdf.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A type-level annotation that controls the namespace prefixes in an {@link
 * com.digirati.taxman.common.rdf.RdfModel}.
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RdfContext {

    /**
     * A list of `key=value` pairs where `key` is the namespace prefix, and `value` is the fully
     * qualified XML namespace that the prefix resolves to.
     */
    String[] value() default {};
}
