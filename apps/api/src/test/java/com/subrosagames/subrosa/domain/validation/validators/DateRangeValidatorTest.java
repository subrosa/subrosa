package com.subrosagames.subrosa.domain.validation.validators;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.subrosagames.subrosa.domain.validation.constraints.DateRange;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * Test {@link DateRangeValidator}.
 */
@RunWith(JUnit4.class)
public class DateRangeValidatorTest {

    // CHECKSTYLE-OFF: JavadocMethod

    @Test
    public void testValidRange() throws Exception {
        Calendar calendar = Calendar.getInstance();
        Date date1 = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        Date date2 = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        Date date3 = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        Date date4 = calendar.getTime();
        TestMe testMe = new TestMe(date1, date2, date3, date4);
        Set<ConstraintViolation<TestMe>> violations = getConstraintViolations(testMe);
        assertThat(violations, empty());
    }

    @Test
    public void testInvalidRange() throws Exception {
        Calendar calendar = Calendar.getInstance();
        Date date1 = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        Date date2 = calendar.getTime();
        TestMe testMe = new TestMe(date2, date1);
        Set<ConstraintViolation<TestMe>> violations = getConstraintViolations(testMe);
        assertThat(violations, hasSize(2));
    }

    @Test
    public void testInvalidRangeWhenEmptyAllowed() throws Exception {
        Calendar calendar = Calendar.getInstance();
        Date date1 = calendar.getTime();
        calendar.add(Calendar.HOUR, 1);
        Date date2 = calendar.getTime();
        TestMe testMe = new TestMe(null, null, date2, date1);
        Set<ConstraintViolation<TestMe>> violations = getConstraintViolations(testMe);
        assertThat(violations, hasSize(2));
    }

    @Test
    public void testEmptyRangeWhenAllowedSucceeds() throws Exception {
        Date date = new Date();
        TestMe testMe = new TestMe(null, null, date, date);
        Set<ConstraintViolation<TestMe>> violations = getConstraintViolations(testMe);
        assertThat(violations, empty());
    }

    @Test
    public void testEmptyRangeWhenDisallowedFails() throws Exception {
        Date date = new Date();
        TestMe testMe = new TestMe(date, date);
        Set<ConstraintViolation<TestMe>> violations = getConstraintViolations(testMe);
        assertThat(violations, hasSize(2));
    }

    @Test
    public void testNullDatesPass() throws Exception {
        TestMe testMe = new TestMe();
        Set<ConstraintViolation<TestMe>> violations = getConstraintViolations(testMe);
        assertThat(violations, empty());
    }

    @Test
    public void testNullEndDateOnNonEmptyableRange() throws Exception {
        TestMe testMe = new TestMe(new Date(), null);
        Set<ConstraintViolation<TestMe>> violations = getConstraintViolations(testMe);
        assertThat(violations, empty());
    }

    @Test
    public void testNullStartDateOnEmptyableRange() throws Exception {
        TestMe testMe = new TestMe(null, null, null, new Date());
        Set<ConstraintViolation<TestMe>> violations = getConstraintViolations(testMe);
        assertThat(violations, empty());
    }

    Set<ConstraintViolation<TestMe>> getConstraintViolations(TestMe testMe) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        return validator.validate(testMe);
    }

    /**
     * Annotated class against which validation tests can run.
     */
    @DateRange.List({
            @DateRange(start = "start", end = "end"),
            @DateRange(start = "emptyRangeStart", end = "emptyRangeEnd", allowEmptyRange = true)
    })
    public static class TestMe {

        private Date start;
        private Date end;
        private Date emptyRangeStart;
        private Date emptyRangeEnd;

        /**
         * Default constructor.
         */
        public TestMe() {
            this(null, null, null, null);
        }

        /**
         * Construct with non-emptyable start and end dates.
         *
         * @param start start
         * @param end   end
         */
        public TestMe(Date start, Date end) {
            this(start, end, null, null);
        }

        /**
         * Construct with non-emptyable and emtpyable ranges.
         *
         * @param start           start
         * @param end             end
         * @param emptyRangeStart emtpyable start
         * @param emptyRangeEnd   emptyable end
         */
        public TestMe(Date start, Date end, Date emptyRangeStart, Date emptyRangeEnd) {
            this.start = start == null ? null : new Date(start.getTime());
            this.end = end == null ? null : new Date(end.getTime());
            this.emptyRangeStart = emptyRangeStart == null ? null : new Date(emptyRangeStart.getTime());
            this.emptyRangeEnd = emptyRangeEnd == null ? null : new Date(emptyRangeEnd.getTime());
        }

        public Date getStart() {
            return start == null ? null : new Date(start.getTime());
        }

        public Date getEnd() {
            return end == null ? null : new Date(end.getTime());
        }

        public Date getEmptyRangeStart() {
            return emptyRangeStart == null ? null : new Date(emptyRangeStart.getTime());
        }

        public Date getEmptyRangeEnd() {
            return emptyRangeEnd == null ? null : new Date(emptyRangeEnd.getTime());
        }
    }

    // CHECKSTYLE-ON: JavadocMethod
}
