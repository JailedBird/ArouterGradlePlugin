package cn.jailedbird.arouter.ksp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

@Route(path = "/app/MainActivity")
class MainActivity : AppCompatActivity() {

    @Autowired
    var list = mutableListOf<String>()

    @Autowired
    var arrayList = arrayListOf<String>()

    @Autowired
    var array = LinkedHashMap<String, ArrayList<Int>>()

    @Autowired
    var hasSet = HashSet<LinkedHashMap<String, ArrayList<Int>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.jump).setOnClickListener {
            ARouter.getInstance().build("/app/SecondActivtiy")
                .withObject("arrayList", arrayListOf("1", "2", "3"))
                .withObject("list", mutableListOf("1", "2", "3", "4"))
                .navigation(this)
        }
    }
}