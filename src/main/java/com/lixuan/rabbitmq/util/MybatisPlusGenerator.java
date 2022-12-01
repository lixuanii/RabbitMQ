package com.lixuan.rabbitmq.util;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.lixuan.rabbitmq.entity.BaseEntity;

import java.util.Collections;

/**
 * @author lixuan
 * @date 2022-11-21 15:54
 */
public class MybatisPlusGenerator {


    public static void main(String[] args) {

        FastAutoGenerator.create("jdbc:mysql://localhost:3306/local_test?useUnicode=true&characterEncoding=utf-8&useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=Hongkong", "root", "123")
                .globalConfig(builder -> builder.author("lixuan")
                        .outputDir(System.getProperty("user.dir") + "/src/main/java")
                        .commentDate("yyyy-MM-dd hh:mm:ss")
                        .dateType(DateType.TIME_PACK)
                        .disableOpenDir())
                .packageConfig(builder -> builder.parent("com.lixuan.rabbitmq")
                        .entity("entity")
                        .service("service")
                        .serviceImpl("service.impl")
                        .mapper("mapper")
                        .controller("controller")
                        .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper")))
                .strategyConfig(builder -> {
                    // 表名
                    builder.addInclude("mq_msg_log")
                            .addTablePrefix("")

                            // mapper
                            .mapperBuilder()
                            .superClass(BaseMapper.class)
                            .formatMapperFileName("%sMapper")
                            .formatXmlFileName("%s")

                            .serviceBuilder()
                            .formatServiceFileName("%sService")
                            .formatServiceImplFileName("%sServiceImpl")

                            .entityBuilder()
                            .enableLombok()
                            .logicDeleteColumnName("deleted")
                            .naming(NamingStrategy.underline_to_camel)
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .enableTableFieldAnnotation()
                            .superClass(BaseEntity.class)
                            .enableRemoveIsPrefix()


                            .controllerBuilder()
                            .formatFileName("%sController")
                            .enableRestStyle();

                })
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }
}
