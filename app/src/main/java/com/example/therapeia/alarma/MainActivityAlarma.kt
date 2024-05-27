package com.example.therapeia.alarma

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.therapeia.AdapterClasses.User_Adapter_Chat
import com.example.therapeia.MainActivity
import com.example.therapeia.MainActivityPaciente
import com.example.therapeia.databinding.ActivityPrinciAlarmBinding
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class MainActivityAlarma : AppCompatActivity() {


    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityPrinciAlarmBinding
    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var progressDialog: ProgressDialog

    companion object {
        private var alarmCounter = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinciAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        createNotificationChannel()

        binding.seleccionarBtn.setOnClickListener {
            showTimePicker()
        }

        binding.cancelarBtn.setOnClickListener {
            cancelAlarm()
        }
    }

    private fun cancelAlarm() {
        if (!::calendar.isInitialized) {
            Toast.makeText(this, "Seleccione la hora primero", Toast.LENGTH_SHORT).show()
            return
        }

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val uniqueId = alarmCounter++
        pendingIntent = PendingIntent.getBroadcast(
            this,
            uniqueId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        Toast.makeText(this, "Alarma Cancelada", Toast.LENGTH_LONG).show()
    }

    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Seleccionar hora de la alarma")
            .build()

        picker.show(supportFragmentManager, "foxandroid")

        picker.addOnPositiveButtonClickListener {
            val formattedTime = if (picker.hour > 12) {
                String.format("%02d", picker.hour - 12) + ":" + String.format(
                    "%02d",
                    picker.minute
                ) + "PM"
            } else {
                String.format("%02d", picker.hour) + ":" + String.format(
                    "%02d",
                    picker.minute
                ) + "AM"
            }

            binding.selectedTime.text = formattedTime

            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
            val TAG = "SearchFragmentComprobante"

            // Imprimir si userAdapterChat es nulo o no
            Log.d(TAG, "userAdapterChat es:  ${calendar}")
            setAlarm()
        }
    }

     private fun AgregarInfoBD() {
          progressDialog.setMessage("Guardando información...")
          val tiempo = System.currentTimeMillis()
          val uid = firebaseAuth.uid

          val datos_alarma: HashMap<String, Any?> = HashMap()
          datos_alarma["uid"] = "$uid"
          datos_alarma["id_alarm"] = "$tiempo"
          datos_alarma["nombres"] = calendar
          datos_alarma["num_documento"] = ""
          datos_alarma["tiempo_registro"] = tiempo

         val ref = FirebaseDatabase.getInstance().getReference("alarma")
         ref.child("$tiempo")
             .setValue(datos_alarma)
             .addOnSuccessListener {
                 Toast.makeText(applicationContext, "Se agregó la alarma a la base de datos", Toast.LENGTH_SHORT).show()
                 startActivity(Intent(this@MainActivityAlarma, MainActivity::class.java))
                 finishAffinity()
             }
             .addOnFailureListener {e->
                 progressDialog.dismiss()
                 Toast.makeText(applicationContext, "No se agregó la alarma debido a ${e.message}", Toast.LENGTH_SHORT).show()

             }

      }

    private fun setAlarm() {
        if (!::calendar.isInitialized) {
            Toast.makeText(this, "Seleccione la hora primero", Toast.LENGTH_SHORT).show()
            return
        }

        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val uniqueId = alarmCounter++
        pendingIntent = PendingIntent.getBroadcast(
            this,
            uniqueId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )

        Toast.makeText(this, "Alarma configurada correctamente", Toast.LENGTH_SHORT).show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "foxandroidReminderChannel"
            val description = "Canal para el Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("foxandroid", name, importance)
            channel.description = description

            val notificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}