package com.example.examenrecu

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.setFragmentResult
import com.example.examenjuanev2casa.ViajesAdapter

class Activity_3 : AppCompatActivity() {
    private lateinit var adapter: ViajesAdapter
    private lateinit var dbHandler: ManejoBBDD
    private lateinit var listViewViajes: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)
        // Inicializar la base de datos
        dbHandler = ManejoBBDD(this)
        listViewViajes = findViewById(R.id.lvActivity3)
        val baresLista = dbHandler.getAllBares() // Obtener todos los bares de la base de datos

        Log.d("Database", "📋 Total de bares obtenidos después de actualización: ${baresLista.size}")

        if (::adapter.isInitialized) {
            adapter.updateList(baresLista) // Actualizar la lista en el adaptador
            adapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
            listViewViajes.invalidateViews() // Forzar redibujado de la lista para reflejar los cambios
        } else {
            adapter = ViajesAdapter(this, baresLista) // Crear un nuevo adaptador
            listViewViajes.adapter = adapter // Asignar el adaptador a la ListView
        }



        val borrar = findViewById<Button>(R.id.btBorrar)
        borrar.setOnClickListener{
            // Crear canal de notificación (necesario para Android 8.0+)
            crearCanalDeNotificacion()

            // Solicitar permisos de notificación en dispositivos con Android 13+
            solicitarPermisoNotificaciones()
            mostrarNotificacion("Hola")
        }



        val volver = findViewById<Button>(R.id.btVolver)
        volver.setOnClickListener{
            startActivity(Intent(this, Activity_2::class.java))
        }
    }

    /**
     * Método para eliminar un bar de la base de datos.
     * @param id ID del bar a eliminar.
     */
    private fun eliminarBar(id: String) {
        val dbHandler = ManejoBBDD(this)
        val barId = id.toIntOrNull()

        if (barId != null) {
            val resultado = dbHandler.deleteBar(Viajes())
            Log.d("Database", "Filas afectadas al eliminar: $resultado")
            Toast.makeText(applicationContext, "✅ Bar eliminado con ID: $id", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, "❌ ID no válido", Toast.LENGTH_SHORT).show()
        }
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
            if (ActivityCompat.checkSelfPermission(this@Activity_3, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
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
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

}