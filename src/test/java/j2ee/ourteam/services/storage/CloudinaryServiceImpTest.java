package j2ee.ourteam.services.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CloudinaryServiceImpTest {

    @InjectMocks
    private CloudinaryServiceImp cloudinaryService;

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private MultipartFile file;

    @Mock
    private Uploader uploader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cloudinary.uploader()).thenReturn(uploader);
    }

    @Test
    void testUploadFile_Success() throws IOException {
        String expectedUrl = "https://res.cloudinary.com/demo/image/upload/sample.jpg";
        when(file.getBytes()).thenReturn("dummy".getBytes());
        when(uploader.upload(any(byte[].class), any(Map.class)))
                .thenReturn(Map.of("secure_url", expectedUrl));

        String result = cloudinaryService.uploadFile(file, "test-folder");

        assertNotNull(result);
        assertEquals(expectedUrl, result);
        verify(uploader, times(1)).upload(any(byte[].class), any(Map.class));
    }

    @Test
    void testUploadFile_ThrowsIOException() throws IOException {
        when(file.getBytes()).thenThrow(new IOException("Failed to read file"));

        IOException exception = assertThrows(IOException.class, () ->
                cloudinaryService.uploadFile(file, "test-folder")
        );

        assertEquals("Failed to read file", exception.getMessage());
    }
}
