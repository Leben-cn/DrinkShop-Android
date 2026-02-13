package com.leben.common.model.bean;

public class CommonEntity<T> {
    private boolean success;
    private int code;
    private String message;
    private T data; // 这里如果是分页，T 就是 PageData

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getMessage() { return message; }
}
