package com.example.cacaoapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Consultar {

    // Método que obtiene los datos del inventario y los muestra en el TableLayout
    public static void obtenerDatos(Context context, TableLayout tablas) {
        // Conexión a la base de datos SQLite
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(context, "Motor de Base de Datos", null, 1);
        SQLiteDatabase baseDatos = motorDato.getReadableDatabase();

        // Consulta todos los registros de la tabla inventario
        Cursor fila = baseDatos.rawQuery("SELECT * FROM inventario", null);

        if (fila.getCount() > 0) {
            // Crear fila de encabezados
            TableRow filaEncabezado = new TableRow(context);
            for (int i = 0; i < fila.getColumnCount(); i++) {
                TextView encabezado = crearCeldaConBorde(context);
                encabezado.setText(fila.getColumnName(i)); // Nombre de la columna
                encabezado.setTypeface(null, Typeface.BOLD); // Negrita
                encabezado.setTextColor(Color.BLACK); // Color del texto
                filaEncabezado.addView(encabezado); // Agregar al encabezado
            }
            tablas.addView(filaEncabezado); // Agregar la fila de encabezado a la tabla

            // Formato para números con coma y dos decimales
            java.text.DecimalFormat formato = new java.text.DecimalFormat("#,###.00");

            // Recorrer cada fila de datos del inventario
            if (fila.moveToFirst()) {
                do {
                    TableRow filaDatos = new TableRow(context);

                    for (int i = 0; i < fila.getColumnCount(); i++) {
                        TextView celda = crearCeldaConBorde(context);
                        String columnaNombre = fila.getColumnName(i);
                        String valor = fila.getString(i);

                        // Formatear las columnas numéricas
                        if ((columnaNombre.equalsIgnoreCase("CantidadCacao") ||
                                columnaNombre.equalsIgnoreCase("PagoDeCacao") ||
                                columnaNombre.equalsIgnoreCase("PrecioAPagar")) && valor != null) {
                            try {
                                double numero = Double.parseDouble(valor.replace(",", ""));
                                celda.setText(formato.format(numero));
                            } catch (NumberFormatException e) {
                                celda.setText(valor);
                            }
                        } else {
                            celda.setText(valor); // Para columnas de texto
                        }

                        filaDatos.addView(celda); // Agregar celda a la fila
                    }

                    tablas.addView(filaDatos); // Agregar la fila a la tabla
                } while (fila.moveToNext());
            }

            // Mostrar totales de columnas seleccionadas
            mostrarTotalCantidad(context, tablas, baseDatos);
        } else {
            // Si no hay datos, mostrar mensaje
            TextView mensaje = new TextView(context);
            mensaje.setText("No hay datos en el inventario.");
            mensaje.setPadding(16, 16, 16, 16);
            tablas.addView(mensaje);
        }

        // Cerrar cursor y base de datos
        fila.close();
        baseDatos.close();
    }

    // Método para mostrar los totales de algunas columnas al final de la tabla
    public static void mostrarTotalCantidad(Context context, TableLayout tablas, SQLiteDatabase baseDatos) {
        Cursor cursor = baseDatos.rawQuery("SELECT * FROM inventario", null);

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            int indexCantidad = cursor.getColumnIndex("CantidadCacao");
            int indexPago = cursor.getColumnIndex("PagoDeCacao");

            double totalCantidad = 0;
            double totalPago = 0;

            // Recorrer el cursor para acumular totales
            do {
                try {
                    if (indexCantidad != -1) {
                        String cantidadStr = cursor.getString(indexCantidad);
                        if (cantidadStr != null) {
                            cantidadStr = cantidadStr.replace(",", "");
                            totalCantidad += Double.parseDouble(cantidadStr);
                        }
                    }

                    if (indexPago != -1) {
                        String pagoStr = cursor.getString(indexPago);
                        if (pagoStr != null) {
                            pagoStr = pagoStr.replace(",", "");
                            totalPago += Double.parseDouble(pagoStr);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Ignorar si hay errores al convertir
                }
            } while (cursor.moveToNext());

            // Crear fila de totales
            TableRow filaTotal = new TableRow(context);
            int totalColumnas = cursor.getColumnCount();
            java.text.DecimalFormat formato = new java.text.DecimalFormat("#,###.00");

            for (int i = 0; i < totalColumnas; i++) {
                TextView celda = crearCeldaConBorde(context);

                if (i == 0) {
                    celda.setText("TOTAL");
                    celda.setTypeface(null, Typeface.BOLD);
                } else if (i == indexCantidad) {
                    celda.setText(formato.format(totalCantidad));
                } else if (i == indexPago) {
                    celda.setText(formato.format(totalPago));
                } else {
                    celda.setText("");
                }

                filaTotal.addView(celda); // Agregar celda a la fila
            }

            tablas.addView(filaTotal); // Agregar fila total a la tabla
        }

        cursor.close(); // Cerrar cursor
    }

    // Método reutilizable que crea una celda con borde negro y formato uniforme
    private static TextView crearCeldaConBorde(Context context) {
        TextView celda = new TextView(context);
        celda.setPadding(16, 16, 16, 16); // Espacio interno
        celda.setTextColor(Color.BLACK); // Color del texto
        celda.setTextSize(14); // Tamaño del texto
        celda.setGravity(Gravity.CENTER); // Centrado del texto

        // Crear borde negro con fondo blanco
        GradientDrawable borde = new GradientDrawable();
        borde.setColor(Color.WHITE);           // Fondo blanco
        borde.setStroke(2, Color.BLACK);       // Grosor y color del borde

        // Asignar borde al fondo del TextView
        celda.setBackground(borde);

        // Agregar márgenes entre celdas para separar visualmente
        TableRow.LayoutParams params = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(1, 1, 1, 1); // Márgenes para simular separación de celdas
        celda.setLayoutParams(params);

        return celda;
    }
}
