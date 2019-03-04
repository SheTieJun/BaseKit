package me.shetj.base.tools.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.Keep;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.Gravity;
import android.view.View;

import java.util.Objects;

@Keep
public class UiUtils {

    /**
     * 当你需要当前界面中的某个元素和新界面中的元素有关时，你可以使用这个动画。效果很赞~！
     */
    public static ActivityOptionsCompat getActivityOptions(Activity activity, View sharedCardView, String  TRANSITION_NAME_CARD) {
        return ActivityOptionsCompat
                .makeSceneTransitionAnimation(activity, sharedCardView, TRANSITION_NAME_CARD);
    }

    /**
     *让新的Activity从一个小的范围扩大到全屏
     */
    public static ActivityOptionsCompat getActivityOptions(View view) {
        return ActivityOptionsCompat.makeScaleUpAnimation(view,
                //The View that the new activity is animating from
                view.getWidth() /2, view.getHeight() /2,
                //拉伸开始的坐标
                0, 0);
        //拉伸开始的区域大小，这里用（0，0）表示从无到全屏
    }

    /**
     *多个元素和新的Activity相关的情况，注意下第二个参数Pair这个键值对后面有...，标明是可以传入多个Pair对象的
     */
    public static ActivityOptionsCompat getActivityOptions(Activity activity, Pair<View, String>... arg1) {
        return  ActivityOptionsCompat.makeSceneTransitionAnimation(activity, arg1);
    }

    public static void startNewAcitivity(Activity activity, ActivityOptionsCompat options, Class activityClass) {
        Intent intent = new Intent(activity,activityClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }


    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setEnterTransition(Activity activity ,  String type){
        switch (type) {
            case  "explode" :
                Explode explode = new Explode();
                explode.setDuration(500L);
                activity.getWindow().setEnterTransition(explode);
                break;
            case "slide" :
                Slide slide =new Slide(Gravity.BOTTOM);
                slide.setDuration(500L);
                activity.getWindow().setEnterTransition( slide);
                break;
            case "fade" :
                Fade fade = new Fade();
                fade.setDuration(500L);
                activity.getWindow().setEnterTransition( fade);
                break;
            default:
                break;
        }
    }


    /**
     * @param slideTransition  = explode(),slide()(),fade
     * @param transition  share view 的transition  一般为changeBound
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setEnterTransition(Fragment fragment, Objects slideTransition, Transition transition){
        fragment.setEnterTransition( slideTransition);
        fragment.setAllowEnterTransitionOverlap(true);
        fragment.setAllowReturnTransitionOverlap(true);
        fragment.setSharedElementEnterTransition(transition);

    }



}
