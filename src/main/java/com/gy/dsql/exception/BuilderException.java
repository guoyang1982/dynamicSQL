package com.gy.dsql.exception;

/**
 * @author guoyang
 * @date 2022/9/2 9:31 下午
 */
public class BuilderException extends RuntimeException {

    public BuilderException() {
        super();
    }

    public BuilderException(String message) {
        super(message);
    }

    public BuilderException(String message, Throwable cause) {
        super(message, cause);
    }

    public BuilderException(Throwable cause) {
        super(cause);
    }
}
