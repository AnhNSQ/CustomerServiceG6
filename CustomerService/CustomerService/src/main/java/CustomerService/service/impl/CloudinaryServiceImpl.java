package CustomerService.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import CustomerService.service.CloudinaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@Slf4j
public class CloudinaryServiceImpl implements CloudinaryService {
    
    private final Cloudinary cloudinary;
    
    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }
    
    @Override
    public String uploadImage(MultipartFile file) {
        try {
            // Validate file
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("File is empty");
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("File must be an image");
            }
            
            // Validate file size (max 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                throw new IllegalArgumentException("File size must be less than 10MB");
            }
            
            log.info("Uploading image to Cloudinary: {}", file.getOriginalFilename());
            
            // Upload to Cloudinary
            Map<String, Object> params = ObjectUtils.asMap(
                "folder", "ticket-images",
                "public_id", System.currentTimeMillis() + "_" + file.getOriginalFilename(),
                "overwrite", true,
                "resource_type", "image"
            );
            
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            String imageUrl = (String) uploadResult.get("secure_url");
            
            log.info("Image uploaded successfully: {}", imageUrl);
            
            return imageUrl;
            
        } catch (IOException e) {
            log.error("Error uploading image to Cloudinary", e);
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteImage(String imageUrl) {
        try {
            if (imageUrl == null || imageUrl.isEmpty()) {
                return;
            }
            
            // Extract public_id from URL
            String publicId = extractPublicIdFromUrl(imageUrl);
            
            if (publicId != null) {
                log.info("Deleting image from Cloudinary: {}", publicId);
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Image deleted successfully");
            }
            
        } catch (Exception e) {
            log.error("Error deleting image from Cloudinary", e);
            // Don't throw exception, just log error
        }
    }
    
    private String extractPublicIdFromUrl(String url) {
        try {
            // Format: https://res.cloudinary.com/{cloud}/image/upload/{folder}/{public_id}.{ext}
            String[] parts = url.split("/upload/");
            if (parts.length < 2) {
                return null;
            }
            
            String path = parts[1];
            // Remove file extension
            int lastDot = path.lastIndexOf(".");
            if (lastDot > 0) {
                path = path.substring(0, lastDot);
            }
            
            return path;
        } catch (Exception e) {
            log.error("Error extracting public_id from URL: {}", url, e);
            return null;
        }
    }
}

