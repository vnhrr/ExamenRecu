package com.example.examenrecu

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable

class Activity1 : AppCompatActivity(), AdapterView.OnItemSelectedListener {

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

    private val telefonos = arrayOf(
        "123456789",
        "987654321",
        "246897531"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_1)

        val selectorCiudades = findViewById<ListView>(R.id.lvActivity1)
        val imLogo = findViewById<ImageButton>(R.id.ivLogo)
        val btTlfn = findViewById<Button>(R.id.btTelefono)
        val adaptadorPersonalizado = AdaptadorPersonalizado(this, R.layout.linea_lista_activity1, centros)
        selectorCiudades.adapter = adaptadorPersonalizado
        solicitarPermisoNotificaciones()


        btTlfn.setOnClickListener{

            val intent = Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + btTlfn.getText().toString()))
            startActivity(intent)
        }


        selectorCiudades.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val selectedCentro = imagenes[position]
                val tlf = telefonos[position]
                imLogo.setImageResource(selectedCentro)
                btTlfn.text = tlf
            }

        imLogo.setOnClickListener{
            startActivity(Intent(this, Activity_2::class.java))
        }

    }

    //Fijamos un escuchador


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

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    /**
     * Crea el canal de notificaciones (Requerido para Android 8.0+)
     */
    private fun crearCanalDeNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Canal de Bares"
            val descripcion = "Notificaciones cuando se añade un nuevo bar"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel("BAR_CHANNEL", nombre, importancia).apply {
                description = descripcion
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(canal) // Registrar el canal de notificaciones
            Log.d("Notificaciones", "✅ Canal de notificación creado correctamente")
        }
    }

    /**
     * Muestra una notificación cuando se añade un bar
     */
    private fun mostrarNotificacion(nombreBar: String) {
        Log.d("Notificaciones", "🔔 Intentando mostrar notificación para el bar: $nombreBar")

        // Crear intent para abrir la actividad principal al hacer clic en la notificación
        val intent = Intent(this, Activity1::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Crear un PendingIntent para la notificación
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Construcción de la notificación
        val builder = NotificationCompat.Builder(this, "BAR_CHANNEL")
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Icono de la notificación
            .setContentTitle("Nuevo Bar Añadido 🍻")
            .setContentText("Se ha añadido \"$nombreBar\" a la lista.")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Alta prioridad para mayor visibilidad
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Se elimina la notificación al hacer clic

        // Enviar la notificación si se tienen permisos
        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(this@Activity1, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(1001, builder.build()) // ID de la notificación
                Log.d("Notificaciones", "✅ Notificación enviada con éxito")
            } else {
                Log.d("Notificaciones", "⚠️ No se tiene permiso para mostrar notificaciones")
            }
        }
    }

    /**
     * Solicitar permisos de notificación en Android 13+
     */
    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 101)
            }
        }
    }
}