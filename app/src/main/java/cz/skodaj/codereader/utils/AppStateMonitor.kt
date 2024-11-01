package cz.skodaj.codereader.utils

import android.util.Log
import cz.skodaj.codereader.model.messaging.Messenger
import cz.skodaj.codereader.model.messaging.Receiver
import cz.skodaj.codereader.model.messaging.messages.ActivityFinishedMessage
import cz.skodaj.codereader.model.messaging.messages.ActivityStartedMessage

/**
 * Object which manages some very basic monitoring of actual application state.
 */
object AppStateMonitor: Receiver {


    /**
     * Initializes basic application state monitor.
     */
    public fun init () {
        Messenger.default.register(ActivityStartedMessage::class, this)
        Messenger.default.register(ActivityFinishedMessage::class, this)
    }

    /**
     * Counter of actually running activities.
     */
    private var runningActivities: Long = 0

    /**
     * Flag, whether any activity has started.
     */
    private var activityStarted: Boolean = false

    /**
     * List of all callable blocks which should be run after application start.
     */
    private val starters: MutableList<() -> Unit> = mutableListOf()

    /**
     * List of all callable blocks which should be run after application finishes.
     */
    private val finishers: MutableList<() -> Unit> = mutableListOf()

    /**
     * Increments counter of running activities.
     */
    private fun incrementRunningActivities(){
        if (this.runningActivities == (0.toLong()) && this.activityStarted == false){
            this.activityStarted = true
            this.runStarters()
        }
        this.runningActivities++
        Log.d("APPSTATEMONITOR", "Detected start of activity (actual running activities: ${this.runningActivities.toString()})")
    }

    /**
     * Decrements counter of running activities.
     */
    private fun decrementRunningActivities(){
        this.runningActivities--
        if (this.runningActivities < 1 && this.activityStarted == true){
            this.runFinishers()
        }
        Log.d("APPSTATEMONITOR", "Detected finished activity (actual running activities: ${this.runningActivities.toString()})")
    }

    /**
     * Adds callable block which should be run after application has started.
     * @param starter Block which will be run after application starts.
     */
    public fun addStarter(starter: () -> Unit){
        this.starters.add(starter)
    }

    /**
     * Adds callable block which should be run after application has finished.
     * @param finisher Block which will be run after application finishes.
     */
    public fun addFinisher(finisher: () -> Unit){
        this.finishers.add(finisher)
    }

    /**
     * Runs all callable blocks which should be run after application is started.
     */
    private fun runStarters(){
        Log.d("APPSTATEMONITOR", "Application started...")
        for(starter in this.starters){
            starter()
        }
        this.starters.clear()
    }

    /**
     * Runs all callable blocks which should be run after application is finished.
     */
    private fun runFinishers(){
        for(finisher in this.finishers){
            finisher()
        }
        this.finishers.clear()
        Log.d("APPSTATEMONITOR", "Application finished.")
    }

    public override fun receive(message: Any) {
        if (message::class == ActivityStartedMessage::class){
            this.incrementRunningActivities()
        }
        else if (message::class == ActivityFinishedMessage::class){
            this.decrementRunningActivities()
        }
    }
}