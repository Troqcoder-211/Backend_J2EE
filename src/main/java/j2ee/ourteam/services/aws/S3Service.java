package j2ee.ourteam.services.aws;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
  private final S3Client s3Client;

  @Value("${aws.s3.bucket}")
  private final String bucketName;

  public S3Service(S3Client s3Client,
      @Value("${aws.s3.bucket}") String bucketName) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
  }

  public String uploadFile(MultipartFile file) throws IOException {
    String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .contentType(file.getContentType())
        .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

    // return "https://" + bucketName + ".s3.ap-southeast-1.amazonaws.com/" + key;
    return key;
  }

  public Resource download(String key) throws IOException {
    try {
      Path tempFile = Files.createTempFile("s3download-", ".jpg");

      s3Client.getObject(
          GetObjectRequest.builder()
              .bucket(bucketName)
              .key(key)
              .build(),
          tempFile);

      Resource resource = new UrlResource(tempFile.toUri());

      if (!resource.exists() || !resource.isReadable())
        throw new IOException("File not found: " + key);

      return resource;
    } catch (Exception e) {
      throw new IOException("Error downloading file from S3: " + e.getMessage(), e);
    }
  }

  public void deleteFile(String fileName) {
    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(bucketName)
        .key(fileName)
        .build();

    s3Client.deleteObject(deleteObjectRequest);
  }

}
