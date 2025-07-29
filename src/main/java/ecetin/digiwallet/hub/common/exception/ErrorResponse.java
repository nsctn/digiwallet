package ecetin.digiwallet.hub.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private Object message;
    private Instant timestamp;
}