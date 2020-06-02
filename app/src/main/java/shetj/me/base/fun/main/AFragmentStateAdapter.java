package shetj.me.base.fun.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AFragmentStateAdapter extends FragmentStateAdapter {
    public AFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public AFragmentStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return BlankFragment.Companion.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 100;
    }
}
