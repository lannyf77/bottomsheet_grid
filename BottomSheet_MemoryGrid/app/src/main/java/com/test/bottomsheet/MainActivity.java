package com.test.bottomsheet;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    BottomSheetBehavior behavior;
    GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View bottomSheet = findViewById(R.id.bottomsheet);
        behavior = BottomSheetBehavior.from(bottomSheet);

        ///
//        behavior.setHideable(true);
//        behavior.setPeekHeight(300);
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        ///

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("+++", "+++: " + slideOffset);
            }
        });
        gridView = (GridView) findViewById(R.id.gridview);
    }

    private void toggleBottomSheet() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                } else if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            gridView.stopTimer();

        } else if (behavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            gridView.startTimer();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.titlebar_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.bottomsheet:
                toggleBottomSheet();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        gridView.stopTimer();
        super.onDestroy();
    }
}
