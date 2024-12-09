package cz.skodaj.codereader.view.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.Log
import androidx.core.graphics.toRectF
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cz.skodaj.codereader.model.Code
import cz.skodaj.codereader.model.CodeInfo
import cz.skodaj.codereader.model.DataType
import cz.skodaj.codereader.model.preferences.PreferencesSet
import cz.skodaj.codereader.utils.DateUtils
import cz.skodaj.codereader.utils.ImageUtils
import cz.skodaj.codereader.utils.StringUtils

/**
 * Class which connects actual tab of detail view to the correct content of the tab.
 */
class DetailPagerAdapter: FragmentStateAdapter {

    /**
     * Information about code which will be displayed.
     */
    private val info: CodeInfo?

    /**
     * Code which will be displayed.
     */
    private val code: Code?

    /**
     * Actual context of the application.
     */
    private val context: Context

    /**
     * Creates new adapter of detail view tabs to tab view itself.
     * @param activity Activity which holds tab view.
     * @param info Object which holds information about code.
     * @param context Actual context of the application.
     */
    public constructor(activity: FragmentActivity, info: CodeInfo, context: Context): super(activity){
        this.info = info
        this.code = null
        this.context = context
    }

    /**
     * Creates new adapter of detail view tabs to tab view itself.
     * @param activity Activity which holds tab view.
     * @param info Object which holds information about code.
     * @param context Actual context of the application.
     */
    public constructor(activity: FragmentActivity, code: Code, context: Context): super(activity){
        this.info = null
        this.code = code
        this.context = context
    }

    override fun getItemCount(): Int = 3 // There are only three tabs: info, data and source

    override fun createFragment(position: Int): Fragment {
        var reti: Fragment = this.getInfoFragment()
        when (position){
            0 -> reti = this.getInfoFragment()
            1 -> reti = this.getDataFragment()
            2 -> reti = this.getSourceFragment()
            else -> reti = this.getInfoFragment()
        }
        return reti
    }

    /**
     * Gets fragment with source of code.
     * @return Fragment with source of code.
     */
    private fun getSourceFragment(): DetailSourceFragment{
        var image: Bitmap = ImageUtils.getUnknownImage(this.context)
        var pos: RectF = RectF(0f, 0f, 0f, 0f)
        if (this.code != null){
            image = this.code.getImage() ?: ImageUtils.getUnknownImage(this.context)
            pos  = this.code.getPosition().toRectF()
        }
        else if (this.info != null){
            image = this.info.getImage() ?: ImageUtils.getUnknownImage(this.context)
            pos = this.info.getPosition().toRectF()
        }
        return DetailSourceFragment.newInstance(image, pos)
    }

    /**
     * Gets fragment with data of code.
     * @return Fragment with data of code.
     */
    private fun getDataFragment(): DetailDataFragment{
        var type: String = StringUtils.translate(this.context, DataType.UNKNOWN.toTranslatableString())
        var rawData: String = ""
        var dataFields: MutableMap<String, String> = mutableMapOf()
        if (this.info != null){
            type = StringUtils.translate(this.context, this.info.getDataType().toTranslatableString())
            rawData = this.info.getData()
            if (rawData.length == 0){
                rawData = this.info.getRawBase64()
            }
            if (this.info.hasDataFields()){
                for(field in this.info.getDataFields()){
                    var fieldVal: String? = this.info.getDataField(field)
                    if (fieldVal != null){
                        val fPhrase: String = "V_${fieldVal}"
                        val fTrans: String = StringUtils.translate(this.context, fPhrase)
                        if (fPhrase != fTrans){
                            fieldVal = fTrans
                        }
                    }
                    if (fieldVal != null && fieldVal.length > 0){
                        val phrase: String = "F_${field.uppercase()}"
                        val translation: String = StringUtils.translate(this.context, phrase)
                        if (translation == phrase){
                            dataFields.put(field.replaceFirstChar { c -> c.uppercase() }, fieldVal)
                        }
                        else{
                            dataFields.put(translation, fieldVal)
                        }
                    }
                }
            }
        }
        else if (this.code != null){
            type = StringUtils.translate(this.context, this.code.getDataType().toTranslatableString())
            rawData = this.code.getData()
            if (rawData.length == 0){
                rawData = this.code.getRawBase64()
            }
            if (this.code.hasDataFields()){
                for(field in this.code.getDataFields()){
                    var fieldVal: String? = this.code.getDataField(field)
                    if (fieldVal != null){
                        val fPhrase: String = "V_${fieldVal}"
                        val fTrans: String = StringUtils.translate(this.context, fPhrase)
                        if (fPhrase != fTrans){
                            fieldVal = fTrans
                        }
                    }
                    if (fieldVal != null && fieldVal.length > 0){
                        val phrase: String = "F_${field.uppercase()}"
                        val translation: String = StringUtils.translate(this.context, phrase)
                        if (translation == phrase){
                            dataFields.put(field.replaceFirstChar { c -> c.uppercase() }, fieldVal)
                        }
                        else{
                            dataFields.put(translation, fieldVal)
                        }
                    }
                }
            }
        }
        return DetailDataFragment.newInstance(rawData, type, dataFields)
    }

    /**
     * Gets fragment with information about code.
     * @return Fragment with information about code.
     */
    private fun getInfoFragment(): DetailInfoFragment{
        var name: String? = null
        var description: String? = null
        var folder: String? = null
        var type: String? = null
        var size: String? = null
        var created: String? = null
        var modified: String? = null
        if (this.code != null){
            name = this.code.getName()
            description = this.code.getDescription()
            folder = this.code.getFolder().getPath()
            type = StringUtils.translate(this.context, this.code.getCodeType().toTranslatableString())
            size = this.code.getSizeString()
            created = DateUtils.format(this.code.getCreationDate(), PreferencesSet.of(this.context))
            modified = DateUtils.format(this.code.getModificationDate(), PreferencesSet.of(this.context))
        }
        else if (this.info != null){
            type = StringUtils.translate(this.context, this.info.getCodeType().toTranslatableString())
            size = this.info.getSizeString()
            created = DateUtils.format(this.info.getCreationDate(), PreferencesSet.of(this.context))
        }
        return DetailInfoFragment.newInstance(
            name, description, folder, type, size, created, modified
        )
    }
}