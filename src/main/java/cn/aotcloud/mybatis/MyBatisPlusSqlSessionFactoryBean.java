package cn.aotcloud.mybatis;

import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.SqlSessionFactory;

public class MyBatisPlusSqlSessionFactoryBean extends MybatisSqlSessionFactoryBean {

    private Resource[] mapperLocations;

    private MybatisConfiguration configuration;

    /**
     * 不能删除
     */
    @Override
    public void setMapperLocations(Resource[] mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    @Override
    public void setConfiguration(MybatisConfiguration configuration) {
        super.setConfiguration(configuration);
        this.configuration = configuration;
    }

    @SuppressWarnings("deprecation")
	@Override
    protected SqlSessionFactory buildSqlSessionFactory() throws Exception {
        SqlSessionFactory sqlSessionFactory = super.buildSqlSessionFactory();
        if (this.mapperLocations != null && this.mapperLocations.length > 0) {
            for (Resource mapperLocation : this.mapperLocations) {
                if (mapperLocation == null) {
                    continue;
                }
                InputStream inputStream = null;
                try {
                	inputStream = mapperLocation.getInputStream();
                    XMLMapperBuilder xmlMapperBuilder = new MyBatisXMLMapperBuilder(inputStream, configuration, mapperLocation.toString(), configuration.getSqlFragments());
                    xmlMapperBuilder.parse();
                } catch (Exception e) {
                    throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
                } finally {
                    IOUtils.closeQuietly(inputStream);
                    ErrorContext.instance().reset();
                }
            }
        }
        return sqlSessionFactory;
    }
}

