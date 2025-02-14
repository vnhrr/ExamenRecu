package com.example.examenrecu

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

// Clase DatabaseHelper que extiende SQLiteOpenHelper para manejar la base de datos de la aplicación.
class ManejoBBDD(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    init {
        val path = context.getDatabasePath(DATABASE_NAME).absolutePath
        Log.d("Database", "📁 Ruta correcta de la base de datos: $path")
    }

    // Bloque companion object para definir constantes que serán usadas en toda la clase.
    // Son equivalentes a los valores estáticos en Java.
    companion object {
        // Nombre de la base de datos.
        private const val DATABASE_NAME = "BaresDatabase"
        // Versión de la base de datos, útil para manejar actualizaciones esquemáticas.
        private const val DATABASE_VERSION = 1
        // Nombre de la tabla donde se almacenarán los bares.
        private const val TABLE_VIAJES = "Viajes"
        // Nombres de las columnas de la tabla.
        private const val KEY_ID = "id"
        private const val KEY_ORIGEN = "origen"
        private const val KEY_DESTINO = "destino"
        private const val KEY_FECHA = "fecha"
    }

    // Método llamado cuando la base de datos se crea por primera vez.
    override fun onCreate(db: SQLiteDatabase) {
        // Define la sentencia SQL para crear la tabla de bares.
        val createBarTable = ("CREATE TABLE " + TABLE_VIAJES + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_ORIGEN + " TEXT,"
                + KEY_DESTINO + " TEXT,"
                + KEY_FECHA + " TEXT" + ")")

        // Ejecuta la sentencia SQL para crear la tabla.
        db.execSQL(createBarTable)
    }

    // Método llamado cuando se necesita actualizar la base de datos, por ejemplo, cuando se incrementa DATABASE_VERSION.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Elimina la tabla existente y crea una nueva.
        db.execSQL("DROP TABLE IF EXISTS $TABLE_VIAJES")
        onCreate(db)
    }

    // Método para obtener todos los bares de la base de datos.
    fun getAllBares(): ArrayList<Viajes> {
        // Lista para almacenar y retornar los bares.
        val barList = ArrayList<Viajes>()
        // Consulta SQL para seleccionar todos los bares.
        val selectQuery = "SELECT  * FROM $TABLE_VIAJES"

        val db = this.readableDatabase
        var cursor: Cursor? = null
        try {
            // Ejecuta la consulta SQL.
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: SQLiteException) {
            // Maneja la excepción en caso de error al ejecutar la consulta.
            db.execSQL(selectQuery)
            return ArrayList()
        }

        // Itera a través del cursor para leer los datos de la base de datos.
        if (cursor.moveToFirst()) {
            do {
                // Obtiene los índices de las columnas.
                val idIndex = cursor.getColumnIndex(KEY_ID)
                val origenIndex = cursor.getColumnIndex(KEY_ORIGEN)
                val destinoIndex = cursor.getColumnIndex(KEY_DESTINO)
                val fechaIndex = cursor.getColumnIndex(KEY_FECHA)

                // Verifica que los índices sean válidos.
                if (idIndex != -1 && origenIndex != -1 && destinoIndex != -1 && fechaIndex != -1) {
                    // Lee los valores y los añade a la lista de bares.
                    val bar = Viajes(
                        id = cursor.getInt(idIndex),
                        origen = cursor.getString(origenIndex),
                        destino = cursor.getString(destinoIndex),
                        fecha = cursor.getString(fechaIndex)
                    )
                    barList.add(bar)
                }
            } while (cursor.moveToNext())
        }

        // Cierra el cursor para liberar recursos.
        cursor.close()
        return barList
    }

    // Método para abrir la base de datos en modo escritura.
    fun openDatabase() {
        val db = this.writableDatabase
        Log.d("Database", "📂 Base de datos abierta correctamente")
    }

    // Método para actualizar un bar en la base de datos.
    fun updateBar(bar: Viajes): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        // Prepara los valores a actualizar.
        contentValues.put(KEY_DESTINO, bar.destino)
        contentValues.put(KEY_FECHA, bar.fecha)

        // Actualiza la fila correspondiente y retorna el número de filas afectadas.
        return db.update(TABLE_VIAJES, contentValues, "$KEY_ID = ?", arrayOf(bar.id.toString()))
    }

    // Método para eliminar un bar de la base de datos.
    fun deleteBar(bar: Viajes): Int {
        val db = this.writableDatabase

        Log.d("Database", "🔍 Buscando y eliminando bar con ID: ${bar.id}") // Verificar el ID antes de eliminar

        val success = db.delete(TABLE_VIAJES, "$KEY_ID = ?", arrayOf(bar.id.toString()))

        Log.d("Database", "✅ Filas eliminadas: $success") // Verificar si realmente se eliminó algo

        return success
    }

    // Método para añadir un nuevo bar a la base de datos.
    fun addBar(bar: Viajes): Long {
        try {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            // Prepara los valores a insertar.
            contentValues.put(KEY_ORIGEN, bar.origen)
            contentValues.put(KEY_DESTINO, bar.destino)
            contentValues.put(KEY_FECHA, bar.fecha)

            // Inserta el nuevo bar y retorna el ID del nuevo bar o -1 en caso de error.
            val success = db.insert(TABLE_VIAJES, null, contentValues)
            return success
        } catch (e: Exception) {
            // Maneja la excepción en caso de error al insertar.
            Log.e("Error", "Error al agregar bar", e)
            return -1
        }
    }
}
