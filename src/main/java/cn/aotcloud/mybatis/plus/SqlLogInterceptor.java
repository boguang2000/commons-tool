package cn.aotcloud.mybatis.plus;

import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;

/*
*描述: 用于输出格式化好的SQL
*/
public class SqlLogInterceptor implements InnerInterceptor {
	
  private static Logger logger = LoggerFactory.getLogger(SqlLogInterceptor.class);
  
  private static boolean printSQL = true;
  
  public static void startPrintSQL(){
      printSQL = Boolean.TRUE;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
	  if(printSQL) {
          MappedStatement mappedStatement = ms;
          String sqlId = mappedStatement.getId();
          Configuration configuration = mappedStatement.getConfiguration();
          String sql = getSql(configuration, boundSql, sqlId);
          logger.info(sql);
      }
  }

  @Override
  public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
	  if(printSQL) {
          MappedStatement mappedStatement = ms;
          String sqlId = mappedStatement.getId();
          Configuration configuration = mappedStatement.getConfiguration();
          BoundSql boundSql = mappedStatement.getBoundSql(parameter);
          String sql = getSql(configuration, boundSql, sqlId);
          logger.info(sql);
      }
  }

  public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId ) {
      try {
          String sql = showSql(configuration, boundSql);
          StringBuilder str = new StringBuilder(100);
          str.append(sqlId);
          str.append(" ==> ");
          str.append(sql);
          str.append(";");
          return str.toString();
      } catch(Error e) {
          logger.error("解析 sql 异常", e);
      }
      return "";
  }
  
  public static String showSql(Configuration configuration, BoundSql boundSql) {
      Object parameterObject = boundSql.getParameterObject();
      List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
      String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
      if (parameterMappings != null && parameterMappings.size() > 0 && parameterObject != null) {
          TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
          if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
              sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
          } else {
              MetaObject metaObject = configuration.newMetaObject(parameterObject);
              for (ParameterMapping parameterMapping : parameterMappings) {
                  String propertyName = parameterMapping.getProperty();
                  if (metaObject.hasGetter(propertyName)) {
                      Object obj = metaObject.getValue(propertyName);
                      sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                  } else if (boundSql.hasAdditionalParameter(propertyName)) {
                      Object obj = boundSql.getAdditionalParameter(propertyName);
                      sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                  }
              }
          }
      }
      return sql;
  }

  private static String getParameterValue(Object obj) {
      String value = null;
      if (obj instanceof String) {
          value = "'" + obj.toString() + "'";
      } else if (obj instanceof Date) {
          DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
          value = "'" + formatter.format(obj) + "'";
      }else if(obj instanceof LocalDate){
          value = "'" + ((LocalDate) obj).format( DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "'";
      }else if(obj instanceof LocalDateTime){
          value = "'" + ((LocalDateTime) obj).format( DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'";
      } else {
          if (obj != null) {
              value = obj.toString();
          } else {
              value = "";
          }
      }
      return value;
  }
}
