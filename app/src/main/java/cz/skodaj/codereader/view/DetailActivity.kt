package cz.skodaj.codereader.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import cz.skodaj.codereader.R
import cz.skodaj.codereader.databinding.ActivityDetailBinding
import cz.skodaj.codereader.databinding.ActivityMainBinding
import cz.skodaj.codereader.model.messaging.Messenger
import cz.skodaj.codereader.model.messaging.messages.CodeScannedMessage

class DetailActivity : AppCompatActivity() {

    /**
     * Binding to view itself.
     */
    private lateinit var viewBinding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        // Perform view binding
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityDetailBinding.inflate(this.layoutInflater)
        this.setContentView(this.viewBinding.root)
    }

    override fun finish() {
        Messenger.default.send(CodeScannedMessage(false))
        super.finish()
    }

    /**
     * Handles click on back button.
     * @param view View which has triggered the event.
     */
    public fun detailBackButtonClicked(view: View){
        this.finish()
    }
}