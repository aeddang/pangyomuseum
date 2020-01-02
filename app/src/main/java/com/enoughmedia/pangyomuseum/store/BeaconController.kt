package com.enoughmedia.pangyomuseum.store

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.RemoteException
import android.widget.Toast
import com.enoughmedia.pangyomuseum.PageID
import com.enoughmedia.pangyomuseum.R
import com.lib.page.PagePresenter
import com.lib.page.PageRequestPermission
import com.lib.util.Log
import io.reactivex.subjects.PublishSubject
import org.altbeacon.beacon.*


class BeaconController (val ctx: Context, val setting: SettingPreference, val museum: Museum): BeaconConsumer{
    private val appTag = javaClass.simpleName
    private var beaconManager:BeaconManager? = null
    private var regions = museum.mounds.map { Region(it.id, null, null, Identifier.fromInt(it.findBeaconID)) }
    private val UNIQUE_ID = "mounds"

    fun initManager(){
        if(!setting.getUseBecon()) return
        PagePresenter.getInstance<PageID>()
            .requestPermission(arrayOf(
                Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
                object : PageRequestPermission {
                    override fun onRequestPermissionResult(resultAll: Boolean, permissions: List<Boolean>?) {
                        Log.d(appTag, "resultAll $resultAll")
                        permissions?.let { permission ->
                            var log = ""
                            permission.forEachIndexed { index, b ->
                                log += " $index $b"
                            }
                            Log.d(appTag, "permissions $log")
                        }

                        if (resultAll) start()
                        else Toast.makeText(ctx, R.string.notice_need_permission, Toast.LENGTH_LONG).show()
                    }
                })
    }


    private var isChecked = false
    private fun start(){

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.isEnabled) {
            if(!isChecked){
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                PagePresenter.getInstance<PageID>().activity?.getCurrentActivity()?.startActivityForResult(enableBtIntent,1)
                isChecked = true
                return
            }else{
                Toast.makeText(ctx, R.string.notice_need_bluetooth, Toast.LENGTH_LONG).show()
            }
        }
        beaconManager = BeaconManager.getInstanceForApplication(ctx)
        beaconManager?.beaconParsers?.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"))
        beaconManager?.foregroundScanPeriod = 500L
        beaconManager?.foregroundBetweenScanPeriod = 500L

        beaconManager?.bind(this)
    }

    fun destroyManager(){
        beaconManager?.unbind(this)
        regions.forEach {  beaconManager?.stopMonitoringBeaconsInRegion(it) }
        beaconManager = null
    }

    override fun getApplicationContext(): Context = ctx
    override fun unbindService(p0: ServiceConnection?) {
        Log.i(appTag, "unbindService")
    }

    override fun bindService(p0: Intent?, p1: ServiceConnection?, p2: Int): Boolean {
        Log.i(appTag, "bindService")
        return true
    }


    enum class Event{ EnterRegion,ExitRegion ,DetermineStateForRegion}
    data class BeaconEvent(val type: Event, val value: String? = null)
    val observable = PublishSubject.create<BeaconEvent>()

    override fun onBeaconServiceConnect() {
        beaconManager?.removeAllMonitorNotifiers()
        beaconManager?.addMonitorNotifier(object : MonitorNotifier {
            override fun didEnterRegion(region: Region?) {
                Toast.makeText(ctx, R.string.notice_beacon_find, Toast.LENGTH_LONG).show()
                Log.i(appTag, "${region.toString()} ${region?.uniqueId} ${region?.getIdentifier(0)} ")
                observable.onNext(BeaconEvent(Event.EnterRegion, region?.uniqueId))
            }

            override fun didExitRegion(region: Region?) {
                Toast.makeText(ctx, R.string.notice_beacon_out, Toast.LENGTH_LONG).show()
                Log.i(appTag, "I no longer see an beacon")
                observable.onNext(BeaconEvent(Event.ExitRegion, region?.uniqueId))
            }

            override fun didDetermineStateForRegion(state: Int, region: Region?) {
                Log.i(appTag, "I have just switched from seeing/not seeing beacons: $state")
                observable.onNext(BeaconEvent(Event.DetermineStateForRegion))
            }
        })

        try {
            regions.forEach {
                beaconManager?.startMonitoringBeaconsInRegion(it)
            }
        } catch (e: RemoteException) {
        }
    }


}