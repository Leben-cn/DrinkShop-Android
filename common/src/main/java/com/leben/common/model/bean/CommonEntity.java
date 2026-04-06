package com.leben.common.model.bean;

public class CommonEntity<T> {
    private boolean success;
    private int code;
    private String message;
    private T data;

    // 建议双重检查，防止后端只传了 code 没传 success
    public boolean isSuccess() {
        return success || code == 200;
    }

    public T getData() { return data; }

    // 防止 message 本身为 null 导致后续报错
    public String getMessage() {
        return message == null ? "" : message;
    }
}
