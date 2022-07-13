package com.mobio.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mobio.analytics.client.MobioSDKClient
import com.mobio.analytics.client.model.old.ScreenConfigObject

class KotlinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)

        val activityConfigObjectHashMap = HashMap<String, ScreenConfigObject>()
        activityConfigObjectHashMap["LoginActivity"] =
            ScreenConfigObject(
                "Login screen", "LoginActivity", intArrayOf(5, 10, 15),
                LoginActivity::class.java, true
            )
        activityConfigObjectHashMap["HomeActivity"] =
            ScreenConfigObject(
                "Home", "HomeActivity", intArrayOf(5, 10),
                HomeActivity::class.java, false
            )
        activityConfigObjectHashMap["SendMoneyInActivity"] =
            ScreenConfigObject(
                "Transfer", "SendMoneyInActivity", intArrayOf(10),
                SendMoneyInActivity::class.java, false
            )
        activityConfigObjectHashMap["TestActivity"] =
            ScreenConfigObject(
                "Test", "TestActivity", intArrayOf(10),
                TestActivity::class.java, false
            )

        val fragmentConfigObjectHashMap = HashMap<String, ScreenConfigObject>()
        fragmentConfigObjectHashMap["FragmentA"] = ScreenConfigObject(
            "A", "FragmentA", intArrayOf(5),
            FragmentA::class.java, false
        )
        fragmentConfigObjectHashMap["FragmentB"] = ScreenConfigObject(
            "B", "FragmentB", intArrayOf(10),
            FragmentB::class.java, false
        )


        val builder = MobioSDKClient.Builder()
            .withSdkCode("m-android-test-1")
            .withSdkSource("MobioBank")
            .withEnvironment("test")
            .withDomainURL("https://t1.mobio.vn/digienty/web/api/v1.1/")
            .withApiToken("Basic f5e27185-b53d-4aee-a9b7-e0579c24d29d")
            .withMerchantId("1b99bdcf-d582-4f49-9715-1b61dfff3924")
            .withApplication(this.application)
            .shouldTrackDeepLink(true)
            .shouldTrackScroll(false)
            .shouldTrackAppLifeCycle(true)
            .shouldTrackScreenLifeCycle(true)
            .withActivityMap(activityConfigObjectHashMap)
            .withFragmentMap(fragmentConfigObjectHashMap)
            .withIntervalSecond(10)
            .shouldRecordScreen(true)

        MobioSDKClient.setSingletonInstance(builder.build())
    }
}