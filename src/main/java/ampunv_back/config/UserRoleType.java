package ampunv_back.config;

import ampunv_back.entity.User.UserRole;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class UserRoleType implements UserType<UserRole> {

    @Override
    public int getSqlType() {
        return Types.OTHER;
    }

    @Override
    public Class<UserRole> returnedClass() {
        return UserRole.class;
    }

    @Override
    public boolean equals(UserRole x, UserRole y) throws HibernateException {
        return Objects.equals(x, y);
    }

    @Override
    public int hashCode(UserRole x) throws HibernateException {
        return Objects.hashCode(x);
    }

    @Override
    public UserRole nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner)
            throws SQLException {
        String columnValue = rs.getString(position);
        if (rs.wasNull() || columnValue == null) {
            return null;
        }
        return UserRole.valueOf(columnValue);
    }

    @Override
    public void nullSafeSet(PreparedStatement st, UserRole value, int index, SharedSessionContractImplementor session)
            throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value.name(), Types.OTHER);
        }
    }

    @Override
    public UserRole deepCopy(UserRole value) throws HibernateException {
        return value;
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(UserRole value) throws HibernateException {
        return value;
    }

    @Override
    public UserRole assemble(Serializable cached, Object owner) throws HibernateException {
        return (UserRole) cached;
    }

    @Override
    public UserRole replace(UserRole detached, UserRole managed, Object owner) throws HibernateException {
        return detached;
    }
}