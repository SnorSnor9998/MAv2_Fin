package com.example.mav2

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.widget.CompoundButton
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.mav2.`class`.fkVolunteer
import com.example.mav2.`class`.fkactivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit.butt_date
import kotlinx.android.synthetic.main.activity_edit.cb_volu
import kotlinx.android.synthetic.main.activity_edit.cna_address
import kotlinx.android.synthetic.main.activity_edit.cna_butt_uploadphoto
import kotlinx.android.synthetic.main.activity_edit.cna_button
import kotlinx.android.synthetic.main.activity_edit.cna_desc
import kotlinx.android.synthetic.main.activity_edit.cna_numVolu
import kotlinx.android.synthetic.main.activity_edit.cna_time_from
import kotlinx.android.synthetic.main.activity_edit.cna_time_to
import kotlinx.android.synthetic.main.activity_edit.cna_title
import kotlinx.android.synthetic.main.activity_edit.confirmLocation
import kotlinx.android.synthetic.main.activity_edit.date_error
import kotlinx.android.synthetic.main.activity_edit.fl_dryfood
import kotlinx.android.synthetic.main.activity_edit.fl_fandv
import kotlinx.android.synthetic.main.activity_edit.fl_fresh
import kotlinx.android.synthetic.main.activity_edit.fl_frozen
import kotlinx.android.synthetic.main.activity_edit.fl_meat
import kotlinx.android.synthetic.main.activity_edit.fl_refriger
import kotlinx.android.synthetic.main.activity_edit.mainAdd
import kotlinx.android.synthetic.main.activity_edit.searchLayout
import kotlinx.android.synthetic.main.activity_edit.textView14
import kotlinx.android.synthetic.main.activity_edit.textView15
import kotlinx.android.synthetic.main.activity_edit.textView16
import kotlinx.android.synthetic.main.activity_edit.textView17
import kotlinx.android.synthetic.main.activity_edit.textView18
import kotlinx.android.synthetic.main.activity_edit.textView20
import kotlinx.android.synthetic.main.activity_edit.textView21
import kotlinx.android.synthetic.main.activity_edit.textView22
import kotlinx.android.synthetic.main.activity_edit.tv_typefooderror
import kotlinx.android.synthetic.main.fragment_addactivity.*
import java.lang.Exception


import java.text.SimpleDateFormat
import java.util.*

class EditActivity : AppCompatActivity() {

    private var updateActivicty = fkactivity()
    var formate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    var timeFormat = SimpleDateFormat("HH:mm", Locale.US)

    var selectedDATE : Date? = null
    var selectedPhotoUri : Uri? = null



    private var placeFields = Arrays.asList(
        Place.Field.ID,
        Place.Field.NAME,
        Place.Field.ADDRESS,
        Place.Field.LAT_LNG)
    private var placesClient: PlacesClient? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        initPlaces()
        setUpAutoComplete()

        cna_address.setOnClickListener{
            searchLayout.isVisible = true
            mainAdd.alpha = 0.1f
        }
        confirmLocation.setOnClickListener{
            searchLayout.isVisible = false
            mainAdd.alpha = 1f

        }

        cna_butt_uploadphoto.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }

        cna_time_from.setOnClickListener{
            val now = Calendar.getInstance()
            val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY,hourOfDay)
                selectedTime.set(Calendar.MINUTE,minute)

                val time = timeFormat.format(selectedTime.time)
                cna_time_from.setText(time)
            },
                now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),false)
            timePicker.show()
        }

        cna_time_to.setOnClickListener {
            val now = Calendar.getInstance()
            val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY,hourOfDay)
                selectedTime.set(Calendar.MINUTE,minute)

                val time = timeFormat.format(selectedTime.time)
                cna_time_to.setText(time)
            },
                now.get(Calendar.HOUR_OF_DAY),now.get(Calendar.MINUTE),false)
            timePicker.show()
        }

        butt_date.setOnClickListener {
            val now = Calendar.getInstance()
            val selectedDate = Calendar.getInstance()
            val datePicker = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                selectedDate.set(Calendar.YEAR,year)
                selectedDate.set(Calendar.MONTH,month)
                selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)

                val date = formate.format(selectedDate.time)
                selectedDATE = selectedDate.time
                butt_date.setText(date.toString())
            },
                now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
            datePicker.show()

        }
        supportActionBar?.title = "Edit Activity"
        val act_id = intent.getStringExtra(fkactivity_page.FKACT_KEY)
        initValue(act_id)
        cb_volu.setOnCheckedChangeListener{ compoundButton: CompoundButton, b: Boolean ->
            if(cb_volu.isChecked){
                cna_numVolu.isEnabled = true
                cna_numVolu.hint = "Max. 30"
            }
            else{
                cna_numVolu.setText("")
                cna_numVolu.isEnabled = false
            }

        }


        cna_button.setOnClickListener {



            var validate : Boolean = true

            if(butt_date.text.toString().equals("Date Picker")||butt_date.text.toString().equals("Date is required")){
                butt_date.setText("Date is required")

                textView22.setTextColor(Color.RED)
                validate = false
            }

            if(cna_time_from.text.toString().equals("Start")||cna_time_from.text.toString().equals("Required")){
                cna_time_from.setText(R.string.validate_required)
                textView15.setTextColor(Color.RED)
                validate = false
            }

            if(cna_time_to.text.toString().equals("End")||cna_time_to.text.toString().equals("Required")){
                cna_time_to.setText(R.string.validate_required)
                textView15.setTextColor(Color.RED)
                validate = false
            }

            if(cna_title.text.isEmpty()){
                cna_title.setHint(R.string.validate_required)
                textView14.setTextColor(Color.RED)
                validate = false
            }

            if(cna_address.text.isEmpty()){
                cna_address.setHint(R.string.validate_required)
                textView16.setTextColor(Color.RED)
                validate = false
            }

            if(cna_desc.text.isEmpty()){
                cna_desc.setHint(R.string.validate_required)
                textView17.setTextColor(Color.RED)
                validate = false
            }

            if(!(fl_dryfood.isChecked||fl_fandv.isChecked||fl_fresh.isChecked||fl_frozen.isChecked||fl_meat.isChecked||fl_refriger.isChecked)){
                tv_typefooderror.setText("(Pick At Least One)")
                tv_typefooderror.setTextColor(Color.RED)
                tv_typefooderror.isVisible = true
                textView18.setTextColor(Color.RED)
                validate = false
            }


            if(cb_volu.isChecked ){


                if(TextUtils.isEmpty(cna_numVolu.text)){
                    textView20.setTextColor(Color.RED)
                    cna_numVolu.hint = "Required"
                    validate = false

                }else{
                    val tmp = Integer.parseInt(cna_numVolu.text.toString())
                    if(tmp<0 || tmp >30){
                        textView21.isVisible =true
                        textView21.setText("Range 1~30")
                        textView20.setTextColor(Color.RED)
                        validate = false
                    }
                }
            }

            val now : Date = Calendar.getInstance().time
            if(now.after(selectedDATE)){
                validate = false
                date_error.setText("Must after\n Current Date")
                date_error.isVisible = true
            }

            butt_date.setOnClickListener {
                val now = Calendar.getInstance()
                val selectedDate = Calendar.getInstance()
                val datePicker = DatePickerDialog(this.baseContext, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    selectedDate.set(Calendar.YEAR,year)
                    selectedDate.set(Calendar.MONTH,month)
                    selectedDate.set(Calendar.DAY_OF_MONTH,dayOfMonth)

                    val date = formate.format(selectedDate.time)
                    selectedDATE = selectedDate.time
                    butt_date.setText(date.toString())
                },
                    now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH))
                datePicker.show()

            }




            updateActivicty.activity_title = cna_title.text.toString()
            updateActivicty.activity_time_start = cna_time_from.text.toString()
            updateActivicty.activity_time_end = cna_time_to.text.toString()
            updateActivicty.activity_date = selectedDATE
            updateActivicty.activity_address = cna_address.text.toString()
            updateActivicty.activity_desc = cna_desc.text.toString()
            updateActivicty.creator_id = FirebaseAuth.getInstance().uid.toString()

            updateActivicty.fkcat.dryfood = fl_dryfood.isChecked
            updateActivicty.fkcat.freshfood = fl_fresh.isChecked
            updateActivicty.fkcat.frozenfood = fl_frozen.isChecked
            updateActivicty.fkcat.fruitandvege = fl_fandv.isChecked
            updateActivicty.fkcat.meat = fl_meat.isChecked
            updateActivicty.fkcat.refrige = fl_refriger.isChecked



            if(validate == true){

                if(cb_volu.isChecked){
                    updateActivicty.volunteer = true
                }


                uploadFirebaseImage()

            }
        }


    }

    private fun uploadFirebaseImage (){
        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {

                updateFKActivity(it.toString())
                //fkact.activity_imageUrl = it.toString()
            }
        }


    }

    private fun updateFKActivity(imageUrl : String){

        val dbact = FirebaseDatabase.getInstance().reference
        updateActivicty.activity_imageUrl = imageUrl



        try{
            dbact.child("Activity").child(updateActivicty.activity_id).child("activity_address").setValue(updateActivicty.activity_address)
            dbact.child("Activity").child(updateActivicty.activity_id).child("activity_date").setValue(updateActivicty.activity_date)
            dbact.child("Activity").child(updateActivicty.activity_id).child("activity_desc").setValue(updateActivicty.activity_desc)
            dbact.child("Activity").child(updateActivicty.activity_id).child("activity_imageUrl").setValue(updateActivicty.activity_imageUrl)
            dbact.child("Activity").child(updateActivicty.activity_id).child("activity_time_end").setValue(updateActivicty.activity_time_end)
            dbact.child("Activity").child(updateActivicty.activity_id).child("activity_time_start").setValue(updateActivicty.activity_time_start)
            dbact.child("Activity").child(updateActivicty.activity_id).child("activity_title").setValue(updateActivicty.activity_title)
            dbact.child("Activity").child(updateActivicty.activity_id).child("fkcat").setValue(updateActivicty.fkcat)
            dbact.child("Activity").child(updateActivicty.activity_id).child("volunteer").setValue(updateActivicty.volunteer)
//            if(cb_volu.isChecked){
//                dbact.child("Volunteer").child(updateActivicty.activity_id).child("size_of_volunteer").setValue(Integer.parseInt(cna_numVolu.text.toString()))
//            }


            Toast.makeText(this,"Acitivity updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(this.baseContext,fkactivity_page::class.java)
            intent.putExtra(addactivity.FKACT_KEY,updateActivicty.activity_id)
            startActivity(intent)
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this,"Update failed", Toast.LENGTH_SHORT).show()
        }






    }



    private fun initValue(act_id:String){

        val ref = FirebaseDatabase.getInstance().getReference("/Activity").orderByChild("activity_id").equalTo(act_id)
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach(){

                    val fkact = it.getValue(fkactivity::class.java)
                    if(fkact!=null){
                        updateActivicty.activity_id = fkact.activity_id
                        Picasso.get().load(fkact.activity_imageUrl).into(cna_butt_uploadphoto)
                        cna_title.setText(fkact.activity_title)
                        cna_time_from.setText(fkact.activity_time_start)
                        cna_time_to.setText(fkact.activity_time_end)
                        var formate = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                        val tmpdate = fkact.activity_date
                        butt_date.setText(formate.format(tmpdate))
                        cna_address.setText(fkact.activity_address)
                        cna_desc.setText(fkact.activity_desc)
                        cna_numVolu.isEnabled = false
                        cb_volu.isEnabled = false

                        if(fkact.fkcat.dryfood)
                            fl_dryfood.isChecked = true


                        if(fkact.fkcat.freshfood)
                            fl_fresh.isChecked = true


                        if(fkact.fkcat.frozenfood)
                            fl_frozen.isChecked = true


                        if(fkact.fkcat.fruitandvege)
                            fl_fandv.isChecked = true


                        if(fkact.fkcat.meat)
                            fl_meat.isChecked = true


                        if(fkact.fkcat.refrige)
                            fl_refriger.isChecked = true

                        if(fkact.volunteer){
                            cb_volu.isChecked =true

                        }
                    }
                }

            }

        })

    }

    private fun initPlaces(){
        Places.initialize(this.baseContext,getString(R.string.google_maps_key))
        placesClient = Places.createClient(this.baseContext)

    }

    private fun setUpAutoComplete(){


        val autoCompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autoCompleteFragment.setPlaceFields(placeFields)

        autoCompleteFragment.view?.setBackgroundColor(Color.BLACK)

        autoCompleteFragment.setCountry("MY")

        autoCompleteFragment.setLocationBias(
            RectangularBounds.newInstance(
                LatLngBounds(
                    LatLng(3.037542, 101.598409),
                    LatLng(3.236713, 101.785864)
                )
            ))



        autoCompleteFragment.setOnPlaceSelectedListener(object: PlaceSelectionListener {
            override fun onPlaceSelected(p0: Place) {
                cna_address.setText(p0.address)
                Toast.makeText(this@EditActivity,""+p0.address,Toast.LENGTH_SHORT).show()

            }

            override fun onError(p0: Status) {
                Toast.makeText(this@EditActivity,""+p0.status,Toast.LENGTH_SHORT).show()
            }

        })
    }



   private fun getVoluSize(act_id:String){
       val voluref = FirebaseDatabase.getInstance().getReference("/Volunteer").orderByChild("activityid").equalTo(act_id)
       voluref.addListenerForSingleValueEvent(object:ValueEventListener{
           override fun onCancelled(p0: DatabaseError) {

           }
           override fun onDataChange(p0: DataSnapshot) {
               p0.children.forEach(){
                   val fkvolu = it.getValue(fkVolunteer::class.java)
                   if(fkvolu!=null){
                       cna_numVolu.isEnabled
                       cna_numVolu.setText(fkvolu.size_of_volunteer)
                   }
               }

           }

       })

   }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this!!.contentResolver,selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            Picasso.get().load(selectedPhotoUri).into(cna_butt_uploadphoto)


        }

    }


}
