package dsa.personal.notespsqlv04;

import java.sql.Timestamp;

public class ErrorResponse {
    private int statusCode;
    private String message;
    private long timestamp;

    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public ErrorResponse(int statusCode, String message, long timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = timestamp;
    }
    public ErrorResponse() {
    }
    @Override
    public String toString() {
        return "ErrorResponse [statusCode=" + statusCode + ", message=" + message + ", timestamp=" + timestamp + "]";
    }
    
}
