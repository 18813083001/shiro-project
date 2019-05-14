package com.thorough.core.modules.pathology.exception;


import com.thorough.library.system.exception.LibraryException;

public class CoreException extends LibraryException{

    public CoreException() {
        super();
    }


    public CoreException(String message) {
        super(message);
    }


    public CoreException(String message, Throwable cause) {
        super(message, cause);
    }


    public CoreException(Throwable cause) {
        super(cause);
    }


    protected CoreException(String message, Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
