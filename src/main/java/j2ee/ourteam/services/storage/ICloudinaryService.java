package j2ee.ourteam.services.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ICloudinaryService {
    String uploadFile(MultipartFile file, String folder) throws IOException;
}
