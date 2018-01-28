package tk.mybatis.mapper;

import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapper.CountryMapper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.model.Country;
import tk.mybatis.spring.annotation.MapperScan;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liuzh
 */

public class SpringAnnotationTest {

    private AnnotationConfigApplicationContext applicationContext;

    @Before
    public void setupContext() {
        applicationContext = new AnnotationConfigApplicationContext();
    }

    private void startContext() {
        applicationContext.refresh();
        applicationContext.start();
        // this will throw an exception if the beans cannot be found
        applicationContext.getBean("sqlSessionFactory");
    }

    @Test
    public void testMyBatisConfigRef() {
        applicationContext.register(MyBatisConfigRef.class);
        startContext();
        CountryMapper countryMapper = applicationContext.getBean(CountryMapper.class);
        List<Country> countries = countryMapper.selectAll();
        Assert.assertNotNull(countries);
        Assert.assertEquals(183, countries.size());
    }

    @Test
    public void testMyBatisConfigProperties() {
        applicationContext.register(MyBatisConfigProperties.class);
        startContext();
        CountryMapper countryMapper = applicationContext.getBean(CountryMapper.class);
        List<Country> countries = countryMapper.selectAll();
        Assert.assertNotNull(countries);
        Assert.assertEquals(183, countries.size());
    }

    @Test(expected = MapperException.class)
    public void testMyBatisConfigError() {
        applicationContext.register(MyBatisConfigError.class);
        startContext();
        CountryMapper countryMapper = applicationContext.getBean(CountryMapper.class);
        List<Country> countries = countryMapper.selectAll();
        Assert.assertNotNull(countries);
        Assert.assertEquals(183, countries.size());
    }

    @Test(expected = MapperException.class)
    public void testMyBatisConfigPropertiesError() {
        applicationContext.register(MyBatisConfigPropertiesError.class);
        startContext();
        CountryMapper countryMapper = applicationContext.getBean(CountryMapper.class);
        List<Country> countries = countryMapper.selectAll();
        Assert.assertNotNull(countries);
        Assert.assertEquals(183, countries.size());
    }

    @Configuration
    @MapperScan(value = "tk.mybatis.mapper.mapper", mapperHelperRef = "mapperHelper")
    public static class MyBatisConfigRef {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .addScript("CreateDB.sql")
                    .build();
        }

        @Bean
        public DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public SqlSessionFactory sqlSessionFactory() throws Exception {
            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource());
            return sessionFactory.getObject();
        }

        @Bean
        public MapperHelper mapperHelper() {
            Config config = new Config();
            List<Class> mappers = new ArrayList<Class>();
            mappers.add(Mapper.class);
            config.setMappers(mappers);

            MapperHelper mapperHelper = new MapperHelper();
            mapperHelper.setConfig(config);
            return mapperHelper;
        }
    }

    @Configuration
    @MapperScan(value = "tk.mybatis.mapper.mapper",
        properties = {
                "mappers=tk.mybatis.mapper.common.Mapper",
                "notEmpty=true"
        }
    )
    public static class MyBatisConfigProperties {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .addScript("CreateDB.sql")
                    .build();
        }

        @Bean
        public DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public SqlSessionFactory sqlSessionFactory() throws Exception {
            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource());
            return sessionFactory.getObject();
        }
    }

    @Configuration
    @MapperScan(value = "tk.mybatis.mapper.mapper",
        properties = {
                //参数配置错误
                "mapperstk.mybatis.mapper.common.Mapper",
                "notEmpty=true"
        }
    )
    public static class MyBatisConfigPropertiesError {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .addScript("CreateDB.sql")
                    .build();
        }

        @Bean
        public DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public SqlSessionFactory sqlSessionFactory() throws Exception {
            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource());
            return sessionFactory.getObject();
        }
    }

    @Configuration
    @MapperScan(value = "tk.mybatis.mapper.mapper")
    public static class MyBatisConfigError {
        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .addScript("CreateDB.sql")
                    .build();
        }

        @Bean
        public DataSourceTransactionManager transactionManager() {
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        public SqlSessionFactory sqlSessionFactory() throws Exception {
            SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
            sessionFactory.setDataSource(dataSource());
            return sessionFactory.getObject();
        }
    }


}
