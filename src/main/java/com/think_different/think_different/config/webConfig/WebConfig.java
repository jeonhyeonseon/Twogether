package com.think_different.think_different.config.webConfig;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CoupleRequiredInterceptor coupleRequiredInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(
                        "file:" + System.getProperty("user.dir") + "/uploads/"
                );
    }

    // 커플 연결이 필요한 화면 (/couple 대시보드는 컨트롤러에서 직접 확인, 초대/연결 화면은 제외)
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(coupleRequiredInterceptor)
                .addPathPatterns(
                        "/calendar/**",
                        "/expense/**",
                        "/record/**",
                        "/bucketlist/**",
                        "/couple/edit"
                );
    }

}
