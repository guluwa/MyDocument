package android.ocr.bmk.ui.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by guluwa on 2018/3/14.
 */

class ViewPagerAdapter(fm: FragmentManager, private val mFragmentList: List<Fragment>, private val mNameList: List<String>) :
        FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mNameList[position]
    }
}