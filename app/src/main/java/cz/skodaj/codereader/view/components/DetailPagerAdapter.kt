package cz.skodaj.codereader.view.components

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Class which connects actual tab of detail view to the correct content of the tab.
 */
class DetailPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3 // There are only three tabs: info, data and source

    override fun createFragment(position: Int): Fragment {

    }
}