package cz.skodaj.codereader.view.components

import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextPaint
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.marginBottom
import cz.skodaj.codereader.R
import cz.skodaj.codereader.configuration.Globals
import cz.skodaj.codereader.databinding.FragmentDetailDataBinding
import cz.skodaj.codereader.databinding.FragmentDetailInfoBinding
import cz.skodaj.codereader.model.DataType
import cz.skodaj.codereader.utils.MapUtils
import cz.skodaj.codereader.utils.StringUtils
import kotlin.math.floor
import kotlin.random.Random

private const val ARG_RAW_DATA = "raw_data"
private const val ARG_DATA_TYPE = "data_type"
private const val ARG_DATA_FIELDS = "data_fields"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailDataFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailDataFragment : Fragment() {

    /**
     * Raw data of code.
     */
    private var rawData: String = ""

    /**
     * Type of data stored in code.
     */
    private var dataType: String = ""

    /**
     * Data fields parsed from raw data.
     */
    private var dataFields: Map<String, String> = emptyMap()

    /**
     * Holder for binding to view itself.
     */
    private var _viewBinding: FragmentDetailDataBinding? = null

    /**
     * Binding to view itself.
     */
    private val viewBinding: FragmentDetailDataBinding get() = this._viewBinding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            this.rawData = it.getString(ARG_RAW_DATA) ?: ""
            this.dataType = it.getString(ARG_DATA_TYPE) ?: ""
            this.dataFields = MapUtils.fromString(it.getString(ARG_DATA_FIELDS) ?: "")
        }
        this.initView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        this._viewBinding = FragmentDetailDataBinding.inflate(this.layoutInflater)
        return this.viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._viewBinding = null
    }

    /**
     * Initializes view.
     */
    private fun initView(){
        // Data type
        this.viewBinding.detailTextViewDataType.setText(this.dataType)

        // Data fields
        if (this.dataFields.isEmpty() == false){
            this.initDataFields()
        }
        else{
            this.viewBinding.detailDataFields.setVisibility(View.GONE)
        }

        // DEBUG: Generate some random raw data
        /*
        val random: Random = Random(System.currentTimeMillis())
        val rstr: String = StringUtils.random(random.nextInt(512, 4096*2))
        val sb: StringBuilder = StringBuilder()
        for(i: Int in 0 until rstr.length){
            sb.append(rstr.get(i))
            if (random.nextInt(0, 100) < 11){
                sb.append(' ')
            }
        }
        this.rawData = sb.toString()
        */

        // Raw data
        this.initRawData()

    }

    /**
     * Initializes field with raw data.
     */
    private fun initRawData(){
        this.viewBinding.detailTextViewRawData.post {
            val text: String = this.rawData.replace(' ', '\u00A0')
            this.viewBinding.detailTextViewRawData.setText(text)
        }
    }

    /**
     * Initializes data fields to view.
     */
    private fun initDataFields() {
        this.viewBinding.detailDataFields.removeAllViewsInLayout()

        for (field: String in this.dataFields.keys) {
            // Create field name TextView
            val fieldHeader = AppCompatTextView(requireContext()).apply {
                setTextAppearance(R.style.DetailTitleTextStyle)
                text = field
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )

            }

            // Create field value TextView
            val fieldValue = AppCompatTextView(requireContext()).apply {
                setTextAppearance(R.style.DetailValueTextStyle)
                text = dataFields[field]
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Compute margin in pixels from dp
            val r: Resources = this.resources
            val px: Int = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                16f,
                r.displayMetrics
            ).toInt()

            // Set margin
            val params: ViewGroup.MarginLayoutParams = fieldValue.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin + px)
            fieldValue.layoutParams = params

            // Add both TextViews to the LinearLayout
            this.viewBinding.detailDataFields.addView(fieldHeader)
            this.viewBinding.detailDataFields.addView(fieldValue)
        }
    }

    companion object {
        /**
         * Creates new fragment with code data.
         * @param rawData Raw data stored in code.
         * @param dataType Type of data stored in code.
         * @param dataFields Data fields of code (if there are any).
         */
        @JvmStatic
        fun newInstance(rawData: String, dataType: String, dataFields: Map<String, String> = emptyMap()) =
            DetailDataFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_RAW_DATA, rawData)
                    putString(ARG_DATA_TYPE, dataType)
                    putString(ARG_DATA_FIELDS, MapUtils.toString(dataFields))
                }
            }
    }
}