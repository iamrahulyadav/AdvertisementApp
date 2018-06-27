package com.projects.owner.camlocation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.projects.owner.camlocation.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

/**
 * Created by Sanawal Alvi on 10/28/2017.
 */

public class AddCampaignFirstActivity extends Activity implements View.OnClickListener {

    int yearTBU, monthTBU, dayTBU;
    private static final int YEAR_MINUS = -18;
    RelativeLayout nextBtn;
    EditText campaignName, companyName, startingDate, endingDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_campaign_first);

        nextBtn = (RelativeLayout) findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(this);
        companyName = (EditText) findViewById(R.id.company_name);
        campaignName = (EditText) findViewById(R.id.campaign_name);
        startingDate = (EditText) findViewById(R.id.starting_date);
        startingDate.setOnClickListener(this);
        endingDate = (EditText) findViewById(R.id.ending_date);
        endingDate.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.starting_date:
                Calendar cal = Calendar.getInstance();
//                cal.add(Calendar.YEAR, YEAR_MINUS);
                DatePickerDialog dpd = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                                                                        @Override
                                                                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
                                                                        {
                                                                            yearTBU = year;
                                                                            monthTBU = monthOfYear;
                                                                            dayTBU = dayOfMonth;
                                                                            showDate(year, monthOfYear, dayOfMonth);
                                                                        }
                                                                    },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setVersion(DatePickerDialog.Version.VERSION_1);
                dpd.show(AddCampaignFirstActivity.this.getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.ending_date:
                Calendar cal2 = Calendar.getInstance();
//                cal2.add(Calendar.YEAR, YEAR_MINUS);
                DatePickerDialog dpd2 = DatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
                                                                        @Override
                                                                        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth)
                                                                        {
                                                                            yearTBU = year;
                                                                            monthTBU = monthOfYear;
                                                                            dayTBU = dayOfMonth;
                                                                            showDate2(year, monthOfYear, dayOfMonth);
                                                                        }
                                                                    },
                        cal2.get(Calendar.YEAR),
                        cal2.get(Calendar.MONTH),
                        cal2.get(Calendar.DAY_OF_MONTH)
                );
                dpd2.setVersion(DatePickerDialog.Version.VERSION_1);
                dpd2.show(AddCampaignFirstActivity.this.getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.next_btn:
                if (companyName.getText().toString().equals("") || campaignName.getText().equals("")||startingDate.getText().equals("")||endingDate.getText().equals(""))
                {
                    Toast.makeText(this, "Please fill all the credentials", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent(this, AddCampaignSecond.class);
                    startActivity(intent);
//                finish();
                }
                break;
        }
    }

    public void showDate(int year, int month, int dayOfMonth)
    {
        startingDate.setText(month + "/" + dayOfMonth + "/" + year);
    }

    public void showDate2(int year, int month, int dayOfMonth)
    {
        endingDate.setText(month + "/" + dayOfMonth + "/" + year);
    }
}
