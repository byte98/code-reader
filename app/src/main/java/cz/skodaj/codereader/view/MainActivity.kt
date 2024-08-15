package cz.skodaj.codereader.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import cz.skodaj.codereader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    /**
     * Binding to view.
     */
    private lateinit var viewBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.viewBinding.root)
    }


}