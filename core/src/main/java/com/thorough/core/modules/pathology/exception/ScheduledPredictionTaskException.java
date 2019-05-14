package com.thorough.core.modules.pathology.exception;

/**
 * Created by chenlinsong on 2018/8/30.
 */
public class ScheduledPredictionTaskException extends RuntimeException{

    private static final long serialVersionUID = 5162710183389028792L;

    /**
     * Constructs a {@code NullPointerException} with no detail message.
     */
    public ScheduledPredictionTaskException() {
        super();
    }

    /**
     * Constructs a {@code NullPointerException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public ScheduledPredictionTaskException(String s) {
        super(s);
    }
}
