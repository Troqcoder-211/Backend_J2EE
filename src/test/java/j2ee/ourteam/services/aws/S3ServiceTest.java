package j2ee.ourteam.services.aws;

import j2ee.ourteam.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class S3ServiceTest extends BaseTest {

    private S3Client s3Client;
    private S3Service s3Service;
    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        s3Client = mock(S3Client.class);
        s3Service = new S3Service(s3Client, bucketName);
    }

    @Test
    void uploadFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "Hello World".getBytes()
        );

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(null);

        String key = s3Service.uploadFile(file);

        assertThat(key).isNotNull();
        verify(s3Client, times(1))
                .putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void download_success() throws Exception {
        String key = "file123.txt";

        // Mock S3Client.getObject để ghi nội dung vào Path
        doAnswer(inv -> {
            Path outputPath = inv.getArgument(1);
            Files.write(outputPath, "hello".getBytes());
            return null;
        }).when(s3Client).getObject(any(GetObjectRequest.class), any(Path.class));

        Resource resource = s3Service.download(key);

        assertThat(resource).isNotNull();
        assertThat(resource.exists()).isTrue();
        assertThat(resource.contentLength()).isEqualTo(5);
    }

    @Test
    void download_fail_throwsIOException() {
        String key = "missing.txt";

        doThrow(new RuntimeException("S3 error"))
                .when(s3Client).getObject(any(GetObjectRequest.class), any(Path.class));

        assertThrows(IOException.class, () -> s3Service.download(key));
    }

    @Test
    void delete_success() {
        String key = "delete.txt";

        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(mock(DeleteObjectResponse.class));

        s3Service.deleteFile(key);

        verify(s3Client, times(1))
                .deleteObject(any(DeleteObjectRequest.class));
    }
}
