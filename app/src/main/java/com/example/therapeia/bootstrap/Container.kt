package com.example.therapeia.bootstrap

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.PowerManager
import android.os.Vibrator
import android.telephony.TelephonyManager
import android.text.format.DateFormat
import com.example.therapeia.data.stores.SharedRxDataStoreFactory
import com.example.therapeia.data.AlarmsRepository
import com.example.therapeia.data.DataStoreAlarmsRepository
import com.example.therapeia.data.DatastoreMigration
import com.example.therapeia.data.Prefs
import com.example.therapeia.data.contentprovider.DatabaseQuery
import com.example.therapeia.data.contentprovider.SQLiteDatabaseQuery
import com.example.therapeia.domain.AlarmCore
import com.example.therapeia.domain.AlarmSetter
import com.example.therapeia.domain.AlarmStateNotifier
import com.example.therapeia.domain.Alarms
import com.example.therapeia.domain.AlarmsScheduler
import com.example.therapeia.domain.Calendars
import com.example.therapeia.domain.IAlarmsManager
import com.example.therapeia.domain.IAlarmsScheduler
import com.example.therapeia.domain.Store
import com.example.therapeia.logger.BugReporter
import com.example.therapeia.logger.Logger
import com.example.therapeia.logger.LoggerFactory
import com.example.therapeia.logger.loggerModule
import com.example.therapeia.notifications.BackgroundNotifications
import com.example.therapeia.platform.WakeLockManager
import com.example.therapeia.platform.Wakelocks
import com.example.therapeia.receivers.ScheduledReceiver
import com.example.therapeia.services.AlertServicePusher
import com.example.therapeia.services.KlaxonPlugin
import com.example.therapeia.services.PlayerWrapper
import com.example.therapeia.ui.details.AlarmDetailsViewModel
import com.example.therapeia.ui.list.ListViewModel
import com.example.therapeia.ui.main.MainViewModel
import com.example.therapeia.ui.state.BackPresses
import com.example.therapeia.ui.state.UiStore
import com.example.therapeia.ui.themes.DynamicThemeHandler
import com.example.therapeia.ui.toast.ToastPresenter
import com.example.therapeia.util.Optional
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.io.File
import java.util.Calendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.Koin
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.binds
import org.koin.dsl.module

fun Scope.logger(tag: String): Logger {
  return get<LoggerFactory>().createLogger(tag)
}

fun Koin.logger(tag: String): Logger {
  return get<LoggerFactory>().createLogger(tag)
}

fun startKoin(context: Context): Koin {
  // The following line triggers the initialization of ACRA

  val module = module {
    single<DynamicThemeHandler> { DynamicThemeHandler(get()) }
    single<BugReporter> { BugReporter(logger("BugReporter"), context) }
    factory<Context> { context }
    factory(named("dateFormatOverride")) { "none" }
    factory<Single<Boolean>>(named("dateFormat")) {
      Single.fromCallable {
        get<String>(named("dateFormatOverride")).let { if (it == "none") null else it.toBoolean() }
            ?: DateFormat.is24HourFormat(context)
      }
    }

    single<Prefs> {
      val factory = SharedRxDataStoreFactory.create(get(), logger("preferences"))
      Prefs.create(get(named("dateFormat")), factory)
    }

    single<Store> {
      Store(
          alarmsSubject = BehaviorSubject.createDefault(ArrayList()),
          next = BehaviorSubject.createDefault<Optional<Store.Next>>(Optional.absent()),
          sets = PublishSubject.create(),
          events = PublishSubject.create())
    }

    viewModelOf(::MainViewModel)
    viewModelOf(::AlarmDetailsViewModel)
    viewModelOf(::ListViewModel)
    single { UiStore() }
    single { BackPresses() }

    factory { get<Context>().getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    single<AlarmSetter> { AlarmSetter.AlarmSetterImpl(logger("AlarmSetter"), get(), get()) }
    factory { Calendars { Calendar.getInstance() } }
    single<AlarmsScheduler> {
      AlarmsScheduler(get(), logger("AlarmsScheduler"), get(), get(), get())
    }
    factory<IAlarmsScheduler> { get<AlarmsScheduler>() }
    single<AlarmCore.IStateNotifier> { AlarmStateNotifier(get()) }
    single<AlarmsRepository> {
      DataStoreAlarmsRepository.createBlocking(
          datastoreDir = get(named("datastore")),
          logger = logger("DataStoreAlarmsRepository"),
          ioScope = CoroutineScope(Dispatchers.IO),
      )
    }
    single(named("datastore")) { File(get<Context>().applicationContext.filesDir, "datastore") }
    factory { get<Context>().contentResolver }
    single<DatabaseQuery> { SQLiteDatabaseQuery(get()) }
    single { Alarms(get(), get(), get(), get(), get(), get(), logger("Alarms"), get()) } binds
        arrayOf(IAlarmsManager::class, DatastoreMigration::class)
    single { ScheduledReceiver(get(), get(), get(), get()) }
    single { ToastPresenter(get(), get()) }
    single { AlertServicePusher(get(), get(), get(), logger("AlertServicePusher")) }
    single { BackgroundNotifications(get(), get(), get(), get(), get()) }
    factory<Wakelocks> { get<WakeLockManager>() }
    factory<Scheduler> { AndroidSchedulers.mainThread() }
    single { WakeLockManager(logger("WakeLockManager"), get()) }
    factory { get<Context>().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }
    factory { get<Context>().getSystemService(Context.POWER_SERVICE) as PowerManager }
    factory { get<Context>().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager }
    factory { get<Context>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    factory { get<Context>().getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    factory { get<Context>().resources }

    factory(named("volumePreferenceDemo")) {
      KlaxonPlugin(
          log = logger("VolumePreference"),
          playerFactory = { PlayerWrapper(get(), get(), logger("VolumePreference")) },
          prealarmVolume = get<Prefs>().preAlarmVolume.observe(),
          fadeInTimeInMillis = Observable.just(100),
          inCall = Observable.just(false),
          scheduler = get(),
      )
    }
  }

  return startKoin {
        allowOverride(true)
        modules(module)
        modules(loggerModule())
      }
      .koin
}

fun overrideIs24hoursFormatOverride(is24hours: Boolean) {
  loadKoinModules(module { factory(named("dateFormatOverride")) { is24hours.toString() } })
}
