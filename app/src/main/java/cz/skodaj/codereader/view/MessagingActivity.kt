package cz.skodaj.codereader.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.skodaj.codereader.model.messaging.Messenger
import cz.skodaj.codereader.model.messaging.messages.ActivityFinishedMessage
import cz.skodaj.codereader.model.messaging.messages.ActivityStartedMessage

/**
 * Class which extends activity by automatic message sending.
 */
open class MessagingActivity:AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Messenger.default.send(ActivityStartedMessage(this))
    }

    public override fun onDestroy() {
        Messenger.default.send(ActivityFinishedMessage(this))
        super.onDestroy()
    }
}