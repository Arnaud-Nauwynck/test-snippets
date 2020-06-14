package fr.an.tests.httpproxy.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import reactor.core.publisher.Mono;

@Configuration
public class FooGatewayConfiguration {

//	@Bean
//	public GatewayFilter fooGatewayFilter() {
//		GatewayFilter res = new GatewayFilter() {
//			@Override
//			public Mono<Void> filter(
//					ServerWebExchange exchange,
//					GatewayFilterChain chain) {
//				ServerHttpRequest request = exchange.getRequest().mutate()
//						.header("X-AddedHeader", "test-gateway")
//						.build();
//
//				return chain.filter(exchange.mutate().request(request).build());
//			}
//		};
//		return res;
//	}
	
    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
                .route(r -> r.path("/cloud-proxy/foo/**")
                        .uri("http://localhost:8080/api/v1/foo/")
//                        .filters(fooGatewayFilter())
                        .id("foo"))

                .build();
    }

}