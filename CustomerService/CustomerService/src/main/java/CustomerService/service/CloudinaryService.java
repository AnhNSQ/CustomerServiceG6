package CustomerService.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Service để upload ảnh lên Cloudinary
 */
public interface CloudinaryService {
    
    /**
     * Upload ảnh lên Cloudinary
     * @param file File ảnh cần upload
     * @return URL của ảnh trên Cloudinary
     */
    String uploadImage(MultipartFile file);
    
    /**
     * Xóa ảnh trên Cloudinary
     * @param imageUrl URL của ảnh cần xóa
     */
    void deleteImage(String imageUrl);
}

