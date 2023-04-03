package cn.aotcloud.mybatis;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.session.Configuration;

public class MyBatisXMLMapperBuilder extends XMLMapperBuilder {
    public MyBatisXMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments, String namespace) {
        super(inputStream, configuration, resource, sqlFragments, namespace);
        setBuilderAssistant(resource);
    }

    public MyBatisXMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
        super(inputStream, configuration, resource, sqlFragments);
        setBuilderAssistant(resource);
    }

    private void setBuilderAssistant(String resource) {
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("builderAssistant");
            if(!field.isAccessible()) {
                field.setAccessible(true);
            }
            field.set(this, new MyBatisMapperBuilderAssistant(configuration, resource));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // ignore
        }
    }
}

