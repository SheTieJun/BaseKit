package me.shetj.base.tools.app;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Keep;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

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


}
