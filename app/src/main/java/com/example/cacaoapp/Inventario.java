package com.example.cacaoapp;

import android.os.Bundle;
import android.widget.TableLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Inventario extends AppCompatActivity {
    private TableLayout tablas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tablas = findViewById(R.id.tabla);

        // Llamar a la clase Consulta para obtener y mostrar los datos
        Consultar.obtenerDatos(this, tablas);
        //TODO:Crear un boton para ver total de la fila en un itent llamado Resultados o Estadistica de Inventario
        //TODO:Poner en una casilla la cantidad de cacao vendido, pago y el precio.
        //TODO: Diseñar de manera apropiada las interfaces Realizar el.
    }
}
