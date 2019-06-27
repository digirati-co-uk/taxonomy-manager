package com.digirati.taxman.common.rdf.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** A type-level annotation that designates a type as an {@link RdfResource}. */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RdfType {

    /**
     * The fully qualified RDF resource this type represents. Resources with a statement of the form
     * <code>&lt;subject_uri&gt; &lt;rdf:Type&gt; value()</code>
     */
    String value() default "rdf:Dataset";
}
