## 一、SpringBoot入门之HelloWorld

### 创建一个Maven工程

![image-20200528234024467](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200528234024467.png)

直接Next下一步

![image-20200528234122620](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200528234122620.png)

填写完GroupId、ArtifactId后直接Next下一步

![image-20200528234306897](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200528234306897.png)

修改Project name和前边ArtifactId一致(不一致也没关系)，选择项目保存目录，Finish完成！

* 导入相关依赖

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.1.RELEASE</version>
    <relativePath/>
</parent>

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

* 编写主程序

```java
package com.tony;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @SpringBootApplication:标明这是一个springboot程序
 */
@SpringBootApplication
public class HelloWorldMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }
}
```

* 编写一个controller

```java
package com.tony.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloWorldController {

    @ResponseBody
    @RequestMapping("hello")
    public String hello(){
        return "hello world";
    }
}
```

* 启动主程序

* 访问地址 http:localhost:8080/hello

```
// 浏览器显示内容
hello world
```

### 打包部署

* 在idea工具的最右侧，按图点击package

![image-20200528235824220](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20200528235824220.png)

* 打包成功信息如下

  ```
  [INFO] Building jar: D:\Workspace\Java\Springboot-Learning\springboot-01-helloworld\target\springboot-01-helloworld-1.0-SNAPSHOT.jar
  [INFO] ------------------------------------------------------------------------
  [INFO] BUILD SUCCESS
  [INFO] ------------------------------------------------------------------------
  [INFO] Total time:  11.769 s
  [INFO] Finished at: 2020-05-28T23:36:30+08:00
  [INFO] ------------------------------------------------------------------------
  ```

* 部署到服务器后，可通过命令启动项目：java -jar jar包名称



## HelloWorld程序启动底层原理探究

### 依赖

```xml
<!--从Pom.xml文件中可以看到，Hello World项目依赖的父工程是org.springframework.boot-->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.2.1.RELEASE</version>
    <relativePath/>
</parent>

<!--
	从idea中点击spring-boot-starter-parent，点进去发现org.springframework.boot又依赖spring-boot-dependencies，它管理着Spring Boot应用所有依赖的版本号，所以以后我们导入的话，不用写版本号(没有在dependencies里面管理的依赖需要手动声明版本号)
-->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-dependencies</artifactId>
    <version>2.2.1.RELEASE</version>
    <relativePath>../../spring-boot-dependencies</relativePath>
</parent>
```

### 启动器

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

**spring-boot-starter-xxx**

* spring-boot-starter-web：spring-boot场景启动器；帮我们导入了web模块正常运行所依赖的组件；

* Spring Boot将所有的功能场景都抽取出来，做成一个个的starter（启动器），只需要在项目里面引入这些starter相关场景的所有依赖都会导入进来。要用什么功能就导入什么场景的启动器就可以了。

### 程序启动类

```java
@SpringBootApplication
public class HelloWorldMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloWorldMainApplication.class,args);
    }
}
```

**@SpringBootApplication**:

​		标明该类为程序主类(程序入口)，通过执行该类启动项目

### @SpringBootApplication注解源码追踪

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited

// 以下三个就是主要的配置注解
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
    excludeFilters = {@Filter(
    type = FilterType.CUSTOM,
    classes = {TypeExcludeFilter.class}
), @Filter(
    type = FilterType.CUSTOM,
    classes = {AutoConfigurationExcludeFilter.class}
)}
)
public @interface SpringBootApplication {
```

##### `@SpringBootConfiguration`：Spring Boot的配置类；标注在某个类上，表示这是一个Spring Boot的配置类；

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface SpringBootConfiguration {
    @AliasFor(
        annotation = Configuration.class
    )
    boolean proxyBeanMethods() default true;
}
```

- `@Configuration`：标明这是一个配置类

  ```java
  @Target({ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Component  // 可以看到@Configuration内部使用的是@Component注解
  public @interface Configuration {
      @AliasFor(
          annotation = Component.class
      )
      String value() default "";
  
      boolean proxyBeanMethods() default true;
  }
  ```

##### `@EnableAutoConfiguration`：开启自动配置

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited

// 包含以下两个重要注解
@AutoConfigurationPackage
@Import({AutoConfigurationImportSelector.class})
public @interface EnableAutoConfiguration {
    String ENABLED_OVERRIDE_PROPERTY = "spring.boot.enableautoconfiguration";

    Class<?>[] exclude() default {};

    String[] excludeName() default {};
}
```

- `@AutoConfigurationPackage`：自动配置包

  ```java
  @Target({ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Inherited
  @Import({Registrar.class})   
  public @interface AutoConfigurationPackage {
  }
  ```

  * `@Import`：导入一个配置类
  * `Registrar.class`：将主配置类所在包及下面所有子包里面的所有组件扫描到Spring容器
  * 追踪`Registrar.class`内容如下

  ```java
  static class Registrar implements ImportBeanDefinitionRegistrar, DeterminableImports {
      Registrar() {
      }
  
      public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
          AutoConfigurationPackages.register(registry, (new AutoConfigurationPackages.PackageImport(metadata)).
                                             ());
      }
  
      public Set<Object> determineImports(AnnotationMetadata metadata) {
          return Collections.singleton(new AutoConfigurationPackages.PackageImport(metadata));
      }
  }
  ```

  * 重新启动程序，debug追踪查看该类干了什么

  ![image-20200529092208654](D:\Workspace\Java\Springboot-Learning\笔记\images\springboot启动原理追踪.png)

  * 我们编写的controller包是在主程序所在的包下，所以会被扫描到，如果放到主类所在包的外面，就会访问不到，页面报错404

  ![image-20200529092208654](D:\Workspace\Java\Springboot-Learning\笔记\images\01_包结构.png)

- `@Import({AutoConfigurationImportSelector.class})`

  `AutoConfigurationImportSelector.class`将所有需要导入的组件以全类名的方式返回；这些组件就会被添加到容器中；会给容器中导入非常多的自动配置类（xxxAutoConfiguration）；就是给容器中导入这个场景需要的所有组件，并配置好这些组件；

  有了自动配置类，免去了我们手动编写配置注入功能组件等的工作；

  ![Configuration](https://cdn.static.note.zzrfdsn.cn/images/springboot/assets/1573638685562.png)

Spring Boot在启动的时候从类路径下的META-INF/spring.factories中获取EnableAutoConfiguration指定的值，将这些值作为自动配置类导入到容器中，自动配置类就生效，帮我们进行自动配置工作；以前我们需要自己配置的东西，自动配置类都帮我们完成了；

## 二、使用Spring Initializer快速创建Spring Boot项目

![快速创建boot1.png](.\images\快速创建boot1.png)

![快速创建boot1.png](.\images\快速创建boot2.png)

![快速创建boot1.png](.\images\快速创建boot3.png)

![快速创建boot1.png](.\images\快速创建boot4.png)

## 三、配置文件

> 默认配置文件是：application.properties

### YML

#### 介绍

* YAML（YAML Ain't Markup Language）

* YAML A Markup Language：是一个标记语言

* YAML isn't Markup Language：不是一个标记语言；

#### YAML语法

* 以空格的缩进来控制层级关系；只要是左对齐的一列数据，都是同一个层级的
* 次等级的前面是空格，不能使用制表符(tab)
* 冒号之后如果有值，那么==冒号和值之间至少有一个空格==，不能紧贴着

#### 普通字符串键值对

* 字符串默认不用加上单引号或者双引号；
* ""：双引号；不会转义字符串里面的特殊字符；特殊字符会作为本身想表示的意思
  * name: "zhangsan \n lisi"：输出；zhangsan 换行 lisi
* ''：单引号；会转义特殊字符，特殊字符最终只是一个普通的字符串数据
  * name: ‘zhangsan \n lisi’：输出；zhangsan \n lisi

#### 对象、Map（属性和值）

* 在下一行来写对象的属性和值的关系,需要缩进

  ```xml
  // 写法一：
  person:
    name: tony
    gender: 男
    age: 25
  
  // 写法二：
  person:{name: tony,gender: 男,age: 25}
  ```

* 数组

  ```
  movies:
   - 《钢铁侠》
   - 《无敌浩克》
   - 《雷神》
  ```

  