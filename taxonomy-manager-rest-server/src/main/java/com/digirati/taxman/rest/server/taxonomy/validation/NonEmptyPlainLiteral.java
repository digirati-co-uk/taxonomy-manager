package com.digirati.taxman.rest.server.taxonomy.validation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NonEmptyPlainLiteral.Validator.class)
@SuppressWarnings("checkstyle:JavadocMethod")
public @interface NonEmptyPlainLiteral {

    String message() default
            "{taxman.taxonomy.NonEmptyPlainLiteral.message}";

    /**
     * {@inheritDoc}
     */
    Class<?>[] groups() default {};

    /**
     * {@inheritDoc}
     */
    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<NonEmptyPlainLiteral, Map<String, String>> {

        @Override
        public boolean isValid(Map<String, String> value, ConstraintValidatorContext context) {
            return !value.isEmpty();
        }
    }
}
