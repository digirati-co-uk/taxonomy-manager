package com.digirati.taxman.rest.server.management.validation;

import com.digirati.taxman.common.taxonomy.ProjectModel;
import com.digirati.taxman.rest.server.management.ProjectModelRepository;
import com.digirati.taxman.rest.server.management.validation.group.CreatingProject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueProjectSlug.Validator.class)
@SuppressWarnings("checkstyle:JavadocMethod")
public @interface UniqueProjectSlug {

    String message() default "{taxman.management.UniqueProjectSlug.message}";

    /**
     * {@inheritDoc}
     */
    Class<?>[] groups() default {CreatingProject.class};

    /**
     * {@inheritDoc}
     */
    Class<? extends Payload>[] payload() default {};

    @ApplicationScoped
    class Validator implements ConstraintValidator<UniqueProjectSlug, String> {

        @Inject
        ProjectModelRepository projects;

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            try {
                projects.find(value);
                return false;
            } catch (Exception ex) {
                return true;
            }
        }
    }
}
