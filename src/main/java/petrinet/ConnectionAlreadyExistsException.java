/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petrinet;

/**
 * @author Mark
 */
public class ConnectionAlreadyExistsException extends RuntimeException {

    public ConnectionAlreadyExistsException() {
        super();
    }

    public ConnectionAlreadyExistsException(String message) {
        super(message);
    }

}
