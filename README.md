# Mybatis 通用 Mapper 和 Spring 集成

本项目主要提供了两种大的配置方式。

- `MapperScannerConfigurer` xml bean 配置
- `@MapperScan` 注解

## 初次使用通用 Mapper 请注意

下面的示例只是演示如何进行配置，具体配置那些参数要自己选择！

所有可配置参数请参考通用 Mapper 文档：

> https://github.com/abel533/Mapper/blob/master/wiki/mapper3/2.Integration.md

## 一、`MapperScannerConfigurer` xml bean 配置

```xml
<bean class="tk.mybatis.spring.mapper.MapperScannerConfigurer">
    <property name="basePackage" value="tk.mybatis.mapper.mapper"/>
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
    <property name="properties">
        <value>
            mappers=tk.mybatis.mapper.common.Mapper
        </value>
    </property>
</bean>
```
注意两点：

 1. 这里使用的 `tk.mybatis.spring.mapper.MapperScannerConfigurer`，不是官方的 `org.xxx`
 2. 所有对通用 Mapper 的配置，参考上面的 mappers=xxx，一行写一个配置即可

## 二、`@MapperScan` 注解

纯注解使用的时候，通用 Mapper 的参数不能像原来那样直接配置，为了适应这种方式，提供了三种可用的方式。

下面按照优先级由高到低的顺序来讲注解配置用法。

### 1. 使用 `mapperHelperRef` 配置

```java
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
```

在这个例子中 `@MapperScan` 唯一特殊的地方在于 `mapperHelperRef` 属性，这个属性用于指定 MapperHelper bean 的 `name`，这里的名字和代码中配置的如下代码一致：
```java
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
```
>Spring 中默认的 name 就是方法名，还可以通过 `@Bean` 注解指定 `name`。

在这种配置方式中，你可以很方便的控制 `MapperHelper` 中的各项配置。

### 2. 使用 `properties` 配置

```java
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
```
如上面代码中所示，这种配置方式和 xml bean 的方式比较接近，就是通过一行一行的 `xx=xxx` 对通用 Mapper 进行配置，配置时参考这里的示例配置即可。

### 3. Spring Boot 环境中使用 `application.[yml|properties]` 配置文件

在 Spring Boot 中使用 Mapper 时，如果选择使用注解方式（可以不引入 mapper-starter 依赖），就可以选择这第 3 种方式。

>特别提醒：Spring Boot 中常见的是配置文件方式，使用环境变量或者运行时的参数都可以配置，这些配置都可以对通用 Mapper 生效。

例如在 yml 格式中配置：
```yml
mapper:
  mappers:
    - tk.mybatis.mapper.common.Mapper
    - tk.mybatis.mapper.common.Mapper2
  not-empty: true
```

在 propertie 配置中：
```properties
mapper.mappers=tk.mybatis.mapper.common.Mapper,tk.mybatis.mapper.common.Mapper2
mapper.not-empty=true
```

>特别提醒：Spring Boot 中支持 relax 方式的参数配置，但是前面两种方式都不支持，配置参数的时候需要保证大小写一致！
