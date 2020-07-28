package shetj.me.base.func.main;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class AFragmentStateAdapter extends FragmentStateAdapter {
    ArrayList<Fragment> fragmentArrayList;
    public AFragmentStateAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Fragment> fragmentArrayList) {
        super(fragmentActivity);
        this.fragmentArrayList = fragmentArrayList;
    }

    public AFragmentStateAdapter(@NonNull Fragment fragment, ArrayList<Fragment> fragmentArrayList) {
        super(fragment);
        this.fragmentArrayList = fragmentArrayList;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
            return fragmentArrayList.get(position);
    }

    @Override
    public int getItemCount() {
        return 100;
    }
}
