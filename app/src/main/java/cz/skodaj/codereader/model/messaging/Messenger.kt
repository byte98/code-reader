package cz.skodaj.codereader.model.messaging

import kotlin.reflect.KClass

/**
 * Class which delivers messages.
 */
class Messenger {

    /**
     * Class which holds some basic information about receiver of messages.
     */
    private data class ReceiverInfo(

        /**
         * Reference to receiver of the message itself.
         */
        val receiver: Receiver,

        /**
         * Flag, whether receiver should receive just one message
         */
        val receiveOnce: Boolean
    )

    /**
     * Map which maps type of message to its receivers.
     */
    private val receivers: MutableMap<KClass<*>, MutableList<Messenger.ReceiverInfo>> = mutableMapOf<KClass<*>, MutableList<Messenger.ReceiverInfo>>()


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
            this.receivers.put(message, mutableListOf<Messenger.ReceiverInfo>())
        }
        this.receivers.get(message)?.add(Messenger.ReceiverInfo(receiver, false))
    }

    /**
     * Registers new receiver of one single message.
     * @param message Type of message accepted once by receiver.
     * @param receiver Receiver of messages.
     */
    public fun registerOnce(message: KClass<*>, receiver: Receiver){
        if (this.receivers.containsKey(message) == false){
            this.receivers.put(message, mutableListOf<Messenger.ReceiverInfo>())
        }
        this.receivers.get(message)?.add(Messenger.ReceiverInfo(receiver, true))
    }

    /**
     * Unregisters receiver of messages.
     * @param message Type of message.
     * @param receiver Receiver which will be unregistered.
     */
    public fun unregister(message: KClass<*>, receiver: Receiver){
        if (this.receivers.containsKey(message)){
            val list: List<Messenger.ReceiverInfo> = this.receivers.get(message) ?: listOf()
            val newList: MutableList<Messenger.ReceiverInfo> = mutableListOf()
            for(info: Messenger.ReceiverInfo in list){
                if (info.receiver != receiver){
                    newList.add(info)
                }
            }
            this.receivers.replace(message, newList)
        }
    }

    /**
     * Sends message.
     * @param message Message which will be sent.
     */
    public fun send(message: Any){
        val msgType: KClass<*> = message::class
        if (this.receivers.containsKey(msgType)){
            val receivers: List<Messenger.ReceiverInfo> = this.receivers.get(msgType) ?: listOf()
            val toUnregister: MutableList<Pair<KClass<*>, Receiver>> = mutableListOf()
            for (rec: Messenger.ReceiverInfo in receivers){
                rec.receiver.receive(message)
                if (rec.receiveOnce == true){
                    toUnregister.add(Pair(msgType, rec.receiver))
                }
            }
            for (un: Pair<KClass<*>, Receiver> in toUnregister){
                this.unregister(un.first, un.second)
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
        val rec: KClass<*> = receiver::class
        if (this.delayedMessages.containsKey(rec)){
            val messages: List<DelayedMessage> = this.delayedMessages.get(rec) ?: emptyList()
            for (msg in messages){
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
        var ms: DelayedMessage? = null
        val rec: KClass<*> = receiver::class
        if (this.delayedMessages.containsKey(rec)){
            val messages: List<DelayedMessage> = this.delayedMessages.get(rec) ?: emptyList()
            for (msg in messages){
                if (msg.isContent(message)){
                    reti = msg.getContent()
                    ms = msg
                    break
                }
            }
        }
        if (ms != null){
            this.removeDelayedMessage(rec, ms)
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