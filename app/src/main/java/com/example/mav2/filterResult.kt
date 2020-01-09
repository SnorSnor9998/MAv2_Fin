package com.example.mav2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mav2.`class`.fkactivity
import com.example.mav2.`class`.fkcategory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_filter_result.*
import kotlinx.android.synthetic.main.fkactivity_row_view.view.*
import java.text.SimpleDateFormat
import java.util.*

class filterResult : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_result)



        fetchFKactivity()
    }

    companion object{
        val FKACT_KEY = ""
    }

    private fun fetchFKactivity(){
        val re = intent.getSerializableExtra(filter.KEY) as fkcategory
        val ref = FirebaseDatabase.getInstance().getReference("/Activity").orderByChild("activity_date/time")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()


                p0.children.forEach {
                    val fkact  = it.getValue(fkactivity::class.java)

                    if(fkact != null){

                            if((fkact.fkcat.freshfood && re.freshfood) || (fkact.fkcat.dryfood&& re.dryfood) || (fkact.fkcat.frozenfood && re.frozenfood) || (fkact.fkcat.fruitandvege && re.fruitandvege)
                                || (fkact.fkcat.meat && re.meat)||(fkact.fkcat.refrige && re.refrige))
                                adapter.add(FKItem(fkact))
                    }

                }

                adapter.setOnItemClickListener{ item, view ->
                    val fkactItem = item as FKItem
                    val intent = Intent(view.context,fkactivity_page::class.java)
                    intent.putExtra(FKACT_KEY,fkactItem.fkact.activity_id)
                    startActivity(intent)
                    finish()

                }
                act_recyc.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    class FKItem(val fkact : fkactivity): Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            viewHolder.itemView.fkact_title_view.text = fkact.activity_title

            var formate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
            val tmpdate = fkact.activity_date
            viewHolder.itemView.fkact_date_view.text = formate.format(tmpdate)

            val time:String = fkact.activity_time_start + " - " + fkact.activity_time_end
            viewHolder.itemView.fkact_time_view.text = time

            Picasso.get().load(fkact.activity_imageUrl).into(viewHolder.itemView.fkact_image_view)
        }

        override fun getLayout(): Int {
            return R.layout.fkactivity_row_view
        }
    }


}
