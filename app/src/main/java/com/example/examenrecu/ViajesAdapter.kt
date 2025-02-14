package com.example.examenjuanev2casa

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.examenrecu.R
import com.example.examenrecu.Viajes

// Adaptador personalizado para mostrar la lista de bares en un ListView
class ViajesAdapter(private val context: Context, private var viajesList: List<Viajes>) : BaseAdapter() {

    // Devuelve el número total de elementos en la lista
    override fun getCount(): Int = viajesList.size

    // Devuelve el objeto Bar en la posición dada
    override fun getItem(position: Int): Any = viajesList[position]

    // Devuelve el ID del objeto en la posición dada
    override fun getItemId(position: Int): Long = viajesList[position].id.toLong()

    // Genera y devuelve la vista para cada elemento de la lista
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Si convertView es nulo, se infla el layout correspondiente
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.linea_lista_viajes, parent, false)

        // Referencias a los TextView en el layout para mostrar la información del bar
        val fechaViaje = view.findViewById<TextView>(R.id.tvFecha)
        val origenViaje = view.findViewById<TextView>(R.id.tvOrigen)
        val destinoViaje = view.findViewById<TextView>(R.id.tvDestino)

        // Obtener el bar actual de la lista y asignar los valores a los TextView
        val viaje = viajesList[position]
        fechaViaje.text = viaje.fecha
        origenViaje.text = viaje.origen
        destinoViaje.text = viaje.destino

        return view
    }

    /**
     * Método para actualizar la lista de bares y notificar cambios en el adaptador
     * @param newList Nueva lista de bares a mostrar
     */
    fun updateList(newList: List<Viajes>) {
        viajesList = newList
        notifyDataSetChanged() // Notifica al adaptador que los datos han cambiado
    }
}
