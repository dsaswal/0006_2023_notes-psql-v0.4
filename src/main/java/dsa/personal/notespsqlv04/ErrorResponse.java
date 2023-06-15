package dsa.personal.notespsqlv04;

public class ErrorResponse {
    private int statusCode;
    private int errorHash;
    private String message;
    private long timestamp;

    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public int getErrorHash() {
        return errorHash;
    }
    public void setErrorHash(int errorHash) {
        this.errorHash = errorHash;
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
    public ErrorResponse(int statusCode, int errorHash, String message, long timestamp) {
        this.statusCode = statusCode;
        this.errorHash = errorHash;
        this.message = message;
        this.timestamp = timestamp;
    }
    public ErrorResponse() {
    }
    @Override
    public String toString() {
        return "ErrorResponse [statusCode=" + statusCode + ", errorHash=\" + errorHash + \", message=" + message + ", timestamp=" + timestamp + "]";
    }
    
}
