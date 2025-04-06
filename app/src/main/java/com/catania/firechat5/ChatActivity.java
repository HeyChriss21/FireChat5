package com.catania.firechat5;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    String receiverId, receiverName, senderRoom, receiverRoom;
    DatabaseReference databaseReferenceSender, databaseReferenceReceiver, userReference;
    ImageView sendBtn;
    EditText messageText;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userReference = FirebaseDatabase.getInstance().getReference("users");
        receiverId = getIntent().getStringExtra("id");
        receiverName = getIntent().getStringExtra("name");

        getSupportActionBar().setTitle(receiverName);
        if(receiverId!=null) {
            senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
            receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();
        }
        sendBtn = findViewById(R.id.sendMessageIcon);
        messageAdapter = new MessageAdapter(this);
        recyclerView = findViewById(R.id.chatrecycler);
        messageText = findViewById(R.id.messageEdit);

        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReferenceSender = FirebaseDatabase.getInstance().getReference("chats").child(senderRoom);
        databaseReferenceReceiver = FirebaseDatabase.getInstance().getReference("chats").child(receiverRoom);

        databaseReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MessageModel> messages = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    messages.add(messageModel);
                }

// Ordina i messaggi in base al timestamp
                Collections.sort(messages, new Comparator<MessageModel>() {
                    @Override
                    public int compare(MessageModel o1, MessageModel o2) {
                        return Long.compare(o1.getTimestamp(), o2.getTimestamp());
                    }
                });

                messageAdapter.clear();
                for (MessageModel message : messages) {
                    messageAdapter.add(message);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageText.getText().toString();
                if(message.trim().length() > 0)
                {
                    SendMessage(message);
                }
                else
                    Toast.makeText(ChatActivity.this, "Il messaggio non può essere vuoto", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void SendMessage(String message) {
        String messageId = UUID.randomUUID().toString();
        long timestamp = System.currentTimeMillis();
        MessageModel messageModel = new MessageModel(
                FirebaseAuth.getInstance().getUid(),
                receiverId,
                message,
                timestamp
        );

        messageAdapter.add(messageModel);

        databaseReferenceSender.child(messageId).setValue(messageModel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, "Errore nell'invio del messaggio", Toast.LENGTH_SHORT).show();
                    }
                });
        databaseReferenceReceiver.child(messageId).setValue(messageModel);
        recyclerView.scrollToPosition(messageAdapter.getItemCount()-1);
        messageText.setText("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ChatActivity.this, SigninActivity.class));
            finish();
            return true;
        }
        return false;
    }
}