package com.asg.test.errors;

import java.util.ArrayList;
import java.util.List;

public class HttpError {

    private int code;
    private String message;
    private List<ErrorDetail> details;

    public HttpError() {}

    public HttpError(int code, String message, List<ErrorDetail> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    public HttpError(int code, String message) {
        this(code, message, (List<ErrorDetail>) null);
    }

    public HttpError(int code, String message, ErrorDetail detail) {
        this(code, message);
        this.details = new ArrayList<>();
        this.details.add(detail);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ErrorDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ErrorDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "HttpError{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", details=" + details +
                '}';
    }
}
