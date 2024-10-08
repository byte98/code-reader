package cz.skodaj.codereader.model.messaging

import kotlin.reflect.KClass

/**
 * Class which delivers messages.
 */
class Messenger {

    /**
     * Map which maps type of message to its receivers.
     */
    private val receivers: MutableMap<KClass<*>, MutableList<Receiver>> = mutableMapOf<KClass<*>, MutableList<Receiver>>()


    /**
     * Map which stores messages, which cannot be sent right now.
     */
    private val delayedMessages: MutableMap<KClass<*>, MutableList<DelayedMessage>> = mutableMapOf<KClass<*>, MutableList<DelayedMessage>>()


    /**
     * Registers new receiver of messages.
     * @param message Type of messages accepted by receiver.
     * @param receiver Receiver of messages.
     */
    public fun register(message: KClass<*>, receiver: Receiver): Unit{
        if (this.receivers.containsKey(message) == false){
            this.receivers.put(message, mutableListOf<Receiver>())
        }
        if (this.receivers.get(message)?.contains(receiver) == false){
            this.receivers.get(message)?.add(receiver)
        }
    }

    /**
     * Sends message.
     * @param message Message which will be sent.
     */
    public fun send(message: Any){
        val msgType: KClass<*> = message::class
        if (this.receivers.containsKey(msgType)){
            val receivers: List<Receiver> = this.receivers.get(msgType) ?: listOf()
            for (rec: Receiver in receivers){
                rec.receive(message)
            }
        }
    }

    /**
     * Sends message without active receive.
     * @param receiver Type of receiver.
     * @param message Message which will be sent.
     */
    public fun sendDelayed(receiver: KClass<*>, message: Any){
        if (this.delayedMessages.containsKey(receiver) == false){
            this.delayedMessages.put(receiver, mutableListOf<DelayedMessage>())
        }
        this.delayedMessages.get(receiver)!!.add(DelayedMessage(receiver, message))
    }

    /**
     * Checks, whether there are any messages for given receiver.
     * @param receiver Receiver which checks for the messages.
     * @param message Expected type of message.
     * @return TRUE if there is at least one message for the given receiver of the given type,
     *         FALSE otherwise.
     */
    public fun hasMessage(receiver: Any, message: KClass<*>): Boolean{
        var reti: Boolean = false
        if (this.delayedMessages.containsKey(receiver::class)){
            for (msg in this.delayedMessages.get(receiver) ?: emptyList<DelayedMessage>()){
                if (msg.isContent(message)){
                    reti = true
                    break
                }
            }
        }
        return reti
    }

    /**
     * Gets message for receiver.
     * @param receiver Receiver which requires message.
     * @param message Expected type of message.
     * @return Message for given receiver of the given type,
     *         or NULL if there is no such message.
     */
    public fun getMessage(receiver: Any, message: KClass<*>): Any?{
        var reti: Any? = null
        var delayedMessage: DelayedMessage? = null
        if (this.delayedMessages.containsKey(receiver::class)){
            for (msg in this.delayedMessages.get(receiver) ?: emptyList<DelayedMessage>()){
                if (msg.isContent(message)){
                    reti = msg.getContent()
                    delayedMessage = msg
                    break
                }
            }
        }
        if (delayedMessage != null){
            this.removeDelayedMessage(receiver::class, delayedMessage)
        }
        return reti
    }

    /**
     * Removes delayed message from the list 'to be received' messages.
     * @param receiver Type of receiver of the message.
     * @param message Message which will be removed.
     */
    private fun removeDelayedMessage(receiver: KClass<*>, message: DelayedMessage){
        if (this.delayedMessages.containsKey(receiver)){
            val list: MutableList<DelayedMessage> = this.delayedMessages.get(receiver) ?: mutableListOf<DelayedMessage>()
            if (list.contains(message)){
                list.remove(message)
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