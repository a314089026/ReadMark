package com.mycompany.readmark.detail;




import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mycompany.readmark.R;
import com.mycompany.readmark.books.BooksBean;
import com.mycompany.readmark.common.DatabaseTableSingleton;
import com.mycompany.readmark.marker.MarkerBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lenovo on 2017/1/3.
 */
public class BookDetailFragment extends Fragment implements SavingDialogFragment.OnPositiveClickListener{

    private ViewPager mViewPager;
    private BooksBean mBook;
    //private OnBackArrowPressedListener mOnBackArrowPressedListener;

    /*public interface OnBackArrowPressedListener{
        void onBackArrowPressed();
    }

    public void setOnBackArrowPressedListener(OnBackArrowPressedListener onBackArrowPressedListener){
        this.mOnBackArrowPressedListener = onBackArrowPressedListener;
    }*/

    public static BookDetailFragment newInstance(BooksBean book){
        Bundle args = new Bundle();
        args.putSerializable("book", book);
        BookDetailFragment fragment = new BookDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_detail, null);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.detail_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);
        /*Toolbar toolbar = (Toolbar) view.findViewById(R.id.detail_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        ((AppCompatActivity)getActivity())
                .getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnBackArrowPressedListener.onBackArrowPressed();
            }
        });*/
        mBook = (BooksBean)getArguments().getSerializable("book");
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)
                view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(mBook.getTitle());
        ImageView ivImage = (ImageView) view.findViewById(R.id.ivImage);
        Glide.with(ivImage.getContext())
                .load(mBook.getImages().getLarge())
                .fitCenter()
                .into(ivImage);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        tabLayout.addTab(tabLayout.newTab().setText("内容简介"));
        tabLayout.addTab(tabLayout.newTab().setText("作者简介"));
        tabLayout.addTab(tabLayout.newTab().setText("目录"));
        tabLayout.setupWithViewPager(mViewPager);

        //setOnBackArrowPressedListener((OnBackArrowPressedListener)getActivity());
        return view;
    }

    private void setupViewPager(ViewPager mViewPager) {
        //注意这里要获取ViewPager的FragmentManager，本层的Manager不负责恢复ViewPager的视图状态
        MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
        adapter.addFragment(TabContentFragment.newInstance(mBook.getSummary()), "内容简介");
        adapter.addFragment(TabContentFragment.newInstance(mBook.getAuthor_intro()), "作者简介");
        adapter.addFragment(TabContentFragment.newInstance(mBook.getCatalog()), "目录");
        mViewPager.setAdapter(adapter);
    }

    static class MyPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Log.e("BookDetailFragment", "onCreateOptionsMenu()");
        menu.clear();
        inflater.inflate(R.menu.menu_detail, menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_add:
                showDialog();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog(){
        SavingDialogFragment dialogFragment = new SavingDialogFragment();
        dialogFragment.setOnPositiveClickListener(this);
        dialogFragment.show(getFragmentManager(), "saving");
    }

    @Override
    public void onPositiveClick() {
        addBookToDatabase(mBook);
    }

    private void addBookToDatabase(BooksBean book){

        //Toast.makeText(getActivity(), "名字是"+book.getAlt_title(), Toast.LENGTH_SHORT).show();
        MarkerBean marker = new MarkerBean();
        marker.setImageUrl(book.getImages().getLarge());
        marker.setProgress(0f);
        marker.setMarkerName(book.getTitle());
        marker.setPages(mBook.getPages());

        DatabaseTableSingleton.getDatabaseTable(getActivity()).saveMarkerInfo(marker);
    }

}
