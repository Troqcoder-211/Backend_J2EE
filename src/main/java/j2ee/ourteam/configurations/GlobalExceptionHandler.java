package j2ee.ourteam.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  // Bắt ResponseStatusException riêng
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
    logger.warn("ResponseStatusException caught:", ex);
    Map<String, Object> body = new HashMap<>();
    // Nếu muốn lấy tên trạng thái dạng "not_found"
    body.put("error", ex.getStatusCode().toString().toLowerCase());
    body.put("message", ex.getReason());
    return ResponseEntity.status(ex.getStatusCode()).body(body);
  }

  // Bắt tất cả exception còn lại
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
    logger.error("Unhandled exception caught:", ex);
    Map<String, Object> body = new HashMap<>();
    body.put("error", "internal_error");
    body.put("message", ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
  }
}
