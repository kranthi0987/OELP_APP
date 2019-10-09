package mahiti.org.oelp.views.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import mahiti.org.oelp.R;

public class TeacherInfo1Fragment extends Fragment {

    private View rootView;
    private LinearLayout linearLayoutTeacherInfo;
    private TextView textViewHeading;
    private TextView textViewValue;
    private View view;
    LayoutInflater inflator;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_teacherinfo, container, false);

        initViews();

        for (int i = 1; i <= 10; i++) {
            TextView textView1 = new TextView(getContext());
            TextView textView2 = new TextView(getContext());
            View view = new View(getContext());
            //rootView = inflator.inflate(R.layout.fragment_teacherinfo,null);
            textView1.setText("TextView1 " + String.valueOf(i));
            textView2.setText("TextView2 " + String.valueOf(i));

            //linearLayout.addView(textView);
            //textViewHeading.setText("Name");
            linearLayoutTeacherInfo.addView(textView1);
            linearLayoutTeacherInfo.addView(textView2);
        }

        return rootView;
    }

    private void initViews() {
        linearLayoutTeacherInfo = (LinearLayout) rootView.findViewById(R.id.linearLayoutTeacherInfo);
        textViewHeading = (TextView) rootView.findViewById(R.id.textViewHeading);
        textViewValue = (TextView) rootView.findViewById(R.id.textViewValue);
        view = (View) rootView.findViewById(R.id.view);
    }





}
