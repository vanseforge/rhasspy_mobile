package org.rhasspy.mobile.android.uiservices

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.savedstate.ViewTreeSavedStateRegistryOwner
import co.touchlab.kermit.Logger
import org.rhasspy.mobile.android.AndroidApplication
import org.rhasspy.mobile.android.AppTheme
import org.rhasspy.mobile.android.screens.Fab
import org.rhasspy.mobile.nativeutils.OverlayPermission
import org.rhasspy.mobile.settings.AppSettings

object MicrophoneOverlay {
    private val logger = Logger.withTag("MicrophoneOverlay")

    private lateinit var mParams: WindowManager.LayoutParams
    private val lifecycleOwner = CustomLifecycleOwner()

    private val overlayWindowManager by lazy {
        AndroidApplication.Instance.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }


    /**
     * view that's displayed as overlay to start wake word detection
     */
    @OptIn(ExperimentalMaterial3Api::class)
    private val view: ComposeView = ComposeView(AndroidApplication.Instance).apply {
        setContent {
            AppTheme(false) {
                val size = 96.dp

                Fab(
                    modifier = Modifier
                        .size(96.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consumeAllChanges()
                                onDragVertical(dragAmount)
                            }
                        },
                    iconSize = (size.value * 0.4).dp,
                    snackbarHostState = null,
                    viewModel = viewModel()
                )
            }
        }
    }

    private fun onDragVertical(delta: Offset) {
        mParams.y = (mParams.y + delta.y).toInt()
        mParams.x = (mParams.x + delta.x).toInt()
        mParams.gravity = Gravity.NO_GRAVITY
        overlayWindowManager.updateViewLayout(view, mParams)
    }

    init {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // set the layout parameters of the window
            mParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.CENTER
            }
        }

        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        ViewTreeLifecycleOwner.set(view, lifecycleOwner)
        ViewTreeSavedStateRegistryOwner.set(view, lifecycleOwner)

        val viewModelStore = ViewModelStore()
        ViewTreeViewModelStoreOwner.set(view) { viewModelStore }
    }

    //stores old value to only react to changes
    private var showOverlayOldValue = false

    /**
     * start service, listen to showVisualIndication and show the overlay or remove it when necessary
     */
    fun start() {
        logger.d { "start" }

        AppSettings.isMicrophoneOverlayEnabled.value.addObserver {
            if (it != showOverlayOldValue) {
                if (it) {
                    if (OverlayPermission.isGranted()) {
                        overlayWindowManager.addView(view, mParams)
                        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
                    }
                } else if (showOverlayOldValue) {
                    overlayWindowManager.removeView(view)
                    lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
                }
                showOverlayOldValue = it
            }
        }
    }
}