package com.example.FinFlow.additional;

import org.springframework.http.HttpStatus;

public class Response {
    Object description;
    HttpStatus httpCode;

    public Response(Object description, int status) {
        this.description = description;
        this.httpCode = HttpStatus.valueOf(status);
    }
    public Response(Object description, HttpStatus status) {
        this.description = description;
        this.httpCode = status;
    }
    public Object getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HttpStatus getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = HttpStatus.valueOf(httpCode);
    }

    @Override
    public String toString() {
        return "Response{" +
                "description=" + description +
                ", httpCode=" + httpCode +
                '}';
    }
}
