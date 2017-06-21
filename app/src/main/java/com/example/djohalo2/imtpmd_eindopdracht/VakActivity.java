package com.example.djohalo2.imtpmd_eindopdracht;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.djohalo2.imtpmd_eindopdracht.Models.Vak;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class VakActivity extends AppCompatActivity {
    int positie = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vak);

        TextView vakTextView = (TextView) findViewById(R.id.vak_naam);

        Button afrondBtn = (Button) findViewById(R.id.submit_vak_btn);
        final EditText cijferInput = (EditText) findViewById(R.id.cijfer_input);
        final Switch gehaaldSwitch = (Switch) findViewById(R.id.gehaald_switch);

        Intent intent = getIntent();

        Bundle bundle = intent.getExtras();

        final String vakNaam = bundle.getString("vaknaam");


        vakTextView.setText("Heb je het vak " + vakNaam + " gehaald?");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

        final DatabaseReference ref = database.getReference("users/" + uid + "/vakken");

        ref.addValueEventListener(new ValueEventListener() {
            int count = 0;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("Count " ,""+dataSnapshot.getChildrenCount());
                for (DataSnapshot vakSnapshot: dataSnapshot.getChildren()) {
                    Vak vak = vakSnapshot.getValue(Vak.class);
                    if(vak.getNaam().equals(vakNaam)){
                        positie = count;
                    }
                    count++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

        Log.d("positie", String.valueOf(positie));


        afrondBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.e("cijferinput", String.valueOf(cijferInput.getText()));
                if(!String.valueOf(cijferInput.getText()).equals("")){
                    double cijfer = Double.parseDouble(String.valueOf(cijferInput.getText()));
                    boolean voldoende = cijfer >= 5.5 && gehaaldSwitch.isChecked();
                    boolean onvoldoende = cijfer < 5.5 && !gehaaldSwitch.isChecked();
                    if(voldoende || onvoldoende){
                        DatabaseReference childRef = database.getReference("users/" + uid + "/vakken/" + positie);
                        Map<String, Object> vakUpdates = new HashMap<String, Object>();
                        vakUpdates.put("gevolgd", true);

                        String toastMessage;
                        if(gehaaldSwitch.isChecked()){
                            vakUpdates.put("gehaald", true);
                            toastMessage = "Gefeliciteerd met het behalen van dit vak!";
                        }else {
                            vakUpdates.put("gehaald", false);
                            toastMessage = "Helaas, volgende keer beter.";
                        }

                        vakUpdates.put("cijfer", Double.parseDouble(cijferInput.getText().toString()));
                        childRef.updateChildren(vakUpdates);

                        Toast t = Toast.makeText(VakActivity.this, toastMessage, Toast.LENGTH_LONG);
                        t.show();

                        Intent intent = new Intent(VakActivity.this, DrawerActivity.class);
                        startActivity(intent);
                        VakActivity.this.finish();
                    } else {
                        Toast t = Toast.makeText(VakActivity.this, "De combinatie klopt niet, controleer de ingevulde gegevens.", Toast.LENGTH_LONG);
                        t.show();
                    }
                } else {
                    Toast t = Toast.makeText(VakActivity.this, "Vul je cijfer in om het vak af te ronden.", Toast.LENGTH_LONG);
                    t.show();
                }
            }
        });
    }
}
