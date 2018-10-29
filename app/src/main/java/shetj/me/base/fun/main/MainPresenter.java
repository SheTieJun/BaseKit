package shetj.me.base.fun.main;

import me.shetj.base.base.BasePresenter;
import me.shetj.base.base.IView;

/**
 * <b>@packageName：</b> shetj.me.base.fun<br>
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2018/10/29 0029<br>
 * <b>@company：</b><br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b><br>
 */
class MainPresenter extends BasePresenter<MainModel> {
	public MainPresenter(IView view) {
		super(view);
		model = new MainModel();
	}
}
