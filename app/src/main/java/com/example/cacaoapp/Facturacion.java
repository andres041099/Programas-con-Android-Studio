package com.example.cacaoapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Facturacion extends AppCompatActivity {
    private TextView comprador, fCliente, fTipo, fCantidad, fPrecio, fPago, fFecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_facturacion);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        comprador = findViewById(R.id.txtComprador);
        fCliente = findViewById(R.id.txtClienteFactura);
        fTipo = findViewById(R.id.txtTipoFactura);
        fCantidad = findViewById(R.id.txtCantidadFactura);
        fPrecio = findViewById(R.id.txtPrecioFactura);
        fPago = findViewById(R.id.txtPagoFactura);
        fFecha = findViewById(R.id.txtFechaFactura);

        mostrarFactura();
    }

    private void mostrarFactura() {
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(this, "Motor de Base de Datos", null, 1);
        SQLiteDatabase baseDatos = motorDato.getReadableDatabase();

        try {
            Cursor cursor = baseDatos.rawQuery(
                    "SELECT Vendedor, Cliente, Tipocacao, CantidadCacao, PrecioAPagar, PagoDeCacao, Fecha\n" +
                            "FROM inventario ORDER BY idInventario DESC LIMIT 1", null);

            if (cursor.moveToFirst()) {
                // Obtener índices de columnas
                int vendedorIndex = cursor.getColumnIndex("Vendedor");
                int clienteIndex = cursor.getColumnIndex("Cliente");
                int tipoCacaoIndex = cursor.getColumnIndex("Tipocacao");
                int cantidadIndex = cursor.getColumnIndex("CantidadCacao");
                int precioIndex = cursor.getColumnIndex("PrecioAPagar");
                int totalIndex = cursor.getColumnIndex("PagoDeCacao");
                int fechaIndex = cursor.getColumnIndex("Fecha");

                if (vendedorIndex != -1 && clienteIndex != -1 && tipoCacaoIndex != -1 &&
                        cantidadIndex != -1 && precioIndex != -1 && totalIndex != -1 && fechaIndex != -1) {

                    // Obtener valores
                    String vendedor = cursor.getString(vendedorIndex);
                    String cliente = cursor.getString(clienteIndex);
                    String tipoCacao = cursor.getString(tipoCacaoIndex);
                    double cantidad = cursor.getDouble(cantidadIndex);
                    double precio = cursor.getDouble(precioIndex);
                    double total = cursor.getDouble(totalIndex);
                    String fecha = cursor.getString(fechaIndex);

                    java.text.DecimalFormat formato = new java.text.DecimalFormat("#,###.00");

                    comprador.setText(vendedor != null ? vendedor : "N/A");
                    fCliente.setText(cliente != null ? cliente : "N/A");
                    fTipo.setText(tipoCacao != null ? tipoCacao : "N/A");
                    fCantidad.setText(formato.format(cantidad));
                    fPrecio.setText(formato.format(precio));
                    fPago.setText(formato.format(total));
                    fFecha.setText(fecha != null ? fecha : "N/A");

                } else {
                    // Alguna columna no existe, muestra datos vacíos
                    mostrarDatosSinDatos();
                }
            } else {
                // No hay datos en el cursor
                mostrarDatosSinDatos();
            }

            cursor.close();
        } catch (Exception e) {
            mostrarDatosError();
            e.printStackTrace();
        }

        baseDatos.close();
    }

    private void mostrarDatosSinDatos() {
        comprador.setText("Sin datos");
        fCliente.setText("Sin datos");
        fTipo.setText("Sin datos");
        fCantidad.setText("Sin datos");
        fPrecio.setText("Sin datos");
        fPago.setText("Sin datos");
        fFecha.setText("Sin datos");
    }

    private void mostrarDatosError() {
        comprador.setText("Error");
        fCliente.setText("Error");
        fTipo.setText("Error");
        fCantidad.setText("Error");
        fPrecio.setText("Error");
        fPago.setText("Error");
        fFecha.setText("Error");
    }

    // TODO: diseño de sistema de facturacion usando un intent nuevo Realizar Domingo.
    // TODO: creación de botones que permitan imprimir, exportar a pdf, compartir, Cancelar.
}
