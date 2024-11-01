package cz.skodaj.codereader.utils

import cz.skodaj.codereader.model.db.DatabaseFactory

/**
 * Object which holds all necessary steps to successful initialization of the appliaction.
 */
object Initializer {

    /**
     * Flag, whether initializer has done all actions (aka. has already run).
     */
    private var done: Boolean = false

    /**
     * Runs initialization.
     */
    public fun run(){
        if (this.done == false) {
            AppStateMonitor.init()
            AppStateMonitor.addFinisher {
                DatabaseFactory.closeAll()
            }
            this.done = true
        }
    }
}