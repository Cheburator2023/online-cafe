package ru.otus.cafe.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Configuration
@Profile("!test")
public class SwaggerUIConfig {

    @Bean
    public RouterFunction<ServerResponse> swaggerUIResources() {
        return RouterFunctions
                .resources("/swagger-ui/**", new ClassPathResource("META-INF/resources/webjars/springfox-swagger-ui/"))
                .and(route(GET("/swagger-ui"),
                        req -> ok().contentType(MediaType.TEXT_HTML)
                                .bodyValue(swaggerUIIndexHtml())));
    }

    private String swaggerUIIndexHtml() {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Swagger UI</title>
                <link rel="stylesheet" type="text/css" href="/webjars/swagger-ui/swagger-ui.css" >
                <link rel="icon" type="image/png" href="/webjars/swagger-ui/favicon-32x32.png" sizes="32x32" />
                <link rel="icon" type="image/png" href="/webjars/swagger-ui/favicon-16x16.png" sizes="16x16" />
                <style>
                    html { box-sizing: border-box; overflow: -moz-scrollbars-vertical; overflow-y: scroll; }
                    *, *:before, *:after { box-sizing: inherit; }
                    body { margin:0; background: #fafafa; }
                </style>
            </head>
            <body>
                <div id="swagger-ui"></div>
                <script src="/webjars/swagger-ui/swagger-ui-bundle.js"></script>
                <script src="/webjars/swagger-ui/swagger-ui-standalone-preset.js"></script>
                <script>
                    window.onload = function() {
                        window.ui = SwaggerUIBundle({
                            url: "/v3/api-docs",
                            dom_id: '#swagger-ui',
                            deepLinking: true,
                            presets: [
                                SwaggerUIBundle.presets.apis,
                                SwaggerUIStandalonePreset
                            ],
                            plugins: [
                                SwaggerUIBundle.plugins.DownloadUrl
                            ],
                            layout: "StandaloneLayout"
                        });
                    };
                </script>
            </body>
            </html>
            """;
    }
}