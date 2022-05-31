package com.keke.cloud.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @auther flk
 * @create 2020/9/21
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.keke.cloud"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * 创建该API的基本信息（这些基本信息会展现在文档页面中）
     *  访问地址：http://localhost:8801/swagger-ui.html
     * @return
     */
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                //当前文档的标题
                .title("CLOUD 前后端API文档")
                //当前文档的详细描述
                .description("请仔细观看request参数和response返回值")
                //版本
                .version("1.0")
                .build();

    }
}
