package com.github.fanzezhen.common.mp.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.github.fanzezhen.common.core.constant.CommonConstant;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author zezhen.fan
 */
public class GeneratorTool {
    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static void generator(AbstractGeneratorBean generatorBean) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(generatorBean.getDataSourceConfigUrl());
        dsc.setDriverName(generatorBean.getDriverName());
        dsc.setUsername(generatorBean.getDbUsername());
        dsc.setPassword(generatorBean.getDbPassword());
        mpg.setDataSource(dsc);

        List<String> moduleNameList = generatorBean.getModuleNameList();
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        StringBuilder moduleLevelName = new StringBuilder();
        for (String s : moduleNameList) {
            moduleLevelName.append(CommonConstant.SEPARATOR_DIR).append(s);
        }
        gc.setOutputDir(projectPath + moduleLevelName + "/src/main/java");
        gc.setAuthor(generatorBean.getAuthor());
        gc.setOpen(false);
        gc.setIdType(IdType.ASSIGN_UUID);
        // 实体属性 Swagger2 注解
        gc.setSwagger2(true);
        mpg.setGlobalConfig(gc);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(generatorBean.getPackageModuleName());
        pc.setParent(generatorBean.getParentPackageName());
        mpg.setPackageInfo(pc);

        // 自定义配置
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                // to do nothing
            }
        };
        List<FileOutConfig> focList = new ArrayList<>();
        focList.add(new FileOutConfig("/templates/mapper.xml.ftl") {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输入文件名称
                return projectPath + moduleLevelName + "/src/main/resources/mapper/" + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });
        cfg.setFileOutConfigList(focList);
        mpg.setCfg(cfg);
        mpg.setTemplate(new TemplateConfig().setXml(null));

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        if (generatorBean.getSuperEntityClass() != null) {
            strategy.setSuperEntityClass(generatorBean.getSuperEntityClass());
            strategy.setSuperEntityColumns(generatorBean.getSuperEntityColumns());
        }
        strategy.setTableFillList(generatorBean.getTableFillList());
        strategy.setEntityLombokModel(true);
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setTablePrefix("t_");
        strategy.setLogicDeleteFieldName(generatorBean.getLogicDeleteFieldName());
        strategy.setInclude(generatorBean.getTableNameStr().split(generatorBean.getTableNameSplitter()));
        mpg.setStrategy(strategy);
        // 选择 freemarker 引擎需要指定如下加，注意 pom 依赖必须有！
        mpg.setTemplateEngine(new FreemarkerTemplateEngine());
        mpg.execute();
    }

}
