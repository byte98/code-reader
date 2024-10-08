package cz.skodaj.codereader.model.messaging.messages

/**
 * Class which represent message which informs about need for camera enable/disable action.
 * @param enabled Flag, whether camera should be enabled (TRUE) or disabled (FALSE)
 */
data class CameraEnabledMessage(
    val enabled: Boolean
)
