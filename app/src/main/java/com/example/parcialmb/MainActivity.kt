package com.example.parcialmb

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import java.util.Calendar
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.net.URLDecoder



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "inicio") {
                composable("inicio") {
                    inicio(navController)
                    // Se llama la funcion inicio y se ejecuta la funcion de inicio
                }
                composable("elegirFechayHora/{cedula}/{nombre}/{telefono}") {
                    // Se llama la funcion elegirFechayHora y se le envian los parametros a recibir
                    bts -> elegirFechayHora(
                        navController = navController,
                        cedula = bts.arguments?.getString("cedula") ?: "",
                        nombre = bts.arguments?.getString("nombre") ?: "",
                        telefono = bts.arguments?.getString("telefono") ?: ""
                        // luego de ser preparados los datos que se recibieron de la pantalla de inicio se mandan a la funcion elegirFechayHora
                    )
                }
                composable("detalles/{cedula}/{nombre}/{telefono}/{fecha}/{hora}") {
                    // Se llama la funcion detalles y se le envian los parametros a recibir
                        bts-> detalles(
                    cedula = bts.arguments?.getString("cedula") ?: "",
                    nombre = bts.arguments?.getString("nombre") ?: "",
                    telefono = bts.arguments?.getString("telefono") ?: "",
                    fecha = bts.arguments?.getString("fecha") ?: "",
                    hora = bts.arguments?.getString("hora") ?: "",
                    navController = navController
                        // luego de ser preparados los datos que se recibieron de la pantalla de inicio y la fecha y hora seleccionada por el usuario se mandan a la funcion detalles
                )
                }
            }
        }
    }
}

@Composable
fun inicio(navController: NavController){// se recibe por parametro el navController que nos ayuda a guiar al sistema entre pantallas
// se declaran las variables que se van a usar en la funcion
    var cedula = remember { mutableStateOf("") }
    var nombre = remember { mutableStateOf("") }
    var telefono = remember { mutableStateOf("") }

    //se crea un contenedor llamado Box para mejor UI del usuario y se le da un color de fondo
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE8DEF8))
            .padding(15.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(10.dp))
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // aqui agregamos un texto para que el usuario sepa que es lo que hace
            Text(text = "Ingrese sus datos personales:", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            // Validación de la cédula (solo números y un máximo de 10 dígitos)
            OutlinedTextField(
                value = cedula.value,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.length <= 10) {
                        cedula.value = it
                    }
                },
                label = { Text("Número de Cédula") }
            )

            // Validación de nombre y apellido (solo letras y un máximo de 25 caracteres)
            OutlinedTextField(
                value = nombre.value,
                onValueChange = {
                    if (it.all { char -> char.isLetter() || char.isWhitespace() } && it.length <= 25) {
                        nombre.value = it
                    }
                },
                label = { Text("Nombre y Apellido") }
            )

            // Validación del teléfono (solo números y máximo 10 dígitos)
            OutlinedTextField(
                value = telefono.value,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.length <= 10) {
                        telefono.value = it
                    }
                },
                label = { Text("Número de Teléfono") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            // luego de validar cada dato de entrada se crea una condicion para evitar que se nos escape algun dato erroneo antes de enviar a la siguiente pantalla
            Button(onClick = {
                if (cedula.value.length in 1..10 && nombre.value.isNotEmpty() && telefono.value.length in 1..10) {
                    // se valida si cedula, nombre, telefono tienen los datos necesarios para continuar
                    // en caso de ser correcto se envian los datos a la siguiente pantalla
                    navController.navigate("elegirFechayHora/${cedula.value}/${nombre.value}/${telefono.value}")
                }else{
                    // en el caso que no cumplio con los requisiitos se mostrara un mensaje que le indica al usuario que hace falta para continuar
                    Toast.makeText(navController.context, "Por favor ingrese todos los datos necesarios y validos", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(text = "Continuar", Modifier.padding(16.dp), fontSize = 17.sp)
            }

        }
    }

}


@Composable
// la duncion recibe por parametros los datos introducidos en la funcion inicio
fun elegirFechayHora(navController: NavController, cedula: String, nombre: String, telefono: String) {
    // se crear las variables necesarias y se almacena la fecha y hora actual del sistema
    val cal = Calendar.getInstance()
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE8DEF8))
            .padding(15.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(10.dp))
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Selecciona la fecha y hora de tu cita", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            // se crea un boton con una funcion onclick para activar la seleccion de la fecha a traves del DatePickerDialog
            OutlinedButton(onClick = {
                val datePickerDialog = DatePickerDialog(
                    navController.context,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        fecha = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
            }) {
                Text(text = if (fecha.isNotEmpty()) "Fecha: $fecha" else "Elegir Fecha", fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            // se crea un boton con una funcion onclick para activar la seleccion de la hora a traves del TimePickerDialog
            OutlinedButton(onClick = {
                val timePickerDialog = TimePickerDialog(
                    navController.context,
                    { _, selectedHour, selectedMinute ->
                        hora = "$selectedHour:$selectedMinute"
                    },
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            }) {
                Text(text = if (hora.isNotEmpty()) "Hora: $hora" else "Elegir Hora", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            // aqui se le agrega una validacion para comprobar que el usuario elijio tanto la fecha como la hora para poder continuar
            if (fecha.isNotEmpty() && hora.isNotEmpty()) {
                val encodedNombre = URLEncoder.encode(nombre, StandardCharsets.UTF_8.toString())
                val encodedFecha = URLEncoder.encode(fecha, StandardCharsets.UTF_8.toString())
                val encodedHora = URLEncoder.encode(hora, StandardCharsets.UTF_8.toString())
                // antes de enviar los datos a la siguiente pantalla se codifican para evitar errores de caracteres especiales o algo que nos afecte el funcionamiento
                Button(onClick = {
                    navController.navigate("detalles/${cedula}/${encodedNombre}/${telefono}/${encodedFecha}/${encodedHora}")
                    // se envian los datos a la siguiente pantalla de Detalles
                }) {
                    Text(text = "Confirmar cita", Modifier.padding(16.dp), fontSize = 17.sp)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))

            // Botón para ir hacia atrás, en caso que el usuario se equivoque al digitar sus datos
            // podra ir hacia atras en la pantalla de inicio donde podra volver a colocar sus datos
            ElevatedButton(onClick = {
                navController.popBackStack()
            }) {
                Text(text = "<-- Corregir datos personales")
            }
        }
    }
}


@Composable
// la funcion recibe por parametros los datos introducidos en la funcion elegirFechayHora y la funcion de Inicio
fun detalles( cedula:String, nombre:String, telefono:String, fecha:String, hora:String, navController: NavController){
    val decodedNombre = URLDecoder.decode(nombre, "UTF-8")
    val decodedHora = URLDecoder.decode(hora, "UTF-8")
    // creamos unas variables para decodificar algunos valores para evitar mostrar datos de forma erronea
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFFE8DEF8))
            .padding(15.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(10.dp))
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // aqui dentro del container Box y Column empezamos a mostrar los detalles de la cita que el usuario ha elegido
            Text(text = "Detalles de tu cita", fontSize = 25.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Numero de Cedula: $cedula", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Nombre y Apellido: $decodedNombre", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Número de Teléfono: $telefono", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Feha de la cita: $fecha", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Hora de la cita: $decodedHora", fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Spacer(modifier = Modifier.height(16.dp))

            // por Ultimo agregamos un boton para que el usuario pueda volver a repetir el proceso
            ElevatedButton(onClick = {
                navController.navigate("inicio") {
                    popUpTo("inicio") { inclusive = true }
                }
            }) {
                Text(text = "<-- Volver a generar una cita")
            }
        }
    }

}

