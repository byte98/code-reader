package cz.skodaj.codereader.model

import java.lang.reflect.Type
import kotlin.reflect.KType
import kotlin.reflect.typeOf

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
     * @param message Example of message which receiver accepts.
     * @param receiver Receiver of messages.
     */
    public fun register(message: Any, receiver: Receiver): Unit{
        val typeName: String = message::class.qualifiedName ?: "<UNKNOWN>"
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