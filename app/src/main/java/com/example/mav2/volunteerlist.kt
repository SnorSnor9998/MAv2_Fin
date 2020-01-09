package com.example.mav2


import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.mav2.`class`.fkVolunteer
import com.example.mav2.`class`.user
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_volunteerlist.*
import kotlinx.android.synthetic.main.user_list.*
import kotlinx.android.synthetic.main.user_list.view.*
import java.util.ArrayList





class volunteerlist : AppCompatActivity() {

    var fkVuserlist = ArrayList<String>()
    val adapter = GroupAdapter<GroupieViewHolder>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteerlist)
        supportActionBar?.title = "Volunteer List"


        val act_id = intent.getStringExtra(homeview.FKACT_KEY)

        textView31.text = act_id

        val ref = FirebaseDatabase.getInstance().getReference("/Volunteer").orderByChild("activityid").equalTo(act_id)
        ref.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    val fkV = it.getValue(fkVolunteer::class.java)
                    if(fkV!=null) {
                         fkVuserlist =fkV.userlist

                        callUser()
                    }else{
                        System.out.println("Fail")
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {}
        })





    }

    private fun callUser(){
        for (i in fkVuserlist ){

            val userref = FirebaseDatabase.getInstance().getReference("/Users").orderByChild("user_id").equalTo(i)
            userref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    p0.children.forEach {
                        val tmpuser = it.getValue(user::class.java)
                        if(tmpuser != null){
                            adapter.add(UserItem(tmpuser))

                        }

                    }

                    adapter.setOnItemClickListener { item, view ->
                        val Uitem = item as UserItem
                        makePhCall(Uitem.user.phnum)


                    }


                }

            })
        }


        userlistview.adapter = adapter
    }


    class UserItem(val user: user): Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            viewHolder.itemView.tv_username.text = user.name
            viewHolder.itemView.tv_useremail.text = user.email
            viewHolder.itemView.tv_usergender.text = user.gender
            viewHolder.itemView.tv_userphno.text = user.phnum





        }


        override fun getLayout(): Int {
            return R.layout.user_list
        }


    }

    private fun makePhCall(phno:String){
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phno")
        startActivity(intent)
    }





}
