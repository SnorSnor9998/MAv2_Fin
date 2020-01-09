package com.example.mav2


import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.mav2.`class`.fkVolunteer
import com.example.mav2.`class`.fkactivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_addactivity.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class addactivity : Fragment() {

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

    private var fkact = fkactivity()

    private lateinit var viewModel: fkactivity


    override fun onStart() {
        super.onStart()


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

        initPlaces()
        setUpAutoComplete()



        cna_time_from.setOnClickListener{
            val now = Calendar.getInstance()
            val timePicker = TimePickerDialog(this.requireContext(), TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
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
            val timePicker = TimePickerDialog(this.requireContext(), TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
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
            val datePicker = DatePickerDialog(this.requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

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


        cb_volu.setOnClickListener {
            if(cb_volu.isChecked){
                cna_numVolu.isEnabled = true
                cna_numVolu.hint = "Max. 30"
            }else if(!cb_volu.isChecked){
                cna_numVolu.isEnabled = false
                cna_numVolu.hint = ""
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

            if(cna_butt_uploadphoto.text.toString().equals("Upload A Photo")){
                cna_butt_uploadphoto.setTextColor(Color.RED)
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




            fkact.activity_title = cna_title.text.toString()
            fkact.activity_time_start = cna_time_from.text.toString()
            fkact.activity_time_end = cna_time_to.text.toString()
            fkact.activity_date = selectedDATE
            fkact.activity_address = cna_address.text.toString()
            fkact.activity_desc = cna_desc.text.toString()
            fkact.creator_id = FirebaseAuth.getInstance().uid.toString()

            fkact.fkcat.dryfood = fl_dryfood.isChecked
            fkact.fkcat.freshfood = fl_fresh.isChecked
            fkact.fkcat.frozenfood = fl_frozen.isChecked
            fkact.fkcat.fruitandvege = fl_fandv.isChecked
            fkact.fkcat.meat = fl_meat.isChecked
            fkact.fkcat.refrige = fl_refriger.isChecked



            if(validate == true){

                if(cb_volu.isChecked){
                    fkact.volunteer = true
                }


                uploadImageToFirebase()
                Toast.makeText(this.requireContext(),"Activity is created",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToFirebase (){
        if(selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            ref.downloadUrl.addOnSuccessListener {

                createFKActivity(it.toString())
                //fkact.activity_imageUrl = it.toString()
            }
        }


    }

    companion object{
        val FKACT_KEY = ""
    }
    private fun createFKActivity(imageUrl : String){
        val filename = UUID.randomUUID().toString()
        val dbact = FirebaseDatabase.getInstance().getReference("Activity/$filename")

        fkact.activity_imageUrl = imageUrl
        fkact.activity_id = filename

        dbact.setValue(fkact)


        if(cb_volu.isChecked) {
            val dbvolu = FirebaseDatabase.getInstance().getReference("Volunteer/$filename")
            val fkvolu = fkVolunteer()
            fkvolu.activityid = filename
            val tmp = Integer.parseInt(cna_numVolu.text.toString())
            fkvolu.size_of_volunteer = tmp
            dbvolu.setValue(fkvolu)
        }

        val intent = Intent(this.requireContext(),fkactivity_page::class.java)
        intent.putExtra(FKACT_KEY,fkact.activity_id)
        clear()
        startActivity(intent)
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver,selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            cna_butt_uploadphoto.setText("")
            cna_butt_uploadphoto.setBackgroundDrawable(bitmapDrawable)

        }

    }


    private fun clear(){
        cna_title.setText("")
        cna_time_from.setText(R.string.cna_timestart)
        cna_time_to.setText(R.string.cna_timeend)
        butt_date.setText(R.string.cna_date)
        cna_address.setText("")
        cna_desc.setText("")
        fl_dryfood.isChecked = false
        fl_fandv.isChecked = false
        fl_fresh.isChecked = false
        fl_frozen.isChecked = false
        fl_meat.isChecked = false
        fl_refriger.isChecked = false
        cna_butt_uploadphoto.setBackgroundDrawable(null)
        cna_butt_uploadphoto.setText(R.string.cna_upPhoto)

        cb_volu.isChecked = false
        cna_numVolu.setText("")
        tv_typefooderror.isVisible = false
        textView21.isVisible= false
        date_error.isVisible = false

        textView14.setTextColor(Color.BLACK)
        textView15.setTextColor(Color.BLACK)
        textView22.setTextColor(Color.BLACK)
        textView16.setTextColor(Color.BLACK)
        textView17.setTextColor(Color.BLACK)
        textView18.setTextColor(Color.BLACK)
        textView20.setTextColor(Color.BLACK)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_addactivity, container, false)
    }

    private fun initPlaces(){
        Places.initialize(this.requireContext(),getString(R.string.google_maps_key))
        placesClient = Places.createClient(this.requireContext())

    }

    private fun setUpAutoComplete(){


        val autoCompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment
        autoCompleteFragment.setPlaceFields(placeFields)

        autoCompleteFragment.view?.setBackgroundColor(Color.GRAY)

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
                Toast.makeText(activity,""+p0.address,Toast.LENGTH_SHORT).show()

            }

            override fun onError(p0: Status) {
                Toast.makeText(activity,""+p0.status,Toast.LENGTH_SHORT).show()
            }

        })
    }


}
