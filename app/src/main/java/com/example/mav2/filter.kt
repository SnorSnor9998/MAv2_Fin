package com.example.mav2

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mav2.`class`.fkcategory
import kotlinx.android.synthetic.main.activity_filter.*
import java.io.Serializable



class filter : AppCompatActivity() {

    companion object{
        val KEY = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        fl_apply.setOnClickListener {

            var filterresult = fkcategory()

            if(fl_dryfood.isChecked)
                filterresult.dryfood = true

            if(fl_fandv.isChecked)
                filterresult.fruitandvege = true

            if(fl_fresh.isChecked)
                filterresult.freshfood = true

            if(fl_frozen.isChecked)
                filterresult.frozenfood = true

            if(fl_refriger.isChecked)
                filterresult.refrige = true

            if(fl_meat.isChecked)
                filterresult.meat = true



            val intent = Intent(this,filterResult::class.java)
            intent.putExtra(KEY,filterresult)
            finish()
            startActivity(intent)


        }

    }
}



//class Object : Serializable {
//    var cat : fkcategory? = null
//}
