package com.example.todolistapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private ArrayList<Task> taskList;

    public TaskAdapter(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }

    // Save Tasks in SharedPreferences
    private void saveTasks(Context context) {

        SharedPreferences sharedPreferences =
                context.getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(taskList);

        editor.putString("tasks", json);
        editor.apply();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {

        Task task = taskList.get(position);

        holder.title.setText(task.getTitle());
        holder.description.setText(task.getDescription());
        holder.priority.setText("Priority : " + task.getPriority());

        if (task.getDueDate() == null || task.getDueDate().isEmpty()) {
            holder.dueDate.setText("Due : Not Selected");
        } else {
            holder.dueDate.setText("Due : " + task.getDueDate());
        }

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(task.isCompleted());

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.setCompleted(isChecked);
            saveTasks(holder.itemView.getContext());
        });

        holder.btnDelete.setOnClickListener(v -> {

            int currentPosition = holder.getAdapterPosition();

            if (currentPosition != RecyclerView.NO_POSITION) {

                taskList.remove(currentPosition);

                saveTasks(holder.itemView.getContext());

                notifyItemRemoved(currentPosition);
                notifyItemRangeChanged(currentPosition, taskList.size());
            }
        });

        holder.btnEdit.setOnClickListener(v -> {

            EditText editText = new EditText(holder.itemView.getContext());
            editText.setText(task.getTitle());

            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Edit Task")
                    .setView(editText)
                    .setPositiveButton("Save", (dialog, which) -> {

                        task.setTitle(editText.getText().toString());

                        saveTasks(holder.itemView.getContext());

                        notifyItemChanged(holder.getAdapterPosition());

                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, priority, dueDate;
        CheckBox checkBox;
        Button btnDelete, btnEdit;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.taskTitle);
            description = itemView.findViewById(R.id.taskDescription);
            priority = itemView.findViewById(R.id.taskPriority);
            dueDate = itemView.findViewById(R.id.taskDueDate);

            checkBox = itemView.findViewById(R.id.taskCheckBox);

            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }
}