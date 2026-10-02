package ua.lpnu.kzp;

/**
 * Indicates an error while storing or processing data.
 */
public class DataStorageException extends Exception {

    /**
     * Creates an exception with a descriptive message.
     *
     * @param message error message
     */
    public DataStorageException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a message and original cause.
     *
     * @param message error message
     * @param cause original cause
     */
    public DataStorageException(
        String message,
        Throwable cause
    ) {
        super(message, cause);
    }
}
