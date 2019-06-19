package com.baoyachi.stepview.demo.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.baoyachi.stepview.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 日期：16/6/24 20:08
 * <p>
 * 描述：
 */
public class HorizontalStepviewFragment extends Fragment {
    View mView, btnNextStep;
    HorizontalStepView stepview;
    int step = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = View.inflate(container.getContext(), R.layout.fragment_horizontal_stepview, null);
        stepview = mView.findViewById(R.id.step_view5);
        btnNextStep = mView.findViewById(R.id.btnNextStep);
        showSetpView5();
        btnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepview.setStep(++step);
            }
        });
        mView.findViewById(R.id.btnPreviousStep).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stepview.setStep(--step);
            }
        });
        mView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                step = 0;
                stepview.setStep(step);
            }
        });
        return mView;
    }

    private void showSetpView5() {
        List<StepBean> stepsBeanList = new ArrayList<>();
        StepBean stepBean0 = new StepBean("1", 0);
        StepBean stepBean1 = new StepBean("2", -1);
        StepBean stepBean2 = new StepBean("3", -1);
        StepBean stepBean3 = new StepBean("4", -1);
        stepsBeanList.add(stepBean0);
        stepsBeanList.add(stepBean1);
        stepsBeanList.add(stepBean2);
        stepsBeanList.add(stepBean3);

        stepview.setStepViewTexts(stepsBeanList)
                .setStepsViewIndicatorCompletedLineColor(0xFF4AAAEE)
                .setStepsViewIndicatorUnCompletedLineColor(0xFF4AAAEE)
                .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(getActivity(), R.drawable.complted));
    }
}
