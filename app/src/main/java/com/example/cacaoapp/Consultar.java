package com.example.cacaoapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Consultar {

    public static void obtenerDatos(Context context, TableLayout tablas) {
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(context, "Motor de Base de Datos", null, 1);
        SQLiteDatabase baseDatos = motorDato.getReadableDatabase();

        Cursor fila = baseDatos.rawQuery("SELECT * FROM inventario", null);

        if (fila.getCount() > 0) {
            // Agrega encabezado
            TableRow filaEncabezado = new TableRow(context);
            for (int i = 0; i < fila.getColumnCount(); i++) {
                TextView encabezado = new TextView(context);
                encabezado.setText(fila.getColumnName(i));
                encabezado.setPadding(16, 16, 16, 16);
                encabezado.setTypeface(null, android.graphics.Typeface.BOLD);
                filaEncabezado.addView(encabezado);
            }
            tablas.addView(filaEncabezado);

            // Agrega filas de datos
            if (fila.moveToFirst()) {
                do {
                    TableRow filaDatos = new TableRow(context);
                    for (int i = 0; i < fila.getColumnCount(); i++) {
                        TextView celda = new TextView(context);
                        celda.setText(fila.getString(i));
                        celda.setPadding(16, 16, 16, 16);
                        celda.setWidth(200);
                        filaDatos.addView(celda);
                    }
                    tablas.addView(filaDatos);
                } while (fila.moveToNext());
            }

            // Muestra la sumatoria después de los datos
            mostrarTotalCantidad(context, tablas, baseDatos);
        } else {
            // Si no hay datos
            TextView mensaje = new TextView(context);
            mensaje.setText("No hay datos en el inventario.");
            mensaje.setPadding(16, 16, 16, 16);
            tablas.addView(mensaje);
        }

        fila.close();
        baseDatos.close();
    }

    // Función para mostrar la suma de la columna cantidad
    public static void mostrarTotalCantidad(Context context, TableLayout tablas, SQLiteDatabase baseDatos) {
        // Ejecuta una consulta SQL para obtener todos los datos de la tabla "inventario"
        Cursor cursor = baseDatos.rawQuery("SELECT * FROM inventario", null);
        // Verifica que haya datos en el cursor y que se pueda mover al primer registro
        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            // Obtiene los índices de las columnas "cantidad" y "pago"
            int indexCantidad = cursor.getColumnIndex("CantidadCacao");
            int indexPago = cursor.getColumnIndex("PagoDeCacao");
// Variables para almacenar los totales
            double totalCantidad = 0;
            double totalPago = 0;
            // Recorre cada fila del cursor para sumar los valores de las columnas
            do {
                try {
                    // Suma los valores de la columna "cantidad"
                    if (indexCantidad != -1) {
                        totalCantidad += Double.parseDouble(cursor.getString(indexCantidad));
                    }
                    // Suma los valores de la columna "pago"
                    if (indexPago != -1) {
                        totalPago += Double.parseDouble(cursor.getString(indexPago));
                    }
                } catch (NumberFormatException e) {
                    // En caso de que algún valor no sea numérico, lo ignora
                }
            } while (cursor.moveToNext());

            // Crea una nueva fila para mostrar los totales
            TableRow filaTotal = new TableRow(context);
// Determina cuántas columnas tiene la tabla
            int totalColumnas = cursor.getColumnCount();
            // Recorre todas las columnas para construir la fila total
            for (int i = 0; i < totalColumnas; i++) {
                TextView celda = new TextView(context);
                celda.setPadding(16, 16, 16, 16); // Espaciado interior

                if (i == 0) {
                    // La primera celda muestra el texto "TOTAL"
                    celda.setText("TOTAL");
                    celda.setTypeface(null, android.graphics.Typeface.BOLD);// Texto en negrita
                } else if (i == indexCantidad) {
                    // En la columna de "cantidad", muestra el total correspondiente
                    celda.setText(String.valueOf(totalCantidad));
                } else if (i == indexPago) {
                    // En la columna de "pago", muestra el total correspondiente
                    celda.setText(String.valueOf(totalPago));
                } else {
                    // Las demás columnas se dejan vacías
                    celda.setText(""); // dejar vacío en otras columnas
                }
                // Añade la celda a la fila de totales
                filaTotal.addView(celda);
            }
            // Finalmente, añade la fila de totales al TableLayout
            tablas.addView(filaTotal);
        }
        // Cierra el cursor para liberar recursos
        cursor.close();
    }
}

