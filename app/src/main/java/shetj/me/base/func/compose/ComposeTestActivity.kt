package shetj.me.base.func.compose

import android.os.Bundle
import android.transition.Slide
import android.view.Gravity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import com.google.android.material.transition.platform.MaterialSharedAxis
import shetj.me.base.R
import shetj.me.base.func.compose.ui.theme.BaseKitTheme

@OptIn(ExperimentalMaterial3Api::class)
class ComposeTestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val exit = MaterialSharedAxis(MaterialSharedAxis.X,true).apply {
            // Only run the transition on the contents of this activity, excluding
            // system bars or app bars if provided by the app’s theme.
            addTarget(R.id.container)
            excludeTarget(android.R.id.statusBarBackground, true)
            excludeTarget(android.R.id.navigationBarBackground, true)
        }
        // Allow Activity A’s exit transition to play at the same time as this Activity’s
        // enter transition instead of playing them sequentially.
        window.enterTransition = exit
        window.allowEnterTransitionOverlap = true
        super.onCreate(savedInstanceState)
        setContent {
            BaseKitTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(title = { Text(text = "Compose") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(imageVector = Icons.Filled.ArrowBackIosNew, contentDescription = "")
                                }
                            })
                    }) {
                    Surface(modifier = Modifier.padding(it), color = MaterialTheme.colorScheme.background) {
                        Greeting("Android")
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(
    showBackground = true,
    wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE
)
@Composable
fun GreetingPreview() {
    BaseKitTheme {
        Greeting("Android")
    }
}