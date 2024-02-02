package vip.logz.rdbsync.job.simple;

import vip.logz.rdbsync.common.rule.Pipeline;
import vip.logz.rdbsync.common.rule.PipelineBuilder;
import vip.logz.rdbsync.common.rule.table.Mapping;
import vip.logz.rdbsync.common.rule.table.MappingBuilder;
import vip.logz.rdbsync.common.rule.table.RegexTableMatcher;
import vip.logz.rdbsync.connector.mysql.rule.Mysql;
import vip.logz.rdbsync.connector.mysql.utils.MysqlDDLGenerator;
import vip.logz.rdbsync.connector.starrocks.rule.Starrocks;
import vip.logz.rdbsync.connector.starrocks.utils.StarrocksDDLGenerator;

/**
 * 简单地维护一下表映射和管道的元数据
 *
 * @author logz
 * @date 2024-01-11
 */
public class Pipelines {

    /*
     Mysql --> Starrocks
     */

    /** 表映射：订单表的Starrocks结构 */
    private final static Mapping<Starrocks> ordersToStarrocksMapping = MappingBuilder.<Starrocks>of()
            .field("order_id").type(Starrocks.INT()).nonNull().primaryKey().comment("订单ID").and()
            .field("order_date").type(Starrocks.DATETIME()).comment("下单日期").and()
            .field("customer_name").type(Starrocks.STRING()).comment("客户名称").and()
            .field("price").type(Starrocks.DECIMAL(10, 5)).comment("价格").and()
            .field("product_id").type(Starrocks.INT()).comment("商品ID").and()
            .field("order_status").type(Starrocks.INT()).comment("订单状态").and()
            .comment("订单")
            .build();

    /** 管道：Mysql同步到Starrocks */
    public final static Pipeline<Starrocks> mysqlToStarrocksPipeline = PipelineBuilder.<Starrocks>of("mysql_to_starrocks")
            .sourceId("simple_source_mysql")
            .distId("simple_dist_starrocks")
            .binding("orders", "orders", ordersToStarrocksMapping)
            .build();


    /*
     Mysql --> Mysql
     */

    /** 表映射：订单表的Mysql结构 */
    private final static Mapping<Mysql> ordersToMysqlMapping = MappingBuilder.<Mysql>of()
            .field("order_id").type(Mysql.INTEGER()).nonNull().primaryKey().comment("订单ID").and()
            .field("order_date").type(Mysql.DATETIME()).comment("下单日期").and()
            .field("customer_name").type(Mysql.VARCHAR(255)).comment("客户名称").and()
            .field("price").type(Mysql.DECIMAL(10, 5)).comment("价格").and()
            .field("product_id").type(Mysql.INTEGER()).comment("商品ID").and()
            .field("order_status").type(Mysql.TINYINT()).comment("订单状态").and()
            .comment("订单")
            .build();

    /** 管道：Mysql同步到Mysql */
    public final static Pipeline<Mysql> mysqlToMysqlPipeline = PipelineBuilder.<Mysql>of("mysql_to_mysql")
            .sourceId("simple_source_mysql")
            .distId("simple_dist_mysql")
            .binding(t -> t.startsWith("orders"), "orders", ordersToMysqlMapping)
            .binding(RegexTableMatcher.of("$orders_\\d+"), "orders", ordersToMysqlMapping)
            .build();


    /*
     自动生成目标表的建表语句
     */

    public static void main(String[] args) {
        System.out.println("Mysql同步到Starrocks：");
        for (String ddl : new StarrocksDDLGenerator().generate(mysqlToStarrocksPipeline)) {
            System.out.println(ddl);
        }

        System.out.println();
        System.out.println("Mysql同步到Mysql：");
        for (String ddl : new MysqlDDLGenerator().generate(mysqlToMysqlPipeline)) {
            System.out.println(ddl);
        }
    }

}
