package cz.skodaj.codereader.view

import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.tabs.TabLayoutMediator
import cz.skodaj.codereader.R
import cz.skodaj.codereader.databinding.ActivityDetailBinding
import cz.skodaj.codereader.databinding.ActivityMainBinding
import cz.skodaj.codereader.model.CodeInfo
import cz.skodaj.codereader.model.messaging.Messenger
import cz.skodaj.codereader.model.messaging.Receiver
import cz.skodaj.codereader.model.messaging.messages.*
import cz.skodaj.codereader.model.preferences.PreferencesSet
import cz.skodaj.codereader.utils.DateUtils
import cz.skodaj.codereader.utils.ImageUtils
import cz.skodaj.codereader.utils.StringUtils
import cz.skodaj.codereader.view.components.DetailPagerAdapter
import java.io.File
import java.io.FileOutputStream
import java.util.*

class DetailActivity : MessagingActivity(), Receiver {

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
        val messenger: Messenger = Messenger.default
        while(messenger.hasMessage(this, CodeInfoMessage::class)){
            var message: Any? = messenger.getMessage(this, CodeInfoMessage::class)
            if (message != null){
                this.receive(message)
            }
        }
    }

    override fun finish() {
        Messenger.default.send(DetailActivityFinishedMessage())
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
        val adapter: DetailPagerAdapter = DetailPagerAdapter(this, info, this)
        this.viewBinding.detailViewPager.setAdapter(adapter)


        TabLayoutMediator(this.viewBinding.detailTabLayout, this.viewBinding.detailViewPager){tab, position ->
            tab.setText(when(position){
                0 -> this.getString(R.string.information).lowercase()
                1 -> this.getString(R.string.data).lowercase()
                2 -> this.getString(R.string.source).lowercase()
                else -> ""
            })
        }.attach()
    }
}