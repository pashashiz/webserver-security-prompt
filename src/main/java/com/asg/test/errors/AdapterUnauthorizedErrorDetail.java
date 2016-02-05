package com.asg.test.errors;

public class AdapterUnauthorizedErrorDetail extends ErrorDetail {

    private String adapter;
    private String authorization;

    public AdapterUnauthorizedErrorDetail() {}

    public AdapterUnauthorizedErrorDetail(String message, String adapter, String authorization) {
        super(message);
        this.adapter = adapter;
        this.authorization = authorization;
    }

    public String getAdapter() {
        return adapter;
    }

    public void setAdapter(String adapter) {
        this.adapter = adapter;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    @Override
    public String toString() {
        return "AdapterUnauthorizedErrorDetail{" +
                "message='" + getMessage() + '\'' +
                "adapter='" + adapter + '\'' +
                ", authorization='" + authorization + '\'' +
                '}';
    }
}
