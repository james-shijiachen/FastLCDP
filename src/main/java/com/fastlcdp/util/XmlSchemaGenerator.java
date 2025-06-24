package com.fastlcdp.util;

import com.fastlcdp.model.DatabaseSchema;
import lombok.extern.slf4j.Slf4j;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

/**
 * XML Schema生成器
 * <p>
 * 用于根据Java模型类生成XSD文件，以便验证XML配置文件的格式和内容
 * </p>
 *
 * @author FastLCDP
 */
@Slf4j
public class XmlSchemaGenerator {

    /**
     * 生成XSD文件
     *
     * @param outputDir 输出目录
     * @throws JAXBException JAXB异常
     * @throws IOException   IO异常
     */
    public static void generateSchema(String outputDir) throws JAXBException, IOException {
        log.info("开始生成XML Schema文件，输出目录: {}", outputDir);
        
        // 创建输出目录
        File dir = new File(outputDir);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("无法创建输出目录: " + outputDir);
            }
        }
        
        // 创建JAXB上下文
        JAXBContext jaxbContext = JAXBContext.newInstance(DatabaseSchema.class);
        
        // 生成Schema文件
        final File schemaFile = new File(dir, "fastlcdp-schema.xsd");
        jaxbContext.generateSchema(new SchemaOutputResolver() {
            @Override
            public Result createOutput(String namespaceUri, String suggestedFileName) {
                StreamResult result = new StreamResult(schemaFile);
                result.setSystemId(schemaFile.toURI().toString());
                return result;
            }
        });
        
        log.info("XML Schema文件生成成功: {}", schemaFile.getAbsolutePath());
    }
    
    /**
     * 主方法，用于命令行调用
     *
     * @param args 命令行参数，第一个参数为输出目录
     */
    public static void main(String[] args) {
        try {
            String outputDir = args.length > 0 ? args[0] : "schema";
            generateSchema(outputDir);
        } catch (Exception e) {
            log.error("生成XML Schema文件失败", e);
            System.exit(1);
        }
    }
}