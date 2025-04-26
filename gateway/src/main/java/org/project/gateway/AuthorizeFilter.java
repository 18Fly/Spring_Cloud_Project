package org.project.gateway;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.project.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

//@Order(value = 0)
@Component
@Slf4j
public class AuthorizeFilter implements GlobalFilter, Ordered {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> Authorization = exchange.getRequest().getHeaders().get("Authorization");

        String path = exchange.getRequest().getPath().toString();

        if (path.contains("login") || path.contains("logout") || path.contains("register")) {
            return chain.filter(exchange);
        }

        if (Authorization != null && !"".equals(Authorization.get(0))) {
            String au = Authorization.get(0);
            DecodedJWT decodedJWT = JWTUtils.verifyToken(au);
            if (decodedJWT != null) {
                Claim user = decodedJWT.getClaim("user");
                Claim employee = decodedJWT.getClaim("employee");
                Claim authToken = user.isMissing() ? employee : user;

                if (!employee.isMissing() && (path.contains("employee") || path.contains("user") || (!path.contains("help") && path.contains("animal")))) {
                    Integer power = (Integer) redisTemplate.opsForValue().get(au);
                    if (power != null && power == 0) {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }
                }

                String string = authToken.asString();
                String o = (String) redisTemplate.opsForValue().get(string);
                if (au.equals(o)) {
                    return chain.filter(exchange);
                }
            }
        }

        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
