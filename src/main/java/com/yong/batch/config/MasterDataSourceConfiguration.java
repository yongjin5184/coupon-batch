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
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@MapperScan(value="com.yong.batch.model.dao.master", sqlSessionFactoryRef="masterSqlSessionFactory")
public class MasterDataSourceConfiguration {
	@Bean
	@ConfigurationProperties(prefix="spring.datasource.master")
    @Primary
	public PoolProperties getMasterPoolProperties() {
		return new PoolProperties();
	}

	@Profile(value ={"local", "dv", "test"})
	@Bean(name="masterDataSource")
    @Primary
	public DataSource masterDataSourceDev() {
		org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource(getMasterPoolProperties());
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
	@Bean(name="masterDataSource", destroyMethod="close")
	@ConfigurationProperties(prefix="spring.datasource.master")
    @Primary
	public DataSource masterDataSourceProd() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name="masterSqlSessionFactory")
    @Primary
	public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource dataSource, ApplicationContext applicationContext) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource);
		sqlSessionFactoryBean.setConfigLocation(applicationContext.getResource("classpath:mapper/mybatis-config.xml"));
		sqlSessionFactoryBean.setMapperLocations(applicationContext.getResources("classpath:mapper/master/**/*.xml"));
		sqlSessionFactoryBean.setTypeAliasesPackage("com.yong.batch.model.vo");
		return sqlSessionFactoryBean.getObject();
	}

	@Bean(name="masterSqlSessionTemplate")
    @Primary
	public SqlSessionTemplate masterSqlSessionTemplate(SqlSessionFactory masterSqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(masterSqlSessionFactory);
	}
}
