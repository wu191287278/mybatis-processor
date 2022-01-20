# mybatis-processor
mybatis-processor 该工具是用于在编译阶段自动生成mybatis 的Example.java 和Mapper.xml文件

### 使用方式
```
依赖:

 <dependency>
      <artifactId>mybatis-processor-core</artifactId>
      <groupId>io.github.wu191287278</groupId>
      <version>2.0.1</version>
</dependency>

maven 编译插件:

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <annotationProcessorPaths>
            <path>
                <artifactId>mybatis-processor-core</artifactId>
                <groupId>com.github.wu191287278</groupId>
                <version>2.0.1</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>

```


1. Domain 类需要打上Example 注解并指定namespace 既可生成mapper.xml 以及example 查询类生成路径为domain类同级.
命名规则为: domain+ExampleMapper.xml,domainExample.java

```
@Example(namespace = "com.example.repositories.CommentRepository") //该namespace 是mapper.xml namespace
@Data
@Accessors(chain = true)
@Entity
@Table(name = "comment")
public class Comment {

    /**
     * primary key
     */
    @Id
    @Column(name = "id", columnDefinition = "VARCHAR", nullable = false, length = 64, precision = 0)
    private String id;

    /**
     * 评论内容
     */
    @Column(name = "message", columnDefinition = "TEXT", nullable = true, length = 65535, precision = 0)
    private String content;

    /**
     * 用户id
     */
    @Column(name = "user_id", columnDefinition = "VARCHAR", nullable = true, length = 64, precision = 0)
    private String userId;

}
```

2. SpringBoot需要扫描生成的DomainExampleMapper.xml文件


application.yml

```
mybatis:
  mapper-locations:
    - classpath:com/example/domain/*ExampleMapper.xml
```

3. 使用方式

```
CommentExample query = CommentExample.create()
    .andUserIdEqualTo("1","2")
    .page(1,10);
List<Comment> comments = commentRepository.selectByExample(query);
```

3. mybatisConfig.xml 使用方式

```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC" />
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.cj.jdbc.Driver" />
                <property name="url" value="jdbc:mysql://localhost/users" />
                <property name="username" value="root" />
                <property name="password" value="root" />
            </dataSource>
        </environment>
    </environments>
    <mappers>
        <mapper resource="com/example/domain/CommentExampleMapper.xml"/>
    </mappers>

</configuration>
```

使用方式

```
    InputStream in = Demo.class.getClassLoader().getResourceAsStream("mybatisConfig.xml");
    SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder()
            .build(in);
    SqlSession sqlSession = sessionFactory.openSession();
    CommentRepository commentRepository = sqlSession.getMapper(CommentRepository.class);
    Comment comment = commentRepository.selectByPrimaryKey(1);
    System.err.println(comment);
```
