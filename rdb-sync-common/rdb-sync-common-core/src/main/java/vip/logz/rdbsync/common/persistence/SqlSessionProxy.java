package vip.logz.rdbsync.common.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import vip.logz.rdbsync.common.utils.ClassScanner;
import vip.logz.rdbsync.common.utils.PropertiesUtils;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * SQL会话代理
 *
 * @author logz
 * @date 2024-01-09
 */
public class SqlSessionProxy {

    /** 已注册的映射器类型 */
    private static final List<Class<?>> MAPPER_CLASSES = new ArrayList<>();
    static {
        // 深度扫描当前包中的所有映射器
        String basePackage = SqlSessionProxy.class.getPackageName();
        Set<Class<?>> mapperClasses = ClassScanner.scanByAnnotation(Mapper.class, basePackage);
        MAPPER_CLASSES.addAll(mapperClasses);
    }

    /** SQL会话工厂 */
    private static SqlSessionFactory sessionFactory;

    /**
     * 构造器
     * @param env 运行环境
     */
    public SqlSessionProxy(String env) {
        // 创建运行环境
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment(env, transactionFactory, createDataSource(env));

        // 创建配置
        Configuration configuration = new Configuration(environment);
        configuration.setMapUnderscoreToCamelCase(true);  // 自动转换命名规则（下划线与驼峰）
        for (Class<?> mapperClass : MAPPER_CLASSES) {
            configuration.addMapper(mapperClass);
        }

        // 创建SQL会话工厂
        sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    /**
     * 创建数据源
     * @param env 运行环境
     */
    private static DataSource createDataSource(String env) {
        // 从配置文件中获取连接信息
        DataSourceProperties dataSourceProperties = PropertiesUtils.get(
                env,
                DataSourceProperties.PATH,
                DataSourceProperties.class
        );
        
        return new PooledDataSource(
                dataSourceProperties.getDriver(),
                dataSourceProperties.getUrl(),
                dataSourceProperties.getUsername(),
                dataSourceProperties.getPassword()
        );
    }

    /**
     * 执行数据库操作
     * @param mapperClass 映射器类型
     * @param process 操作过程回调函数。提供期望的映射器，操作完成后会话将自动销毁。
     * @return 返回操作结果
     * @param <T> 映射器类型
     * @param <R> 操作结果类型
     */
    public <T, R> R execute(Class<T> mapperClass, Function<T, R> process) {
        try (SqlSession session = sessionFactory.openSession(true)) {
            T mapper = session.getMapper(mapperClass);
            return process.apply(mapper);
        }
    }

}
