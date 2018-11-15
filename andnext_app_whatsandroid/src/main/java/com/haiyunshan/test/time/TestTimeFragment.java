package com.haiyunshan.test.time;


import android.os.Bundle;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haiyunshan.whatsandroid.R;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.time.Clock;

/**
 * A simple {@link Fragment} subclass.
 */
public class TestTimeFragment extends Fragment {


    public TestTimeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_time, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        {
            DateTime dateTime = DateTime.now();

            Log.w("AA", dateTime.toString());
        }

        {
            DateTime dateTime = new DateTime(System.currentTimeMillis());
            Log.w("AA", dateTime.toString());
        }

        {
            LocalDateTime dateTime = new LocalDateTime(System.currentTimeMillis());
            Log.w("AA", dateTime.toString());
        }

        {
            DateTime dateTime = DateTime.parse("2018-11-15T10:40:58.391+08:00");
            Log.w("AA", dateTime.toString());
        }

        {
            Clock.systemUTC();
        }
    }
}
