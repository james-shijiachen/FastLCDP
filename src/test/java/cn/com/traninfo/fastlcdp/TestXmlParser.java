package cn.com.traninfo.fastlcdp;

import cn.com.traninfo.fastlcdp.model.*;
import cn.com.traninfo.fastlcdp.service.XmlParserService;
import cn.com.traninfo.fastlcdp.service.SqlGeneratorService;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.io.StringReader;
import java.util.List;

/**
 * 简单的测试类，用于验证XML解析和SQL生成功能
 */
public class TestXmlParser {
    
    public static void main(String[] args) {
        try {
            // 创建测试XML内容
            String xmlContent = createTestXml();
            System.out.println("=== 测试XML内容 ===");
            System.out.println(xmlContent);
            System.out.println();
            
            // 解析XML
            DatabaseSchema schema = parseXml(xmlContent);
            System.out.println("=== XML解析结果 ===");
            System.out.println("数据库名称: " + schema.getName());
            System.out.println("表数量: " + schema.getTables().size());
            
            for (TableDefinition table : schema.getTables()) {
                System.out.println("表名: " + table.getName() + ", 字段数量: " + table.getFields().size());
            }
            System.out.println();
            
            // 生成SQL
            SqlGeneratorService sqlGenerator = new SqlGeneratorService();
            String sql = sqlGenerator.generateCreateDatabaseSql(schema) + "\n\n";
            
            for (TableDefinition table : schema.getTables()) {
                sql += sqlGenerator.generateCreateTableSql(table) + "\n\n";
            }
            
            System.out.println("=== 生成的SQL语句 ===");
            System.out.println(sql);
            
            System.out.println("=== 测试完成 ===");
            System.out.println("XML解析和SQL生成功能正常工作！");
            
        } catch (Exception e) {
            System.err.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String createTestXml() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
               "<database name=\"test_db\" comment=\"测试数据库\">\n" +
               "    <tables>\n" +
               "        <table name=\"user\" comment=\"用户表\">\n" +
               "            <fields>\n" +
               "                <field name=\"id\" type=\"LONG\" primaryKey=\"true\" autoIncrement=\"true\" comment=\"用户ID\"/>\n" +
               "                <field name=\"username\" type=\"STRING\" length=\"50\" nullable=\"false\" comment=\"用户名\"/>\n" +
               "                <field name=\"email\" type=\"STRING\" length=\"100\" comment=\"邮箱\"/>\n" +
               "                <field name=\"created_at\" type=\"DATETIME\" defaultValue=\"CURRENT_TIMESTAMP\" comment=\"创建时间\"/>\n" +
               "            </fields>\n" +
               "            <indexes>\n" +
               "                <index name=\"uk_username\" type=\"UNIQUE\">\n" +
               "                    <columns>\n" +
               "                        <column name=\"username\"/>\n" +
               "                    </columns>\n" +
               "                </index>\n" +
               "            </indexes>\n" +
               "        </table>\n" +
               "        <table name=\"order\" comment=\"订单表\">\n" +
               "            <fields>\n" +
               "                <field name=\"id\" type=\"LONG\" primaryKey=\"true\" autoIncrement=\"true\" comment=\"订单ID\"/>\n" +
               "                <field name=\"user_id\" type=\"LONG\" nullable=\"false\" comment=\"用户ID\"/>\n" +
               "                <field name=\"amount\" type=\"DECIMAL\" length=\"10\" scale=\"2\" comment=\"金额\"/>\n" +
               "                <field name=\"status\" type=\"STRING\" length=\"20\" defaultValue=\"pending\" comment=\"状态\"/>\n" +
               "            </fields>\n" +
               "            <relations>\n" +
               "                <relation name=\"fk_order_user\" column=\"user_id\" referenceTable=\"user\" referenceColumn=\"id\" onDelete=\"CASCADE\"/>\n" +
               "            </relations>\n" +
               "        </table>\n" +
               "    </tables>\n" +
               "</database>";
    }
    
    private static DatabaseSchema parseXml(String xmlContent) {
        try {
            JAXBContext context = JAXBContext.newInstance(DatabaseSchema.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return (DatabaseSchema) unmarshaller.unmarshal(new StringReader(xmlContent));
        } catch (Exception e) {
            throw new RuntimeException("XML解析失败: " + e.getMessage(), e);
        }
    }
}