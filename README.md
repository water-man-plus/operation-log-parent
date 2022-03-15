# operation-log-parent
操作日志生成组件（操作日志又称系统变更日志、审计日志等）

# 背景
不管是B端还是C端系统，在用户使用过程中，都会涉及到对相关资源进行更新或者删除的操作，如电商系统中商家修改商品售价，OA系统中管理员修改用户的权限等，数据库中一般记录的都是资源的最后修改时间和修改人。第一是可读性比较差，只能是程序员能够查询使用，第二是缺少修改前的值，无法对数据进行追溯。
# 问题
1. 如何生成可读性高的操作日志
2. 操作日志内容包含修改前后的值，方便后期的数据追溯
# 如何生成可读性高的操作日志
一个可读性高的操作日志，应包含下面几部分
- 操作人：张三
- 操作时间：2022-03-23 19:00:00
- 业务模块：商品
- 业务标识号：100878（这里是操作的资源对象id，当前案例是商品id）
- 操作内容：修改了商品价格，xxxx
# 操作日志内容包含修改前后的值
- 操作内容：修改了商品价格，从12￥调整到0.1￥
- 操作内容：修改了商品价格，从14￥调整到0.01￥
# 理想的操作日志列表展示
| 操作人 | 操作时间 | 业务模块 | 业务标识号 | 操作内容 |
| --- | --- | --- | --- | --- |
| 张三 | 2022-03-23 19:00:00 | 商品 | 100878 | 修改了商品价格，从12￥调整到0.1￥ |
| 李四 | 2022-03-24 19:00:00 | 商品 | 200878 | 修改了商品价格，从14￥调整到0.01￥ |
# 如何优雅的生成操作日志
## 方案一：手动在业务代码中记录（不够优雅）
所有的埋点在业务代码里面手动埋入，在变更事件触发之前，查询一次资源对应的状态并记录在内存中，变更之后再记录变更后资源的状态，最后将前后状态、操作人、变更时间一起写到数据库埋点的代码中。
```
public void updateApp(SmtApp newApp){
   
     //操作日志实体对象
    OperateLog operateLog = new OperateLog();
    operateLog.setUserId("当前用户ID");
    //业务对象标识-这里是应用id
    operateLog.setBizNo(newApp.id);
    //操作发生时间
    operateLog.setCreatedTime(new Date);
    //操作类别，这里是应用变更
    operateLog.setCategory("应用变更");
    //操作日志详情,记录操作的业务对象的变更信息
    operateLog.setDetail("更新了应用信息，应用名称：oldName --> newName");
    //保存操作日志
    operateLogService.save(operateLog);
    
    /******忽略后续的应用更新代码************/
}
```
优点：
- 方案简单，没有难度，堆人堆时间就能完成，简单明了

缺点：
- 业务侵入性大，完全耦合正常的业务
- 扩展性非常差，想增加其他维度的埋点时，需要修改所有埋点的业务代码

## 方案二：使用 Canal 监听数据库记录操作日志（不够优雅）
canal 是一款基于 MySQL 数据库增量日志解析，提供增量数据订阅和消费的开源组件，通过采用监听数据库 Binlog 的方式，这样可以从底层知道是哪些数据做了修改，然后根据更改的数据记录操作日志。

优点：
-  和业务逻辑完全分离

缺点：
- 难以记录操作前的内容
- 只能针对数据库的更改做操作记录，如涉及到和外部交互的部分，无法记录，如发送邮件、短信、RPC调用
- 记录的操作结果内容只适合开发人员看，无法给到产品和运营人员使用
## 方案三：基于AOP方法注解实现操作日志
为了解决上面几个方案所带来的问题，一般采用 AOP 的方式记录日志，让操作日志和业务逻辑解耦，接下来看一个简单的 AOP 日志的例子。伪代码如下：
```
@LogRecordAnnotation(detail = "更新了用户名称，从{#oldName}改为{#newName}", bizNo = "#userId", category = "用户更改")
public void updateNameById(Long userId, Sting oldName,String newName) {
    // do update action
}
```
# 使用AOP方案优雅的记录操作日志
结合上一节如何生成可读性高的操作日志，那么AOP注解接口需要包含以下几个关键属性：

```
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogRecordAnnotation {

    String operator() default "";

    String bizNo();

    String category();

    String detail() default "";

    String condition() default "true";
}
```

| 属性 | 是否必须 | 说明 |
| --- | --- | --- |
| operator | 否 | 操作人 |
| bizNo | 是 | 操作的业务模块 |
| category | 是 | 操作的业务资源标识 |
| detail | 是 | 操作日志详情 |
| condition | 否 | 操作日志记录条件，表达式返回boolean类型 |

> conditon属性我们稍后再展开说明

**operator**属性设置成非必填主要是为了减少冗余的赋值操作，正常的项目中，都会保存当前的用户信息到一个请求上下文中，如UserContext，操作日志组件提供统一的OperatorGetService接口，由使用方实现，返回当前用户信息
```
//组件提供接口
public interface OperatorGetService {

    Operator getUser();

    @Data
    class Operator {

        private String id;

        private String name;
    }
}
//使用方实现
public static class MarketOperatorGetServiceImpl implements OperatorGetService {
    @Override
    public Operator getUser() {
        Long userId = UserContext.getUserId();
        String userName = UserContext.getRealName();
        return Operator.builder()
                .id(String.valueOf(userId))
                .name(userName)
                .build();
    }
}
```

下面的讲解统一使用更新用户信息为例子展开
```
    @Data
    public class User {
        /**
         * 用户id
         */
        private Integer id;
        /**
         * 用户所属部门id
         */
        private Long departmentId;

        /**
        * 用户名称
        */
        private String name;
        
        /**
        * 用户年龄
        */
        private Integer age;
        
        /**
        *  用户状态
        *  0-禁用，1-启用
        */
        private Integer status;
        
        private Date createdTime;
        
        private Date updatedTime;
    }
```
## 更新用户单个属性（名称）
常规的更新用户名称的业务方法如下
```
void updateNameById(Integer id, String newName) {
    //doUpdate
}
```
在方法上面加上我们定义的注解
```
 @LogRecordAnnotation(detail = "更新了用户名称，改为{#newName}", bizNo = "#id", category = "用户更改")
void updateNameById(Integer id, String newName) {
    //doUpdate
}
```
很明显，操作日志详情缺少旧的用户名，无法满足使用需要，解决方法有两种：

**第一种是让开发在方法参数中加上旧的用户名称**：
```
 @LogRecordAnnotation(detail = "更新了用户名称，从{#oldName}改为{#newName}", bizNo = "#id", category = "用户更改")
void updateNameById(Integer id,String oldName, String newName) {
    //doUpdate
}
```
这种方式在原有方法上面强行增加了一个也业务无关的参数，既不符合相关设计原则，也无法说服带有强迫症的开发，毕竟这种方式违反开发常识了。

**第二种是使用自定义模板表达式，来获取旧的用户名称：**
```
 @LogRecordAnnotation(detail = "更新了用户名称，从{getUserNameById(#id)}改为{#newName}", bizNo = "#id", category = "用户更改")
void updateNameById(Integer id,String newName) {
    //doUpdate
}

String getUserNameById(Integer id){
    User user =; //get user by select db 
    return user.getName();
}
```
- 模板表达式```{getUserNameById(#id)}```用来获取旧的用户名称，代表调用```getUserNameById(#id)``` 方法来获取用户名称，其中的参数使用`spel`表达式引用了原方法中的参数`id`（用户id）
- 模板表达式`{#newName}` 用来获取新用户名称，`#newName`使用spel表达式引用了原方法中的参数`newName` 
-  外层使用大括号`{}`括起来是为了方便和`spel`表达式作区分
> 自定义模板相关的详细说明将放在代码实现章节讲解

## 更新用户多个属性
上一节我们讲解了在更新单个属性的时候，如何去记录操作日志，如果是多个属性的情况，我们又改如何去记录呢？如下面的业务方法：
```
/**
* 通过用户id更新用户信息
* @param userId  用户id
* @param newUser 新的用户信息
*/
void updateById(Integer userId, User newUser) {
    //doUpdate
}
```
用户信息的更新场景包含下面几种情形：
- 只是更新了单个属性
- 同时更新了多个属性
- 不需要记录`updatedTime`属性的变更，因为操作日志本身包含了操作时间

那么我们如何在不修改原有业务代码的情况下实现上面3中情形呢？

**第一需要使用到对象的``diff``操作**
>对象`diff`操作说明：对比两个同类型对象的属性值差异，并得出差异结果，如更改了用户的多个属性，那么期望得到的操作内容：name: yyq-old --> yyq-plus，age: 28 --> 29

我们将自定义模板中的diff声音定义如下：
```
diff({oldObject},{newObject})
```
使用约定大于配置的理论，将需要`diff`的两个对象使用`diff()`表达式包起来，其中的`{oldObject}`和`{newObject}`两个子表达式用法和上一节中的用法一样，可以调用方法，也可以直接引用方法参数

**第二需要定义一个注解来标识目标对象有哪些属性是需要进行diff操作：**
```
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface OpLogField {

    String fieldName() default "";
   
    String fieldMapping() default "{}";

    String dateFormat() default "yyyy-MM-dd HH:mm:ss";

    String decimalFormat() default "";
    
    //先讲关键属性，暂时跳过其他属性
```

| 属性 | 是否必须 | 说明 |
| --- | --- | --- |
| fieldName| 否 | diff结果中显示属性别名，起到易读作用，如name显示为名称 |
| fieldMapping| 否 |  diff结果中显示属性值别名，起到易读作用，json格式，如status可设置为{"0": "禁用", "1": "启用"} |
| dateFormat| 否 | diff结果中的时间类型属性值格式化样式，起到易读作用 |
| decimalFormat| 否 | diff结果中的数值类型格式化样式，默认不格式化，如设置为#,###.##则Double d = 554545.4545454的数值将被显示为 554,545.45|

使用该注解对User相关属性进行标识：
```
    @Data
    private static class User {
       
        private Integer id;
        
        @OpLogField(fieldName="部门id")
        private Long departmentId;

        @OpLogField(fieldName="名称")
        private String name;
        
        @OpLogField(fieldName="年龄")
        private Integer age;
        
        @OpLogField(fieldName="状态",="{"0": "禁用", "1": "启用"}")
        private Integer status;
        
        private Date createdTime;
        
        pricate Date updatedTime;
    }
```
接着使用操作日志注解标注该方法
```
/**
* 通过用户id更新用户信息
* @param userId  用户id
* @param newUser 新的用户信息
*/
@LogRecordAnnotation(detail = "更新了用户名称，diff({getUserById(#userId)},{#newUser})", bizNo = "#id", category = "用户更改")
void updateById(Integer userId, User newUser) {
    //doUpdate
}

String getUserById(Integer id){
    User user =; //get user by select db 
    return user;
}
```
假定`oldUser`是`(id=1, departmentId=1, name=yyq-old, age=28, status=0)`
假定`newUser`是`(id=1, departmentId=1, name=yyq-plus, age=29, status=1)`
执行该方法将得到的操作日志内容：
> 更改用户信息，名称: yyq-old --> yyq-plus，年龄: 28 --> 29，状态：禁用 --> 启用

# 代码实现架构
## 仓库模块说明
- operation-log 操作日志组件核心代码
- operation-log-starter 操作日志starter模块，支持spring-boot和普通spring项目

持续补充

# 使用手册
持续补充
