package shetj.me.base.common.manager;

import me.shetj.base.base.BasePresenter;
import me.shetj.base.base.IView;

/**
 * <b>@packageName：</b> shetj.me.base.common.manager<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/10/29 0029<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b> 有些Activity 的活动少  <br>
 */
public class CommonPresenter extends BasePresenter<CommonModel> {
	public CommonPresenter(IView view) {
		super(view);
		model = new CommonModel();
	}
}
