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
    //Metodo para Guardar en la base de Datos
    public void Guardar(View view){
        // Creamos una instancia del administrador de la base de datos.
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(this, "Motor de Base de Datos", null, 1);

        // Abrimos la base de datos en modo escritura.
        SQLiteDatabase baseDatos = motorDato.getWritableDatabase();

        // Obtenemos la fecha actual.
        fechas = obtenerFechaActual();

        // Obtenemos los valores de los campos del formulario.
        String vendedor = Dvendedor.getText().toString().trim();
        String cliente = Dcliente.getText().toString().trim();
        String cantidad = Dcantidad.getText().toString().trim();
        String tipo = Dtipo.getSelectedItem().toString().trim();
        String precio = Dprecio.getText().toString().trim();
        String pago = Dpago.getText().toString().trim();

        // Verificamos que los campos obligatorios no estén vacíos.
        if (!vendedor.isEmpty() && !cliente.isEmpty() && !cantidad.isEmpty() && !precio.isEmpty() && !pago.isEmpty()) {

            // Verificamos si el cliente ya existe usando el método clienteExiste.
            if (clienteExiste(baseDatos, cliente)) {
                // Si el cliente existe, mostramos un mensaje de advertencia.
                Toast.makeText(getApplicationContext(), "Este cliente ya existe. porfavor Cree otro", Toast.LENGTH_SHORT).show();
                solucion.setText("Este cliente ya existe. porfavor Cree otro.");
            } else {
                ContentValues guardar = new ContentValues();
                guardar.put("Fecha", fechas);
                guardar.put("Vendedor", vendedor);
                guardar.put("Cliente", cliente);
                guardar.put("Tipocacao", tipo);
                guardar.put("CantidadCacao", cantidad);
                guardar.put("PrecioAPagar", precio);
                guardar.put("PagoDeCacao", pago);

                // Insertamos los datos en la tabla "inventario".
                baseDatos.insert("inventario", null, guardar);

                // Limpiamos los campos del formulario.
                Dvendedor.setText("");
                Dcliente.setText("");
                Dcantidad.setText("");
                Dprecio.setText("");
                Dpago.setText("");

                // Mostramos un mensaje indicando que la operación fue exitosa.
                Toast.makeText(getApplicationContext(), "Felicidades, se ha guardado exitosamente.", Toast.LENGTH_SHORT).show();
                solucion.setText("Felicidades, se ha guardado exitosamente.");
            }
        } else {
            // Si algún campo está vacío, mostramos un mensaje indicando que se deben llenar los campos.
            Toast.makeText(getApplicationContext(), "Campos vacíos. Por favor, intenta llenar los campos.", Toast.LENGTH_SHORT).show();
            solucion.setText("Campos vacíos. Por favor, intenta llenar los campos.");
        }

        // Cerramos la base de datos.
        baseDatos.close();
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
                    Dtipo.setSelection(0); // Si no es SECO, selecciona la posición 0
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
        // Creamos una instancia del administrador de la base de datos.
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(this, "Motor de Base de Datos", null, 1);

        // Abrimos la base de datos en modo escritura.
        SQLiteDatabase baseDatos = motorDato.getWritableDatabase();

        // Obtenemos los valores del formulario.
        String vendedor = Dvendedor.getText().toString().trim();
        String cliente = Dcliente.getText().toString().trim();
        String cantidad = Dcantidad.getText().toString().trim();
        String tipo = Dtipo.getSelectedItem().toString().trim();
        String precio = Dprecio.getText().toString().trim();
        String pago = Dpago.getText().toString().trim();

        // Verificamos que los campos obligatorios no estén vacíos.
        if (!vendedor.isEmpty() && !cliente.isEmpty() && !cantidad.isEmpty() &&
                !tipo.isEmpty() && !precio.isEmpty() && !pago.isEmpty()) {

            // Verificamos si el cliente existe usando el método clienteExiste.
            if (!clienteExiste(baseDatos, cliente)) {
                // Si el cliente no existe, mostramos un mensaje de advertencia.
                Toast.makeText(this, "No se puede modificar. El cliente no existe.", Toast.LENGTH_SHORT).show();
                solucion.setText("No se puede modificar. El cliente no existe.");
            } else {
                // Si el cliente existe, actualizamos los datos en la base de datos.
                ContentValues datosActualizados = new ContentValues();
                datosActualizados.put("Vendedor", vendedor);
                datosActualizados.put("Tipocacao", tipo);
                datosActualizados.put("CantidadCacao", cantidad);
                datosActualizados.put("PrecioAPagar", precio);
                datosActualizados.put("PagoDeCacao", pago);

                // Realizamos la actualización de los datos en la base de datos.
                int filas = baseDatos.update("inventario", datosActualizados, "Cliente = ?", new String[]{cliente});

                // Verificamos si la actualización fue exitosa.
                if (filas == 1) {
                    // Si la actualización fue exitosa, mostramos un mensaje de éxito.
                    Toast.makeText(this, "Felicidades, ha sido modificado correctamente.", Toast.LENGTH_SHORT).show();
                    solucion.setText("Felicidades, ha sido modificado correctamente.");
                } else {
                    // Si la actualización falló, mostramos un mensaje de error.
                    Toast.makeText(this, "No se pudo modificar correctamente.", Toast.LENGTH_SHORT).show();
                    solucion.setText("No se pudo modificar correctamente.");
                }
            }
        } else {
            // Si algún campo está vacío, mostramos un mensaje indicando que se deben llenar los campos.
            Toast.makeText(this, "Campos vacíos. Por favor, completa todos los datos.", Toast.LENGTH_SHORT).show();
            solucion.setText("Campos vacíos. Por favor, completa todos los datos.");
        }

        // Cerramos la base de datos.
        baseDatos.close();
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
    // Método para realizar el cálculo del cacao
    public void CalculoCacao() {
        // Obtener el texto ingresado en los campos de cantidad y precio
        // Dcantidad y Dprecio son EditText donde el usuario introduce la cantidad y el precio del cacao.
        String cantida = Dcantidad.getText().toString();
        String tprecio = Dprecio.getText().toString();

        // Convertir el texto obtenido a tipo double para realizar cálculos
        // Se usa Double.parseDouble para convertir los valores a números decimales.
        double cantidadCacao = Double.parseDouble(cantida);
        double precioCacao = Double.parseDouble(tprecio);

        // Guardar las cantidades de cacao y precio para uso posterior
        cantidadCacaoBruto = cantidadCacao;
        precioBruto = precioCacao;

        // Realizar un ajuste en la cantidad de cacao multiplicándola por un porcentaje
        // Se usa Math.floor para redondear hacia abajo el valor calculado.
        cantidadCacao = Math.floor(cantidadCacaoBruto * porcentajeQuintal);

        // Ajustar el precio del cacao según un porcentaje
        precioCacao = precioBruto / porCentajePrecio;

        // Calcular el total del cacao multiplicando la cantidad ajustada por el precio ajustado
        double resultadoCacao = cantidadCacao * precioCacao;

        // Formatear el resultado para mostrarlo con separadores de miles y dos decimales
        // DecimalFormat se utiliza para formatear el número de forma legible, en este caso como dinero.
        DecimalFormat formato = new DecimalFormat("#,###.00");

        // Convertir el resultado a una cadena formateada
        String resultado = formato.format(resultadoCacao);

        // Mostrar el resultado en un TextView (suponiendo que 'solucion' es un TextView)
        // Se concatena el signo "$" al resultado para indicar que es un valor monetario.
        solucion.setText(resultado + "$");
    }

    // Método para que los botones no se proboque salidas abructas en el programa poniendo un mensaje en su lugar.
    public void InabilitarBotones(){
        Toast.makeText(getApplicationContext(),"Boton no Programado. Funcionara cuando el Desarrolador lo Termine",Toast.LENGTH_SHORT).show();
    }
    // Método que obtiene la fecha y hora actual en un formato específico.
    public String obtenerFechaActual() {
        // Creamos una instancia de SimpleDateFormat para definir el formato de la fecha y hora.
        // El formato "yyyy-MM-dd HH:mm:ss" muestra la fecha como Año-Mes-Día Hora:Minuto:Segundo.
        // Locale.getDefault() obtiene la configuración regional predeterminada del dispositivo,
        // lo que asegura que el formato de la fecha sea adecuado para el idioma/región del usuario.
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());

        // Creamos una instancia de Date, que representa la fecha y hora actuales.
        Date fecha = new Date();

        // Usamos el método format() del objeto SimpleDateFormat para convertir la fecha en una cadena con el formato especificado.
        return formato.format(fecha);
    }
    // Método que elimina los datos de la tabla "inventario" de la base de datos y reinicia el contador de IDs.
    public void eliminarDatosDeTabla(){
        // Creamos una instancia de AdminSQLiteOpenHelper para poder acceder a la base de datos.
        AdminSQLiteOpenHelper motorDato = new AdminSQLiteOpenHelper(this, "Motor de Base de Datos", null, 1);

        // Abrimos la base de datos en modo escritura.
        SQLiteDatabase baseDatos = motorDato.getWritableDatabase();

        // Ejecutamos una operación de eliminación sobre la tabla "inventario". En este caso, no se especifican condiciones
        // (es decir, se eliminarán todos los registros de la tabla).
        int filasAfectadas = baseDatos.delete("inventario", null, null);

        // Reiniciamos el contador del ID autoincremental de la tabla "inventario", lo cual borra la secuencia de IDs.
        // Esto es importante si queremos que los próximos registros empiecen con el ID 1 nuevamente.
        baseDatos.execSQL("DELETE FROM sqlite_sequence WHERE name='inventario'");

        // Cerramos la base de datos para liberar los recursos.
        baseDatos.close();

        // Verificamos si la operación de eliminación afectó filas, es decir, si se eliminaron registros.
        if (filasAfectadas > 0) {
            // Si se eliminaron filas, mostramos un mensaje de éxito al usuario con un Toast.
            Toast.makeText(Formulario.this, "Felicidades El cacao fue despachado", Toast.LENGTH_SHORT).show();
            // También actualizamos un componente en la interfaz (presumiblemente un TextView) con el mensaje de éxito.
            solucion.setText("Felicidades El cacao fue despachado");
        } else {
            // Si no se eliminaron filas, significa que la tabla estaba vacía. Mostramos un mensaje de error.
            Toast.makeText(Formulario.this, "No hay caco a Despachar, porfavor intente llenar el formulario", Toast.LENGTH_SHORT).show();
            // Actualizamos el componente de la interfaz con un mensaje indicando que no hay cacao para despachar.
            solucion.setText("No hay caco a Despachar, porfavor intente llenar el formulario");
        }
    }
    // Método que muestra un cuadro de diálogo de confirmación antes de despachar el cacao.
    private void mostrarDialogoConfirmacion() {
        // Creamos un objeto AlertDialog.Builder para construir el cuadro de diálogo.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Establecemos el título del cuadro de diálogo.
        builder.setTitle("Confirmar de Despache de cacao");

        // Establecemos el mensaje que aparecerá en el cuadro de diálogo.
        builder.setMessage("¿Estás seguro de que deseas despachar el cacao?");

        // Configuramos el botón positivo del cuadro de diálogo, que se activa si el usuario presiona "Sí".
        // En este caso, si el usuario acepta, se llamará al método eliminarDatosDeTabla().
        builder.setPositiveButton("Sí", (dialog, which) -> eliminarDatosDeTabla());

        // Configuramos el botón negativo del cuadro de diálogo, que se activa si el usuario presiona "No".
        // Si el usuario decide no despachar el cacao, se muestra un Toast indicando que no se pudo despachar el cacao,
        // y luego se cierra el cuadro de diálogo.
        builder.setNegativeButton("No", (dialog, which) -> {
            // Mostramos un mensaje de Toast con la información correspondiente.
            Toast.makeText(Formulario.this, "No se pudo despachar el cacao, intentelo en otro momento", Toast.LENGTH_SHORT).show();
            // Cerramos el cuadro de diálogo.
            dialog.dismiss();
        });

        // Finalmente, mostramos el cuadro de diálogo al usuario.
        builder.show();
    }
    // Método para verificar si un cliente ya existe en la base de datos.
    private boolean clienteExiste(SQLiteDatabase db, String cliente) {
        // Realizamos una consulta SQL para verificar si hay alguna fila con el cliente especificado.
        Cursor cursor = db.rawQuery("SELECT 1 FROM inventario WHERE Cliente = ?", new String[]{cliente});

        // Si el cursor se puede mover al primer resultado, entonces el cliente existe.
        boolean existe = cursor.moveToFirst();

        // Cerramos el cursor para liberar los recursos.
        cursor.close();

        // Devolvemos el resultado: true si existe, false si no.
        return existe;
    }

        // TODO: diseño de sistema de facturacion usando un itent nuevo Realizar Domingo.
        // TODO: creacion un cajas de texto donde se vera o modificara los datos de la factura.
        // TODO: creacion crear botones que permitan buscar, borrar, exportar a pdf, imprimir, compartir.
       //TODO: Diseñar de manera apropiada las interfaces Realizar el .
}