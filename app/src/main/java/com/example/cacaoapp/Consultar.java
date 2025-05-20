package com.example.cacaoapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Consultar {

    // Método para obtener y mostrar todos los datos en un TableLayout
    public static void obtenerDatos(Context context, TableLayout tablas) {
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(context, "Motor de Base de Datos", null, 1);
        SQLiteDatabase baseDatos = motorDato.getReadableDatabase();

        // Consulta todos los registros de la tabla "inventario"
        Cursor fila = baseDatos.rawQuery("SELECT * FROM inventario", null);

        if (fila.getCount() > 0) {
            // Crear fila de encabezados
            TableRow filaEncabezado = new TableRow(context);
            for (int i = 0; i < fila.getColumnCount(); i++) {
                TextView encabezado = new TextView(context);
                encabezado.setText(fila.getColumnName(i));
                encabezado.setPadding(16, 16, 16, 16);
                encabezado.setTypeface(null, android.graphics.Typeface.BOLD);
                filaEncabezado.addView(encabezado);
            }
            tablas.addView(filaEncabezado);

            // Formateador para mostrar números con comas y dos decimales
            java.text.DecimalFormat formato = new java.text.DecimalFormat("#,###.00");

            // Iterar sobre cada fila del cursor
            if (fila.moveToFirst()) {
                do {
                    TableRow filaDatos = new TableRow(context);
                    for (int i = 0; i < fila.getColumnCount(); i++) {
                        TextView celda = new TextView(context);
                        celda.setPadding(16, 16, 16, 16);
                        celda.setWidth(200);

                        String columnaNombre = fila.getColumnName(i);
                        String valor = fila.getString(i);

                        // Formatear solo columnas numéricas
                        if ((columnaNombre.equalsIgnoreCase("CantidadCacao") ||
                                columnaNombre.equalsIgnoreCase("PagoDeCacao") ||
                                columnaNombre.equalsIgnoreCase("PrecioAPagar")) && valor != null) {
                            try {
                                // Eliminar comas previas y convertir a número
                                double numero = Double.parseDouble(valor.replace(",", ""));
                                // Formatear con comas y decimales
                                celda.setText(formato.format(numero));
                            } catch (NumberFormatException e) {
                                celda.setText(valor); // Mostrar original si hay error
                            }
                        } else {
                            // Mostrar texto normal para columnas no numéricas
                            celda.setText(valor);
                        }

                        filaDatos.addView(celda);
                    }
                    tablas.addView(filaDatos);
                } while (fila.moveToNext());
            }

            // Mostrar totales de Cantidad y Pago al final
            mostrarTotalCantidad(context, tablas, baseDatos);
        } else {
            // Si no hay datos en la base, mostrar mensaje
            TextView mensaje = new TextView(context);
            mensaje.setText("No hay datos en el inventario.");
            mensaje.setPadding(16, 16, 16, 16);
            tablas.addView(mensaje);
        }

        // Cerrar cursor y base de datos
        fila.close();
        baseDatos.close();
    }

    // Método para calcular y mostrar totales de CantidadCacao y PagoDeCacao
    public static void mostrarTotalCantidad(Context context, TableLayout tablas, SQLiteDatabase baseDatos) {
        Cursor cursor = baseDatos.rawQuery("SELECT * FROM inventario", null);

        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
            // Obtener índices de columnas relevantes
            int indexCantidad = cursor.getColumnIndex("CantidadCacao");
            int indexPago = cursor.getColumnIndex("PagoDeCacao");

            double totalCantidad = 0;
            double totalPago = 0;

            // Sumar cantidades y pagos
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
                    // Ignorar errores de conversión
                }
            } while (cursor.moveToNext());

            // Crear fila para mostrar totales
            TableRow filaTotal = new TableRow(context);
            int totalColumnas = cursor.getColumnCount();

            // Formateador con comas y dos decimales
            java.text.DecimalFormat formato = new java.text.DecimalFormat("#,###.00");

            for (int i = 0; i < totalColumnas; i++) {
                TextView celda = new TextView(context);
                celda.setPadding(16, 16, 16, 16);

                if (i == 0) {
                    celda.setText("TOTAL");
                    celda.setTypeface(null, android.graphics.Typeface.BOLD);
                } else if (i == indexCantidad) {
                    celda.setText(formato.format(totalCantidad));
                } else if (i == indexPago) {
                    celda.setText(formato.format(totalPago));
                } else {
                    celda.setText(""); // Vacío para las demás columnas
                }

                filaTotal.addView(celda);
            }

            // Agregar fila total al final de la tabla
            tablas.addView(filaTotal);
        }

        cursor.close(); // Cerrar cursor
    }
}