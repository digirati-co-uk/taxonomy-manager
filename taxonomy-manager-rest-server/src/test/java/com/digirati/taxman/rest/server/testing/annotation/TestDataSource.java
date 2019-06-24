package com.digirati.taxman.rest.server.testing.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD})
public @interface TestDataSource {
}
