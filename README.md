# Mini Spring Framework

一个用于学习目的的简化版Spring框架实现，包含了Spring的核心功能：IoC容器、依赖注入、AOP切面编程、注解驱动配置等。

## 功能特性

### 核心功能
- ✅ **IoC容器** - 控制反转容器，管理Bean的创建和生命周期
- ✅ **依赖注入** - 通过注解自动注入依赖关系
- ✅ **AOP切面编程** - 支持面向切面编程，包含前置、后置、环绕、异常通知
- ✅ **Spring MVC** - Web MVC框架，支持RESTful和传统Web应用
- ✅ **Bean工厂** - 提供Bean的创建、获取和管理功能
- ✅ **注解驱动** - 支持丰富的注解配置
- ✅ **组件扫描** - 自动扫描和注册带@Component注解的类
- ✅ **应用上下文** - 提供高级的容器管理和配置加载功能
- ✅ **代理机制** - 支持CGLIB动态代理

### 支持的注解

#### IoC注解
- `@Component` - 标识组件类，自动注册为Bean
- `@Autowired` - 自动装配依赖，支持字段和方法注入
- `@Value` - 注入配置值，支持占位符和默认值
- `@ComponentScan` - 指定组件扫描的包路径
- `@PostConstruct` - Bean初始化后回调
- `@PreDestroy` - Bean销毁前回调

#### AOP注解
- `@Aspect` - 标识切面类
- `@Pointcut` - 定义切点表达式
- `@Before` - 前置通知
- `@After` - 后置通知
- `@AfterReturning` - 返回后通知
- `@AfterThrowing` - 异常通知
- `@Around` - 环绕通知

#### Web MVC注解
- `@Controller` - 标识控制器类
- `@RequestMapping` - 映射HTTP请求到处理方法
- `@RequestParam` - 绑定请求参数到方法参数
- `@PathVariable` - 绑定URI模板变量到方法参数
- `@ResponseBody` - 将返回值直接写入HTTP响应体

## 项目结构

```
mini-spring/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/minispring/
│   │   │       ├── aop/                     # AOP切面编程
│   │   │       │   ├── annotation/          # AOP注解
│   │   │       │   │   ├── Aspect.java
│   │   │       │   │   ├── Pointcut.java
│   │   │       │   │   ├── Before.java
│   │   │       │   │   ├── After.java
│   │   │       │   │   ├── AfterReturning.java
│   │   │       │   │   ├── AfterThrowing.java
│   │   │       │   │   └── Around.java
│   │   │       │   ├── framework/           # AOP框架核心
│   │   │       │   │   ├── AopBeanPostProcessor.java
│   │   │       │   │   ├── AspectInfo.java
│   │   │       │   │   ├── AspectParser.java
│   │   │       │   │   └── ProxyFactory.java
│   │   │       │   ├── pointcut/            # 切点表达式
│   │   │       │   │   └── PointcutExpression.java
│   │   │       │   ├── proxy/               # 代理实现
│   │   │       │   │   └── CglibAopProxy.java
│   │   │       │   ├── JoinPoint.java       # 连接点
│   │   │       │   └── ProceedingJoinPoint.java
│   │   │       ├── ioc/                     # IoC容器
│   │   │       │   ├── annotation/          # IoC注解
│   │   │       │   │   ├── Component.java
│   │   │       │   │   ├── Autowired.java
│   │   │       │   │   ├── Value.java
│   │   │       │   │   ├── ComponentScan.java
│   │   │       │   │   ├── PostConstruct.java
│   │   │       │   │   └── PreDestroy.java
│   │   │       │   ├── beans/               # Bean相关类
│   │   │       │   │   ├── BeanDefinition.java
│   │   │       │   │   ├── BeanFactory.java
│   │   │       │   │   ├── DefaultBeanFactory.java
│   │   │       │   │   ├── BeanPostProcessor.java
│   │   │       │   │   ├── InitializingBean.java
│   │   │       │   │   ├── DisposableBean.java
│   │   │       │   │   └── ...
│   │   │       │   ├── context/             # 应用上下文
│   │   │       │   │   ├── ApplicationContext.java
│   │   │       │   │   ├── AnnotationConfigApplicationContext.java
│   │   │       │   │   └── ComponentScanner.java
│   │   │       │   └── core/                # 核心工具
│   │   │       │       ├── ReflectionUtils.java
│   │   │       │       └── TypeConverter.java
│   │   │       ├── webmvc/                  # Spring MVC框架
│   │   │       │   ├── annotation/          # MVC注解
│   │   │       │   │   ├── Controller.java
│   │   │       │   │   ├── RequestMapping.java
│   │   │       │   │   ├── RequestParam.java
│   │   │       │   │   ├── PathVariable.java
│   │   │       │   │   ├── ResponseBody.java
│   │   │       │   │   └── RequestMethod.java
│   │   │       │   ├── servlet/             # Servlet支持
│   │   │       │   │   └── DispatcherServlet.java
│   │   │       │   ├── handler/             # 处理器相关
│   │   │       │   │   ├── HandlerMapping.java
│   │   │       │   │   ├── HandlerAdapter.java
│   │   │       │   │   ├── HandlerExecutionChain.java
│   │   │       │   │   ├── HandlerInterceptor.java
│   │   │       │   │   ├── HandlerMethod.java
│   │   │       │   │   ├── RequestMappingInfo.java
│   │   │       │   │   ├── RequestMappingHandlerMapping.java
│   │   │       │   │   └── RequestMappingHandlerAdapter.java
│   │   │       │   ├── view/                # 视图解析
│   │   │       │   │   ├── View.java
│   │   │       │   │   ├── ViewResolver.java
│   │   │       │   │   ├── InternalResourceView.java
│   │   │       │   │   └── InternalResourceViewResolver.java
│   │   │       │   ├── context/             # Web上下文
│   │   │       │   │   └── WebMvcConfigurationSupport.java
│   │   │       │   ├── example/             # MVC示例
│   │   │       │   │   ├── UserController.java
│   │   │       │   │   ├── WebMvcConfig.java
│   │   │       │   │   ├── WebApplicationInitializer.java
│   │   │       │   │   └── SpringMvcDemo.java
│   │   │       │   └── ModelAndView.java
│   │   │       ├── integration/             # IoC与AOP集成
│   │   │       │   └── AopIocIntegration.java
│   │   │       └── example/                 # 示例代码
│   │   │           ├── aop/                 # AOP示例
│   │   │           ├── ioc/                 # IoC示例
│   │   │           ├── integration/         # 集成示例
│   │   │           ├── AopDemo.java
│   │   │           ├── CglibProxyDemo.java
│   │   │           ├── EnhancedMiniSpringDemo.java
│   │   │           └── MiniSpringDemo.java
│   │   └── resources/
│   │       └── application.properties       # 配置文件
│   └── test/
│       └── java/
│           └── com/minispring/
│               ├── AopTest.java             # AOP测试
│               ├── EnhancedMiniSpringTest.java
│               ├── MiniSpringTest.java      # IoC测试
│               └── example/                 # 测试示例
└── pom.xml
```

## 核心原理

### IoC容器
- 使用`DefaultBeanFactory`作为基础容器实现
- 支持单例和原型两种Bean作用域
- 提供Bean的注册、创建、获取功能
- 支持Bean生命周期回调

### 依赖注入
- 通过反射扫描@Autowired注解
- 支持字段注入和setter方法注入
- 自动解析Bean之间的依赖关系
- 支持循环依赖检测

### AOP切面编程
- 基于CGLIB实现动态代理
- 支持方法级别的切点表达式
- 提供完整的通知类型（前置、后置、环绕、异常）
- 通过BeanPostProcessor集成到IoC容器

### 组件扫描
- 使用Reflections库扫描包路径
- 自动发现@Component和@Aspect注解的类
- 将组件类注册为BeanDefinition

### 配置管理
- 支持properties文件配置
- @Value注解支持占位符表达式
- 自动类型转换（String转基本类型）

### Spring MVC框架
- **DispatcherServlet** - 前端控制器，统一处理HTTP请求
- **HandlerMapping** - 将请求URL映射到处理器方法
- **HandlerAdapter** - 适配不同类型的处理器
- **ViewResolver** - 解析视图名称为具体视图
- **ModelAndView** - 封装模型数据和视图信息
- **拦截器链** - 支持请求预处理和后处理

## 与Spring的对比

| 功能 | Mini Spring | Spring Framework |
|------|-------------|------------------|
| IoC容器 | ✅ 完整实现 | ✅ 完整实现 |
| 依赖注入 | ✅ 注解驱动 | ✅ 注解+XML |
| 组件扫描 | ✅ 基础扫描 | ✅ 高级过滤 |
| 配置管理 | ✅ Properties | ✅ 多种格式 |
| AOP | ✅ 基础实现 | ✅ 完整支持 |
| 生命周期 | ✅ 基础支持 | ✅ 完整支持 |
| 代理机制 | ✅ CGLIB | ✅ JDK+CGLIB |
| Spring MVC | ✅ 核心功能 | ✅ 完整功能 |
| 请求映射 | ✅ @RequestMapping | ✅ 多种映射方式 |
| 参数绑定 | ✅ 基础绑定 | ✅ 高级绑定 |
| 视图解析 | ✅ JSP支持 | ✅ 多种视图技术 |
| JSON支持 | ✅ 简单实现 | ✅ 完整支持 |
| 拦截器 | ✅ 基础支持 | ✅ 完整支持 |
| 事务管理 | ❌ 未实现 | ✅ 声明式事务 |
| 数据绑定 | ❌ 未实现 | ✅ 高级绑定 |
| 国际化 | ❌ 未实现 | ✅ 完整支持 |

## 学习价值

通过实现这个Mini Spring框架，你可以深入理解：

1. **IoC原理** - 控制反转如何解耦对象创建和依赖关系
2. **依赖注入机制** - 如何通过反射实现自动装配
3. **AOP原理** - 面向切面编程的实现机制和代理模式
4. **Spring MVC架构** - Web MVC框架的设计模式和工作流程
5. **前端控制器模式** - DispatcherServlet如何统一处理请求
6. **策略模式应用** - HandlerMapping、HandlerAdapter、ViewResolver的设计
7. **责任链模式** - 拦截器链的实现和应用
8. **适配器模式** - 如何适配不同类型的处理器
9. **注解处理** - 如何解析和处理自定义注解
10. **Bean生命周期** - Bean从创建到销毁的完整过程
11. **容器设计模式** - 如何设计一个可扩展的IoC容器
12. **代理模式** - 动态代理在框架中的应用

## 快速开始

### 运行IoC和AOP演示
```java
// 运行IoC演示
java com.minispring.example.MiniSpringDemo

// 运行AOP演示
java com.minispring.example.AopDemo

// 运行完整功能演示
java com.minispring.example.EnhancedMiniSpringDemo
```

### 运行Spring MVC演示
```java
// 推荐：运行简化版MVC演示（学习用）
java com.minispring.webmvc.example.SimpleMvcDemo

// 运行完整版MVC演示（了解完整功能）
java com.minispring.webmvc.example.SpringMvcDemo
```

### 🎯 学习路径建议
1. **SimpleMvcDemo** - 理解核心设计理念，专注学习
2. **SpringMvcDemo** - 了解完整功能，参考实现

这将展示：
- ✅ ApplicationContext创建和Bean注册
- ✅ HandlerMapping请求映射功能
- ✅ HandlerAdapter方法适配功能
- ✅ ViewResolver视图解析功能
- ✅ 完整的MVC请求处理流程

### 示例Controller
```java
@Controller
@RequestMapping("/users")
public class UserController {
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getAllUsers() {
        ModelAndView mv = new ModelAndView("user/list");
        mv.addObject("users", getUserList());
        return mv;
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public User getUserById(@PathVariable("id") Long id) {
        return findUserById(id);
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView createUser(@RequestParam("name") String name, 
                                  @RequestParam("email") String email) {
        User newUser = createUser(name, email);
        return new ModelAndView("redirect:/users/" + newUser.getId());
    }
}
```

## 技术依赖

- **JDK 8+** - 基础运行环境
- **CGLIB 3.3.0** - AOP动态代理
- **Reflections 0.10.2** - 包扫描
- **Servlet API 4.0.1** - Web支持
- **Jackson 2.13.0** - JSON序列化
- **JUnit 5** - 单元测试

## 📋 设计改进

### 渐进式学习设计
项目提供了**简化版**和**完整版**两套实现：

#### 简化版（推荐学习使用）
- `SimpleDispatcherServlet` - 去掉复杂配置，突出核心流程
- `SimpleRequestMappingInfo` - 只保留路径和HTTP方法匹配
- `SimpleMvcDemo` - 清晰的演示，无复杂Mock实现
- `SimpleController` - 基本功能展示

#### 完整版（参考完整功能）
- `DispatcherServlet` - 包含生产级特性
- `RequestMappingInfo` - 完整的条件匹配
- `SpringMvcDemo` - 全功能演示
- `UserController` - 复杂业务场景

### 设计原则
- ✅ **约定优于配置** - 合理的默认行为
- ✅ **渐进式复杂度** - 从简单到复杂的学习路径
- ✅ **Spring设计理念** - 遵循Spring源码的优秀设计
- ✅ **实用主义** - 专注核心价值，避免过度设计

### 代码质量保证
- ✅ **最终代码审视** - 确保优雅、不过度设计
- ✅ **Spring设计契合** - 完全遵循Spring源码理念
- ✅ **渐进式学习** - 从简单到复杂的完整路径

### 技术文档
- 📖 **[IOC代码链路详解](docs/IOC_CODE_FLOW.md)** - 详细分析IOC容器执行流程
- 🎯 **[AOP代码链路详解](docs/AOP_CODE_FLOW.md)** - 详细分析AOP框架执行流程
- 📋 **[最终代码审视报告](FINAL_REVIEW.md)** - 代码质量和设计理念评估
