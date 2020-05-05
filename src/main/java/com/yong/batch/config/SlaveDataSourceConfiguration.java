package com.yong.batch.config;

import javax.sql.DataSource;
import net.sf.log4jdbc.Log4jdbcProxyDataSource;
import net.sf.log4jdbc.tools.Log4JdbcCustomFormatter;
import net.sf.log4jdbc.tools.LoggingType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@MapperScan(value="com.yong.batch.model.dao.slave", sqlSessionFactoryRef="slaveSqlSessionFactory")
public class SlaveDataSourceConfiguration {

	@Bean
	@ConfigurationProperties(prefix="spring.datasource.slave")
	public PoolProperties getSlavePoolProperties() {
		return new PoolProperties();
	}

    @Profile(value ={"local", "dv", "test"})
	@Bean(name="slaveDataSource")
	public DataSource slaveDataSourceDev() {
		org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(getSlavePoolProperties());
		Log4jdbcProxyDataSource proxyDataSource  =  new Log4jdbcProxyDataSource(dataSource);
		
		//SQL Formatter
		Log4JdbcCustomFormatter customFormatter = new Log4JdbcCustomFormatter();
		LoggingType loggingType = LoggingType.MULTI_LINE;
		customFormatter.setLoggingType(loggingType);
		customFormatter.setSqlPrefix("───────────────────────────────────────────────[ SQL ]\n");
		
		proxyDataSource.setLogFormatter(customFormatter);		
		return proxyDataSource;
	}

	@Profile("op")
	@Bean(name="slaveDataSource")
	@ConfigurationProperties(prefix="spring.datasource.slave")
	public DataSource slaveDataSourceProd() {
        return DataSourceBuilder.create().build();
	}

	@Bean(name="slaveSqlSessionFactory")
	public SqlSessionFactory slaveSqlSessionFactory(@Qualifier("slaveDataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mapper/mybatis-config.xml"));
		sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/slave/**/*.xml"));
		sqlSessionFactoryBean.setTypeAliasesPackage("com.yong.batch.model.vo");

		return sqlSessionFactoryBean.getObject();
	}

	@Bean(name="slaveSqlSessionTemplate")
	public SqlSessionTemplate slaveSqlSessionTemplate(SqlSessionFactory slaveSqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(slaveSqlSessionFactory);
	}
}
