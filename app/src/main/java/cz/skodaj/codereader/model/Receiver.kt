package cz.skodaj.codereader.model

/**
 * Interface which defines contract for all message receivers.
 */
interface Receiver {

    /**
     * Function which will be called when there is any message to receive.
     * @param message Message which should be received by object.
     */
    public fun receive(message: Any)
}