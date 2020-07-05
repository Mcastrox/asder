package com.example.tutoapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.tutoapp.adapter.GvAdapter
import com.example.tutoapp.components.ExpandableHeightGridView
import com.example.tutoapp.models.Disciplina
import com.example.tutoapp.models.Model
import com.example.tutoapp.models.RatingModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_pseleccionado.*


class PseleccionadoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var nombre_estudiante: String
    private lateinit var apellido_estudiante: String
    private lateinit var telefono_estudiante: String
    private lateinit var correo_estudiante: String
    private lateinit var gvDisciplinas: ExpandableHeightGridView
    private var value: Float = 0.0f
    private lateinit var rating1: RatingBar
    private lateinit var myRate : LinearLayout
    private var avg : Float = 0.0f

    private var gv_adapter: GvAdapter? = null

    private lateinit var url: String
    var mStorageRef: StorageReference? = null
    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pseleccionado)

        gvDisciplinas = findViewById(R.id.gv_disciplina)
        auth = FirebaseAuth.getInstance()
        val user: FirebaseUser? = auth.currentUser
        rating1 = findViewById(R.id.rating1)
        myRate = findViewById(R.id.myRate)
        var ModelDialog = AlertDialog.Builder(this)
        val DialogView = layoutInflater.inflate(R.layout.rating_alert, null)
        val btnCancel = DialogView.findViewById<TextView>(R.id.action_cancelar)
        val btnRate = DialogView.findViewById<TextView>(R.id.action_calificar)
        val rating = DialogView.findViewById<RatingBar>(R.id.rating)

        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitle("")
        setSupportActionBar(toolbar)

        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        ModelDialog.setView(DialogView)


        val tutor = intent.getSerializableExtra("tutor") as Model

        nombre_tutor.text = tutor.name
        AverageRating( tutor.ratings )
        lastname_tutor.text = tutor.lastname
        location_tutor.text = tutor.location
        ocupation_tutor.text = tutor.ocupacion
        nivel_tutor.text = tutor.nivel
        correo_tutor.text = tutor.correo
        descripcion_tutor.text = tutor.descripcion
        tutor.listaDisciplina?.let { gridViewCreated(it) }


        var alert_dialog = ModelDialog.create()

        rating.setOnRatingBarChangeListener(object : RatingBar.OnRatingBarChangeListener {
            override fun onRatingChanged(ratingBar: RatingBar?, rating: Float, b: Boolean) {
                value = rating
            }
        })

        myRate.setOnClickListener {
            alert_dialog.show()

            btnCancel.setOnClickListener {
                alert_dialog.dismiss()
            }

            btnRate.setOnClickListener {
                val tutorRef = FirebaseDatabase.getInstance().getReference("Users").child(tutor.id)

                Toast.makeText(this, "${value}", Toast.LENGTH_LONG).show()

                tutor.ratings?.add(RatingModel(value.toString()))

                tutorRef.child("ratings").setValue(tutor.ratings)
                alert_dialog.dismiss()
            }

        }

        Picasso.get().load(tutor.ruta).into(image_tutor)
        Picasso.get().load(tutor.ruta).into(user_tutor)

        Picasso.get()
            .load(tutor.ruta)
            .transform(BlurTransformation(this, 25, 3))
            .into(user_tutor)

        mStorageRef = FirebaseStorage.getInstance().reference
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        val userRef = ref.child(user?.uid!!)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                nombre_estudiante = dataSnapshot.child("Name").value as String
                apellido_estudiante = dataSnapshot.child("lastName").value as String
                telefono_estudiante=dataSnapshot.child("telefono").value as String
                correo_estudiante=dataSnapshot.child("correo").value as String
                url = dataSnapshot.child("urlImage").value as String

            }

        })


        action_contactar.setOnClickListener {
            val seleccion = intent?.getStringExtra("seleccion")
            val intent = Intent(this, SolicitudActivity::class.java)
            intent.putExtra("idEstudiante", user?.uid)
            intent.putExtra("nombre_estudiante", nombre_estudiante)
            intent.putExtra("apellido_estudiante", apellido_estudiante)
            intent.putExtra("foto_estudiante", url)
            intent.putExtra("idTutor", tutor.id)
            intent.putExtra("nombre_tutor",tutor.name)
            intent.putExtra("apellido_tutor", tutor.lastname)
            intent.putExtra("foto_tutor", tutor.ruta)
            intent.putExtra("correo_estudiante",correo_estudiante)
            intent.putExtra("telefono_estudiante",telefono_estudiante)
            intent.putExtra("seleccion", seleccion)
            //startActivity(Intent(this,SolicitudActivity::class.java))
            startActivity(intent)
        }

    }

    private fun gridViewCreated(listaDisciplina: ArrayList<Disciplina>){
        gv_adapter =
            listaDisciplina?.let { GvAdapter(this, it) }

        gvDisciplinas?.adapter = gv_adapter
        gvDisciplinas.isExpanded = true
    }

    private fun AverageRating(ratings: ArrayList<RatingModel>?){

        if (ratings != null) {
            for( rating in ratings){
                avg += rating.value!!.toFloat() / ratings.size
            }
        }

        rating1.rating = avg

    }

}
