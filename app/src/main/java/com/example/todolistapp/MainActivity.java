package com.example.todolistapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {


    EditText etTask;
    Button btnAdd, btnDate;
    RecyclerView recyclerView;
    TaskAdapter adapter;
    ArrayList<Task> taskList;
    Spinner spPriority;

    SharedPreferences sharedPreferences;

    String selectedDate = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);


        sharedPreferences = getSharedPreferences(
                "TaskPrefs",
                MODE_PRIVATE
        );


        etTask = findViewById(R.id.etTask);
        btnAdd = findViewById(R.id.btnAdd);
        btnDate = findViewById(R.id.btnDate);
        spPriority = findViewById(R.id.spPriority);
        recyclerView = findViewById(R.id.recyclerView);



        loadTasks();



        adapter = new TaskAdapter(taskList);


        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerView.setAdapter(adapter);



        // Priority Spinner

        String[] priorities = {
                "High",
                "Medium",
                "Low"
        };


        ArrayAdapter<String> priorityAdapter =
                new ArrayAdapter<String>(
                        this,
                        R.layout.spinner_item,
                        priorities
                ){

                    @Override
                    public View getView(
                            int position,
                            View convertView,
                            ViewGroup parent
                    ){

                        TextView text =
                                (TextView) super.getView(
                                        position,
                                        convertView,
                                        parent
                                );

                        text.setTextColor(
                                android.graphics.Color.BLACK
                        );

                        return text;
                    }


                    @Override
                    public View getDropDownView(
                            int position,
                            View convertView,
                            ViewGroup parent
                    ){

                        TextView text =
                                (TextView) super.getDropDownView(
                                        position,
                                        convertView,
                                        parent
                                );

                        text.setTextColor(
                                android.graphics.Color.BLACK
                        );

                        text.setBackgroundColor(
                                android.graphics.Color.WHITE
                        );

                        return text;
                    }

                };


        priorityAdapter.setDropDownViewResource(
                R.layout.spinner_dropdown_item
        );


        spPriority.setAdapter(priorityAdapter);



        // Date Picker

        btnDate.setOnClickListener(v -> {


            Calendar calendar =
                    Calendar.getInstance();


            int year =
                    calendar.get(Calendar.YEAR);

            int month =
                    calendar.get(Calendar.MONTH);

            int day =
                    calendar.get(Calendar.DAY_OF_MONTH);



            DatePickerDialog dialog =
                    new DatePickerDialog(
                            MainActivity.this,

                            (view, y, m, d) -> {


                                selectedDate =
                                        d + "/" +
                                                (m + 1) + "/" +
                                                y;


                                btnDate.setText(
                                        selectedDate
                                );

                            },

                            year,
                            month,
                            day
                    );


            dialog.show();

        });




        // Add Task Button

        btnAdd.setOnClickListener(v -> {


            String title =
                    etTask.getText()
                            .toString()
                            .trim();


            String priority =
                    spPriority.getSelectedItem()
                            .toString();



            if(!title.isEmpty()){


                Task task =
                        new Task(
                                title,
                                "",
                                false,
                                priority,
                                selectedDate
                        );


                taskList.add(task);


                adapter.notifyItemInserted(
                        taskList.size()-1
                );


                saveTasks();



                etTask.setText("");

                btnDate.setText(
                        "Select Due Date"
                );

                selectedDate = "";

            }

        });

    }




    private void saveTasks(){

        Gson gson = new Gson();


        String json =
                gson.toJson(taskList);



        sharedPreferences
                .edit()
                .putString(
                        "tasks",
                        json
                )
                .apply();

    }




    private void loadTasks(){


        Gson gson =
                new Gson();


        String json =
                sharedPreferences.getString(
                        "tasks",
                        null
                );



        Type type =
                new TypeToken<ArrayList<Task>>() {}
                        .getType();



        taskList =
                gson.fromJson(
                        json,
                        type
                );



        if(taskList == null){

            taskList =
                    new ArrayList<>();

        }

    }

}