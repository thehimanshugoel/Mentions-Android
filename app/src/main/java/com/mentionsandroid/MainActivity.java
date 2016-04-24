package com.mentionsandroid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editText;
    ArrayAdapter<String> adapter;
    ListView listView;
    List<ModelUser> modelUserList;
    int StringStartPosition;
    int StringEndPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.edtText);
        listView = (ListView) findViewById(R.id.listView);
        modelUserList = new ArrayList<>();
        final String[] arr = {"Paries", "PA", "Parana", "Padua", "Pasadena"};

        adapter = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1, android.R.id.text1, arr);
        listView.setAdapter(adapter);
        listView.setVisibility(View.GONE);
        InputFilter inputFilter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.toString().contains("@")) {
                    Log.d("Souce String is ", source.toString());
                    listView.setVisibility(View.VISIBLE);
                    int cursorPosition = editText.getSelectionStart();
                    Log.d("Cursor start", String.valueOf(cursorPosition));
                }
                StringStartPosition = start;
                Log.d("String is ", source.toString());
                return null;
            }
        };
        editText.setFilters(new InputFilter[]{inputFilter});

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editText.append(arr[position] + " ");
                int cursorPostion = editText.getSelectionStart();
                StringEndPosition = cursorPostion;
                Log.d("cursor End", String.valueOf(cursorPostion));
                modelUserList.add(new ModelUser(StringStartPosition, StringEndPosition, arr[position]));
                listView.setVisibility(View.GONE);

            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("CharSequence", s.toString());

                int cursorPosition = editText.getSelectionStart();

                for (int i = 0; i < modelUserList.size(); i++) {
                    if (cursorPosition > modelUserList.get(i).getStartPosition() && cursorPosition < modelUserList.get(i).getEndPosition()) {
                        modelUserList.remove(i);
                        Log.d("Item removed", String.valueOf(i));
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }
}
