package com.subrosagames.subrosa.util.validation.validators;

import java.util.Date;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.subrosagames.subrosa.util.validation.constraints.DateRange;

/**
 * Validates that two {@code Date} fields make up a valid date range.
 */
public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private static final Logger LOG = LoggerFactory.getLogger(DateRangeValidator.class);

    private String startField;
    private String endField;
    private boolean allowEmptyRange;
    private String message;

    @Override
    public void initialize(DateRange dateRange) {
        startField = dateRange.start();
        endField = dateRange.end();
        allowEmptyRange = dateRange.allowEmptyRange();
        message = dateRange.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        boolean isValid = false;
        try {
            // create new Date objects from the first so we won't get bit if one is actually a Timestamp
            Object startProperty = PropertyUtils.getProperty(value, startField);
            Object endProperty = PropertyUtils.getProperty(value, endField);
            final Date startDate = startProperty == null ? null : new Date(((Date) startProperty).getTime());
            final Date endDate = endProperty == null ? null : new Date(((Date) endProperty).getTime());
            isValid = startDate == null || endDate == null
                    || (allowEmptyRange
                    ? startDate.compareTo(endDate) <= 0
                    : startDate.compareTo(endDate) < 0);
        } catch (final Exception e) { // SUPPRESS CHECKSTYLE IllegalThrows
            LOG.warn("Got exception attempting to validate date range", e);
        }
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addNode(startField).addConstraintViolation()
                    .buildConstraintViolationWithTemplate(message)
                    .addNode(endField).addConstraintViolation();
        }
        return isValid;
    }

}
