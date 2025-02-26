package com.example.myapplication;

import static android.app.ProgressDialog.show;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    String receiverId, receiverName,senderRoom, receverRoom;
    String senderId, snderName;
    DatabaseReference dbreferenceSender, dbreferenceRecevier,userReference;
   ImageView SendBtn;
   EditText messageText;
   RecyclerView recyclerView;
   MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Toolbar toolbar= findViewById(R.id.chattoolbar);
        setSupportActionBar(toolbar);


        userReference= FirebaseDatabase.getInstance().getReference("users");
        receiverId=getIntent().getStringExtra("id");
        receiverName=getIntent().getStringExtra("name");

        if(receiverId==null||receiverName==null){
            Toast.makeText(this, "Error Missing chat details", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getSupportActionBar().setTitle(receiverName);
        if(receiverId!=null){
            senderRoom= FirebaseAuth.getInstance().getUid()+receiverId;
            receverRoom=receiverId+FirebaseAuth.getInstance().getUid();
        }

        SendBtn=findViewById(R.id.sendMessageIcon);
        messageAdapter=new MessageAdapter(this);
        recyclerView = findViewById(R.id.chatRecyler);
        messageText=findViewById(R.id.messegeEdit);


        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(senderRoom != null && receverRoom !=null) {
            dbreferenceSender = FirebaseDatabase.getInstance().getReference("chats").child(senderRoom);
            dbreferenceRecevier = FirebaseDatabase.getInstance().getReference("chats").child(receverRoom);
        }
        else {
            Log.e("ChatActivity","erro :senderoom or recerverROom is null");
        }
       dbreferenceSender.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               List<MessageModel> messages = new ArrayList<>();
               for(DataSnapshot dataSnapshot:snapshot.getChildren())
               {
                   MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                   messages.add(messageModel);
               }
               messageAdapter.clear();
               for(MessageModel message: messages){
                   messageAdapter.add(message);
               }
               messageAdapter.notifyDataSetChanged();
           }



           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
        dbreferenceRecevier.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MessageModel> messages = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    messages.add(messageModel);
                }
                updateRecyclerView(messages);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DEBUG", "Firebase Error: " + error.getMessage());
            }
        });


       SendBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               String message = messageText.getText().toString();
               if(message.trim().length()>0){
                   SendMessage(message);
               }
               else {
                   Toast.makeText(ChatActivity.this,"Message can not be empty",Toast.LENGTH_SHORT).show();

               }
           }
       });

    }

    private void updateRecyclerView(List<MessageModel> messages) {
        messageAdapter.clear();
        for(MessageModel message : messages){
            messageAdapter.add(message);
        }
        messageAdapter.notifyDataSetChanged();

        if (messageAdapter.getItemCount() > 0) {
            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }

    private void SendMessage(String message){
        if (message.trim().isEmpty()) {
            Toast.makeText(ChatActivity.this, "Message cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String messageId = UUID.randomUUID().toString();
        MessageModel messageModel = new MessageModel(messageId, FirebaseAuth.getInstance().getUid(), message);

        dbreferenceSender.child(messageId).setValue(messageModel)
                .addOnSuccessListener(unused -> Log.d("DEBUG", "Message sent successfully"))
                .addOnFailureListener(e ->
                        Toast.makeText(ChatActivity.this, "Failed to send the message", Toast.LENGTH_SHORT).show()
                );

        dbreferenceRecevier.child(messageId).setValue(messageModel)
                .addOnSuccessListener(unused -> Log.d("DEBUG", "Message received successfully"))
                .addOnFailureListener(e ->
                        Toast.makeText(ChatActivity.this, "Failed to deliver message", Toast.LENGTH_SHORT).show()
                );

        messageAdapter.add(messageModel);
        messageAdapter.notifyDataSetChanged();

        if (messageAdapter.getItemCount() > 0) {
            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        }

        messageText.setText(""); // Clear message input field

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(ChatActivity.this, SignActivity.class));
            finish();
            return true;
        }
        return false;
    }
}