package shetj.me.base.func.koog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import shetj.me.base.R
import shetj.me.base.func.compose.ui.theme.BaseKitTheme
import com.google.android.material.transition.platform.MaterialSharedAxis

class KoogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val exit = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            addTarget(R.id.container)
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
        }
        window.enterTransition = exit
        window.allowEnterTransitionOverlap = true
        super.onCreate(savedInstanceState)

        setContent {
            BaseKitTheme {
                KoogApp(onFinish = { finish() })
            }
        }
    }
}

enum class KoogScreen {
    Chat,
    Settings
}

@OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
private fun KoogApp(onFinish: () -> Unit) {
    var currentScreen by remember { mutableStateOf(KoogScreen.Chat) }
    var targetScreen by remember { mutableStateOf<KoogScreen?>(null) }

    androidx.compose.animation.AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (targetState == KoogScreen.Settings) {
                slideInHorizontally(animationSpec = tween(300)) { it } togetherWith
                        slideOutHorizontally(animationSpec = tween(300)) { -it / 2 }
            } else {
                slideInHorizontally(animationSpec = tween(300)) { -it } togetherWith
                        slideOutHorizontally(animationSpec = tween(300)) { it / 2 }
            }
        },
        label = "ScreenTransition"
    ) { screen ->
        when (screen) {
            KoogScreen.Chat -> KoogChatScreen(
                onBack = onFinish,
                onSettings = { currentScreen = KoogScreen.Settings }
            )
            KoogScreen.Settings -> KoogSettingsScreen(
                onBack = { currentScreen = KoogScreen.Chat }
            )
        }
    }
}
