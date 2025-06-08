package com.example.cacaoapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

private EditText casillaQuintales,casillaCantidad, casillaPrecio;
private TextView resultado;
Spinner envase;
public  double cantidadCacaoBruto;
public  double precioBruto;
public  double porCentajePrecio= 50;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        casillaQuintales=(EditText)findViewById(R.id.txtQuintales);
        casillaCantidad=(EditText)findViewById(R.id.txtCantidad);
        casillaPrecio=(EditText)findViewById(R.id.txtPrecio);
        resultado=(TextView)findViewById(R.id.Resultado);
        envase=(Spinner) findViewById(R.id.Envase);
        String [] envases = {"SACO","CUBO"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_items_tipo_envase, envases);
        envase.setAdapter(adapter);
    }
    public void CalculoCacao(){
        String porcentajeQuintal = casillaQuintales.getText().toString();
        String cantida = casillaCantidad.getText().toString();
        String precio = casillaPrecio.getText().toString();
        String seleccion = envase.getSelectedItem().toString();
        double cantidadCacao = Double.parseDouble(cantida);
        double QuintalesCacao = Double.parseDouble(porcentajeQuintal);
        double precioCacao = Double.parseDouble(precio);
        cantidadCacaoBruto = cantidadCacao;
        precioBruto = precioCacao;
        //Condicion que  determinar si es un saco que reste uno.
        if (seleccion.equals("SACO")) {
            double quintalPromedio = porCentajePrecio / QuintalesCacao;
            String redondeado = String.format("%.2f", quintalPromedio);
            double quintal = Double.parseDouble(redondeado);
           double cantidaReducidaSaco= cantidadCacaoBruto-1;
            cantidadCacao = Math.floor(cantidaReducidaSaco * quintal);
            precioCacao = precioBruto / porCentajePrecio;
            double resultadoCacao = cantidadCacao * precioCacao;

            // Usamos DecimalFormat para agregar el separador de miles
            DecimalFormat formato = new DecimalFormat("#,###.00");
            String resultante = formato.format(resultadoCacao);
            resultado.setText(resultante + "$");
            //Condicion que  determinar si es un cubo que reste 2.
        } else if (seleccion.equals("CUBO")) {
            double quintalPromedio = porCentajePrecio / QuintalesCacao;
            String redondeado = String.format("%.2f", quintalPromedio);
            double quintal = Double.parseDouble(redondeado);
            double cantidaReducidaSaco= cantidadCacaoBruto-2;
            cantidadCacao = Math.floor(cantidaReducidaSaco * quintal);
            precioCacao = precioBruto / porCentajePrecio;
            double resultadoCacao = cantidadCacao * precioCacao;

            // Usamos DecimalFormat para agregar el separador de miles
            DecimalFormat formato = new DecimalFormat("#,###.00");
            String resultante = formato.format(resultadoCacao);
            resultado.setText(resultante + "$");
        }else {
            Toast.makeText(getApplicationContext(),"No se puede calcular esta cifra intetalo en otra ocacion",Toast.LENGTH_SHORT).show();
        }
    }
    public void Calcular(View view){
        if (casillaQuintales.getText().toString().trim().isEmpty() ||
                casillaCantidad.getText().toString().trim().isEmpty() ||
                casillaPrecio.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(),"No se puede calcular esta cifra intetalo en otra ocacion",Toast.LENGTH_SHORT).show();
        } else{
            CalculoCacao();
        }
    }
    public void Formulario(View view){
        Intent formulario = new Intent(this,Formulario.class);
        startActivity(formulario);
    }
    public void verInventario(View view){
        Intent Inventario = new Intent(this,Inventario.class);
        startActivity(Inventario);
    }
    public void verContador(View view){
        Intent contador = new Intent(this,Contable.class);
        startActivity(contador);
    }
    //TODO: Diseñar de manera apropiada las interfaces Realizar el .
}