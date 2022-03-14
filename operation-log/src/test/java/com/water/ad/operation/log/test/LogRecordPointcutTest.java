package com.water.ad.operation.log.test;

import com.water.ad.operation.log.annotation.LogRecordAnnotation;
import com.water.ad.operation.log.annotation.OpLogField;
import com.water.ad.operation.log.aop.LogRecordPointcut;
import com.water.ad.operation.log.core.LogRecordContext;
import com.water.ad.operation.log.core.record.LogRecordService;
import com.water.ad.operation.log.model.LogRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author yyq
 * @create 2022-02-17
 **/
@SuppressWarnings("")
public class LogRecordPointcutTest {

    private ApplicationContext applicationContext;
    private UserService userServiceProxy;

    @Before
    public void before() {
        applicationContext = new AnnotationConfigApplicationContext(LogRecordPointcutTestConfig.class);
        UserService userService = applicationContext.getBean(UserService.class);
        LogRecordPointcut logRecordPointcut = applicationContext.getBean(LogRecordPointcut.class);
        Assert.assertNotNull(userService);
        AspectJProxyFactory factory = new AspectJProxyFactory(userService);
        factory.addAspect(logRecordPointcut);
        userServiceProxy = factory.getProxy();
    }

    /**
     * pre方法调用（单参数），post引用方法参数
     */
    @Test
    public void testPreMethodSingleParamPostQuote() {
        userServiceProxy.updateNameById(1, "yyq-plus");
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNotNull(logRecordService.record());
        Assert.assertEquals("更新了用户名称，从yyq-old改为yyq-plus", logRecordService.record().getDetail());
    }


    /**
     * pre方法调用（多参数），post引用方法参数
     */
    @Test
    public void testPreMethodMultiParamPostQuote() {
        userServiceProxy.updateNameById(2L, 1, "yyq-plus");
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNotNull(logRecordService.record());
        Assert.assertEquals("更新了用户名称，从yyq-old改为yyq-plus", logRecordService.record().getDetail());
    }

    /**
     * pre引用方法参数，post方法调用（单参数)
     */
    @Test
    public void testPreQuotePostMethodSigleParam() {
        userServiceProxy.updateNameByIdWithOldName(1, "yyq-old");
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNotNull(logRecordService.record());
        Assert.assertEquals("更新了用户名称，从yyq-old改为yyq-plus", logRecordService.record().getDetail());
    }

    /**
     * pre引用方法参数，post方法调用（多参数)
     */
    @Test
    public void testPreQuotePostMethodMultiParam() {
        userServiceProxy.updateNameByIdWithOldName(2L, 1, "yyq-old");
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNotNull(logRecordService.record());
        Assert.assertEquals("更新了用户名称，从yyq-old改为yyq-plus", logRecordService.record().getDetail());
    }


    /**
     * diff比较
     * pre方法调用（单参数），post引用方法参数
     */
    @Test
    public void testDiffPreMethodSingleParamPostQuote() {
        userServiceProxy.updateUser(User.builder().id(1).name("yyq-plus").age(29).build());
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNotNull(logRecordService.record());
        Assert.assertEquals("更改用户信息，名称: yyq-old --> yyq-plus\n" +
                "年龄: 28 --> 29", logRecordService.record().getDetail());
    }

    /**
     * diff比较
     * pre方法调用（多参数），post引用方法参数
     */
    @Test
    public void testDiffPreMethodMultiParamPostQuote() {
        userServiceProxy.updateUserWithCompanyId(2L, User.builder().id(1).name("yyq-plus").age(29).build());
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNotNull(logRecordService.record());
        Assert.assertEquals("更改用户信息，名称: yyq-old --> yyq-plus\n" +
                "年龄: 28 --> 29", logRecordService.record().getDetail());
    }

    /**
     * diff比较
     * pre引用方法参数，post方法调用（单参数）
     */
    @Test
    public void testDiffPreQuotePostMethodSingleParam() {
        userServiceProxy.updateUserWithOldUser(User.builder().id(1).name("yyq-old").age(28).build());
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNotNull(logRecordService.record());
        Assert.assertEquals("更改用户信息，名称: yyq-old --> yyq-plus\n" +
                "年龄: 28 --> 29", logRecordService.record().getDetail());
    }

    /**
     * diff比较
     * pre引用方法参数，post方法调用（单参数）
     */
    @Test
    public void testDiffPreQuotePostMethodMultiParam() {
        userServiceProxy.updateUserWithOldUser(2L, User.builder().id(1).name("yyq-old").age(28).build());
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNotNull(logRecordService.record());
        Assert.assertEquals("更改用户信息，名称: yyq-old --> yyq-plus\n" +
                "年龄: 28 --> 29", logRecordService.record().getDetail());
    }

    /**
     * diff比较
     * pre引用方法参数，post引用方法参数
     */
    @Test
    public void testDiffPreQuotePostQuote() {
        userServiceProxy.updateUser(User.builder().id(1).name("yyq-old").age(28).build(), User.builder().id(1).name("yyq-plus").age(29).build());
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNotNull(logRecordService.record());
        Assert.assertEquals("更改用户信息，名称: yyq-old --> yyq-plus\n" +
                "年龄: 28 --> 29", logRecordService.record().getDetail());
    }


    /**
     * 用户直接指定操作内容
     */
    @Test
    public void testCustomContent() {
        userServiceProxy.updateUserWithCustomContent(User.builder().id(1).name("yyq-old").age(28).build(), User.builder().id(1).name("yyq-plus").age(29).build());
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNotNull(logRecordService.record());
        Assert.assertEquals("直接指定操作内容，更改用户信息，名称: yyq-old --> yyq-plus\n" +
                "年龄: 28 --> 29", logRecordService.record().getDetail());
    }

    /**
     * 用户附加操作内容
     */
    @Test
    public void testCustomAppendContent() {
        userServiceProxy.updateUserWithOldUserAppend(User.builder().id(1).name("yyq-old").age(28).build());
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNotNull(logRecordService.record());
        Assert.assertEquals("更改用户信息，名称: yyq-old --> yyq-plus\n" +
                "年龄: 28 --> 29 我是附加的内容", logRecordService.record().getDetail());
    }

    /**
     * condition false
     */
    @Test
    public void testConditionFalse() {
        userServiceProxy.updateUserWithConditionFalse(User.builder().id(1).name("yyq-old").age(28).build(), User.builder().id(1).name("yyq-plus").age(29).build());
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNull(logRecordService.record());

    }

    /**
     * condition false
     */
    @Test
    public void testConditionFalseUserRet() {
        User user = userServiceProxy.updateUserWithConditionFalseUseRet(User.builder().id(1).name("yyq-old").age(28).build(), User.builder().id(1).name("yyq-plus").age(29).build());
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNull(user);
        Assert.assertNull(logRecordService.record());
    }

    /**
     * condition false
     */
    @Test
    public void testConditionFalseTrueRet() {
        User user = userServiceProxy.updateUserWithConditionTrueUseRet(User.builder().id(1).name("yyq-old").age(28).build(), User.builder().id(1).name("yyq-plus").age(29).build());
        TestLogRecordRecordService logRecordService = applicationContext.getBean(TestLogRecordRecordService.class);
        Assert.assertNotNull(logRecordService);
        Assert.assertNotNull(user);
        Assert.assertNotNull(logRecordService.record());
        Assert.assertEquals("updateUserWithConditionTrueUseRet", logRecordService.record().getDetail());
    }


    @Slf4j
    public static class UserService {

        /**
         *
         * @param id 用户id
         * @param newName 新用户名
         */
        @LogRecordAnnotation(detail = "更新了用户名称，从{$$before.getNameById(#id)}改为{#newName}", bizNo = "#id", category = "用户更改")
        void updateNameById(Integer id, String newName) {
            log.info("######### updateNameById id {} newName {} #####", id, newName);
        }


        @LogRecordAnnotation(detail = "更新了用户名称，从{$$before.getNameById(#departmentId,#id)}改为{#newName}", bizNo = "#id", category = "用户更改")
        void updateNameById(Long departmentId, Integer id, String newName) {
            log.info("######### updateNameById departmentId {},id {},newName {}#####", departmentId, id, newName);
        }

        @LogRecordAnnotation(detail = "更新了用户名称，从{#oldName}改为{getNameByIdNew(#id)}", bizNo = "#id", category = "用户更改")
        void updateNameByIdWithOldName(Integer id, String oldName) {
            log.info("######### updateNameById id {},oldName {} #####", id, oldName);
        }

        @LogRecordAnnotation(detail = "更新了用户名称，从{#oldName}改为{getNameByIdNew(#departmentId,#id)}", bizNo = "#id", category = "用户更改")
        void updateNameByIdWithOldName(Long departmentId, Integer id, String oldName) {
            log.info("######### updateNameById departmentId {},id {},oldName {} ##### ", departmentId, id, oldName);
        }

        @LogRecordAnnotation(detail = "更改用户信息，diff({$$before.getUserById(#newUser.id)},{#newUser})", bizNo = "#newUser.id", category = "用户更改")
        void updateUser(User newUser) {
            log.info("######### update user #####");
        }

        @LogRecordAnnotation(detail = "更改用户信息，diff({#oldUser},{#newUser})", bizNo = "#oldUser.id", category = "用户更改")
        void updateUser(User oldUser, User newUser) {
            log.info("######### update user #####");
        }

        @LogRecordAnnotation(detail = "更改用户信息，diff({$$before.getUserById(#departmentId,#newUser.id)},{#newUser})", bizNo = "#newUser.id", category = "用户更改")
        void updateUserWithCompanyId(Long departmentId, User newUser) {
            log.info("######### update user #####");
        }

        @LogRecordAnnotation(detail = "更改用户信息，diff({#oldUser},{getUserByIdNew(#oldUser.id)})", bizNo = "#oldUser.id", category = "用户更改")
        void updateUserWithOldUser(User oldUser) {
            log.info("######### update user #####");
        }

        @LogRecordAnnotation(detail = "更改用户信息，diff({#oldUser},{$$before.getUserByIdNew(#departmentId,#oldUser.id)})", bizNo = "#oldUser.id", category = "用户更改")
        void updateUserWithOldUser(Long departmentId, User oldUser) {
            log.info("######### update user #####");
        }

        @LogRecordAnnotation(detail = "更改用户信息，diff({#oldUser},{getUserByIdNew(#oldUser.id)})", bizNo = "#oldUser.id", category = "用户更改")
        void updateUserWithOldUserAppend(User oldUser) {
            log.info("######### update user #####");
            LogRecordContext.putLogDetailAppend(" 我是附加的内容");
        }

        @LogRecordAnnotation(bizNo = "#oldUser.id", category = "用户更改")
        void updateUserWithCustomContent(User oldUser, User newUser) {
            LogRecordContext.putLogDetail(String.format("直接指定操作内容，更改用户信息，名称: %s --> %s\n" +
                    "年龄: %s --> %s", oldUser.getName(), newUser.getName(), oldUser.getAge(), newUser.getAge()));
            log.info("######### update user #####");
        }

        @LogRecordAnnotation(bizNo = "#oldUser.id", category = "用户更改", condition = "false")
        void updateUserWithConditionFalse(User oldUser, User newUser) {
            log.info("######### update user #####");
        }

        @LogRecordAnnotation(bizNo = "#oldUser.id", category = "用户更改", condition = "#ret!=null")
        User updateUserWithConditionFalseUseRet(User oldUser, User newUser) {
            log.info("######### update user #####");
            return null;
        }

        @LogRecordAnnotation(bizNo = "#oldUser.id", detail = "updateUserWithConditionTrueUseRet", category = "用户更改", condition = "#ret!=null")
        User updateUserWithConditionTrueUseRet(User oldUser, User newUser) {
            log.info("######### update user #####");
            return newUser;
        }

        public String getNameById(Integer id) {
            return getUserById(id).getName();
        }

        public String getNameByIdNew(Integer id) {
            return getUserByIdNew(id).getName();
        }

        public String getNameById(Long departmentId, Integer id) {
            return getUserById(departmentId, id).getName();
        }

        public String getNameByIdNew(Long departmentId, Integer id) {
            return getUserByIdNew(departmentId, id).getName();

        }

        public User getUserById(Integer id) {
            return User.builder().id(id).name("yyq-old").age(28).build();
        }

        public User getUserByIdNew(Integer id) {
            return User.builder().id(id).name("yyq-plus").age(29).build();
        }

        public User getUserById(Long departmentId, Integer id) {
            return User.builder().id(id).departmentId(departmentId).name("yyq-old").age(28).build();
        }

        public User getUserByIdNew(Long departmentId, Integer id) {
            return User.builder().id(id).departmentId(departmentId).name("yyq-plus").age(29).build();
        }

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    private static class User {
        /**
         * 用户id
         */
        private Integer id;
        /**
         * 用户所属部门id
         */
        private Long departmentId;

        @OpLogField(fieldName = "名称")
        private String name;

        @OpLogField(fieldName = "年龄")
        private Integer age;
    }

    @Slf4j
    public static class TestLogRecordRecordService implements LogRecordService {

        static ThreadLocal<LogRecord> threadLocal = new ThreadLocal<>();

        @Override
        public void record(LogRecord logRecord) {
            threadLocal.set(logRecord);
            log.info("【logRecord】log={}", logRecord);
        }

        LogRecord record() {
            return threadLocal.get();
        }
    }
}
