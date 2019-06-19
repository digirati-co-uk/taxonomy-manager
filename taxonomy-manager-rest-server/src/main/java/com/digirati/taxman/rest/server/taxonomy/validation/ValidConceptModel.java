package com.digirati.taxman.rest.server.taxonomy.validation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

import com.digirati.taxman.common.taxonomy.ConceptRdfModel;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;
import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidConceptModel.Validator.class)
@ReportAsSingleViolation
public @interface ValidConceptModel {

    String message() default
            "com.digirati.taxman.server.analysis.input.validation.ValidInput.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<ValidConceptModel, ConceptRdfModel> {

        @Override
        public boolean isValid(ConceptRdfModel value, ConstraintValidatorContext context) {
            context.disableDefaultConstraintViolation();

            Map<String, String> prefLabels = value.getPreferredLabel();
            boolean valid = true;

            if (prefLabels.isEmpty()) {
                context.buildConstraintViolationWithTemplate("must be a plain literal present")
                        .addPropertyNode("skos:plainLiteral")
                        .addConstraintViolation();
                valid = false;
            }

            return valid;
        }
    }
}
