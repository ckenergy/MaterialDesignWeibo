package com.ckenergy.transitionsview.transitions;

import android.view.View;

public interface ITransferView {

     void start(MoveInfo bean, View child, OnShowListener listener);

     void back(MoveInfo bean, View child, OnShowListener listener);

    interface OnShowListener {
        void onStart();
        void onEnd();
    }

}
