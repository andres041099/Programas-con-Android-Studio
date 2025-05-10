package com.example.cacaoapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Formulario extends AppCompatActivity {
private EditText Dvendedor, Dcliente, Dcantidad, Dprecio, Dpago;
private Spinner Dtipo;
private TextView solucion;
private  String fechas;
    public  double cantidadCacaoBruto;
    public  double precioBruto;
    public  double porCentajePrecio= 50;
    public  double porcentajeQuintal= 0.38;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Dvendedor =(EditText)findViewById(R.id.txtVendedor);
        Dcliente =(EditText)findViewById(R.id.txtCliente);
        Dcantidad =(EditText)findViewById(R.id.txtcantidades);
        Dprecio =(EditText)findViewById(R.id.txtprecio);
        Dpago =(EditText)findViewById(R.id.txtpago);
        Dtipo =(Spinner) findViewById(R.id.tipo);
        solucion=(TextView)findViewById(R.id.txtresultados);

        String [] selectorTipo = {"VERDE","SECO"};
        ArrayAdapter <String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_items_menucacao, selectorTipo);
        Dtipo.setAdapter(adapter);
    }
    public void Guardar(View view){
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(this, "Motor de Base de Datos", null,1);
        SQLiteDatabase baseDatos= motorDato.getWritableDatabase();
        fechas = obtenerFechaActual();
        String vendedor = Dvendedor.getText().toString();
        String cliente = Dcliente.getText().toString();
        String cantidad = Dcantidad.getText().toString();
        String tipo = Dtipo.getSelectedItem().toString();
        String precio = Dprecio.getText().toString();
        String pago = Dpago.getText().toString();

        if (!vendedor.isEmpty() && !cliente.isEmpty() && !cantidad.isEmpty() && !pago.isEmpty() && !precio.isEmpty()) {
            ContentValues guardar = new ContentValues();
            guardar.put("Fecha", fechas);
            guardar.put("Vendedor", vendedor);
            guardar.put("Cliente", cliente);
            guardar.put("Tipocacao", tipo);
            guardar.put("CantidadCacao", cantidad);
            guardar.put("PrecioAPagar", precio);
            guardar.put("PagoDeCacao", pago); // ← Corregido

            baseDatos.insert("inventario", null, guardar);
            baseDatos.close();

            Dvendedor.setText("");
            Dcliente.setText("");
            Dcantidad.setText("");
            Dprecio.setText("");
            Dpago.setText("");
            Toast.makeText(getApplicationContext(),"Felicidades, se ha guardado exitosamente.",Toast.LENGTH_SHORT).show();
            solucion.setText("Felicidades, se ha guardado exitosamente.");
        } else {
            Toast.makeText(getApplicationContext(),"Campos vacíos. Por favor, intenta llenar los campos",Toast.LENGTH_SHORT).show();
            solucion.setText("Campos vacíos. Por favor, intenta llenar los campos.");
        }

    }
    public void Calcular(View view){
        if (Dvendedor.getText().toString().trim().isEmpty() ||
                Dcliente.getText().toString().trim().isEmpty() ||
                Dcantidad.getText().toString().trim().isEmpty() ||
                Dprecio.getText().toString().trim().isEmpty()){
            Toast.makeText(getApplicationContext(),"No se puede calcular esta cifra intetalo en otra ocacion",Toast.LENGTH_SHORT).show();
        } else{
            CalculoCacao();
        }
    }
    //Funcion para Buscar datos
    public void buscar(View view) {
        // Instancia la clase que gestiona la base de datos SQLite
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(this, "Motor de Base de Datos", null, 1);

        // Obtiene acceso de lectura y escritura a la base de datos
        SQLiteDatabase baseDatos = motorDato.getWritableDatabase();

        // Toma el valor del campo de texto del cliente y le quita espacios extras
        String cliente = Dcliente.getText().toString().trim();

        // Verifica si el campo del cliente no está vacío
        if (!cliente.isEmpty()) {
            // Realiza una consulta SQL usando un parámetro (evita inyección SQL)
            Cursor filaTabla = baseDatos.rawQuery(
                    "SELECT Vendedor, Tipocacao, CantidadCacao, PrecioAPagar, PagoDeCacao FROM inventario WHERE Cliente = ?",
                    new String[]{cliente});

            // Si hay resultados en la consulta (el cliente existe en la base de datos)
            if (filaTabla.moveToFirst()) {
                // Obtiene los valores por índice de columna (de 0 a 4) y los muestra en los campos correspondientes

                // Coloca el valor del vendedor en el campo Dvendedor
                Dvendedor.setText(filaTabla.getString(0)); // Índice 0: Vendedor

                // Obtiene el tipo de cacao (HUMEDO o SECO)
                String tipo = filaTabla.getString(1); // Índice 1: Tipocacao

                // Selecciona la opción correcta en el Spinner dependiendo del tipo de cacao
                if (tipo.equalsIgnoreCase("SECO")) {
                    Dtipo.setSelection(1); // Si el tipo es SECO, selecciona la posición 1
                } else {
                    Dtipo.setSelection(0); // Si no es SECO (por ejemplo HUMEDO), selecciona la posición 0
                }

                // Coloca los otros datos en los campos correspondientes
                Dcantidad.setText(filaTabla.getString(2)); // Índice 2: CantidadCacao
                Dprecio.setText(filaTabla.getString(3));   // Índice 3: PrecioAPagar
                Dpago.setText(filaTabla.getString(4));     // Índice 4: PagoDeCacao

                // Muestra un mensaje indicando que se encontraron los datos
                Toast.makeText(getApplicationContext(), "Datos encontrados correctamente", Toast.LENGTH_SHORT).show();
            } else {
                // Si no se encuentra el cliente, se muestra este mensaje
                Toast.makeText(getApplicationContext(), "No se encontró el cliente", Toast.LENGTH_SHORT).show();
            }

            // Cierra el cursor y la base de datos después de usarla
            filaTabla.close();
            baseDatos.close();
        } else {
            // Si el campo del cliente está vacío, se notifica al usuario
            Toast.makeText(getApplicationContext(), "Por favor, llena el campo del cliente", Toast.LENGTH_SHORT).show();
        }
    }
    //Funcion para modificar datos
    public void modificarTabla(View view) {
        // Creamos una instancia del helper para interactuar con la base de datos SQLite
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(this, "Motor de Base de Datos", null, 1);

        // Obtenemos acceso de escritura a la base de datos
        SQLiteDatabase baseDatos = motorDato.getWritableDatabase();

        // Extraemos los datos ingresados por el usuario desde los componentes de la interfaz
        String vendedor = Dvendedor.getText().toString().trim();         // Campo de texto para el vendedor
        String cliente = Dcliente.getText().toString().trim();           // Campo de texto para el cliente
        String cantidad = Dcantidad.getText().toString().trim();         // Campo de texto para la cantidad de cacao
        String tipo = Dtipo.getSelectedItem().toString().trim();         // Elemento seleccionado del Spinner para tipo de cacao
        String precio = Dprecio.getText().toString().trim();             // Campo de texto para el precio a pagar
        String pago = Dpago.getText().toString().trim();                 // Campo de texto para el pago recibido

        // Verificamos que ninguno de los campos esté vacío
        if (!vendedor.isEmpty() && !cliente.isEmpty() && !cantidad.isEmpty() &&
                !tipo.isEmpty() && !precio.isEmpty() && !pago.isEmpty()) {

            // Creamos un objeto ContentValues para almacenar los datos que serán actualizados
            ContentValues datosActualizados = new ContentValues();
            datosActualizados.put("Vendedor", vendedor);                  // Asignamos nuevo valor para el campo "Vendedor"
            datosActualizados.put("Tipocacao", tipo);                     // Asignamos nuevo valor para el campo "Tipocacao"
            datosActualizados.put("CantidadCacao", cantidad);            // Asignamos nuevo valor para el campo "CantidadCacao"
            datosActualizados.put("PrecioAPagar", precio);               // Asignamos nuevo valor para el campo "PrecioAPagar"
            datosActualizados.put("PagoDeCacao", pago);                  // Asignamos nuevo valor para el campo "PagoDeCacao"

            // Realizamos la operación de actualización en la tabla 'inventario'
            // Usamos WHERE con parámetros seguros (evita inyecciones SQL y errores por texto malformado)
            int filasModificadas = baseDatos.update(
                    "inventario",                     // Nombre de la tabla
                    datosActualizados,                // Datos nuevos que se actualizarán
                    "Cliente = ?",                    // Condición WHERE para seleccionar la fila (por Cliente)
                    new String[]{ cliente }           // Argumento que reemplaza el "?" en el WHERE
            );

            // Cerramos la base de datos para liberar recursos del sistema
            baseDatos.close();

            // Verificamos si se modificó exactamente una fila
            if (filasModificadas == 1) {
                // Si todo salió bien, mostramos mensaje de éxito al usuario
                String mensaje = "Felicidades " + cliente + ", ha sido modificado correctamente.";
                Toast.makeText(Formulario.this, mensaje, Toast.LENGTH_SHORT).show(); // Mostrar mensaje en pantalla
                solucion.setText(mensaje); // Mostrar mensaje en un TextView en la interfaz
            } else {
                // Si no se modificó ninguna fila, puede que el cliente no exista
                String mensaje = "No se pudo modificar. Verifica si el cliente existe o si los datos están correctos.";
                Toast.makeText(Formulario.this, mensaje, Toast.LENGTH_SHORT).show(); // Mostrar error al usuario
                solucion.setText(mensaje); // Mostrar error en el TextView
            }

        } else {
            // Si uno o más campos están vacíos, avisamos al usuario
            String mensaje = "Campos vacíos. Por favor, completa todos los datos.";
            Toast.makeText(Formulario.this, mensaje, Toast.LENGTH_SHORT).show(); // Mostrar advertencia
            solucion.setText(mensaje); // Mostrar advertencia en el TextView
        }
    }
    public void eliminarTabla(View view){
        mostrarDialogoConfirmacion();
    }

    public void VerInventario(View view){
        Intent Inventario = new Intent(this,Inventario.class);
        startActivity(Inventario);
    }
    public void Calculadora(View view){
        Intent calculadora = new Intent(this,MainActivity.class);
        startActivity(calculadora);
    }
    public void CalculoCacao(){
        String cantida= Dcantidad.getText().toString();
        String tprecio= Dprecio.getText().toString();
        double cantidadCacao= Double.parseDouble(cantida);
        double precioCacao= Double.parseDouble(tprecio);
        cantidadCacaoBruto= cantidadCacao;
        precioBruto= precioCacao;
        cantidadCacao = Math.floor(cantidadCacaoBruto * porcentajeQuintal);
        precioCacao = precioBruto / porCentajePrecio;
        double resultadoCacao = cantidadCacao * precioCacao;
        DecimalFormat formato = new DecimalFormat("#,###.00");
        String resultado = formato.format(resultadoCacao);
        solucion.setText(resultado+"$");
    }
    public void InabilitarBotones(){
        Toast.makeText(getApplicationContext(),"Boton no Programado. Funcionara cuando el Desarrolador lo Termine",Toast.LENGTH_SHORT).show();
    }
    public String obtenerFechaActual() {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date fecha = new Date();
        return formato.format(fecha);
    }
    public void eliminarDatosDeTabla(){
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(this, "Motor de Base de Datos", null,1);
        SQLiteDatabase baseDatos= motorDato.getWritableDatabase();
        int filasAfectadas = baseDatos.delete("inventario", null, null);

        // Reinicia el contador del ID autoincremental
        baseDatos.execSQL("DELETE FROM sqlite_sequence WHERE name='inventario'");
        baseDatos.close();// Cierra la base de datos

        if (filasAfectadas > 0) {
            Toast.makeText(Formulario.this, "Felicidades El cacao fue despachado", Toast.LENGTH_SHORT).show();
            solucion.setText("Felicidades El cacao fue despachado");
        } else {
            Toast.makeText(Formulario.this, "No hay caco a Despachar, porfavor intente llenar el formulario", Toast.LENGTH_SHORT).show();
            solucion.setText("No hay caco a Despachar, porfavor intente llenar el formulario");
        }
    }
    private void mostrarDialogoConfirmacion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar de Despache de cacao");
        builder.setMessage("¿Estás seguro de que deseas despachar el cacao?");

        builder.setPositiveButton("Sí", (dialog, which) -> eliminarDatosDeTabla());

        builder.setNegativeButton("No", (dialog, which) -> {
            Toast.makeText(Formulario.this, "No se pudo despachar el cacao, intentelo en otro momento", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        builder.show();
    }

        // TODO: creacion de sistema de facturacion usando un itent nuevo Realizar Domingo.
        // TODO: creacion un cajas de texto donde se vera o modificara los datos de la factura Realizar el Martes.
        // TODO: creacion crear botones que permitan buscar, borrar, exportar a pdf, imprimir, compartir Realizar el Martes.
        // TODO: Modificar la fecha con el horario de republica Dominicana Realizar el Miercoles.
        //TODO: Hacer que los datos introducidos se dividan por coma Realizar el Miercoles.
       //TODO: Diseñar de manera apropiada las interfaces Realizar el .
}