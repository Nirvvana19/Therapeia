package com.example.therapeia.alarma

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.CountDownTimer
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.therapeia.R

class AlarmReceiver : BroadcastReceiver() {

    private lateinit var ringtone: Ringtone
    private lateinit var timer: CountDownTimer

    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationId = System.currentTimeMillis().toInt() // Generar un identificador único basado en la hora actual

        val notification = createNotification(context!!)
        showNotification(context, notification, notificationId)

        // Iniciar el temporizador de la alarma
        startAlarmTimer(context)
    }

    private fun createNotification(context: Context): NotificationCompat.Builder {
        //Declaramos una instancia para que nos lleve a detener la alarma(Funciona)
        val intent = Intent(context, AlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val vibrationPattern =
            longArrayOf(0, 1000, 500, 1000) // Vibración: 1 seg, pausa: 0.5 seg, vibración: 1 seg

        return NotificationCompat.Builder(context, "foxandroid")
            .setSmallIcon(R.drawable.ic_stop)
            .setContentTitle("Therapeia Alarm")
            .setContentText("¡Es hora de tomar su medicamento!")
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(vibrationPattern)
            .setContentIntent(pendingIntent)
    }

    private fun showNotification(
        context: Context,
        notification: NotificationCompat.Builder,
        notificationId: Int
    ) {
        val notificationManager = NotificationManagerCompat.from(context)
        createNotificationChannel(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return@showNotification
        }
        notificationManager.notify(notificationId, notification.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "foxandroidReminderChannel"
            val description = "Canal para el Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("foxandroid", name, importance)
            channel.description = description

            // Obtener la instancia correcta de NotificationManager
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Crear el canal de notificación
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun startAlarmTimer(context: Context) {
        // Obtener el sonido de la alarma desde los recursos
        val alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        // Reproducir el sonido de la alarma
        ringtone = RingtoneManager.getRingtone(context, alarmSoundUri)
        ringtone.play()

        // Configurar un temporizador para detener el sonido después de cierto tiempo
        timer = object : CountDownTimer(30000, 1000) { // 30 segundos, tick cada segundo
            override fun onTick(millisUntilFinished: Long) {
                // No se requiere ninguna acción en cada tick
            }

            override fun onFinish() {
                // Detener el sonido de la alarma cuando el temporizador finaliza
                ringtone.stop()
            }
        }.start()
    }

    fun stopAlarm() {
        // Detener el sonido de la alarma
        if (::ringtone.isInitialized && ringtone.isPlaying) {
            ringtone.stop()
        }
        // Detener el temporizador
        timer.cancel()
    }
}