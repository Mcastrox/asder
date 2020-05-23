package com.example.tutoapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_tutor_filtrado.*

class TutorFiltradoActivity : AppCompatActivity() {

    private var listaTutores = mutableListOf<Model>()
    private lateinit var adapter : TutorAdapter
    private lateinit var lista_categoriaSeleccionada : ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutor_filtrado)

        var eleccion = intent.getStringExtra("seleccion")

        lista_categoriaSeleccionada=findViewById(R.id.lista_categoriaSeleccionada)
        adapter= TutorAdapter(this,listaTutores)

        val ref = FirebaseDatabase.getInstance().getReference("Users") // referencia a la bd
        ref.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                if (listaTutores.size > 0) {
                    listaTutores.clear()
                }
                for (e in p0.children) {
                    var lastName: String = ""
                    var direccion: String = ""
                    var rol: String = ""
                    var ruta: String = ""
                    var categoria: String = ""
                    var id: String = e.child("ID").value as String

                    if (e.child("lastName").value != null) {
                        lastName = e.child("lastName").value as String
                    }
                    if (e.child("direccion").value != null) {
                        direccion = e.child("direccion").value as String
                    }
                    if (e.child("Rol").value != null) {
                        rol = e.child("Rol").value as String
                    }
                    if (e.child("urlImage").value != null) {
                        ruta = e.child("urlImage").value as String
                    }


                    if (rol == "Tutor") {
                        if (e.child("disciplinas").child("2").child("seleccionado").value == true) {
                            listaTutores.add(
                                Model(
                                    id,
                                    lastName,
                                    direccion,
                                    R.drawable.ic_art,
                                    ruta
                                )
                            )
                        }
                    }

                }


                lista_categoriaSeleccionada.adapter = adapter
            }
            })
    }
}
