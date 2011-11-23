package com.subrosa.db;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.lang.ObjectUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.usertype.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Custom Hibernate type for storing a {@link URI} as a string in the database.
 *
 * To use this type you need to register it with Hibernate via a package annotation.
 * <p/>
 * In package-info.java:<br/>
 * &#64;TypeDef(name = UriUserType.HIBERNATE_TYPE_NAME, typeClass = UriUserType.class)<br/>
 * package com.lulu.project.domain<br/>
 * import org.hibernate.annotations.TypeDef;<br/>
 * import com.lulu.db.UriUserType;<br/>
 * <p/>
 * You can then annotation your field in the domain object.
 * <p/>
 * &#64;Column(name = "uri")<br/>
 * &#64;Type(type = UriUserType.HIBERNATE_TYPE_NAME)<br/>
 * private URI uri;<br/>
 */
public class UriUserType implements UserType {

    /**
     * Defines the type name as registered with Hibernate.
     */
    public static final String HIBERNATE_TYPE_NAME = "UriUserType";

    private static final Logger LOG = LoggerFactory.getLogger(UriUserType.class); // NOPMD

    @Override
    public Object assemble(Serializable cached, Object owner) {
        return deepCopy(cached);
    }

    @Override
    public Object deepCopy(Object value) {
        return value;
    }

    @Override
    public Serializable disassemble(Object value) {
        return value.toString();
    }

    @Override
    public boolean equals(Object x, Object y) {
        return ObjectUtils.equals(x, y);
    }

    @Override
    public int hashCode(Object x) {
        return x.toString().hashCode();
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] strings, SessionImplementor sessionImplementor, Object o) throws HibernateException, SQLException {
        String value = (String) StandardBasicTypes.STRING.nullSafeGet(resultSet, strings[0], sessionImplementor, o);
        try {
            return ((value != null) ? new URI(value) : null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object o, int i, SessionImplementor sessionImplementor) throws HibernateException, SQLException {
        StandardBasicTypes.STRING.nullSafeSet(preparedStatement, (o != null) ? o.toString() : null, i, sessionImplementor);
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Object replace(Object original, Object target, Object owner) {
        return deepCopy(original);
    }

    @Override
    public Class returnedClass() {
        return URI.class;
    }

    @Override
    public int[] sqlTypes() {
        final int[] sqlTypes = { Types.VARCHAR };
        return sqlTypes;
    }
}
