package org.elnix.dragonlauncher.utils.logs

fun Any.logD(tag: String = this::class.java.simpleName, message: String) {
    android.util.Log.d(tag, message)
    DragonLogManager.log(android.util.Log.DEBUG, tag, message)
}

fun Any.logI(tag: String = this::class.java.simpleName, message: String) {
    android.util.Log.i(tag, message)
    DragonLogManager.log(android.util.Log.INFO, tag, message)
}

fun Any.logW(tag: String = this::class.java.simpleName, message: String) {
    android.util.Log.w(tag, message)
    DragonLogManager.log(android.util.Log.WARN, tag, message)
}

fun Any.logE(tag: String = this::class.java.simpleName, message: String, throwable: Throwable? = null) {
    android.util.Log.e(tag, message, throwable)
    DragonLogManager.log(android.util.Log.ERROR, tag, message, throwable)
}
