package cz.skodaj.codereader.model.messaging.messages

import android.app.Activity

/**
 * Class which informs about start of activity.
 */
data class ActivityStartedMessage (

    /**
     * Reference to activity which has been started.
     */
    val activity: Activity
)