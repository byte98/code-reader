package cz.skodaj.codereader.model.messaging.messages

/**
 * Message which informs whether code is actually being scanned or not.
 * @param scanned Flag, whether code is actually being scanned or not.
 */
data class CodeScannedMessage(
    val scanned: Boolean
)
