package cz.skodaj.codereader.view.components

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cz.skodaj.codereader.databinding.ActivityDetailBinding
import cz.skodaj.codereader.databinding.FragmentDetailInfoBinding

private const val ARG_NAME        = "name"
private const val ARG_DESCRIPTION = "description"
private const val ARG_FOLDER      = "folder"
private const val ARG_CODETYPE    = "codetype"
private const val ARG_SIZE        = "size"
private const val ARG_CREATED     = "created"
private const val ARG_MODIFIED    = "modified"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailInfoFragment : Fragment() {

    /**
     * Displayed name of code.
     */
    private var name: String? = null

    /**
     * Displayed description of code.
     */
    private var description: String? = null

    /**
     * Displayed folder of code.
     */
    private var folder: String? = null

    /**
     * Displayed type of code.
     */
    private var codeType: String? = null

    /**
     * Displayed size of code.
     */
    private var size: String? = null

    /**
     * Displayed date of creation of code.
     */
    private var created: String? = null

    /**
     * Displayed date of last modification of code.
     */
    private var modified: String? = null

    /**
     * Holder for binding to view itself.
     */
    private var _viewBinding: FragmentDetailInfoBinding? = null

    /**
     * Binding to view itself.
     */
    private val viewBinding: FragmentDetailInfoBinding get() = this._viewBinding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        this._viewBinding = FragmentDetailInfoBinding.inflate(this.layoutInflater)
        return this.viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            this.name = it.getString(ARG_NAME)
            this.description = it.getString(ARG_DESCRIPTION)
            this.folder = it.getString(ARG_FOLDER)
            this.codeType = it.getString(ARG_CODETYPE)
            this.size = it.getString(ARG_SIZE)
            this.created = it.getString(ARG_CREATED)
            this.modified = it.getString(ARG_MODIFIED)
        }
        this.initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._viewBinding = null
    }

    /**
     * Initializes view according to the passed arguments.
     */
    private fun initView(){
        this.hideAll()
        if (this.name != null){
            this.viewBinding.detailTextViewNameHeader.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewName.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewName.setText(this.name)
        }
        if (this.description != null){
            this.viewBinding.detailTextViewDescriptionHeader.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewDescription.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewDescription.setText(this.description)
        }
        if (this.folder != null){
            this.viewBinding.detailTextViewFolderHeader.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewFolder.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewFolder.setText(this.folder)
        }
        if (this.codeType != null){
            this.viewBinding.detailTextViewTypeHeader.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewType.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewType.setText(this.codeType)
        }
        if (this.size != null){
            this.viewBinding.detailTextViewDataSizeHeader.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewDataSize.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewDataSize.setText(this.size)
        }
        if (this.created != null){
            this.viewBinding.detailTextViewCreatedHeader.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewCreated.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewCreated.setText(this.created)
        }
        if (this.modified != null){
            this.viewBinding.detailTextViewModifiedHeader.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewModified.setVisibility(View.VISIBLE)
            this.viewBinding.detailTextViewModified.setText(this.modified)
        }
    }

    /**
     * Hides all fields.
     */
    private fun hideAll(){
        this.viewBinding.detailTextViewName.setVisibility(View.GONE)
        this.viewBinding.detailTextViewNameHeader.setVisibility(View.GONE)
        this.viewBinding.detailTextViewDescription.setVisibility(View.GONE)
        this.viewBinding.detailTextViewDescriptionHeader.setVisibility(View.GONE)
        this.viewBinding.detailTextViewFolder.setVisibility(View.GONE)
        this.viewBinding.detailTextViewFolderHeader.setVisibility(View.GONE)
        this.viewBinding.detailTextViewType.setVisibility(View.GONE)
        this.viewBinding.detailTextViewTypeHeader.setVisibility(View.GONE)
        this.viewBinding.detailTextViewDataSize.setVisibility(View.GONE)
        this.viewBinding.detailTextViewDataSizeHeader.setVisibility(View.GONE)
        this.viewBinding.detailTextViewCreated.setVisibility(View.GONE)
        this.viewBinding.detailTextViewCreatedHeader.setVisibility(View.GONE)
        this.viewBinding.detailTextViewModified.setVisibility(View.GONE)
        this.viewBinding.detailTextViewModifiedHeader.setVisibility(View.GONE)
    }

    companion object {
        /**
         * Creates new fragment with code detail information.
         * Note: if any of given parameters is NULL, given information will not be displayed (this
         * can be used to select which information should be displayed).
         * @param name Displayed name of code.
         * @param description Displayed description of code.
         * @param folder Displayed folder of code.
         * @param codeType Displayed type of code.
         * @param size Displayed size of code.
         * @param creationDate Displayed date of creation of code.
         * @param modificationDate Displayed date of last modification of code.
         * @return Newly created fragment with code detail information.
         */
        @JvmStatic
        fun newInstance(
            name: String?,
            description: String?,
            folder: String?,
            codeType: String?,
            size: String?,
            creationDate: String?,
            modificationDate: String?
        ) =
            DetailInfoFragment().apply {
                arguments = Bundle().apply {
                    if (name != null)             putString(ARG_NAME,        name)
                    if (description != null)      putString(ARG_DESCRIPTION, description)
                    if (folder != null)           putString(ARG_FOLDER,      folder)
                    if (codeType != null)         putString(ARG_CODETYPE,    codeType)
                    if (size != null)             putString(ARG_SIZE,        size)
                    if (creationDate != null)     putString(ARG_CREATED,     creationDate)
                    if (modificationDate != null) putString(ARG_MODIFIED,    modificationDate)
                }
            }
    }
}