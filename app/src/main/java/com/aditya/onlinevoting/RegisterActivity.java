package com.aditya.onlinevoting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aditya.onlinevoting.User.LoginActivity;
import com.aditya.onlinevoting.User.UserPollActivity;
import com.aditya.onlinevoting.Utils.SecureQrCode;
import com.developer.kalert.KAlertDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class RegisterActivity extends AppCompatActivity {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private String uid,name,dob;

    private CircularProgressButton circRegisterBtn;
    private EditText emailTxt,passwordTxt;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();

        circRegisterBtn = (CircularProgressButton) findViewById(R.id.cirRegisterButton);
        emailTxt = (EditText) findViewById(R.id.editTextEmail);
        passwordTxt = (EditText) findViewById(R.id.editTextPassword);
        loadingBar = new ProgressDialog(this);

        circRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        int c=0;
        String email = emailTxt.getText().toString();
        String password = passwordTxt.getText().toString();

        if(TextUtils.isEmpty(email)){
            c++;
            Toast.makeText(RegisterActivity.this, "Please Enter The Email Id...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password)){
            c++;
            Toast.makeText(RegisterActivity.this, "Please Enter The Password...", Toast.LENGTH_SHORT).show();
        }
        if(uid.equals("")){
            c++;
            Toast.makeText(RegisterActivity.this, "Please Scan your Aadhaar Card", Toast.LENGTH_SHORT).show();
        }

        if(c==0){
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait while we are creating your account...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mRef.child("Uid").child(uid).setValue("");
                        loadingBar.dismiss();
                        sendToUserPoll();
                        Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String error = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error :"+error, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    private void sendToUserPoll() {
        Intent intent = new Intent(RegisterActivity.this, UserPollActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginClick(View view) {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    public boolean checkCameraPermission (){
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            return false;
        }
        return true;
    }

    public void scanQR(View view) {
        if(!checkCameraPermission()){return;}

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan a Aadharcard QR Code");
        integrator.setResultDisplayDuration(500);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //we have a result
            String scanContent = scanningResult.getContents();
            Log.d("Adi", "onActivityResult: "+scanContent);
            String scanFormat = scanningResult.getFormatName();

            // process received data
            if (scanContent != null && !scanContent.isEmpty()) {
                processScannedData(scanContent);
            } else {
                showWarningPrompt("Scan Cancelled");
            }

        } else {
            showWarningPrompt("No scan data received!");
        }
    }

    protected void processScannedData(String scanData){
        // check if the scanned string is XML
        // This is to support old QR codes

        if(isXMLValid(scanData)){
            Log.d("Adi", "processScannedData: "+"Yes");
            XmlPullParserFactory pullParserFactory;

            try {
                // init the parserfactory
                pullParserFactory = XmlPullParserFactory.newInstance();
                // get the parser
                XmlPullParser parser = pullParserFactory.newPullParser();

                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(new StringReader(scanData));

                String q = "PrintLetterBarcodeData";
                // parse the XML
                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_DOCUMENT) {
                        Log.d("Rajdeol","Start document");
                    } else if(eventType == XmlPullParser.START_TAG && q.equals(parser.getName())) {
                        // extract data from tag
                        //uid
                        uid = parser.getAttributeValue(null,"uid");
                        //name
                        name = (parser.getAttributeValue(null,"name"));
                        //dob
                        dob = (parser.getAttributeValue(null,"dob"));
                        Log.d("Adi", "processScannedData: "+uid);

                    } else if(eventType == XmlPullParser.END_TAG) {
                        Log.d("Adi","End tag "+parser.getName());

                    } else if(eventType == XmlPullParser.TEXT) {

                        Log.d("Adi","Text "+parser.getText());

                    }
                    // update eventType
                    eventType = parser.next();
                }

                return;
            } catch (XmlPullParserException e) {
                showErrorPrompt("Error in processing QRcode XML");
                e.printStackTrace();
                return;
            } catch (IOException e) {
                showErrorPrompt(e.toString());
                e.printStackTrace();
                return;
            }
        }
        // process secure QR code
       else {
            processEncodedScannedData(scanData);
        }
    }// EO function


    protected void processEncodedScannedData(String scanData){
        try {
            SecureQrCode decodedData = new SecureQrCode(this,scanData);
            ArrayList<String> aadharCard = decodedData.getScannedAadharCard();
            // display the Aadhar Data
            Log.d("Adi", "processEncodedScannedData: " + aadharCard.get(1));
            showSuccessPrompt("Scanned Aadhar Card Successfully");
            //displayScannedData();
        } catch (Exception e) {
            showErrorPrompt(e.toString());
            e.printStackTrace();
        }
    }


    public void showErrorPrompt(String message){
        new KAlertDialog(this, KAlertDialog.ERROR_TYPE)
                .setTitleText("Error")
                .setContentText(message)
                .show();


    }

    public void showSuccessPrompt(String message){
        new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE)
                .setTitleText("Success")
                .setContentText(message)
                .show();
           }

    public void showWarningPrompt(String message){
        new KAlertDialog(this, KAlertDialog.WARNING_TYPE)
                .setContentText(message)
                .show();
    }

    public static boolean isXMLValid(String string) {
        try {
            SAXParserFactory.newInstance().newSAXParser().getXMLReader().parse(new InputSource(new StringReader(string)));
            return true;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            return false;
        }
    }
}