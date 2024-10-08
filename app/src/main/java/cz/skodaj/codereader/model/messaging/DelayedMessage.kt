package cz.skodaj.codereader.model.messaging

import kotlin.reflect.KClass

/**
 * Class which wraps message which cannot be sent right now and will be received later.
 */
class DelayedMessage {
    /**
     * Class to which is message sent.
     */
    private val address: KClass<*>

    /**
     * Content of the message itself.
     */
    private val content: Any

    /**
     * Creates new message with delayed receiving.
     * @param addresss Class to which is message sent.
     * @param content Content of the message.
     */
    public constructor(address: KClass<*>, content: Any) {
        this.address = address
        this.content = content
    }

    /**
     * Gets content of the message.
     * @return Any object which represents content of the message.
     */
    public fun getContent(): Any{
        return this.content
    }

    /**
     * Checks, whether passed class is address of the message.
     * @param adr Class which will be checked.
     * @return TRUE if given class is address of the message,
     *         FALSE otherwise.
     */
    public fun isAddress(adr: KClass<*>): Boolean{
        return adr == this.address
    }

    /**
     * Checks, whether content is in given format.
     * @param cls Class representing format of content.
     * @return TRUE if content of the message corresponds to given class,
     *         FALSE otherwise.
     */
    public fun isContent(cls: KClass<*>): Boolean{
        return this.content::class == cls
    }
}