package cn.aotcloud.mybatis;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.apache.ibatis.session.Configuration;

public class MyBatisMapperBuilderAssistant extends MapperBuilderAssistant {
    public MyBatisMapperBuilderAssistant(Configuration configuration, String resource) {
        super(configuration, resource);
    }

    @Override
    public void setCurrentNamespace(String currentNamespace) {
        // 关键逻辑，处理自定义的namespace规则
        if(StringUtils.endsWith(currentNamespace,"_sqlmap")) {
            super.setCurrentNamespace(StringUtils.removeEnd(currentNamespace,"_sqlmap"));
        }
        else {
            super.setCurrentNamespace(currentNamespace);
        }
    }
}

