package com.example.mav2.`class`

import android.annotation.SuppressLint
import android.os.Parcel

import androidx.lifecycle.ViewModel
import java.util.*


class fkactivity : ViewModel() {

    var activity_id : String = ""
    var activity_title : String = ""
    var activity_time_start : String = ""
    var activity_time_end : String = ""
        //var activity_date : String =""
    var activity_date : Date? = null

    var activity_address :String =""
    var activity_desc : String =""

    var activity_imageUrl : String= ""

    var creator_id : String =""

    var fkcat = fkcategory()

    var volunteer : Boolean = false

}