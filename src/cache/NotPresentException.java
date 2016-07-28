/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cache;

/**
 *
 * @author v.yanchenko
 */
public class NotPresentException extends Exception {

    public NotPresentException() {
        super();
    }

    public NotPresentException(String message) {
        super(message);
    }

    public NotPresentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotPresentException(Throwable cause) {
        super(cause);
    }
}
