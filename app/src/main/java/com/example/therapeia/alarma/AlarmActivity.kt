package com.example.therapeia.alarma

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.therapeia.databinding.ActivityAlarmBinding

class AlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAlarmBinding
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtener el AlarmManager y el PendingIntent
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Configuración del mensaje y el botón de cancelar alarma
        binding.stopAlarmButton.setOnClickListener {
            cancelAlarm()
            finish() // Cerrar la actividad
        }
    }

    private fun cancelAlarm() {
        // Cancelar la alarma usando el PendingIntent
        alarmManager.cancel(pendingIntent)

        // Detener el sonido de la alarma y el temporizador en el Receiver
        val receiver = AlarmReceiver()
        receiver.stopAlarm()
    }
}
