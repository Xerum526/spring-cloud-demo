package com.spgcld.demo.config.database;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter; 

@Configuration
@EnableTransactionManagement //使用事务
public class DruidDataSourceConfig {
		
	@Autowired
	private DruidDataSourceSettings druidSettings;
	
	public static String DRIVER_CLASSNAME ;
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyConfigure(){
		return new PropertySourcesPlaceholderConfigurer();
	}	
	
    @Bean
    public ServletRegistrationBean druidServlet() {   	
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");
        reg.addInitParameter("allow", "127.0.0.1");
        return reg;
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico, /druid/*");
        return filterRegistrationBean;
    }
    
	@Bean
	public DataSource dataSource() throws SQLException {
		DruidDataSource ds = new DruidDataSource();
		ds.setDriverClassName(druidSettings.getDriverClassName());
		DRIVER_CLASSNAME = druidSettings.getDriverClassName();
		ds.setUrl(druidSettings.getUrl());
		ds.setUsername(druidSettings.getUsername());
		ds.setPassword(druidSettings.getPassword());
		ds.setInitialSize(druidSettings.getInitialSize());
		ds.setMinIdle(druidSettings.getMinIdle());
		ds.setMaxActive(druidSettings.getMaxActive());
		ds.setTimeBetweenEvictionRunsMillis(druidSettings.getTimeBetweenEvictionRunsMillis());
		ds.setMinEvictableIdleTimeMillis(druidSettings.getMinEvictableIdleTimeMillis());
		ds.setValidationQuery(druidSettings.getValidationQuery());
		ds.setTestWhileIdle(druidSettings.isTestWhileIdle());
		ds.setTestOnBorrow(druidSettings.isTestOnBorrow());
		ds.setTestOnReturn(druidSettings.isTestOnReturn());
		ds.setPoolPreparedStatements(druidSettings.isPoolPreparedStatements());
		ds.setMaxPoolPreparedStatementPerConnectionSize(druidSettings.getMaxPoolPreparedStatementPerConnectionSize());
		ds.setFilters(druidSettings.getFilters());
		ds.setConnectionProperties(druidSettings.getConnectionProperties());
		return ds;
	}

	@Bean
	public PlatformTransactionManager transactionManager() throws Exception {
		DataSourceTransactionManager txManager = new DataSourceTransactionManager();
		txManager.setDataSource(dataSource());
		return txManager;
	}
    
}


