package vip.logz.rdbsync.common.job.context;

import org.postgresql.PGProperty;
import org.postgresql.xa.PGXADataSource;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 可序列化的 Postgres XA 数据源
 *
 * @author logz
 * @date 2024-02-25
 */
public class SerializablePGXADataSource extends PGXADataSource implements Externalizable {

    /**
     * 忽略序列化的属性
     * <p>这些属性值有独立的字段在保存，忽略是为了避免覆盖字段的值。
     */
    private static final Set<PGProperty> IGNORED_PROPS = Set.of(
            PGProperty.PG_HOST,
            PGProperty.PG_DBNAME,
            PGProperty.USER,
            PGProperty.PASSWORD,
            PGProperty.PG_PORT
    );

    /** 构造器 */
    public SerializablePGXADataSource() {
    }

    /**
     * 序列化
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(getServerNames());
        out.writeObject(getDatabaseName());
        out.writeObject(getUser());
        out.writeObject(getPassword());
        out.writeObject(getPortNumbers());

        // 序列化属性
        Map<String, String> props = new HashMap<>();
        for (PGProperty prop : PGProperty.values()) {
            if (IGNORED_PROPS.contains(prop)) {
                continue;
            }

            String value = getProperty(prop);
            if (value != null) {
                props.put(prop.getName(), value);
            }
        }
        out.writeObject(props);
    }

    /**
     * 反序列化
     */
    @Override
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        setServerNames((String[]) in.readObject());
        setDatabaseName((String) in.readObject());
        setUser((String) in.readObject());
        setPassword((String) in.readObject());
        setPortNumbers((int[]) in.readObject());

        // 反序列化属性
        Map<String, String> props = (Map<String, String>) in.readObject();
        for (PGProperty prop : PGProperty.values()) {
            if (IGNORED_PROPS.contains(prop)) {
                continue;
            }

            String value = props.get(prop.getName());
            if (value != null) {
                setProperty(prop, value);
            }
        }
    }

}
