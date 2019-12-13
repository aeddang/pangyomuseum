package com.skeleton.view.camera


class CameraViewModel{
    var flashSupported: Boolean = false; internal set
    var isFront:Boolean = false
    var isFlash:Boolean = true
    var permissionGranted: Camera.PermissionGranted = Camera.PermissionGranted.UnChecked

    fun resetPermissionGranted(){
        if(permissionGranted == Camera.PermissionGranted.Denied) permissionGranted = Camera.PermissionGranted.UnChecked
    }

    fun destroy(){

    }

}