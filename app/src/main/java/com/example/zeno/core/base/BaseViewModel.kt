package com.example.zeno.core.base

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.CoroutineExceptionHandler

/**
 * Base ViewModel that provides a global [CoroutineExceptionHandler].
 *
 * All ViewModels that extend this class get automatic crash protection:
 * - The real exception is logged to Logcat.
 * - A generic, user-friendly Toast is shown.
 * - The app never crashes due to unhandled coroutine exceptions.
 */
abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("ZenoError", "Unhandled exception in ViewModel", throwable)
        Toast.makeText(
            getApplication(),
            "حدث خطأ غير متوقع، يرجى المحاولة لاحقاً",
            Toast.LENGTH_SHORT
        ).show()
    }
}
