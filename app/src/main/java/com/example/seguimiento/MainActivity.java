package com.example.seguimiento;

import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Variables de la interfaz
    private Spinner spinnerArea;
    private EditText nombreEditText, codigoEditText;

    // Adaptador para la lista de registros
    private ArrayAdapter<String> registrosAdapter;

    private String nombreActual = "";

    // Contraseña para eliminar
    private static final String PASSWORD = "Admon123";

    // ViewModel para mantener los datos
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar el ViewModel
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Inicializar los elementos de la interfaz
        spinnerArea = findViewById(R.id.spinner_area);
        nombreEditText = findViewById(R.id.cantidad_manual);
        codigoEditText = findViewById(R.id.edittext_codigo);
        ListView registrosListView = findViewById(R.id.listview_registros);
        Button addButton = findViewById(R.id.button_add);
        Button exportButton = findViewById(R.id.button_exportar);

        // Configurar las opciones del Spinner para el área
        configurarSpinnerArea();

        // Configurar el adaptador para la lista de registros
        registrosAdapter = new ArrayAdapter<>(this, R.layout.list_item_layout, new ArrayList<>());
        registrosListView.setAdapter(registrosAdapter);

        // Cargar registros desde el ViewModel
        actualizarListaRegistros();

        // Listener para procesar lecturas automáticas con la pistola lectora
        codigoEditText.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                procesarLectura();
                return true;
            }
            return false;
        });

        // Botón Añadir Lectura
        addButton.setOnClickListener(view -> procesarLectura());

        // Botón Exportar a Excel
        exportButton.setOnClickListener(view -> exportToExcel());

        // Listener para eliminar un registro con clic largo
        registrosListView.setOnItemLongClickListener((parent, view, position, id) -> {
            if (viewModel.registros.isEmpty()) {
                Toast.makeText(this, "No hay registros para eliminar.", Toast.LENGTH_SHORT).show();
            } else {
                // Solicitar contraseña para eliminar el registro seleccionado
                promptPasswordForDeletion(position);
            }
            return true;
        });
    }


    private void configurarSpinnerArea() {
        // Opciones para el Spinner del área
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.area_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea.setAdapter(adapter);
    }


    private void procesarLectura() {
        String codigo = codigoEditText.getText().toString().trim();
        String nombre = nombreEditText.getText().toString().trim();
        String area = spinnerArea.getSelectedItem().toString();

        if (codigo.isEmpty() || (nombre.isEmpty() && nombreActual.isEmpty()) || area.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }


        if (codigo.length() < 10) {
            Toast.makeText(this, "El código debe tener al menos 10 dígitos.", Toast.LENGTH_SHORT).show();
            codigoEditText.setText("");
            return;
        }


        if (nombre.isEmpty()) {
            nombre = nombreActual;
        }

        String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String talla = codigo.length() >= 2 ? codigo.substring(codigo.length() - 2) : "N/A";
        String sku = codigo.substring(0, 3);

        boolean codigoExistente = false;
        for (Registro registro : viewModel.registros) {
            if (registro.codigo.equals(codigo)) {
                registro.cantidad++;
                codigoExistente = true;
                break;
            }
        }
        if (!codigoExistente) {
            viewModel.registros.add(new Registro(nombre, area, codigo, fecha, talla, sku));
        }
        nombreActual = nombre;
        actualizarListaRegistros();
        codigoEditText.setText("");
        nombreEditText.setText(nombreActual);
        Toast.makeText(this, "Lectura procesada.", Toast.LENGTH_SHORT).show();
    }


    private void actualizarListaRegistros() {
        registrosAdapter.clear();
        for (Registro registro : viewModel.registros) {
            registrosAdapter.add(registro.toString());
        }
        registrosAdapter.notifyDataSetChanged();
    }


    private void eliminarRegistro(int position) {
        viewModel.registros.remove(position);
        actualizarListaRegistros();
        Toast.makeText(this, "Registro eliminado.", Toast.LENGTH_SHORT).show();
    }


    private void exportToExcel() {
        if (viewModel.registros.isEmpty()) {
            Toast.makeText(this, "No hay registros para exportar.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nombre = nombreEditText.getText().toString().trim();
        String area = spinnerArea.getSelectedItem().toString();

        if (nombre.isEmpty() || area.isEmpty()) {
            Toast.makeText(this, "Por favor, asegúrate de ingresar un nombre y seleccionar un área.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Registros");

            // Crear la fila de encabezados
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Nombre");
            headerRow.createCell(1).setCellValue("Área");
            headerRow.createCell(2).setCellValue("Código");
            headerRow.createCell(3).setCellValue("Fecha");
            headerRow.createCell(4).setCellValue("Talla");
            headerRow.createCell(5).setCellValue("SKU");
            headerRow.createCell(6).setCellValue("Cantidad");

            // Crear filas para los registros
            for (int i = 0; i < viewModel.registros.size(); i++) {
                Registro registro = viewModel.registros.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(registro.nombre);
                row.createCell(1).setCellValue(registro.area);
                row.createCell(2).setCellValue(registro.codigo);
                row.createCell(3).setCellValue(registro.fecha);
                row.createCell(4).setCellValue(registro.talla);
                row.createCell(5).setCellValue(registro.sku);
                row.createCell(6).setCellValue(registro.cantidad);
            }

            // Generar el nombre del archivo con el nombre y el área
            String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = nombre + "_" + area + "_" + fecha + ".xlsx";
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File file = new File(downloadsDir, fileName);

            // Escribir el archivo
            FileOutputStream fileOut = new FileOutputStream(file);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            Toast.makeText(this, "Datos exportados correctamente: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();

            // Limpiar los registros y actualizar la interfaz
            viewModel.registros.clear();
            actualizarListaRegistros();
            spinnerArea.setSelection(0); // Restablece el área seleccionada
        } catch (Exception e) {
            Toast.makeText(this, "Error al exportar los datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void promptPasswordForDeletion(int position) {
        EditText passwordInput = new EditText(this);
        passwordInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("Introduce la contraseña para eliminar este producto:")
                .setView(passwordInput)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    String enteredPassword = passwordInput.getText().toString();
                    if (PASSWORD.equals(enteredPassword)) {
                        eliminarRegistro(position);
                    } else {
                        Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}