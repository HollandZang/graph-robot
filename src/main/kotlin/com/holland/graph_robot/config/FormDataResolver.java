package com.holland.graph_robot.config;

import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import org.springframework.web.reactive.result.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;

@Configuration
public class FormDataResolver implements WebFluxConfigurer {

    @Resource
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void configureArgumentResolvers(@NotNull ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new RequestFormDataMethodArgumentResolver(applicationContext.getBeanFactory(), ReactiveAdapterRegistry.getSharedInstance(), true));
    }

    public static class RequestFormDataMethodArgumentResolver extends RequestParamMethodArgumentResolver {
        /**
         * Class constructor with a default resolution mode flag.
         *
         * @param factory              a bean factory used for resolving  ${...} placeholder
         *                             and #{...} SpEL expressions in default values, or {@code null} if default
         *                             values are not expected to contain expressions
         * @param registry             for checking reactive type wrappers
         * @param useDefaultResolution in default resolution mode a method argument
         *                             that is a simple type, as defined in {@link BeanUtils#isSimpleProperty},
         *                             is treated as a request parameter even if it isn't annotated, the
         *                             request parameter name is derived from the method parameter name.
         */
        public RequestFormDataMethodArgumentResolver(ConfigurableBeanFactory factory, ReactiveAdapterRegistry registry, boolean useDefaultResolution) {
            super(factory, registry, useDefaultResolution);
        }

        @Override
        protected Object resolveNamedValue(@NotNull String name, @NotNull MethodParameter parameter, @NotNull ServerWebExchange exchange) {
            Object queryValue = super.resolveNamedValue(name, parameter, exchange);
            if (null != queryValue) return queryValue;

            MultiValueMap<String, String> formData = exchange.getFormData().block();
            return formData == null ? null : formData.getFirst(name);
        }
    }
}
