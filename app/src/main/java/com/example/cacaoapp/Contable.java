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

public class Contable extends AppCompatActivity {
    private TextView cantidad;
    private TextView pago;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contable);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        cantidad = findViewById(R.id.txtContadorCantidad);
        pago = findViewById(R.id.txtContadorPago);
        contadores();
    }

    private void contadores() {
        // Creamos una instancia del helper para abrir la base de datos en modo lectura
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(this, "Motor de Base de Datos", null, 1);
        SQLiteDatabase baseDatos = motorDato.getReadableDatabase();

        // Ejecutamos una consulta SQL que suma todas las cantidades de cacao y los pagos
        Cursor cursor = baseDatos.rawQuery(
                "SELECT SUM(CantidadCacao) AS TotalCacao, SUM(PagoDeCacao) AS TotalPago FROM inventario", null);

        // Verificamos si el cursor contiene al menos una fila
        if (cursor.moveToFirst()) {

            // Obtenemos los índices de las columnas del resultado
            int idxCantidad = cursor.getColumnIndex("TotalCacao");
            int idxPago = cursor.getColumnIndex("TotalPago");

            // Verificamos que los índices existan correctamente
            if (idxCantidad != -1 && idxPago != -1) {
                // Leemos los valores numéricos sumados de las columnas
                double totalCantidad = cursor.getDouble(idxCantidad);
                double totalPago = cursor.getDouble(idxPago);

                // Usamos un formato para que los números se vean bonitos (ej: 1,234.00)
                java.text.DecimalFormat formato = new java.text.DecimalFormat("#,###.00");

                // Mostramos los datos en los TextView (se supone que ya están inicializados: cantidad y pago)
                cantidad.setText(formato.format(totalCantidad));
                pago.setText(formato.format(totalPago));
            } else {
                // Si las columnas no existen o hubo un error al encontrarlas
                cantidad.setText("Columnas no encontradas");
                pago.setText("Columnas no encontradas");
            }
        } else {
            // Si la tabla está vacía y no devuelve ninguna fila
            cantidad.setText("No hay datos");
            pago.setText("No hay datos");
        }

        // Cerramos el cursor y la base de datos para liberar recursos
        cursor.close();
        baseDatos.close();
    }
}
