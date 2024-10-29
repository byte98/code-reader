package cz.skodaj.codereader.model.messaging.messages

import androidx.appcompat.app.AppCompatActivity

/**
 * Class which informs about finish of activity.
 */
data class ActivityFinishedMessage (

    /**
     * Reference to activity which has been started.
     */
    val activity: AppCompatActivity
)