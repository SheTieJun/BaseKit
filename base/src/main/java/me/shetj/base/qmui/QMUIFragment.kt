package me.shetj.base.qmui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.core.view.ViewCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils

import com.qmuiteam.qmui.QMUILog
import com.qmuiteam.qmui.util.QMUIKeyboardHelper
import com.qmuiteam.qmui.util.QMUIViewHelper
import com.trello.rxlifecycle3.components.support.RxFragment

import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

import me.shetj.base.R
import timber.log.Timber
import java.util.*


/**
 * With the use of [QMUIFragmentActivity], [QMUIFragment] brings more features,
 * such as swipe back, transition config, and so on.
 *
 *
 * Created by cgspine on 15/9/14.
 */
abstract class QMUIFragment : RxFragment() {
    private var mSourceRequestCode = NO_REQUEST_CODE
    private var mResultData: Intent? = null
    private var mResultCode = RESULT_CANCELED


    private var mBaseView: View? = null
    private var mCacheSwipeBackLayout: SwipeBackLayout? = null
    private var mCacheRootView: View? = null
    private var isCreateForSwipeBack = false
    private var mBackStackIndex = 0

    private var mEnterAnimationStatus = ANIMATION_ENTER_STATUS_NOT_START
    private var mCalled = true
    private val mDelayRenderRunnableList = ArrayList<Runnable>()

    val baseFragmentActivity: QMUIFragmentActivity?
        get() = activity as QMUIFragmentActivity?

    val isAttachedToActivity: Boolean
        get() = !isRemoving && mBaseView != null

    override fun onDetach() {
        super.onDetach()
        mBaseView = null
    }

    /**
     * see [QMUIFragmentActivity.startFragmentAndDestroyCurrent]
     *
     * @param fragment fragment
     * @param useNewTransitionConfigWhenPop ~
     */
    @JvmOverloads
    protected fun startFragmentAndDestroyCurrent(fragment: QMUIFragment, useNewTransitionConfigWhenPop: Boolean = true) {
        if (targetFragment != null) {
            // transfer target fragment
            fragment.setTargetFragment(targetFragment, targetRequestCode)
            setTargetFragment(null, 0)
        }
        val baseFragmentActivity = this.baseFragmentActivity
        if (baseFragmentActivity != null) {
            if (this.isAttachedToActivity) {
                baseFragmentActivity.startFragmentAndDestroyCurrent(fragment, useNewTransitionConfigWhenPop)
            } else {
                Timber.e("fragment not attached:%s", this)
            }
        } else {
            Timber.e("startFragment null:%s", this)
        }
    }

    protected fun startFragment(fragment: QMUIFragment) {
        val baseFragmentActivity = this.baseFragmentActivity
        if (baseFragmentActivity != null) {
            if (this.isAttachedToActivity) {
                baseFragmentActivity.startFragment(fragment)
            } else {
                Timber.e("fragment not attached:%s", this)
            }
        } else {
            Timber.e("startFragment null:%s", this)
        }
    }

    /**
     * simulate the behavior of startActivityForResult/onActivityResult:
     * 1. Jump fragment1 to fragment2 via startActivityForResult(fragment2, requestCode)
     * 2. Pass data from fragment2 to fragment1 via setFragmentResult(RESULT_OK, data)
     * 3. Get data in fragment1 through onFragmentResult(requestCode, resultCode, data)
     *
     * @param fragment    target fragment
     * @param requestCode request code
     */
    fun startFragmentForResult(fragment: QMUIFragment, requestCode: Int) {
        if (requestCode == NO_REQUEST_CODE) {
            throw RuntimeException("requestCode can not be $NO_REQUEST_CODE")
        }
        fragment.setTargetFragment(this, requestCode)
        mSourceRequestCode = requestCode
        startFragment(fragment)
    }


    fun setFragmentResult(resultCode: Int, data: Intent) {
        val targetRequestCode = targetRequestCode
        if (targetRequestCode == 0) {
            QMUILog.w(TAG, "call setFragmentResult, but not requestCode exists")
            return
        }
        val fragment = targetFragment as? QMUIFragment ?: return
        val targetFragment = fragment

        if (targetFragment.mSourceRequestCode == targetRequestCode) {
            targetFragment.mResultCode = resultCode
            targetFragment.mResultData = data
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragmentManager = fragmentManager
        if (fragmentManager != null) {
            val backStackEntryCount = fragmentManager.backStackEntryCount
            for (i in backStackEntryCount - 1 downTo 0) {
                val entry = fragmentManager.getBackStackEntryAt(i)
                if (javaClass.simpleName == entry.name) {
                    mBackStackIndex = i
                    break
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val requestCode = mSourceRequestCode
        val resultCode = mResultCode
        val data = mResultData

        mSourceRequestCode = NO_REQUEST_CODE
        mResultCode = RESULT_CANCELED
        mResultData = null

        if (requestCode != NO_REQUEST_CODE) {
            onFragmentResult(requestCode, resultCode, data)
        }
    }

    private fun newSwipeBackLayout(): SwipeBackLayout {
        var rootView = mCacheRootView
        if (rootView == null) {
            rootView = onCreateView()
            mCacheRootView = rootView
        } else {
            if (rootView.parent != null) {
                (rootView.parent as ViewGroup).removeView(rootView)
            }
        }

        rootView.fitsSystemWindows = !translucentFull()

        val swipeBackLayout = SwipeBackLayout.wrap(rootView, dragBackEdge(),object :SwipeBackLayout.Callback{
            override fun canSwipeBack(): Boolean {
                if (mEnterAnimationStatus != ANIMATION_ENTER_STATUS_END) {
                    return false
                }
                return canDragBack()
            }
        })
        swipeBackLayout.addSwipeListener(object : SwipeBackLayout.SwipeListener {

            private var mModifiedFragment: QMUIFragment? = null

            override fun onScrollStateChange(state: Int, scrollPercent: Float) {
                Timber.i(TAG, "SwipeListener:onScrollStateChange: state = $state ;scrollPercent = $scrollPercent")
                val container = baseFragmentActivity!!.fragmentContainer
                val childCount = container!!.childCount
                if (state == SwipeBackLayout.STATE_IDLE) {
                    if (scrollPercent <= 0.0f) {
                        for (i in childCount - 1 downTo 0) {
                            val view = container.getChildAt(i)
                            val tag = view.getTag(R.id.base_swipe_layout_in_back)
                            if (SWIPE_BACK_VIEW == tag) {
                                container.removeView(view)
                                if (mModifiedFragment != null) {
                                    // give up swipe back, we should reset the revise
                                    try {
                                        val viewField = Fragment::class.java.getDeclaredField("mView")
                                        viewField.isAccessible = true
                                        viewField.set(mModifiedFragment, null)
                                        val childFragmentManager = mModifiedFragment!!.childFragmentManager
                                        val dispatchCreatedMethod = childFragmentManager.javaClass.getMethod("dispatchCreate")
                                        dispatchCreatedMethod.isAccessible = true
                                        dispatchCreatedMethod.invoke(childFragmentManager)
                                    } catch (e: NoSuchFieldException) {
                                        e.printStackTrace()
                                    } catch (e: NoSuchMethodException) {
                                        e.printStackTrace()
                                    } catch (e: IllegalAccessException) {
                                        e.printStackTrace()
                                    } catch (e: InvocationTargetException) {
                                        e.printStackTrace()
                                    }

                                    mModifiedFragment = null
                                }

                            }
                        }
                    } else if (scrollPercent >= 1.0f) {
                        for (i in childCount - 1 downTo 0) {
                            val view = container.getChildAt(i)
                            val tag = view.getTag(R.id.base_swipe_layout_in_back)
                            if (SWIPE_BACK_VIEW == tag) {
                                container.removeView(view)
                            }
                        }
                        val fragmentManager = fragmentManager
                        Utils.findAndModifyOpInBackStackRecord(fragmentManager, -1,object :  Utils.OpHandler {
                            override fun handle(op: Any): Boolean {
                                val cmdField: Field
                                try {
                                    cmdField = op.javaClass.getDeclaredField("cmd")
                                    cmdField.isAccessible = true
                                    val cmd = cmdField.get(op) as Int
                                    if (cmd == 1) {
                                        val popEnterAnimField = op.javaClass.getDeclaredField("popEnterAnim")
                                        popEnterAnimField.isAccessible = true
                                        popEnterAnimField.set(op, 0)
                                    } else if (cmd == 3) {
                                        val popExitAnimField = op.javaClass.getDeclaredField("popExitAnim")
                                        popExitAnimField.isAccessible = true
                                        popExitAnimField.set(op, 0)
                                    }
                                } catch (e: NoSuchFieldException) {
                                    e.printStackTrace()
                                } catch (e: IllegalAccessException) {
                                    e.printStackTrace()
                                }
                                return false
                            }

                        })
                        popBackStack()
                    }
                }
            }

            override fun onScroll(edgeFlag: Int, scrollPercent: Float) {
                val targetOffset = (Math.abs(backViewInitOffset()) * (1 - scrollPercent)).toInt()
                val container = baseFragmentActivity!!.fragmentContainer
                val childCount = container!!.childCount
                for (i in childCount - 1 downTo 0) {
                    val view = container.getChildAt(i)
                    val tag = view.getTag(R.id.base_swipe_layout_in_back)
                    if (SWIPE_BACK_VIEW == tag) {
                        if (edgeFlag == EDGE_BOTTOM) {
                            ViewCompat.offsetTopAndBottom(view, targetOffset - view.top)
                        } else if (edgeFlag == EDGE_RIGHT) {
                            ViewCompat.offsetLeftAndRight(view, targetOffset - view.left)
                        } else {
                            Timber.i(TAG, "targetOffset = " + targetOffset + " ; view.getLeft() = " + view.left)
                            ViewCompat.offsetLeftAndRight(view, -targetOffset - view.left)
                        }
                    }
                }
            }

            @SuppressLint("PrivateApi")
            override fun onEdgeTouch(edgeFlag: Int) {
                Timber.i(TAG, "SwipeListener:onEdgeTouch: edgeFlag = %s", edgeFlag)
                val fragmentManager = fragmentManager ?: return
                QMUIKeyboardHelper.hideKeyboard(swipeBackLayout)
                val backStackCount = fragmentManager.backStackEntryCount
                if (backStackCount > 1) {
                    try {
                        val backStackEntry = fragmentManager.getBackStackEntryAt(backStackCount - 1)

                        val opsField = backStackEntry.javaClass.getDeclaredField("mOps")
                        opsField.isAccessible = true
                        val opsObj = opsField.get(backStackEntry)
                        if (opsObj is List<*>) {
                            for (op in opsObj) {
                                val cmdField = op!!.javaClass.getDeclaredField("cmd")
                                cmdField.isAccessible = true
                                val cmd = cmdField.get(op) as Int
                                if (cmd == 3) {
                                    val popEnterAnimField = op.javaClass.getDeclaredField("popEnterAnim")
                                    popEnterAnimField.isAccessible = true
                                    popEnterAnimField.set(op, 0)

                                    val fragmentField = op.javaClass.getDeclaredField("fragment")
                                    fragmentField.isAccessible = true
                                    val fragmentObject = fragmentField.get(op)
                                    if (fragmentObject is QMUIFragment) {
                                        mModifiedFragment = fragmentObject
                                        val container = baseFragmentActivity!!.fragmentContainer
                                        mModifiedFragment!!.isCreateForSwipeBack = true
                                        val baseView = mModifiedFragment!!.onCreateView(LayoutInflater.from(context), container, null)
                                        mModifiedFragment!!.isCreateForSwipeBack = false
                                        if (baseView != null) {
                                            baseView.setTag(R.id.base_swipe_layout_in_back, SWIPE_BACK_VIEW)
                                            container!!.addView(baseView, 0)

                                            // handle issue #235
                                            val viewField = Fragment::class.java.getDeclaredField("mView")
                                            viewField.isAccessible = true
                                            viewField.set(mModifiedFragment, baseView)
                                            val childFragmentManager = mModifiedFragment!!.childFragmentManager
                                            val dispatchCreatedMethod = childFragmentManager.javaClass.getMethod("dispatchActivityCreated")
                                            dispatchCreatedMethod.isAccessible = true
                                            dispatchCreatedMethod.invoke(childFragmentManager)

                                            val offset = Math.abs(backViewInitOffset())
                                            if (edgeFlag == EDGE_BOTTOM) {
                                                ViewCompat.offsetTopAndBottom(baseView, offset)
                                            } else if (edgeFlag == EDGE_RIGHT) {
                                                ViewCompat.offsetLeftAndRight(baseView, offset)
                                            } else {
                                                ViewCompat.offsetLeftAndRight(baseView, -1 * offset)
                                            }
                                        }
                                    }
                                }
                            }
                        }


                    } catch (e: NoSuchFieldException) {
                        e.printStackTrace()
                    } catch (e: IllegalAccessException) {
                        e.printStackTrace()
                    } catch (e: NoSuchMethodException) {
                        e.printStackTrace()
                    } catch (e: InvocationTargetException) {
                        e.printStackTrace()
                    }

                } else if (parentFragment == null) {
                    if (activity != null) {
                        activity!!.window.decorView.setBackgroundColor(0)
                        Utils.convertActivityToTranslucent(activity!!)
                    }
                }

            }

            override fun onScrollOverThreshold() {
                Timber.i(TAG, "SwipeListener:onEdgeTouch:onScrollOverThreshold")
            }
        })
        return swipeBackLayout
    }

    private fun canNotUseCacheViewInCreateView(): Boolean {
        return mCacheSwipeBackLayout!!.parent != null || ViewCompat.isAttachedToWindow(mCacheSwipeBackLayout!!)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val swipeBackLayout: SwipeBackLayout
        if (mCacheSwipeBackLayout == null) {
            swipeBackLayout = newSwipeBackLayout()
            mCacheSwipeBackLayout = swipeBackLayout
        } else if (isCreateForSwipeBack) {
            // in swipe back, exactly not in animation
            swipeBackLayout = mCacheSwipeBackLayout as SwipeBackLayout
        } else {

            if (canNotUseCacheViewInCreateView()) {
                // try removeView first
                container!!.removeView(mCacheSwipeBackLayout)
            }

            if (canNotUseCacheViewInCreateView()) {
                // give up!!!
                swipeBackLayout = newSwipeBackLayout()
                mCacheSwipeBackLayout = swipeBackLayout
            } else {
                swipeBackLayout = mCacheSwipeBackLayout as SwipeBackLayout
            }
        }


        if (!isCreateForSwipeBack) {
            mBaseView = swipeBackLayout.contentView
            swipeBackLayout.setTag(R.id.base_swipe_layout_in_back, null)
        }

        ViewCompat.setTranslationZ(swipeBackLayout, mBackStackIndex.toFloat())

        swipeBackLayout.fitsSystemWindows = false

        if (activity != null) {
            QMUIViewHelper.requestApplyInsets(activity!!.window)
        }

        return swipeBackLayout
    }

    fun popBackStack() {
        if (mEnterAnimationStatus != ANIMATION_ENTER_STATUS_END) {
            return
        }
        baseFragmentActivity!!.popBackStack()
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (!enter && parentFragment != null && parentFragment!!.isRemoving) {
            // This is a workaround for the bug where child fragments disappear when
            // the parent is removed (as all children are first removed from the parent)
            // See https://code.google.com/p/android/issues/detail?id=55228
            val doNothingAnim = AlphaAnimation(1f, 1f)
            val duration = resources.getInteger(R.integer.qmui_anim_duration)
            doNothingAnim.duration = duration.toLong()
            return doNothingAnim
        }
        var animation: Animation? = null
        if (enter) {
            try {
                animation = AnimationUtils.loadAnimation(context, nextAnim)

            } catch (ignored: Resources.NotFoundException) {

            } catch (ignored: RuntimeException) {

            }

            if (animation != null) {
                animation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {
                        onEnterAnimationStart(animation)
                    }

                    override fun onAnimationEnd(animation: Animation) {
                        checkAndCallOnEnterAnimationEnd(animation)
                    }

                    override fun onAnimationRepeat(animation: Animation) {

                    }
                })
            } else {
                onEnterAnimationStart(null)
                checkAndCallOnEnterAnimationEnd(null)
            }
        }
        return animation
    }


    private fun checkAndCallOnEnterAnimationEnd(animation: Animation?) {
        mCalled = false
        onEnterAnimationEnd(animation)
        if (!mCalled) {
            throw RuntimeException("QMUIFragment " + this + " did not call through to super.onEnterAnimationEnd(Animation)")
        }
    }


    /**
     * onCreateView
     */
    protected abstract fun onCreateView(): View

    /**
     * Will be performed in onStart
     *
     * @param requestCode request code
     * @param resultCode  result code
     * @param data        extra data
     */
    protected fun onFragmentResult(requestCode: Int, resultCode: Int, data: Intent?) {

    }

    /**
     * disable or enable drag back
     *
     * @return boolean canDragBack
     */
    protected fun canDragBack(): Boolean {
        return true
    }

    /**
     * if enable drag back,
     *
     * @return backViewInitOffset
     */
    protected fun backViewInitOffset(): Int {
        return 0
    }

    protected fun dragBackEdge(): Int {
        return EDGE_LEFT
    }

    /**
     * When data is rendered duration the transition animation, it will cause a choppy. this method
     * will promise the data is rendered before or after transition animation
     *
     * @param runnable the action to perform
     * @param onlyEnd  if true, the action is only performed after the enter animation is finished,
     * otherwise it can be performed before the start of the enter animation start
     * or after the enter animation is finished.
     */
    @JvmOverloads
    fun runAfterAnimation(runnable: Runnable, onlyEnd: Boolean = false) {
        Utils.assertInMainThread()
        val ok = if (onlyEnd)
            mEnterAnimationStatus == ANIMATION_ENTER_STATUS_END
        else
            mEnterAnimationStatus != ANIMATION_ENTER_STATUS_STARTED
        if (ok) {
            runnable.run()
        } else {
            mDelayRenderRunnableList.add(runnable)
        }
    }

    protected fun onEnterAnimationStart(animation: Animation?) {
        mEnterAnimationStatus = ANIMATION_ENTER_STATUS_STARTED
    }

    protected fun onEnterAnimationEnd(animation: Animation?) {
        if (mCalled) {
            throw IllegalAccessError("don't call #onEnterAnimationEnd() directly")
        }
        mCalled = true
        if (mDelayRenderRunnableList.size > 0) {
            for (i in mDelayRenderRunnableList.indices) {
                mDelayRenderRunnableList[i].run()
            }
            mDelayRenderRunnableList.clear()
        }
        mEnterAnimationStatus = ANIMATION_ENTER_STATUS_END
    }

    /**
     * Immersive processing
     *
     * @return if true, the area under status bar belongs to content; otherwise it belongs to padding
     */
    protected fun translucentFull(): Boolean {
        return false
    }

    /**
     * When finishing to pop back last fragment, let activity have a chance to do something
     * like start a new fragment
     *
     * @return QMUIFragment to start a new fragment or Intent to start a new Activity
     */
    fun onLastFragmentFinish(): Any? {
        return null
    }

    /**
     * Fragment Transition Controller
     */
    fun onFetchTransitionConfig(): TransitionConfig {
        return SLIDE_TRANSITION_CONFIG
    }


    class TransitionConfig(val enter: Int, val exit: Int, val popenter: Int, val popout: Int) {

        constructor(enter: Int, popout: Int) : this(enter, 0, 0, popout)
    }

    companion object {
        private val SWIPE_BACK_VIEW = "swipe_back_view"
        private val TAG = QMUIFragment::class.java.simpleName

        /**
         * Edge flag indicating that the left edge should be affected.
         */
        val EDGE_LEFT = SwipeBackLayout.EDGE_LEFT

        /**
         * Edge flag indicating that the right edge should be affected.
         */
        val EDGE_RIGHT = SwipeBackLayout.EDGE_RIGHT

        /**
         * Edge flag indicating that the bottom edge should be affected.
         */
        val EDGE_BOTTOM = SwipeBackLayout.EDGE_BOTTOM

        protected val SLIDE_TRANSITION_CONFIG = TransitionConfig(
                R.anim.slide_in_right, R.anim.slide_out_left,
                R.anim.slide_in_left, R.anim.slide_out_right)

        protected val SCALE_TRANSITION_CONFIG = TransitionConfig(
                R.anim.scale_enter, R.anim.slide_still,
                R.anim.slide_still, R.anim.scale_exit)


        val RESULT_CANCELED = Activity.RESULT_CANCELED
        val RESULT_OK = Activity.RESULT_CANCELED
        val RESULT_FIRST_USER = Activity.RESULT_FIRST_USER

        val ANIMATION_ENTER_STATUS_NOT_START = -1
        val ANIMATION_ENTER_STATUS_STARTED = 0
        val ANIMATION_ENTER_STATUS_END = 1


        private val NO_REQUEST_CODE = 0
    }
}
/**
 * the action will be performed before the start of the enter animation start or after the
 * enter animation is finished
 *
 * @param runnable the action to perform
 */

