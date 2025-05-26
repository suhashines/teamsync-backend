//package edu.teamsync.teamsync.config;
//
//public class JwtConstant {
//    public static final String SECRET_KEY = "asdfghjklzxcvbnmqwertyuiopndblds";
//    public static final String JWT_HEADER = "Authorization";
//}


package edu.teamsync.teamsync.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtConstant {
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public static final String JWT_HEADER = "Authorization";

    // Getter for SECRET_KEY
    public String getSecretKey() {
        return SECRET_KEY;
    }
}