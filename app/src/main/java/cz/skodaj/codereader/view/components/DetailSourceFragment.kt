package cz.skodaj.codereader.view.components

import android.graphics.Bitmap
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import cz.skodaj.codereader.databinding.FragmentDetailSourceBinding
import cz.skodaj.codereader.utils.ImageUtils

private const val ARG_IMAGE = "IMG"
private const val ARG_TOP = "TOP"
private const val ARG_LEFT = "LEFT"
private const val ARG_BOTTOM = "BOTTOM"
private const val ARG_RIGHT = "RIGHT"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailSourceFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailSourceFragment : Fragment() {

    /**
     * Image where code is located.
     */
    private var image: Bitmap? = null

    /**
     * Position of code in the image.
     */
    private var position: RectF = RectF(0f, 0f, 0f, 0f)

    /**
     * Holder for binding to view itself.
     */
    private var _viewBinding: FragmentDetailSourceBinding? = null

    /**
     * Binding to view itself.
     */
    private val viewBinding: FragmentDetailSourceBinding get() = this._viewBinding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       this._viewBinding = FragmentDetailSourceBinding.inflate(inflater)
        return this.viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            if (it.getString(ARG_IMAGE) != null){
                this.image = ImageUtils.toBitmap(it.getString(ARG_IMAGE)!!) ?: ImageUtils.getUnknownImage(this.requireContext())
            }
            this.position = RectF(
                (it.getString(ARG_LEFT) ?: "0").toFloat(),
                (it.getString(ARG_TOP) ?: "0").toFloat(),
                (it.getString(ARG_RIGHT) ?: "0").toFloat(),
                (it.getString(ARG_BOTTOM) ?: "0").toFloat()
            )
            this.initView()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        this._viewBinding = null
    }

    /**
     * Initializes view with actual data.
     */
    private fun initView(){
        val bitmap: Bitmap = this.image ?: ImageUtils.getUnknownImage(this.requireContext())
        val view: RectangleImageView = this.viewBinding.detailImageViewSource
        view.post {
            view.setImage(bitmap)
            view.setRectangle(this.position)
        }
    }


    companion object {

        /**
         * Creates new fragment with source of code.
         * @param image Image in which is code located.
         * @param position Position of code in image.
         */
        @JvmStatic
        fun newInstance(image: Bitmap, position: RectF) =
            DetailSourceFragment().apply {
                val imageString: String? = ImageUtils.toBase64(image)
                arguments = Bundle().apply {
                    putString(ARG_IMAGE, imageString)
                    putString(ARG_TOP, position.top.toString())
                    putString(ARG_RIGHT, position.right.toString())
                    putString(ARG_BOTTOM, position.bottom.toString())
                    putString(ARG_LEFT, position.left.toString())
                }
            }
    }
}