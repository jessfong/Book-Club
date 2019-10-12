package ca.mohawkcollege.bookclub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class CreateMeetingViewBottom extends Fragment {

    public CreateMeetingViewBottom() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.create_meeting_bottom, container, false);

        return view;
    }
}