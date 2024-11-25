package cz.skodaj.codereader.view.components

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import cz.skodaj.codereader.model.Code
import cz.skodaj.codereader.model.CodeInfo
import cz.skodaj.codereader.model.preferences.PreferencesSet
import cz.skodaj.codereader.utils.DateUtils
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
        // TODO: Implement other two fragments
        return this.getInfoFragment()
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