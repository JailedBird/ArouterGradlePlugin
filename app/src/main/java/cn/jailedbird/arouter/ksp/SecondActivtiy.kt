package cn.jailedbird.arouter.ksp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter

@Route(path = "/app/SecondActivtiy")
class SecondActivtiy : AppCompatActivity() {
    @Autowired
    var list = mutableListOf<String>()

    @Autowired(required = true)
    var arrayList = arrayListOf<String>()

    @Autowired
    var array = LinkedHashMap<String, ArrayList<Int>>()

    @Autowired
    var hasSet = HashSet<LinkedHashMap<String, ArrayList<Int>>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ARouter.getInstance().inject(this)
        setContentView(R.layout.second_main)
        Log.d("TAG", arrayList.toString())
        Log.d("TAG", list.toString())

    }
}