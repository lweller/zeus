package ch.wellernet.zeus.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2()
public class SwaggerConfiguration {

  @Bean
  public Docket publicApiDocumentation() {
    return new Docket(DocumentationType.SWAGGER_2).select().build()
        .apiInfo(new ApiInfoBuilder().title("Zeus API v1").build()).forCodeGeneration(true);
  }
}