package com.example.makoto.collapseactionbar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends Activity implements ObservableScrollViewCallbacks {

    @InjectView(R.id.list)
    ObservableListView mListView;
    @InjectView(R.id.header)
    LinearLayout mHeaderView;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    private int mBaseTranslationY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        mListView.setScrollViewCallbacks(this);
        mListView.addHeaderView(inflater.inflate(R.layout.padding, null)); // sticky view
    }

    @Override
    protected void onStart() {
        super.onStart();

        ArrayList<Integer> dataList = new ArrayList<Integer>();
        for (int i = 0; i < 60; i++) {
            dataList.add(i);
        }

        mListView.setAdapter(new MyArrayAdapter(this, dataList));
    }

    private class MyArrayAdapter extends ArrayAdapter<Integer> {

        public MyArrayAdapter(Context context, ArrayList<Integer> list) {
            super(context, 0, list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Integer data = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            // Populate the data into the template view using the data object
            viewHolder.itemText.setText(data.toString());
            // Return the completed view to render on screen

            return convertView;
        }

        private class ViewHolder {
            TextView itemText;

            public ViewHolder(View view) {
                itemText = (TextView) view.findViewById(R.id.item_text);
            }
        }

    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        Log.d("デバッグ", "changed");
        // ドラッグ中のみ反応させ、手を離した後はアニメーションに任せる
//        if (dragging) {
//            Log.d("デバッグ", "dragging");
//
//            int toolbarHeight = mToolbarView.getHeight();
//            if (firstScroll) {
//                Log.d("デバッグ", "firstScroll");
//                // ある程度スクロールした状態から動かすときは現在のスクロール位置を基準にする
//                float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
//                if (-toolbarHeight < currentHeaderTranslationY && toolbarHeight < scrollY) {
//                    mBaseTranslationY = scrollY;
//                }
//            }
//            // Toolbarの可動範囲を-toolbarHeightから0までに制限する
//            int headerTranslationY = Math.min(0, Math.max(-toolbarHeight, -(scrollY - mBaseTranslationY)));
//
//            // 動作中のアニメーションをキャンセルして移動
//            ViewPropertyAnimator.animate(mHeaderView).cancel();
//            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);
//        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbar.getHeight();
        if (scrollState == ScrollState.UP) {
            Log.d("デバッグ", "up");

            // Toolbarを隠す
            if (toolbarHeight < mListView.getCurrentScrollY()) {
                if (headerTranslationY != -toolbarHeight) {
                    ViewPropertyAnimator.animate(mHeaderView).cancel();
                    // +x で　xの分残してアニメーションする
                    ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight + 50).setDuration(200).start();
                }
            }
        } else if (scrollState == ScrollState.DOWN) {
            Log.d("デバッグ", "down");

            // Toolbarを表示する
            if (toolbarHeight < mListView.getCurrentScrollY()) {
                if (headerTranslationY != 0) {
                    ViewPropertyAnimator.animate(mHeaderView).cancel();
                    ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
                }
            }
        }
    }
}
