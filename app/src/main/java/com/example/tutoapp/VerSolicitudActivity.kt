package com.example.tutoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tutoapp.viewmodel.SolicitudViewModel
import com.squareup.picasso.Picasso


class VerSolicitudActivity : AppCompatActivity() {
        private lateinit var name: TextView
        private lateinit var hora: TextView
        private lateinit var notas: TextView
        private lateinit var btnAceptar: Button
        private lateinit var btnRechazar: Button
        private lateinit var imgEstudiante: ImageView
        private lateinit var idEstudiante: String
        private val viewModel by lazy { ViewModelProvider(this).get(SolicitudViewModel::class.java) }

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_ver_solicitud)

                initialize()


                observerData()




            }

            fun initialize() {
                imgEstudiante = findViewById(R.id.foto_estudiante)
                name = findViewById(R.id.nombre_estudiante)
                hora = findViewById(R.id.hora_tutoria)
                notas = findViewById(R.id.notas_tutoria)
                btnAceptar = findViewById(R.id.aceptar_tutoria)
                btnRechazar = findViewById(R.id.rechazar_tutoria)
                val solicitud = intent.getSerializableExtra("solicitud") as TutoriaModel

                idEstudiante=solicitud.solicitante
                name.text = solicitud.nombre_estudiante
                hora.text = solicitud.hora
                notas.text = solicitud.nota

            }
        private fun observerData() {
            viewModel.getUserSolicitud(idEstudiante).observe(this, Observer {
                //it trae el objeto persona de la base de datos
                Picasso.get().load(it.urlImage).into(imgEstudiante)
            })

        }
        }
