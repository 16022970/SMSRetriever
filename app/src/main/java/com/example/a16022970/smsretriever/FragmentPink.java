package com.example.a16022970.smsretriever;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.BidiFormatter;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPink extends Fragment {
    TextView tv2;
    EditText etWord2;
    Button btnRetrieve2;
    TextView tvResult2;
    Button btnEmail2;
    String emailText;

    public FragmentPink() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pink, container, false);

        tv2 = (TextView) view.findViewById(R.id.tv2);
        etWord2 = (EditText) view.findViewById(R.id.etWord2);
        btnRetrieve2 = (Button) view.findViewById(R.id.btnRetrieve2);
        tvResult2 = (TextView) view.findViewById(R.id.tvResult2);
        btnEmail2 = (Button) view.findViewById(R.id.btnEmail2);


        btnRetrieve2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int permissionCheck = PermissionChecker.checkSelfPermission
                        (getActivity(), Manifest.permission.READ_SMS);

                if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_SMS}, 0);
                    // stops the action from proceeding further as permission not
                    //  granted yet
                    return;
                }
                Uri uri = Uri.parse("content://sms");
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();

                String word = etWord2.getText().toString();
                String filter = "body LIKE ?";

                if (!word.equals("") || !word.matches("")) {
                    String[] filterArgs = {"%" + word + "%"};
                    // Fetch SMS Message from Built-in Content Provider
                    Cursor cursor = cr.query(uri, reqCols, filter, filterArgs, null);
                    String smsBody = "";
                    if (cursor.moveToFirst()) {
                        do {
                            long dateInMillis = cursor.getLong(0);
                            String date = (String) DateFormat
                                    .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                            String address = cursor.getString(1);
                            String body = cursor.getString(2);
                            String type = cursor.getString(3);
                            if (type.equalsIgnoreCase("1")) {
                                type = "Inbox:";
                            } else {
                                type = "Sent:";
                            }
                            smsBody += type + " " + address + "\n at " + date
                                    + "\n\"" + body + "\"\n\n";
                            emailText += body + "\n";
                        } while (cursor.moveToNext());
                    }
                    tvResult2.setText(smsBody);
                }else{
                    tvResult2.setText("");
                    Toast.makeText(getActivity(), "Empty input",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnEmail2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = etWord2.getText().toString();

                if (!word.equals("") || !word.matches("")) {

// The action you want this intent to do;
                    // ACTION_SEND is used to indicate sending text
                    Intent email = new Intent(Intent.ACTION_SEND);
                    // Put essentials like email address, subject & body text
                    email.putExtra(Intent.EXTRA_EMAIL,
                            new String[]{"jason_lim@rp.edu.sg"});
                    email.putExtra(Intent.EXTRA_SUBJECT,
                            "Email texts");
                    email.putExtra(Intent.EXTRA_TEXT,
                            emailText);
                    // This MIME type indicates email
                    email.setType("message/rfc822");
                    // createChooser shows user a list of app that can handle
                    // this MIME type, which is, email
                    startActivity(Intent.createChooser(email,
                            "Choose an Email client :"));

                } else {
                    Toast.makeText(getActivity(), "Empty input",Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnRetrieve2.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(getActivity(), "Permission not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

}
