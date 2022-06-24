package org.miaomiao.generate.util;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.miaomiao.generate.model.BaseResModel;
import org.miaomiao.generate.model.BasicInfo;
import org.miaomiao.generate.model.FileType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FreemarkerUtils {

    public static void ftlToFile(BasicInfo basicInfo, FileType fileType, String savePath) {
        if (fileType.equals(FileType.CONTROLLER)) {
            createFile(basicInfo, savePath + File.separator + basicInfo.getEntityName() + "Controller.java", "controller.ftl");
        } else if (fileType.equals(FileType.ENTITY)) {
            createFile(basicInfo, savePath + File.separator + basicInfo.getEntityName() + ".java", "entity.ftl");
        } else if (fileType.equals(FileType.SERVICE)) {
            createFile(basicInfo, savePath + File.separator + basicInfo.getEntityName() + "Service.java", "service.ftl");
        }else if (fileType.equals(FileType.IMPL)) {
            createFile(basicInfo, savePath + File.separator + basicInfo.getEntityName() + "ServiceImpl.java", "impl.ftl");
        }else if (fileType.equals(FileType.MAPPER)) {
            createFile(basicInfo, savePath + File.separator + basicInfo.getEntityName() + "Mapper.java", "mapper.ftl");
        }else if (fileType.equals(FileType.XML)) {
            createFile(basicInfo, savePath + File.separator + basicInfo.getEntityName() + "Mapper.xml", "xml.ftl");
        }
    }

    private static BaseResModel createFile(BasicInfo basicInfo, String savePath, String ftlName) {
        BaseResModel resModel = new BaseResModel();
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setClassForTemplateLoading(FreemarkerUtils.class, "/templates");
        Template template;
        try {
            template = configuration.getTemplate(ftlName);
            File file = new File(savePath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            } else {
                resModel.setCode(1001);
                resModel.setContent("文件已存在！");
            }
            //创建输出流
            FileWriter out = new FileWriter(file);
            //
            template.process(basicInfo, out);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
        return resModel;
    }

}
