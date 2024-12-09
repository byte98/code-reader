package cz.skodaj.codereader.configuration

import android.Manifest
import android.os.Build

/**
 * Object which holds Android specific configuration.
 */
object Android {

    /**
     * Array of all required permissions.
     */
    public final val PERMISSIONS: Array<String> = mutableListOf<String>(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        .toTypedArray()

}