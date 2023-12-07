package shetj.me.base.func.slidingpane

import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.slidingpanelayout.widget.SlidingPaneLayout

/**
 * Two pane on back pressed callback
 * ```
 * class TwoPaneFragment : Fragment(R.layout.two_pane) {
 *
 *     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
 *         val binding = TwoPaneBinding.bind(view)
 *
 *         // Connect the SlidingPaneLayout to the system back button.
 *         requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
 *             TwoPaneOnBackPressedCallback(binding.slidingPaneLayout))
 *
 *         // Setup the RecyclerView adapter, etc.
 *     }
 * }
 * ```
 *
 * @property slidingPaneLayout
 * @constructor Create empty Two pane on back pressed callback
 */
class TwoPaneOnBackPressedCallback(
    private val slidingPaneLayout: SlidingPaneLayout
) : OnBackPressedCallback(
    // Set the default 'enabled' state to true only if it is slidable (i.e., the panes
    // are overlapping) and open (i.e., the detail pane is visible).
    slidingPaneLayout.isSlideable && slidingPaneLayout.isOpen
), SlidingPaneLayout.PanelSlideListener {

    init {
        slidingPaneLayout.addPanelSlideListener(this)
    }

    override fun handleOnBackPressed() {
        // Return to the list pane when the system back button is pressed.
        slidingPaneLayout.closePane()
    }

    override fun onPanelSlide(panel: View, slideOffset: Float) { }

    override fun onPanelOpened(panel: View) {
        // Intercept the system back button when the detail pane becomes visible.
        isEnabled = true
    }

    override fun onPanelClosed(panel: View) {
        // Disable intercepting the system back button when the user returns to the
        // list pane.
        isEnabled = false
    }
}