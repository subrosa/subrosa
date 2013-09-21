//package com.subrosagames.subrosa.spring;
//
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
//
///**
// */
////@Component
//public class ExceptionHandlerExceptionResolverFactoryBean implements FactoryBean<ExceptionHandlerExceptionResolver> {
//
//    @Autowired
//    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
//
//    public ExceptionHandlerExceptionResolver getObject()
//            throws Exception
//    {
//        ExceptionHandlerExceptionResolver r = new ExceptionHandlerExceptionResolver();
//        r.setMessageConverters(requestMappingHandlerAdapter.getMessageConverters());
//        return r;
//    }
//
//    @Override
//    public Class<?> getObjectType() {
//        return ExceptionHandlerExceptionResolver.class;
//    }
//
//    @Override
//    public boolean isSingleton() {
//        return true;
//    }
//}
