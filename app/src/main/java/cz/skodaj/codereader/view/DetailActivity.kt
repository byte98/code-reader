package cz.skodaj.codereader.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import cz.skodaj.codereader.R
import cz.skodaj.codereader.databinding.ActivityDetailBinding
import cz.skodaj.codereader.databinding.ActivityMainBinding
import cz.skodaj.codereader.model.CodeInfo
import cz.skodaj.codereader.model.messaging.Messenger
import cz.skodaj.codereader.model.messaging.Receiver
import cz.skodaj.codereader.model.messaging.messages.CodeInfoMessage
import cz.skodaj.codereader.model.messaging.messages.CodeScannedMessage
import cz.skodaj.codereader.utils.DateUtils

class DetailActivity : AppCompatActivity(), Receiver {

    init{
        Messenger.default.register(CodeInfoMessage::class, this)
    }

    /**
     * Binding to view itself.
     */
    private lateinit var viewBinding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        // Perform view binding
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityDetailBinding.inflate(this.layoutInflater)
        this.setContentView(this.viewBinding.root)

        // Check for the content of the view in the messenger
        while(Messenger.default.hasMessage(this, CodeInfoMessage::class)){
            var message: Any? = Messenger.default.getMessage(this, CodeInfoMessage::class)
            if (message != null){
                this.receive(message)
            }
        }
    }

    override fun finish() {
        // TODO: Enable camera
        //       Has to resolve when?
        //       Why? Because this activity is supposed to run not only from "main activity"
        //       (aka scanner itself) but also from other different parts of application.
        super.finish()
    }

    /**
     * Handles click on back button.
     * @param view View which has triggered the event.
     */
    public fun detailBackButtonClicked(view: View){
        this.finish()
    }

    public override fun receive(message: Any) {
        if (message::class == CodeInfoMessage::class){
            val msg: CodeInfoMessage = message as CodeInfoMessage
            this.initCodeInfo(msg.data)
        }
    }


    /**
     * Initializes view with data from code information object.
     * @param info Object with information about code.
     */
    private fun initCodeInfo(info: CodeInfo){
        Log.d("CODEREADER", ">>>>>>>>>> INITIALIZATION")
        this.viewBinding.detailTextViewDate.text = DateUtils.format(info.getCreationDate(), this)
        this.viewBinding.detailTextViewType.text = this.getString(this.resources.getIdentifier("ct_${info.getCodeType().toString()}", "string", this.packageName))
        this.viewBinding.detailTextViewDataType.text = this.getString(this.resources.getIdentifier("dt_${info.getDataType().toString()}", "string", this.packageName))
        this.viewBinding.detailTextViewDataSize.text = info.getSizeString()
    }
}