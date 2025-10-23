package j2ee.ourteam.models.conversation;

import lombok.Data;

@Data
public class ResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;

    public static <T> ResponseDTO<T> success(String message, T data) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ResponseDTO<T> error(String message) {
        ResponseDTO<T> response = new ResponseDTO<>();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }
}