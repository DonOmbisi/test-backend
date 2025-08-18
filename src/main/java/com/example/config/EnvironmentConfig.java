package com.example.config;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentConfig implements CommandLineRunner {

    @Autowired
    private Environment environment;

    @Override
    public void run(String... args) throws Exception {
        // Validate required environment variables
        validateEnvironmentVariables();
    }

    private void validateEnvironmentVariables() {
        String[] requiredVars = {
            "DATABASE_URL",
            "DATABASE_USERNAME",
            "DATABASE_PASSWORD"
        };

        for (String var : requiredVars) {
            String value = environment.getProperty(var);
            if (value == null || value.trim().isEmpty()) {
                System.err.println("⚠️  WARNING: Environment variable '" + var + "' is not set!");
                System.err.println("   Please check your .env file or system environment variables.");
                System.err.println("   See SETUP_ENVIRONMENT.md for configuration instructions.");
            }
        }

        // Log successful configuration
        System.out.println("✅ Environment configuration validated successfully");
        System.out.println("📊 Database URL: " + maskSensitiveData(environment.getProperty("DATABASE_URL")));
        System.out.println("👤 Database User: " + environment.getProperty("DATABASE_USERNAME"));
        System.out.println("🔐 Database Password: " + (environment.getProperty("DATABASE_PASSWORD") != null ? "***SET***" : "***NOT SET***"));
    }

    private String maskSensitiveData(String url) {
        if (url == null) return "NOT SET";
        // Mask password in URL if present
        return url.replaceAll("://[^:]+:[^@]+@", "://***:***@");
    }
}
