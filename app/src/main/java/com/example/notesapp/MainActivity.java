package com.example.notesapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.PreferencesFactory;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "NotesPrefs";
    private static final String KEY_NOTE_COUNT = "NotesCount";
    private LinearLayout notesContainer;
    private List<Notes> notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        notesContainer = findViewById(R.id.notesContainer);
        Button saveBtn = findViewById(R.id.saveBtn);
        notesList = new ArrayList<>();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNotes();
            }
        });
        loadNotesFromPreference();
        displayNotes();
    }

    private void loadNotesFromPreference(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int notesCount = sharedPreferences.getInt(KEY_NOTE_COUNT, 0);
        for(int i = 0; i < notesCount; i++){
            String title = sharedPreferences.getString("notes_title_" + i, "");
            String content = sharedPreferences.getString("notes_content_" + i, "");
            Notes notes = new Notes();
            notes.setTitle(title);
            notes.setContent(content);
            notesList.add(notes);
        }
    }

    private void displayNotes(){
        for(Notes notes : notesList){
            createNotesView(notes);
        }
    }

    private void saveNotes(){
        EditText titleEditText = findViewById(R.id.title);
        EditText contentEditText = findViewById(R.id.noteContent);
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        if(!title.isEmpty() && !content.isEmpty()){
            Notes notes = new Notes();
            notes.setTitle(title);
            notes.setContent(content);
            notesList.add(notes);
            saveNotesToPreferences();
            createNotesView(notes);
            clearInputFields();
        }
    }

    private void saveNotesToPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_NOTE_COUNT, notesList.size());
        for(int i = 0; i < notesList.size(); i++){
            Notes notes = notesList.get(i);
            editor.putString("notes_title_" + i, notes.getTitle());
            editor.putString("notes_content_" + i, notes.getContent());
        }
        editor.apply();
    }

    private void createNotesView(final Notes notes){
        View notesView = getLayoutInflater().inflate(R.layout.notes_item, null);
        TextView titleTextView = notesView.findViewById(R.id.title);
        TextView contentTextView = notesView.findViewById(R.id.content);
        titleTextView.setText(notes.getTitle());
        contentTextView.setText(notes.getContent());
        notesView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showDeleteDialog(notes);
                return true;
            }
        });
        notesContainer.addView(notesView);
    }

    private void showDeleteDialog(final Notes notes){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this note");
        builder.setMessage("Are you sure you want to delete this note?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNoteAndRefresh(notes);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void deleteNoteAndRefresh(Notes notes){
        notesList.remove(notes);
        saveNotesToPreferences();
        refreshNotesView();
    }

    private void refreshNotesView(){
        notesContainer.removeAllViews();
        displayNotes();
    }

    private void clearInputFields(){
        EditText titleEditText = findViewById(R.id.title);
        EditText contentEditText = findViewById(R.id.noteContent);
        titleEditText.getText().clear();
        contentEditText.getText().clear();
    }
}