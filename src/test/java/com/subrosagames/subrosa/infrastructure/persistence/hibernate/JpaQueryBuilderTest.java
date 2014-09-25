package com.subrosagames.subrosa.infrastructure.persistence.hibernate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.hamcrest.Matchers;
import org.hamcrest.beans.HasPropertyWithValue;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;

import com.subrosa.api.actions.list.Operator;
import com.subrosa.api.actions.list.QueryBuilder;
import com.subrosa.api.actions.list.QueryCriteria;
import com.subrosa.api.actions.list.TimestampToDateTranslator;
import com.subrosa.api.actions.list.annotation.Filterable;
import com.subrosagames.subrosa.util.RequestUtils;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


/**
 * Test {@link JpaQueryBuilder}.
 */
public class JpaQueryBuilderTest {

    private static final Logger LOG = LoggerFactory.getLogger(JpaQueryBuilderTest.class);

    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private Connection connection;

    @Before
    public void setUp() throws Exception {
        LOG.info("Starting in-memory HSQL database for unit tests");
        Class.forName("org.hsqldb.jdbcDriver");
        connection = DriverManager.getConnection("jdbc:hsqldb:mem:unit-testing-jpa", "sa", "");
        LOG.info("Building JPA EntityManager for unit tests");
        entityManagerFactory = Persistence.createEntityManagerFactory("testPU");
        entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();
        entityManager.persist(new TestObject(1, 1.1, "5", new DateTime(2014, 1, 1, 0, 0, 0, 0).toDate(), 1));
        entityManager.persist(new TestObject(2, 2.2, "5", new DateTime(2014, 2, 1, 0, 0, 0, 0).toDate(), 1));
        entityManager.persist(new TestObject(3, 3.3, "3", new DateTime(2014, 3, 1, 0, 0, 0, 0).toDate(), 3, "set"));
        entityManager.persist(new TestObject(4, 4.4, "2", new DateTime(2014, 4, 1, 0, 0, 0, 0).toDate(), 3, "also set"));
        entityManager.persist(new TestObject(5, 5.5, "1", new DateTime(2014, 5, 1, 0, 0, 0, 0).toDate(), 5));
        TestObject testObject;
        final EventObject eventObject1;
        testObject = new TestObject();
        testObject.setId(6);
        eventObject1 = new EventObject(1, new DateTime(2014, 5, 1, 0, 0, 0, 0).toDate());
        entityManager.persist(eventObject1);
        testObject.setEvent(new ArrayList<EventObject>() {
            {
                add(eventObject1);
            }
        });
        entityManager.persist(testObject);
        testObject = new TestObject();
        testObject.setId(7);
        final EventObject eventObject2 = new EventObject(2, new DateTime(2013, 5, 1, 0, 0, 0, 0).toDate());
        entityManager.persist(eventObject2);
        testObject.setEvent(new ArrayList<EventObject>() {
            {
                add(eventObject2);
            }
        });
        entityManager.persist(testObject);
        entityManager.getTransaction().commit();
    }

    @After
    public void tearDown() throws Exception {
        LOG.info("Shuting down Hibernate JPA layer.");
        if (entityManager != null) {
            entityManager.close();
        }
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
        LOG.info("Stopping in-memory HSQL database.");
        connection.createStatement().execute("SHUTDOWN");
    }

    @Test
    public void testEmptyCriteria() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        List<TestObject> results = findForRequest(request);
        assertEquals(7, results.size());
    }

    @Test
    public void testNonFilterableField() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("notAnnotated", "3");
        List<TestObject> results = findForRequest(request);
        assertEquals(7, results.size());
    }

    @Test
    public void testUnsupportedOperator() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setParameter("textLessThan", "3");
        request.setParameter("idGreaterThan", "3");
        request.setParameter("scoreNotEqual", "3");
        request.setParameter("time", new Date().toString());
        List<TestObject> results = findForRequest(request);
        assertEquals(7, results.size());
    }

    @Test
    public void testFilterOnEquality() throws Exception {
        MockHttpServletRequest request;
        List<TestObject> results;

        request = new MockHttpServletRequest();
        request.addParameter("id", "1");
        results = findForRequest(request);
        assertEquals(1, results.size());
        assertThat(results, everyItem(HasPropertyWithValue.<TestObject>hasProperty("id", is(1))));

        request = new MockHttpServletRequest();
        request.addParameter("text", "5");
        results = findForRequest(request);
        assertEquals(2, results.size());
        assertThat(results, everyItem(HasPropertyWithValue.<TestObject>hasProperty("text", is("5"))));
    }

    @Test
    public void testEqualityMultipleFields() throws Exception {
        MockHttpServletRequest request;
        List<TestObject> results;

        request = new MockHttpServletRequest();
        request.addParameter("text", "5");
        request.addParameter("id", "2");
        results = findForRequest(request);
        assertEquals(1, results.size());
        assertThat(results, everyItem(allOf(
                HasPropertyWithValue.<TestObject>hasProperty("text", is("5")),
                HasPropertyWithValue.<TestObject>hasProperty("id", is(2)))));

        request = new MockHttpServletRequest();
        request.addParameter("text", "5");
        request.addParameter("id", "3");
        results = findForRequest(request);
        assertEquals(0, results.size());
    }

    @Test
    public void testNotEqual() throws Exception {
        MockHttpServletRequest request;
        List<TestObject> results;

        request = new MockHttpServletRequest();
        request.addParameter("textNot", "5");
        results = findForRequest(request);
        assertEquals(3, results.size());
        assertThat(results, everyItem(HasPropertyWithValue.<TestObject>hasProperty("text", not("5"))));

        request = new MockHttpServletRequest();
        request.addParameter("textNot", "5");
        request.addParameter("idNot", "3");
        results = findForRequest(request);
        assertEquals(2, results.size());
        assertThat(results, everyItem(allOf(
                HasPropertyWithValue.<TestObject>hasProperty("text", not("5")),
                HasPropertyWithValue.<TestObject>hasProperty("id", not(3)))));
    }

    @Test
    public void testInequalities() throws Exception {
        MockHttpServletRequest request;
        List<TestObject> results;

        request = new MockHttpServletRequest();
        request.addParameter("scoreGreaterThan", "3.2");
        results = findForRequest(request);
        assertEquals(3, results.size());
        assertThat(results, everyItem(HasPropertyWithValue.<TestObject>hasProperty("score", greaterThan(3.2))));

        request = new MockHttpServletRequest();
        request.addParameter("scoreGreaterThan", "3.3");
        results = findForRequest(request);
        assertEquals(2, results.size());
        assertThat(results, everyItem(HasPropertyWithValue.<TestObject>hasProperty("score", greaterThan(3.3))));

        request = new MockHttpServletRequest();
        request.addParameter("scoreLessThan", "2");
        results = findForRequest(request);
        assertEquals(1, results.size());
        assertThat(results, everyItem(HasPropertyWithValue.<TestObject>hasProperty("score", lessThan(2.0))));

        request = new MockHttpServletRequest();
        request.addParameter("scoreLessThan", "1.1");
        results = findForRequest(request);
        assertEquals(0, results.size());
    }

    @Test
    public void testSetAndUnsetOperators() throws Exception {
        QueryCriteria<TestObject> criteria;
        List<TestObject> results;

        criteria = new QueryCriteria<TestObject>(TestObject.class);
        criteria.setBypassFilterableChecks(true);
        criteria.addFilter("nullableSet", true);
        results = findForCriteria(criteria);
        assertEquals(2, results.size());
        assertThat(results, everyItem(HasPropertyWithValue.<TestObject>hasProperty("notAnnotated", is(3))));

        criteria = new QueryCriteria<TestObject>(TestObject.class);
        criteria.setBypassFilterableChecks(true);
        criteria.addFilter("nullableUnset", true);
        results = findForCriteria(criteria);
        assertEquals(5, results.size());
        assertThat(results, everyItem(HasPropertyWithValue.<TestObject>hasProperty("notAnnotated", not(3))));
    }

    @Test
    public void testFilterTransformations() throws Exception {
        MockHttpServletRequest request;
        List<TestObject> results;

        request = new MockHttpServletRequest();
        request.addParameter("timeBefore", Long.toString(new DateTime(2014, 2, 28, 0, 0, 0, 0).toDate().getTime()));
        results = findForRequest(request);
        assertEquals(2, results.size());
        assertThat(results, everyItem(
                HasPropertyWithValue.<TestObject>hasProperty("time", lessThan(new DateTime(2014, 2, 28, 0, 0, 0, 0).toDate()))
        ));

        request = new MockHttpServletRequest();
        request.addParameter("timeAfter", Long.toString(new DateTime(2014, 2, 28, 0, 0, 0, 0).toDate().getTime()));
        results = findForRequest(request);
        assertEquals(3, results.size());
        assertThat(results, everyItem(
                HasPropertyWithValue.<TestObject>hasProperty("time", greaterThan(new DateTime(2014, 2, 28, 0, 0, 0, 0).toDate()))
        ));
    }

    @Test
    public void testChildOperand() throws Exception {
        MockHttpServletRequest request;
        List<TestObject> results;

        request = new MockHttpServletRequest();
        request.addParameter("eventAfter", Long.toString(new DateTime(2014, 1, 1, 0, 0, 0, 0).toDate().getTime()));
        results = findForRequest(request);
        assertEquals(1, results.size());
        assertThat(results, everyItem(HasPropertyWithValue.<TestObject>hasProperty("id", is(6))));

        request = new MockHttpServletRequest();
        request.addParameter("eventBefore", Long.toString(new DateTime(2014, 1, 1, 0, 0, 0, 0).toDate().getTime()));
        results = findForRequest(request);
        assertEquals(1, results.size());
        assertThat(results, everyItem(HasPropertyWithValue.<TestObject>hasProperty("id", is(7))));
    }

    private List<TestObject> findForRequest(MockHttpServletRequest request) {
        QueryCriteria<TestObject> criteria = RequestUtils.createQueryCriteriaFromRequestParameters(request, TestObject.class);
        return findForCriteria(criteria);
    }

    private List<TestObject> findForCriteria(QueryCriteria<TestObject> criteria) {
        QueryBuilder<TestObject, TypedQuery<TestObject>, TypedQuery<Long>> queryBuilder = new JpaQueryBuilder<TestObject>(entityManager);
        TypedQuery<TestObject> query = queryBuilder.getQuery(criteria);
        return query.getResultList();
    }

    @Entity
    @Table(name = "test")
    public static class TestObject {

        @Id
        @Column
        @Filterable
        private Integer id;

        @Column
        @Filterable(operators = { Operator.EQUAL, Operator.LESS_THAN, Operator.GREATER_THAN })
        private Double score;

        @Column
        @Filterable
        private String text;

        @Column
        @Filterable(
                operators = { Operator.GREATER_THAN, Operator.LESS_THAN },
                translator = TimestampToDateTranslator.class
        )
        private Date time;

        @OneToMany
        @Filterable(
                operators = { Operator.GREATER_THAN, Operator.LESS_THAN },
                translator = TimestampToDateTranslator.class,
                childOperand = "innerDate"
        )
        private List<EventObject> event;

        @Column
        private Integer notAnnotated;

        @Column
//        @Filterable(operators = { Operator.SET, Operator.UNSET })
        private String nullable;

        public TestObject(Integer id, Double score, String text, Date time, Integer notAnnotated) {
            this(id, score, text, time, notAnnotated, null);
        }

        public TestObject(Integer id, Double score, String text, Date time, Integer notAnnotated, String nullable) {
            this.id = id;
            this.score = score;
            this.text = text;
            this.time = time;
            this.notAnnotated = notAnnotated;
            this.nullable = nullable;
        }

        public TestObject() {
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer number) {
            this.id = number;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Date getTime() {
            return time;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public List<EventObject> getEvent() {
            return event;
        }

        public void setEvent(List<EventObject> event) {
            this.event = event;
        }

        public Integer getNotAnnotated() {
            return notAnnotated;
        }

        public void setNotAnnotated(Integer notAnnotated) {
            this.notAnnotated = notAnnotated;
        }

        public String getNullable() {
            return nullable;
        }

        public void setNullable(String nullable) {
            this.nullable = nullable;
        }
    }

    @Entity
    @Table(name = "event")
    public static class EventObject {

        @Id
        private Integer id;

        @Column
        private Date innerDate;

        public EventObject() {
        }

        public EventObject(Integer id, Date innerDate) {
            this.id = id;
            this.innerDate = innerDate;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Date getInnerDate() {
            return innerDate;
        }

        public void setInnerDate(Date innerDate) {
            this.innerDate = innerDate;
        }
    }

}
