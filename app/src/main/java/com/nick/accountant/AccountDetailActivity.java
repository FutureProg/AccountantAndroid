package com.nick.accountant;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AccountDetailActivity extends AppCompatActivity {

    public static final String INTENT_ACCOUNT_NAME = "AccountDetailActivity.account";
    Account account;
    Spinner accountTypeSpinner;
    EditText accountNameField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);

        String accountName = (String) getIntent().getStringExtra(INTENT_ACCOUNT_NAME);
        setTitle(accountName);
        account = Bin.getAccount(accountName);

        accountTypeSpinner = (Spinner)findViewById(R.id.account_detail_type);
        String[] arr = getResources().getStringArray(R.array.account_type_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                arr);
        accountTypeSpinner.setAdapter(adapter);
        accountTypeSpinner.setSelection(account.getType());
        accountNameField = (EditText)findViewById(R.id.account_detail_name);
        accountNameField.setText(accountName);

        String txt = String.format("$%.02f", account.getAmount());
        ((TextView)findViewById(R.id.account_detail_amount)).setText(txt);
    }

    public void onSaveChanges(View view){
        if(!accountNameField.getText().toString().equals(account.getName())){
            account.setName(accountNameField.getText().toString());
            setTitle(account.getName());
        }
        if(accountTypeSpinner.getSelectedItemPosition() != account.getType()){
            account.setType(accountTypeSpinner.getSelectedItemPosition());
        }
        Bin.accountsDbHelper.saveChanges(account);
        Bin.accountsDbHelper.updateAccountNames();
        Toast.makeText(this,"Changes saved",Toast.LENGTH_SHORT);
    }
}
