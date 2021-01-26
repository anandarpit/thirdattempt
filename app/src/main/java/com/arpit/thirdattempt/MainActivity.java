package com.arpit.thirdattempt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.DataStoreException;
import com.amplifyframework.datastore.generated.model.Priority;
import com.amplifyframework.datastore.generated.model.Todo;

public class MainActivity extends AppCompatActivity {
EditText name, desription;
RadioGroup priorityGroup;
Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.name);
        desription = findViewById(R.id.description);
        priorityGroup = findViewById(R.id.radioGroup);
        button = findViewById(R.id.submit);

        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.configure(getApplicationContext());

            Log.i("Tutorial", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("Tutorial", "Could not initialize Amplify", e);
        }

        button.setOnClickListener(view -> addtoDatastore());
    }

    private void addtoDatastore() {
        if(name.getText().toString().isEmpty()){
            name.setError("Name Cannot be empty");
            Toast.makeText(MainActivity.this, "Name Cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        Todo item = Todo.builder()
                .name(name.getText().toString())
                .priority(getPriority())
                .description(desription.getText().toString().trim())
                .build();
        Amplify.DataStore.save(
                item,
                success -> onSuccess(),
                this::onFailure
        );

    }

    private void onFailure(DataStoreException e) {
        Log.e("Tutorial", "Could not save item to DataStore", e);
    }

    private void onSuccess() {
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        name.setText("");
        desription.setText("");
        priorityGroup.clearCheck();
    }

    private Priority getPriority() {
        if(priorityGroup.getCheckedRadioButtonId() == -1){
            return null;
        }
        else{
            switch (priorityGroup.getCheckedRadioButtonId()) {
                case 0:
                    return  Priority.LOW;
                case 1:
                    return  Priority.NORMAL;
                case 2:
                    return  Priority.HIGH;
                default:
                    return null;
            }
        }
    }
}