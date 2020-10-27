
import MainLayout
import ResultLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class PageAdapter(fm: FragmentManager?, private val numOfTabs: Int) : FragmentPagerAdapter(fm!!) {
    override fun getItem(i: Int): Fragment {
        return when (i) {
            0 -> MainLayout()
            1 -> ResultLayout()
            else -> null
        }
    }

    override fun getCount(): Int {
        return numOfTabs
    }

}