package cz.skodaj.codereader.model.messaging.messages

import android.app.Activity

/**
 * Class which informs about finish of activity.
 */
data class ActivityFinishedMessage (

    /**
     * Reference to activity which has been started.
     */
    val activity: Activity
)