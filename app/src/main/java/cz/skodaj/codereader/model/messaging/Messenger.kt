package cz.skodaj.codereader.model.messaging

import kotlin.reflect.KClass

/**
 * Class which delivers messages.
 */
class Messenger {

    /**
     * Map which maps type of message to its receivers.
     */
    private val receivers: MutableMap<String, MutableList<Receiver>> = mutableMapOf<String, MutableList<Receiver>>()

    /**
     * Registers new receiver of messages.
     * @param message Type of messages accepted by receiver.
     * @param receiver Receiver of messages.
     */
    public fun register(message: KClass<*>, receiver: Receiver): Unit{
        val typeName: String = message.qualifiedName ?: "<UNKNOWN>"
        if (this.receivers.containsKey(typeName) == false){
            this.receivers.put(typeName, mutableListOf<Receiver>())
        }
        if (this.receivers.get(typeName)?.contains(receiver) == false){
            this.receivers.get(typeName)?.add(receiver)
        }
    }

    /**
     * Sends message.
     * @param message Message which will be sent.
     */
    public fun send(message: Any){
        val typeName: String = message::class.qualifiedName ?: "<UNKNOWN>"
        if (this.receivers.containsKey(typeName)){
            val receivers: List<Receiver> = this.receivers.get(typeName) ?: listOf()
            for (rec: Receiver in receivers){
                rec.receive(message)
            }
        }
    }

    companion object{

        /**
         * Default instance of messenger.
         */
        val default: Messenger by lazy{
            Messenger()
        }

    }

}