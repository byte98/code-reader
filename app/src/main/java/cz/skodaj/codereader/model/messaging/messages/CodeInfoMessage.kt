package cz.skodaj.codereader.model.messaging.messages

import cz.skodaj.codereader.model.CodeInfo

/**
 * Class which represents message which holds information about code.
 * @param data Data which will be sent (information about code).
 */
data class CodeInfoMessage(
    val data: CodeInfo
)
