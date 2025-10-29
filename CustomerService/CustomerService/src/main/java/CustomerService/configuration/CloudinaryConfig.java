package CustomerService.configuration;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name:}")
    private String cloudNameFromProps;

    @Value("${cloudinary.api-key:}")
    private String apiKeyFromProps;

    @Value("${cloudinary.api-secret:}")
    private String apiSecretFromProps;

    @Bean
    public Cloudinary cloudinary() {
        String cloudName;
        String apiKey;
        String apiSecret;
        
        // Ưu tiên đọc từ .env, nếu không có thì dùng application.properties
        try {
            String workingDir = System.getProperty("user.dir");
            System.out.println("Current working directory: " + workingDir);
            
            // Thử nhiều đường dẫn có thể có .env file
            Dotenv dotenv = null;
            String[] pathsToTry = {
                workingDir,  // Current working directory
                workingDir + "/CustomerService/CustomerService",  // Project root
                workingDir + "/../",  // Parent directory
                ".",  // Relative current
                "./CustomerService/CustomerService"  // Relative project root
            };
            
            for (String path : pathsToTry) {
                try {
                    java.io.File envFile = new java.io.File(path, ".env");
                    System.out.println("Trying .env at: " + envFile.getAbsolutePath() + " exists: " + envFile.exists());
                    
                    if (envFile.exists()) {
                        dotenv = Dotenv.configure()
                            .directory(path)
                            .load();
                        System.out.println("Successfully loaded .env from: " + path);
                        break;
                    }
                } catch (Exception e) {
                    // Continue to next path
                    System.out.println("Failed to load .env from " + path + ": " + e.getMessage());
                }
            }
            
            if (dotenv != null) {
                cloudName = dotenv.get("CLOUDINARY_CLOUD_NAME");
                apiKey = dotenv.get("CLOUDINARY_API_KEY");
                apiSecret = dotenv.get("CLOUDINARY_API_SECRET");
                System.out.println("Loaded from .env - cloudName: " + (cloudName != null ? "***" : "null"));
            } else {
                cloudName = null;
                apiKey = null;
                apiSecret = null;
                System.out.println(".env file not found, will use application.properties");
            }
            
            // Nếu .env không có giá trị, fallback về application.properties
            if (cloudName == null || cloudName.isEmpty()) {
                cloudName = cloudNameFromProps;
            }
            if (apiKey == null || apiKey.isEmpty()) {
                apiKey = apiKeyFromProps;
            }
            if (apiSecret == null || apiSecret.isEmpty()) {
                apiSecret = apiSecretFromProps;
            }
        } catch (Exception e) {
            // Nếu .env không tồn tại hoặc lỗi, dùng application.properties
            cloudName = cloudNameFromProps;
            apiKey = apiKeyFromProps;
            apiSecret = apiSecretFromProps;
        }
        
        // Validate
        if (cloudName == null || cloudName.isEmpty() || 
            apiKey == null || apiKey.isEmpty() || 
            apiSecret == null || apiSecret.isEmpty()) {
            throw new IllegalStateException(
                "Cloudinary credentials not configured! " +
                "Please set cloudinary.cloud-name, cloudinary.api-key, and cloudinary.api-secret in application.properties " +
                "or create .env file with CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET"
            );
        }
        
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        
        return new Cloudinary(config);
    }
}

