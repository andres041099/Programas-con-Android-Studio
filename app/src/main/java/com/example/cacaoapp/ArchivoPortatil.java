package com.example.cacaoapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ArchivoPortatil {
        public static void inventarioPDF(Context context) {
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(context, "Motor de Base de Datos", null, 1);
        SQLiteDatabase baseDatos = motorDato.getReadableDatabase();

        Cursor fila = baseDatos.rawQuery("SELECT * FROM inventario", null);

        if (fila.getCount() == 0) {
            Toast.makeText(context, "No hay datos para guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Preparar formateador para números con coma en miles y punto en decimales
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);

        PdfDocument documento = new PdfDocument();
        Paint paintTexto = new Paint();
        Paint paintLinea = new Paint();
        paintLinea.setStyle(Paint.Style.STROKE);
        paintLinea.setStrokeWidth(1);
        paintLinea.setColor(0xFF000000); // negro para líneas

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(842, 595, 1).create();
        PdfDocument.Page pagina = documento.startPage(pageInfo);
        Canvas canvas = pagina.getCanvas();

        int margenIzquierdo = 30;
        int margenSuperior = 50;
        int alturaFila = 30;

        paintTexto.setTextSize(14);
        paintTexto.setFakeBoldText(true);

        int columnas = fila.getColumnCount();
        String[] nombresColumnas = new String[columnas];

        for (int i = 0; i < columnas; i++) {
            nombresColumnas[i] = fila.getColumnName(i);
        }

        // Índices de columnas a sumar (ajusta estos nombres según tu BD)
        int idxCantidadCacao = -1;
        int idxTotalPago = -1;
        int idxPagoDeCacao = -1;
        int idxPrecioAPagar = -1;

        for (int i = 0; i < columnas; i++) {
            String colName = nombresColumnas[i].toLowerCase();
            if (colName.equals("cantidad_cacao") || colName.equals("cantidadcacao")) {
                idxCantidadCacao = i;
            } else if (colName.equals("total_pago") || colName.equals("totalpago")) {
                idxTotalPago = i;
            } else if (colName.equals("pagodecacao")) {
                idxPagoDeCacao = i;
            } else if (colName.equals("precioapagar")) {
                idxPrecioAPagar = i;
            }
        }

        // Variables para totales
        double sumaCantidadCacao = 0;
        double sumaTotalPago = 0;
        double sumaPagoDeCacao = 0;

        // Calcular anchos columnas según encabezados y datos
        int[] anchosColumnas = new int[columnas];
        int maxAnchoPagina = 780;

        paintTexto.setTextSize(14);
        for (int i = 0; i < columnas; i++) {
            anchosColumnas[i] = (int) paintTexto.measureText(nombresColumnas[i]) + 20;
        }

        paintTexto.setTextSize(12);
        paintTexto.setFakeBoldText(false);

        if (fila.moveToFirst()) {
            do {
                for (int i = 0; i < columnas; i++) {
                    String dato = fila.getString(i);
                    if (dato == null) dato = "";
                    int anchoDato = (int) paintTexto.measureText(dato) + 20;
                    if (anchoDato > anchosColumnas[i]) {
                        anchosColumnas[i] = anchoDato;
                    }
                }
                // Sumar las columnas cantidad cacao, total pago y pago de cacao
                if (idxCantidadCacao != -1) {
                    try {
                        sumaCantidadCacao += Double.parseDouble(fila.getString(idxCantidadCacao));
                    } catch (Exception e) {}
                }
                if (idxTotalPago != -1) {
                    try {
                        sumaTotalPago += Double.parseDouble(fila.getString(idxTotalPago));
                    } catch (Exception e) {}
                }
                if (idxPagoDeCacao != -1) {
                    try {
                        sumaPagoDeCacao += Double.parseDouble(fila.getString(idxPagoDeCacao));
                    } catch (Exception e) {}
                }
            } while (fila.moveToNext());
            fila.moveToFirst();
        }

        // Ajustar ancho si excede página
        int sumaAnchos = 0;
        for (int w : anchosColumnas) sumaAnchos += w;

        if (sumaAnchos > maxAnchoPagina) {
            float factor = (float) maxAnchoPagina / sumaAnchos;
            for (int i = 0; i < anchosColumnas.length; i++) {
                anchosColumnas[i] = (int) (anchosColumnas[i] * factor);
                if (anchosColumnas[i] < 50) anchosColumnas[i] = 50;
            }
        }

        // Dibujar encabezados
        int x = margenIzquierdo;
        int y = margenSuperior;

        paintTexto.setTextSize(14);
        paintTexto.setFakeBoldText(true);

        for (int i = 0; i < columnas; i++) {
            float textoAncho = paintTexto.measureText(nombresColumnas[i]);
            int centroColumna = x + anchosColumnas[i] / 2;
            canvas.drawText(nombresColumnas[i], centroColumna - textoAncho / 2, y, paintTexto);
            x += anchosColumnas[i];
        }

        // Líneas horizontales encabezado
        int xLineaInicio = margenIzquierdo;
        int xLineaFin = margenIzquierdo + sumaAnchos;
        int yLineaSuperior = y - 20;
        int yLineaInferior = y + 10;

        canvas.drawLine(xLineaInicio, yLineaSuperior, xLineaFin, yLineaSuperior, paintLinea);
        canvas.drawLine(xLineaInicio, yLineaInferior, xLineaFin, yLineaInferior, paintLinea);

        // Líneas verticales encabezado
        x = margenIzquierdo;
        for (int i = 0; i <= columnas; i++) {
            canvas.drawLine(x, yLineaSuperior, x, yLineaInferior, paintLinea);
            if (i < columnas) x += anchosColumnas[i];
        }

        // Dibujar filas datos
        y = yLineaInferior + 20;
        paintTexto.setTextSize(12);
        paintTexto.setFakeBoldText(false);

        do {
            x = margenIzquierdo;
            for (int i = 0; i < columnas; i++) {
                String dato = fila.getString(i);
                if (dato == null) dato = "";

                // Si la columna es PrecioAPagar o PagoDeCacao formatear con coma y punto decimal
                if ((i == idxPrecioAPagar || i == idxPagoDeCacao) && !dato.isEmpty()) {
                    try {
                        double valor = Double.parseDouble(dato);
                        dato = decimalFormat.format(valor);
                    } catch (Exception e) {
                        // si falla el parseo dejar el dato como está
                    }
                }

                float textoAncho = paintTexto.measureText(dato);
                if (textoAncho > anchosColumnas[i] - 10) {
                    while (dato.length() > 0 && paintTexto.measureText(dato + "...") > anchosColumnas[i] - 10) {
                        dato = dato.substring(0, dato.length() - 1);
                    }
                    dato += "...";
                }

                int centroColumna = x + anchosColumnas[i] / 2;
                canvas.drawText(dato, centroColumna - paintTexto.measureText(dato) / 2, y, paintTexto);
                x += anchosColumnas[i];
            }
            canvas.drawLine(margenIzquierdo, y + 10, margenIzquierdo + sumaAnchos, y + 10, paintLinea);

            y += alturaFila;
        } while (fila.moveToNext());

        // Dibujar fila de totales (negrita)
        paintTexto.setFakeBoldText(true);

        x = margenIzquierdo;
        String textoTotal = "TOTAL:";
        int columnaTotalTexto = 0; // puedes ponerlo en la primera columna o donde prefieras
        for (int i = 0; i < columnas; i++) {
            int centroColumna = x + anchosColumnas[i] / 2;
            if (i == columnaTotalTexto) {
                canvas.drawText(textoTotal, centroColumna - paintTexto.measureText(textoTotal) / 2, y, paintTexto);
            } else if (i == idxCantidadCacao) {
                String totalCantStr = decimalFormat.format(sumaCantidadCacao);
                canvas.drawText(totalCantStr, centroColumna - paintTexto.measureText(totalCantStr) / 2, y, paintTexto);
            } else if (i == idxTotalPago) {
                String totalPagoStr = decimalFormat.format(sumaTotalPago);
                canvas.drawText(totalPagoStr, centroColumna - paintTexto.measureText(totalPagoStr) / 2, y, paintTexto);
            } else if (i == idxPagoDeCacao) {
                String totalPagoDeCacaoStr = decimalFormat.format(sumaPagoDeCacao);
                canvas.drawText(totalPagoDeCacaoStr, centroColumna - paintTexto.measureText(totalPagoDeCacaoStr) / 2, y, paintTexto);
            }
            x += anchosColumnas[i];
        }

        // Línea horizontal inferior fila total
        canvas.drawLine(margenIzquierdo, y + 10, margenIzquierdo + sumaAnchos, y + 10, paintLinea);

        // Líneas verticales tabla completas
        x = margenIzquierdo;
        int yLineaInferiorFinal = y + 10;
        for (int i = 0; i <= columnas; i++) {
            canvas.drawLine(x, yLineaSuperior, x, yLineaInferiorFinal, paintLinea);
            if (i < columnas) x += anchosColumnas[i];
        }

        // Línea inferior tabla (debajo fila total)
        canvas.drawLine(margenIzquierdo, yLineaInferiorFinal, margenIzquierdo + sumaAnchos, yLineaInferiorFinal, paintLinea);

        documento.finishPage(pagina);

        File directorioDescargas = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        if (!directorioDescargas.exists()) {
            directorioDescargas.mkdirs();
        }

        String nombreArchivo = "Inventario_Cacao.pdf";
        File archivoPDF = new File(directorioDescargas, nombreArchivo);

        try {
            documento.writeTo(new FileOutputStream(archivoPDF));
            Toast.makeText(context, "Archivo PDF generado en Descargas", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al generar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        documento.close();
        baseDatos.close();
    }
}