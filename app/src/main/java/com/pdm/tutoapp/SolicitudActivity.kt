package com.pdm.tutoapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.pdm.tutoapp.models.Disciplina
import com.pdm.tutoapp.models.Model
import com.pdm.tutoapp.models.TutoriaModel
import com.pdm.tutoapp.viewmodel.TutorViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SolicitudActivity : AppCompatActivity() {
    private lateinit var txt_direccion: EditText
    private lateinit var sp_categoria: Spinner
    private lateinit var txt_fecha: EditText
    private lateinit var txt_hora: EditText
    private lateinit var txt_notas: EditText
    private lateinit var btn_solicitar: Button
    private lateinit var idTutor: String
    private lateinit var estado: String
    private lateinit var nombre_estudiante: String
    private lateinit var apellido_estudiante: String
    private lateinit var nombre_tutor: String
    private lateinit var apellido_tutor: String
    private lateinit var foto_estudiante: String
    private lateinit var foto_tutor: String
    private lateinit var idEstudiante: String
    private lateinit var seleccion: String
    private lateinit var correoEstudiante: String
    private lateinit var telefonoEstudiante: String
    private lateinit var tutor: Model
    private lateinit var sp_horas: Spinner
    private lateinit var cuota_total : String


    //lazy se usa para instanciar el objeto hasta que se necesite
    private val viewModel by lazy { ViewModelProvider(this).get(TutorViewModel::class.java) }

    var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_solicitud)


        toolbar = findViewById(R.id.toolbar)
        toolbar?.setTitle(R.string.solicitud_txt)
        setSupportActionBar(toolbar)

        var actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        initialize()


        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        txt_fecha.setOnClickListener {
            val dp = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    txt_fecha.setText("$dayOfMonth/${month + 1}/$year")
                },
                year,
                month,
                day
            )

            dp.show()
        }

        txt_hora.setOnClickListener {
            val tp = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                c.set(Calendar.HOUR_OF_DAY, hourOfDay)
                c.set(Calendar.MINUTE, minute)
                txt_hora.setText(SimpleDateFormat("hh:mm a").format(c.time))
            }
            TimePickerDialog(
                this,
                tp,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                false
            ).show()
        }



        btn_solicitar.setOnClickListener {

            if (txt_direccion.text.isNotEmpty() && sp_categoria.toString()
                    .isNotEmpty() && txt_fecha.text.isNotEmpty() &&
                txt_hora.text.isNotEmpty() && txt_notas.text.isNotEmpty() &&
                sp_horas.selectedItem.toString() != ""
            ) {

                if(txt_notas.text.length >= 20){
                    crearSolicitud()
                    finish()
                }else {
                    txt_notas.setError("Debe tener al menos 20 caracteres.")
                }

            } else {
                Toast.makeText(this, "Por favor rellene todos los campos", Toast.LENGTH_LONG).show()

            }
        }


    }

    fun initialize() {

        tutor = intent.getSerializableExtra("tutor") as Model

        idEstudiante = intent.getStringExtra("idEstudiante")
        nombre_estudiante = intent.getStringExtra("nombre_estudiante")
        foto_estudiante = intent.getStringExtra("foto_estudiante")
        apellido_estudiante = intent.getStringExtra("apellido_estudiante")
        correoEstudiante = intent.getStringExtra("correo_estudiante")
        telefonoEstudiante = intent.getStringExtra("telefono_estudiante")
        idTutor = tutor.id
        foto_tutor = tutor.ruta
        nombre_tutor = tutor.name
        apellido_tutor = tutor.lastname
        txt_direccion = findViewById(R.id.txt_direccion)
        sp_categoria = findViewById(R.id.sp_categoria)
        txt_fecha = findViewById(R.id.txt_fecha)
        txt_hora = findViewById(R.id.txt_hora)
        txt_notas = findViewById(R.id.notas_tutor)
        estado = "En espera"
        btn_solicitar = findViewById(R.id.action_solicitar)
        sp_horas = findViewById(R.id.sp_horas)
        sp_categoria = findViewById(R.id.sp_categoria)

        selectedCategory()

        val categorias = initArray(tutor.listaDisciplina)

        sp_horas.adapter = ArrayAdapter(this, R.layout.item_spinner, arrayListOf<String>("1:00","2:00","3:00","4:00","5:00","6:00","7:00", "8:00"))

        sp_categoria.adapter =
            ArrayAdapter(this, R.layout.item_spinner, categorias!!)

        for (i in categorias!!.indices) {
            if (categorias[i].equals(seleccion))
                sp_categoria.setSelection(i)
        }

    }

    private fun crearSolicitud() {
        var id = UUID.randomUUID().toString()

        cuota_total = amountCalc(sp_horas.selectedItem.toString(),tutor.cuota.toDouble())

        var solicitud = TutoriaModel(
            id,
            txt_direccion.text.toString(),
            sp_categoria.selectedItem.toString(),
            txt_fecha.text.toString(),
            txt_hora.text.toString(),
            txt_notas.text.toString(),
            idEstudiante,
            idTutor,
            estado,
            nombre_estudiante,
            foto_estudiante,
            apellido_estudiante,
            nombre_tutor,
            apellido_tutor,
            foto_tutor,
            correoEstudiante,
            telefonoEstudiante,
            sp_horas.selectedItem.toString(),
            cuota_total
        )

        viewModel.postUserData(solicitud, idTutor, idEstudiante)

        Toast.makeText(this, "Solicitud enviada con exito", Toast.LENGTH_LONG).show()
    }

    private fun initArray(arrayList: ArrayList<Disciplina>?): Array<String?>? {
        val arrayString = arrayList?.size?.let { arrayOfNulls<String>(it) }
        if (arrayString != null) {
            for (i in arrayString.indices) {
                arrayString[i] = arrayList[i].name
            }
        }
        return arrayString
    }

    private fun amountCalc(duration: String, cuota: Double):String{
        var amount = "$0.00"

        var durationDouble = duration[0].toString().toInt() * cuota
        amount = "%.2f".format(durationDouble)

        return amount
    }

    private fun selectedCategory() {
        when (intent.getStringExtra("seleccion")) {
            "0" -> {
                seleccion = resources.getString(R.string.art_txt)
            }
            "1" -> {
                seleccion = resources.getString(R.string.languages_txt)
            }
            "2" -> {
                seleccion = resources.getString(R.string.math_txt)
            }
            "3" -> {
                seleccion = resources.getString(R.string.design_txt)
            }
            "4" -> {
                seleccion = resources.getString(R.string.economy_txt)
            }
            "5" -> {
                seleccion = resources.getString(R.string.social_abilities_txt)
            }
            "6" -> {
                seleccion = resources.getString(R.string.physics_txt)
            }
            "7" -> {
                seleccion = resources.getString(R.string.computer_science)
            }
            "8" -> {
                seleccion = resources.getString(R.string.chemistry_txt)
            }
            "9" -> {
                seleccion = resources.getString(R.string.music_txt)
            }
            "10" -> {
                seleccion = resources.getString(R.string.maths_s_txt)
            }
            "11" -> {
                seleccion = resources.getString(R.string.socials_sciences_txt)
            }
            else -> {
                seleccion = ""
            }
        }

    }

}
