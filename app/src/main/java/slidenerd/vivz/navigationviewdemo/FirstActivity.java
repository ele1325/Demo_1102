package slidenerd.vivz.navigationviewdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class FirstActivity extends AppCompatActivity{

    EditText etFirstname2;
    EditText etLastname2;
    Button btnDML2;
    Button btnUpdate;
    SQLiteHelper sQLiteHelper;

    android.widget.LinearLayout parentLayout;
    LinearLayout layoutDisplayPeople;

    TextView tvNoRecordsFound;
    private String rowID = null;

    private ArrayList<HashMap<String, String>> tableData = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        getAllWidgets();
        sQLiteHelper = new SQLiteHelper(FirstActivity.this);
        bindWidgetsWithEvent();
        displayAllRecords();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String firstname = data.getStringExtra(Constants.FIRST_NAME);
            String lastname = data.getStringExtra(Constants.LAST_NAME);

            ContactModel contact = new ContactModel();
            contact.setFirstName(firstname);
            contact.setLastName(lastname);

            if (requestCode == Constants.ADD_RECORD) {
                //sQLiteHelper.insertRecord(firstname, lastname);
                sQLiteHelper.insertRecord(contact);
            } else if (requestCode == Constants.UPDATE_RECORD) {
                contact.setID(rowID);
                //sQLiteHelper.updateRecord(firstname, lastname, rowID);
                sQLiteHelper.updateRecord(contact);
            }
            displayAllRecords();
        }
    }


    private void getAllWidgets() {
        etFirstname2 = (EditText) findViewById(R.id.etFirstName2);
        etLastname2 = (EditText) findViewById(R.id.etLastname2);
        btnDML2 = (Button) findViewById(R.id.btnDML2);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        parentLayout = (LinearLayout) findViewById(R.id.parentLayout);
        layoutDisplayPeople = (LinearLayout) findViewById(R.id.layoutDisplayPeople);
        tvNoRecordsFound = (TextView) findViewById(R.id.tvNoRecordsFound);
    }

    private void bindWidgetsWithEvent() {
        btnDML2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClick();
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onButtonUpdate();
            }


        });
    }

    private void onButtonClick() {
        if (etFirstname2.getText().toString().equals("") || etLastname2.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Add Both Fields", Toast.LENGTH_LONG).show();
        } else {
            ContactModel contact = new ContactModel();
            contact.setFirstName(etFirstname2.getText().toString());
            contact.setLastName(etLastname2.getText().toString());
            sQLiteHelper.insertRecord(contact);
            displayAllRecords();
        }
    }
    private void onButtonUpdate() {
        if (etFirstname2.getText().toString().equals("") || etLastname2.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Add Both Fields", Toast.LENGTH_LONG).show();
        } else {
            ContactModel contact = new ContactModel();
            contact.setFirstName(etFirstname2.getText().toString());
            contact.setLastName(etLastname2.getText().toString());
            contact.setID(rowID);
            sQLiteHelper.updateRecord(contact);
            displayAllRecords();
        }
    }

    private void onUpdateRecord(String firstname, String lastname) {
        btnDML2.setVisibility(View.INVISIBLE);
        etFirstname2.setText(firstname);
        etLastname2.setText(lastname);
    }


    private void displayAllRecords() {

        LinearLayout inflateParentView;
        parentLayout.removeAllViews();

        ArrayList<ContactModel> contacts = sQLiteHelper.getAllRecords();

        if (contacts.size() > 0) {
            tvNoRecordsFound.setVisibility(View.GONE);
            ContactModel contactModel;
            for (int i = 0; i < contacts.size(); i++) {

                contactModel = contacts.get(i);

                final Holder holder = new Holder();
                final View view = LayoutInflater.from(this).inflate(R.layout.inflate_record, null);
                inflateParentView = (LinearLayout) view.findViewById(R.id.inflateParentView);
                holder.tvFullName = (TextView) view.findViewById(R.id.tvFullName);


                view.setTag(contactModel.getID());
                holder.firstname = contactModel.getFirstName();
                holder.lastname = contactModel.getLastName();
                String personName = holder.firstname + " " + holder.lastname;
                holder.tvFullName.setText(personName);

                final CharSequence[] items = {Constants.UPDATE, Constants.DELETE};
                inflateParentView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(FirstActivity.this);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {

                                    rowID = view.getTag().toString();
                                    onUpdateRecord(holder.firstname, holder.lastname.toString());
                                } else {
                                    AlertDialog.Builder deleteDialogOk = new AlertDialog.Builder(FirstActivity.this);
                                    deleteDialogOk.setTitle("Delete Contact?");
                                    deleteDialogOk.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    //sQLiteHelper.deleteRecord(view.getTag().toString());
                                                    ContactModel contact = new ContactModel();
                                                    contact.setID(view.getTag().toString());
                                                    sQLiteHelper.deleteRecord(contact);
                                                    displayAllRecords();
                                                }
                                            }
                                    );
                                    deleteDialogOk.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    deleteDialogOk.show();
                                }
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return true;
                    }
                });
                parentLayout.addView(view);
            }
        } else {
            tvNoRecordsFound.setVisibility(View.VISIBLE);
        }
    }

    private class Holder {
        TextView tvFullName;
        String firstname;
        String lastname;
    }

}