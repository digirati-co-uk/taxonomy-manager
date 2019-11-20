package com.digirati.taxman.rest.server.taxonomy.validation;

import com.digirati.taxman.rest.server.taxonomy.ConceptModelRepository;
import com.google.common.collect.Multimap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import java.util.stream.Stream;

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

    @ApplicationScoped
    class Validator implements ConstraintValidator<NonEmptyPlainLiteral, Multimap<String, String>> {
        @Override
        public boolean isValid(Multimap<String, String> value, ConstraintValidatorContext context) {
            return !value.isEmpty()
                    && value.keys().stream().allMatch(s -> s != null && s.length() > 0)
                    && value.values().stream().allMatch(s -> s != null && s.length() > 0);
        }
    }

}
