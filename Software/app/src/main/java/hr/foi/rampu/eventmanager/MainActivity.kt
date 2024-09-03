package hr.foi.rampu.eventmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import hr.foi.rampu.eventmanager.adapters.MainPagerAdapter
import hr.foi.rampu.eventmanager.entity.Event
import hr.foi.rampu.eventmanager.fragments.EventFragment
import hr.foi.rampu.eventmanager.fragments.LocationFragment
import hr.foi.rampu.eventmanager.fragments.ProfileFragment
import hr.foi.rampu.eventmanager.fragments.TicketsFragment
import java.util.Date

class MainActivity : AppCompatActivity() {
    lateinit var tabLayout: TabLayout
    lateinit var viewPager2: ViewPager2
    lateinit var mainPagerAdapter: MainPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeMainPagerAdapter()

        connectViewPagerWithTabLayout()
    }

    private fun caller() {

    }

    private fun connectViewPagerWithTabLayout() {
        tabLayout = findViewById(R.id.tabs)
        viewPager2 = findViewById(R.id.viewpager)

        viewPager2.adapter = mainPagerAdapter

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewPager2.isUserInputEnabled = position != 3
            }
        })

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.setText(mainPagerAdapter.fragmentItems[position].titleRes)
            tab.setIcon(mainPagerAdapter.fragmentItems[position].iconRes)
        }.attach()
    }





    private fun initializeMainPagerAdapter() {
        mainPagerAdapter = MainPagerAdapter(supportFragmentManager, lifecycle)
        fillAdapterWithFragments()
    }

    private fun fillAdapterWithFragments() {
        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.Event_Fragment,
                R.drawable.baseline_home_24,
                EventFragment::class
            )
        )
        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.Tickets_Fragment,
                R.drawable.baseline_airplane_ticket_24,
                TicketsFragment::class
            )
        )

        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.Profile_Fragment,
                R.drawable.baseline_person_24,
                ProfileFragment::class
            )
        )

        mainPagerAdapter.addFragment(
            MainPagerAdapter.FragmentItem(
                R.string.Location_Fragment,
                R.drawable.baseline_location_on_24,
                LocationFragment::class
            )
        )




    }
}