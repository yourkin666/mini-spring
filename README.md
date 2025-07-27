# Mini Spring Framework

一个用于学习目的的简化版Spring框架实现，包含了Spring的核心功能：IoC容器、依赖注入、注解驱动配置等。

## 功能特性

### 核心功能
- ✅ **IoC容器** - 控制反转容器，管理Bean的创建和生命周期
- ✅ **依赖注入** - 通过注解自动注入依赖关系
- ✅ **Bean工厂** - 提供Bean的创建、获取和管理功能
- ✅ **注解驱动** - 支持@Component、@Autowired、@Value等核心注解
- ✅ **组件扫描** - 自动扫描和注册带@Component注解的类
- ✅ **应用上下文** - 提供高级的容器管理和配置加载功能

### 支持的注解
- `@Component` - 标识组件类，自动注册为Bean
- `@Autowired` - 自动装配依赖，支持字段和方法注入
- `@Value` - 注入配置值，支持占位符和默认值
- `@ComponentScan` - 指定组件扫描的包路径

## 项目结构

```
mini-spring/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/minispring/
│   │   │       ├── annotation/          # 注解定义
│   │   │       │   ├── Component.java
│   │   │       │   ├── Autowired.java
│   │   │       │   ├── Value.java
│   │   │       │   └── ComponentScan.java
│   │   │       ├── beans/               # Bean相关类
│   │   │       │   ├── BeanDefinition.java
│   │   │       │   ├── BeanFactory.java
│   │   │       │   ├── DefaultBeanFactory.java
│   │   │       │   ├── AnnotationBeanFactory.java
│   │   │       │   └── BeansException.java
│   │   │       ├── context/             # 应用上下文
│   │   │       │   ├── ApplicationContext.java
│   │   │       │   ├── AnnotationConfigApplicationContext.java
│   │   │       │   └── ComponentScanner.java
│   │   │       └── core/                # 核心工具
│   │   │           └── ReflectionUtils.java
│   │   └── resources/
│   │       └── application.properties   # 配置文件
│   └── test/
│       └── java/
│           └── com/minispring/
│               ├── MiniSpringTest.java  # 测试用例
│               └── example/             # 示例代码
│                   ├── UserService.java
│                   ├── UserRepository.java
│                   ├── AppConfig.java
│                   └── MiniSpringDemo.java
└── pom.xml
```

## 快速开始

### 1. 定义服务类

```java
@Component
public class UserRepository {
    @Value("${db.url:jdbc:h2:mem:test}")
    private String databaseUrl;
    
    public String save(String username) {
        return "User '" + username + "' saved to " + databaseUrl;
    }
}

@Component
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Value("${app.name:Mini Spring}")
    private String appName;
    
    public String createUser(String username) {
        return userRepository.save(username);
    }
}
```

### 2. 创建配置类

```java
@ComponentScan(basePackages = "com.example")
public class AppConfig {
}
```

### 3. 使用应用上下文

```java
public class Application {
    public static void main(String[] args) {
        // 创建应用上下文
        AnnotationConfigApplicationContext context = 
            new AnnotationConfigApplicationContext(AppConfig.class);
        
        // 获取Bean
        UserService userService = context.getBean(UserService.class);
        
        // 使用服务
        String result = userService.createUser("张三");
        System.out.println(result);
        
        // 关闭上下文
        context.close();
    }
}
```

## 运行示例

### 运行Demo
```bash
mvn compile exec:java -Dexec.mainClass="com.minispring.example.MiniSpringDemo"
```

### 运行测试
```bash
mvn test
```

## 核心原理

### IoC容器
- 使用`DefaultBeanFactory`作为基础容器实现
- 支持单例和原型两种Bean作用域
- 提供Bean的注册、创建、获取功能

### 依赖注入
- 通过反射扫描@Autowired注解
- 支持字段注入和setter方法注入
- 自动解析Bean之间的依赖关系

### 组件扫描
- 使用Reflections库扫描包路径
- 自动发现@Component注解的类
- 将组件类注册为BeanDefinition

### 配置管理
- 支持properties文件配置
- @Value注解支持占位符表达式
- 自动类型转换（String转基本类型）

## 与Spring的对比

| 功能 | Mini Spring | Spring Framework |
|------|-------------|------------------|
| IoC容器 | ✅ 基础实现 | ✅ 完整实现 |
| 依赖注入 | ✅ 注解驱动 | ✅ 注解+XML |
| 组件扫描 | ✅ 基础扫描 | ✅ 高级过滤 |
| 配置管理 | ✅ Properties | ✅ 多种格式 |
| AOP | ❌ 未实现 | ✅ 完整支持 |
| 事务管理 | ❌ 未实现 | ✅ 声明式事务 |

## 学习价值

通过实现这个Mini Spring框架，你可以深入理解：

1. **IoC原理** - 控制反转如何解耦对象创建和依赖关系
2. **依赖注入机制** - 如何通过反射实现自动装配
3. **注解处理** - 如何解析和处理自定义注解
4. **Bean生命周期** - Bean从创建到销毁的完整过程
5. **容器设计模式** - 如何设计一个可扩展的IoC容器

## 扩展建议

可以继续添加以下功能来增强框架：

1. **AOP支持** - 实现切面编程
2. **事件机制** - 应用事件的发布和监听
3. **条件注入** - @Conditional注解支持
4. **配置类** - @Configuration和@Bean注解
5. **Profile支持** - 环境配置管理
6. **循环依赖处理** - 三级缓存解决方案

## 注意事项

这是一个学习用的简化实现，不建议在生产环境中使用。真实项目请使用官方的Spring Framework。