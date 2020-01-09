package com.example.mav2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.mav2.`class`.fkVolunteer
import com.example.mav2.`class`.fkactivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_fkpage.*
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*


class fkactivity_page : AppCompatActivity() {

    companion object{
        val FKACT_KEY = ""
    }


    var act_id:String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fkpage)

        supportActionBar?.title = "Activity Detail"

        cna_address.setOnClickListener{
            val gmmIntentUri: Uri = Uri.parse("google.navigation:q="+cna_address.text.toString())
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            startActivity(mapIntent)
        }

        btnDelete.setOnClickListener { deleteActivity() }
        btnEdit.setOnClickListener { controller(1) }



        act_id = intent.getStringExtra(homeview.FKACT_KEY)

        if(act_id == null){
            act_id = intent.getStringExtra(addactivity.FKACT_KEY)
        }


        val ref = FirebaseDatabase.getInstance().getReference("/Activity").orderByChild("activity_id").equalTo(act_id)

        ref.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val fkact  = it.getValue(fkactivity::class.java)
                    if(fkact != null){


                        textView12.setText(fkact.activity_title)
                        Picasso.get().load(fkact.activity_imageUrl).into(cna_butt_uploadphoto)
                        cna_time_from.setText(fkact.activity_time_start)
                        cna_time_to.setText(fkact.activity_time_end)

                        var formate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                        val tmpdate = fkact.activity_date
                        butt_date.text = formate.format(tmpdate)

                        cna_address.setText(fkact.activity_address)

                        cna_desc.setText(fkact.activity_desc)

                        if(fkact.fkcat.dryfood)
                            fl_dryfood.isChecked = true
                        else
                            fl_dryfood.isVisible = false

                        if(fkact.fkcat.freshfood)
                            fl_fresh.isChecked = true
                        else
                            fl_fresh.isVisible = false

                        if(fkact.fkcat.frozenfood)
                            fl_frozen.isChecked = true
                        else
                            fl_frozen.isVisible = false

                        if(fkact.fkcat.fruitandvege)
                            fl_fandv.isChecked = true
                        else
                            fl_fandv.isVisible = false

                        if(fkact.fkcat.meat)
                            fl_meat.isChecked = true
                        else
                            fl_meat.isVisible = false

                        if(fkact.fkcat.refrige)
                            fl_refriger.isChecked = true
                        else
                            fl_refriger.isVisible = false

                        if(fkact.creator_id.equals(FirebaseAuth.getInstance().uid ?:"")){
                            if(fkact.volunteer){
                                btnList.isVisible = true
                            }
                            cna_button.isVisible = false
                            btnDelete.isVisible = true
                            btnEdit.isVisible = true
                            val now : Date = Calendar.getInstance().time
                            if(now == fkact.activity_date ||now.after(fkact.activity_date)){
                                btnEdit.isVisible = false
                            }

                        }


                       if(!fkact.volunteer){
                           cna_button.isVisible = false
                       }
                       else{

                            var userid = FirebaseAuth.getInstance().uid ?: ""
                            val voluref = FirebaseDatabase.getInstance().getReference("/Volunteer").orderByChild("activityid").equalTo(fkact.activity_id)
                            val dbup = FirebaseDatabase.getInstance().getReference("/Volunteer/${fkact.activity_id}")
                            voluref.addListenerForSingleValueEvent(object : ValueEventListener{

                                override fun onDataChange(p0: DataSnapshot) {
                                    p0.children.forEach{
                                        val fkvolu = it.getValue(fkVolunteer::class.java)
                                        if(fkvolu != null){
                                            val tmpstr : String = "("+fkvolu.space+"/"+fkvolu.size_of_volunteer+") Join Volunteer"
                                            val listStr : String = "("+fkvolu.space+"/"+fkvolu.size_of_volunteer+") Volunteer Count"
                                            cna_button.setText(tmpstr)
                                            btnList.setText(listStr)


                                            val i =0
                                            var found : Boolean = false

                                            fkvolu.userlist.forEach{
                                                if(it.contentEquals(userid))
                                                    found = true
                                            }


                                            if(found){
                                                cna_button.setText("Not Going")
                                                cna_button.setOnClickListener {

                                                    fkvolu.userlist.remove(userid)
                                                    fkvolu.space--

                                                    dbup.child("userlist")
                                                        .setValue(fkvolu.userlist)
                                                    dbup.child("space").setValue(fkvolu.space)

                                                    finish()
                                                    startActivity(getIntent())

                                                }
                                            }else {
                                                    cna_button.setOnClickListener {
                                                        if (fkvolu.space < fkvolu.size_of_volunteer) {
                                                        fkvolu.userlist.add(userid)
                                                        fkvolu.space++

                                                        val tmpstr: String =
                                                            "(" + fkvolu.space + "/" + fkvolu.size_of_volunteer + ") Join Volunteer"
                                                        cna_button.setText(tmpstr)

                                                        dbup.child("userlist")
                                                            .setValue(fkvolu.userlist)
                                                        dbup.child("space").setValue(fkvolu.space)
                                                            finish()
                                                            startActivity(getIntent())
                                                        } else {
                                                            toast("It's full")
                                                        }
                                                    }
                                            }
                                        }
                                    }
                                }
                                override fun onCancelled(p0: DatabaseError) {}
                            })
                        }
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
    private fun deleteActivity(){


        val ref = FirebaseDatabase.getInstance().reference
        val removeQuery = ref.child("Activity").orderByChild("activity_id").equalTo(act_id)

        removeQuery.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    it.ref.removeValue()
                    toast("Item is removed")
                }


            }

        })
        val intent = Intent(this,homev2Activity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        finish()
        startActivity(intent)


    }

    private fun startVolunteerList(){
        val ref = FirebaseDatabase.getInstance().reference
        val removeQuery = ref.child("Activity").orderByChild("activity_id").equalTo(act_id)

        removeQuery.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    it.ref.removeValue()
                    toast("Item is removed")
                }


            }

        })


    }

    private fun controller(option:Int){
        val ref = FirebaseDatabase.getInstance().reference
        val query = ref.child("Activity").orderByChild("activity_id").equalTo(act_id)


        query.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach{
                    val fkact  = it.getValue(fkactivity::class.java)
                    if(option==1){
                        funStart(fkact)

                    }

                }
            }

        })

    }


    private fun funStart(fkact: fkactivity?){
        val intent = Intent(this,EditActivity::class.java)
        intent.putExtra(FKACT_KEY,fkact?.activity_id)
        finish()
        startActivity(intent)
    }



}
