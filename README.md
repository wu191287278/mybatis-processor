# mybatis-example-processor
mybatis-example-processor 该工具是用于在编译阶段自动生成mybatis 的Example.java
和Mapper.xml文件

### 使用方式
```
依赖:

 <dependency>
      <artifactId>mybatis-example-processor</artifactId>
      <groupId>com.vcg</groupId>
      <version>0.0.1-SNAPSHOT</version>
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
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>${lombok.version}</version>
            </path>
            <path>
                <artifactId>mybatis-example-processor</artifactId>
                <groupId>com.vcg</groupId>
                <version>0.0.1-SNAPSHOT</version>
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

2. 需要扫描生成的DomainExampleMapper.xml文件

```
mybatis:
  mapper-locations:
    - classpath:com/example/domain/*ExampleMapper.xml
```
