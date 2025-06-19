package common;

import com.bcupen.pocket_coach_service.common.ApiException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiExceptionTest {
    @Test
    public void testApiException() {
        // Create an instance of ApiException
        HttpStatus statusCode = HttpStatus.BAD_REQUEST;
        String message = "Invalid request";
        ApiException apiException = new ApiException(statusCode, message);

        // Verify the status code and message
        assertEquals(statusCode, apiException.getStatusCode());
        assertEquals(message, apiException.getMessage());
    }
}
