/*
 * MIT License
 *
 * Copyright (c) 2021 SheTieJun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package shetj.me.base.utils;

import android.app.NotificationManager;
import android.content.Context;

/**
 * <code>Notification
 *          //设置勿扰模式---重复来电者
 *         ZenModePriorityUtils.zenMode_switch(this,NotificationManager.Policy.PRIORITY_CATEGORY_REPEAT_CALLERS,false);
 *         //设置勿扰模式---来电
 *         ZenModePriorityUtils.zenMode_switch(this,NotificationManager.Policy.PRIORITY_CATEGORY_CALLS,false);
 *         //设置勿扰模式---媒体(设备有广告，媒体音量不加入勿扰模式)
 *         ZenModePriorityUtils.zenMode_switch(this,NotificationManager.Policy.PRIORITY_CATEGORY_MEDIA,true);
 *         //设置勿扰模式---讯息
 *         ZenModePriorityUtils.zenMode_switch(this,NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES,false);
 *         //设置勿扰模式---闹铃
 *         ZenModePriorityUtils.zenMode_switch(this,NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS,false);
 *         //设置勿扰模式---提醒
 *         ZenModePriorityUtils.zenMode_switch(this,NotificationManager.Policy.PRIORITY_CATEGORY_REMINDERS,false);
 *         //设置勿扰模式---活动
 *         ZenModePriorityUtils.zenMode_switch(this,NotificationManager.Policy.PRIORITY_CATEGORY_EVENTS,false);
 * </>
 */
public class ZenModePriorityUtils {


    public static void zenMode_switch(Context context, int categoryType, boolean allowed) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
        NotificationManager.Policy policy = nm.getNotificationPolicy();
        int priorityCategories = getNewPriorityCategories(policy, allowed, categoryType);
        NotificationManager.Policy newPolicy = new NotificationManager.Policy(priorityCategories, policy.priorityCallSenders, policy.priorityMessageSenders,
                policy.suppressedVisualEffects);
        nm.setNotificationPolicy(newPolicy);

    }


    private static boolean isPriorityCategoryEnabled(NotificationManager.Policy mPolicy, int categoryType) {
        return (mPolicy.priorityCategories & categoryType) != 0;
    }

    private static int getNewPriorityCategories(NotificationManager.Policy mPolicy, boolean allow, int categoryType) {
        int priorityCategories = mPolicy.priorityCategories;
        if (allow) {
            priorityCategories |= categoryType;
        } else {
            priorityCategories &= ~categoryType;
        }
        return priorityCategories;
    }
}
