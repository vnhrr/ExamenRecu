package com.example.examenrecu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.timer

class Activity_2 : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var dbHandler: ManejoBBDD // Manejador de base de datos
    private lateinit var selectorDestino: Spinner
    private lateinit var selectorOrigen: Spinner

    private val centros = arrayOf(
        "IES Julian Marias",
        "IES Virgen del Espino",
        "IES Claudio Moyano"
    )

    private val imagenes = intArrayOf(
        R.drawable.julian_marias_logo,
        R.drawable.virgen_espino_logo,
        R.drawable.claudio_moyano_logo
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        val verviajes = findViewById<Button>(R.id.btVerViajes)
        verviajes.setOnClickListener{
            startActivity(Intent(this, Activity_3::class.java))
        }

        selectorOrigen = findViewById(R.id.spinnerOrigen)
        selectorDestino = findViewById(R.id.spinnerDestino)

        val adaptadorPersonalizado = AdaptadorPersonalizado(this, R.layout.linea_spinner_fragment, centros)
        selectorOrigen.adapter = adaptadorPersonalizado
        selectorOrigen.onItemSelectedListener = this


        selectorDestino.adapter = adaptadorPersonalizado
        selectorOrigen.onItemSelectedListener = this

        // Inicializar la base de datos
        dbHandler = ManejoBBDD(this)

        // Botón para añadir un bar a la base de datos
        val incluir = findViewById<Button>(R.id.btGuardar)
        incluir.setOnClickListener {
            addBar()
        }
    }

    /**
     * Método para agregar un nuevo bar a la base de datos
     * @return `true` si el bar se añadió correctamente, `false` si hubo un error
     */
    private fun addBar(): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("es", "ES"))
        val currentdate = sdf.format(Date())
        val fechaactual = sdf.parse(currentdate)
        Log.d("fechaactual", fechaactual.toString())
        val origen = selectorOrigen.toString()
        val destino = selectorDestino.toString()
        val fecha =fechaactual

        // Verificar que todos los campos estén llenos antes de continuar
        if (origen.isNotEmpty() && destino.isNotEmpty() && fecha.toString().isNotEmpty()) {
            val bar = Viajes(
                origen = origen,
                destino = destino,
                fecha = fecha.toString()
            )

            val status = dbHandler.addBar(bar) // Llamada a la base de datos para añadir el bar

            if (status > -1) { // Si se ha insertado correctamente
                Toast.makeText(applicationContext, "viaje añadido correctamente", Toast.LENGTH_LONG).show()
                return true
            }
        } else {
            Toast.makeText(applicationContext, "Todos los datos son requeridos", Toast.LENGTH_LONG).show()
            return false
        }
        return false
    }


    private inner class AdaptadorPersonalizado(
        context: Context,
        resource: Int,
        objects: Array<String>
    ) : ArrayAdapter<String>(context, resource, objects) {
        //Constructor de mi adaptador paso el contexto (this)
        // el layout, y los elementos

        /**
         * Reescribo el método getDropDownView para que me devuelva una fila personalizada en la
         * lista desplegable en vez del elemento que se encuentra en esa posición. Se ejecuta
         * cuando se despliega el menu.
         *
         * @param posicion
         * @param ViewConvertida
         * @param padre
         * @return
         */
        override fun getDropDownView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {


            return crearFilaPersonalizada(position, convertView, parent)
        }

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            return crearFilaPersonalizada(position, convertView, parent)
        }
        private fun crearFilaPersonalizada(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            val layoutInflater = LayoutInflater.from(context)
            val rowView = convertView ?: layoutInflater.inflate(R.layout.linea_lista_activity1, parent, false)

            rowView.findViewById<TextView>(R.id.nombre).text = centros[position]
            rowView.findViewById<ImageView>(R.id.imagenCiudad).setImageResource(imagenes[position])
            return rowView
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // hace referencia al textview que especifica la linea del spinner
        val c = view?.findViewById<TextView>(R.id.nombre)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}