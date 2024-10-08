package cz.skodaj.codereader.utils

import cz.skodaj.codereader.model.messaging.Messenger
import cz.skodaj.codereader.model.messaging.Receiver
import cz.skodaj.codereader.model.messaging.messages.CameraEnabledMessage

/**
 * Class which groups common attributes of all other tools which works with camera.
 */
abstract class CameraHelper: Receiver {

    /**
     * Flag, whether tool is active.
     */
    private var active: Boolean = true

    /**
     * Creates new tool which somehow works with camera.
     */
    public fun constructor(){
        Messenger.default.register(CameraEnabledMessage::class, this)
    }

    /**
     * Gets flag, whether tool should be active or not.
     * @return TRUE if tool should be active,
     *         FALSE otherwise.
     */
    protected fun isActive(): Boolean{
        return this.active
    }

    public override fun receive(message: Any) {
        if (message::class == CameraEnabledMessage::class){
            val msg = message as CameraEnabledMessage
            this.active = msg.enabled
        }
    }
}