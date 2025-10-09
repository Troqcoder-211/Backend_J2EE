package j2ee.ourteam.services.aws;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {
  private final S3Client s3Client;

  @Value("${aws.s3.bucket}")
  private final String bucketName;

  public S3Service(S3Client s3Client,
      @Value("${aws.s3.bucket") String bucketName) {
    this.s3Client = s3Client;
    this.bucketName = bucketName;
  }

  public String uploadFile(MultipartFile file) throws IOException {
    String key = UUID.randomUUID() + "_" + file.getOriginalFilename();

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .acl("public-read")
        .contentType(file.getContentType())
        .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

    return "https://" + bucketName + ".s3.ap-southeast-1.amazonaws.com/" + key;
  }

  public void deleteFile(String fileName) {
    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
        .bucket(bucketName)
        .key(fileName)
        .build();

    s3Client.deleteObject(deleteObjectRequest);
  }

}
