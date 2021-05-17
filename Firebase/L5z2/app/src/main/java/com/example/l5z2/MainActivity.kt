package com.example.l5z2

import android.app.AlertDialog
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class  MainActivity : AppCompatActivity() {

    private val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())    // dostępne metody logowania (mail)
    private lateinit var user: FirebaseUser     // obecnie zalogowany użytkownik
    private var oppPosition: Int = -1          // wybrany przeciwnik
    private val database = Firebase.database("https://l5z2-c6296-default-rtdb.europe-west1.firebasedatabase.app")
    private var usersMails: ArrayList<String?> = ArrayList()
    private var usersIds: ArrayList<String?> = ArrayList()
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createLogin()
    }

    // tworzenie okna logowania
    private fun createLogin() {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN)
    }

    // odbieranie wyniku z okna logowania
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                val button = findViewById<Button>(R.id.button)
                button.visibility = View.VISIBLE
                user = FirebaseAuth.getInstance().currentUser as FirebaseUser // obecnie zalogowany użytkownik
                val myRef = database.reference.child("Users")       // obiekt użytkownicy w bazie
                myRef.addValueEventListener (                                // aktulizuj, gdy dojdzie nowy użytkownik do bazy
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            // pousuwaj poprzednie wartości
                            usersMails.clear()
                            usersIds.clear()
                            // iteruj po użytkownikach
                            for (ds in dataSnapshot.children) {
                                val mail: String? = ds.value as String?
                                val id: String? = ds.key
                                // wyświetlaj tylko innych użytkowników
                                if (id != user.uid) {
                                    usersMails.add(mail)
                                    usersIds.add(id)
                                }
                            }
                            // zaaktualizuj listView, który wyświetla wszystkich dostępnych uzytkowników
                            adapter.notifyDataSetChanged()
                        }
                        override fun onCancelled(databaseError: DatabaseError) {}
                    }
                )
                //  dodaj zalogowanego użytkownika do bazy
                myRef.child(user.uid).setValue(user.email)
                adapter = ArrayAdapter<String>(this, R.layout.activity_listview, usersMails)
                val listView = findViewById<ListView>(R.id.listView)
                listView.adapter = adapter
                // przy kliknięciu na danego gracza przejdź do rozrywki, podając dane graczy
                listView.onItemClickListener = OnItemClickListener { _, _, position, _ ->
                    oppPosition = position
                    database.reference.child(usersIds[position].toString()).child("position")
                        .setValue("-1")
                    database.reference.child(usersIds[position].toString()).child("gameStatus")
                        .setValue(user.email)
                }
                // każda gra jest ideksowana ID zaproszonego gracza do gry
                // gracze na przemian zmieniają wartość "position" wskazująca na kliknięty kwadrat
                database.reference.addValueEventListener (
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (ds in dataSnapshot.children) {
                                val id: String? = ds.key
                                val gameStatus = ds.child("gameStatus").value
                                val position = ds.child("position").value

                                if (id == user.uid && gameStatus is String) {
                                    // w gameStatus znajduje się nazwa gracza zapraszającego
                                    // wyświetlamy u użytkownika zaproszonego, w id znajduje się id zaproszonego
                                    val builder = AlertDialog.Builder(this@MainActivity)
                                    builder.setTitle("Notification")
                                    builder.setMessage("You are invited to play with $gameStatus. Do you accept?")
                                    builder.setPositiveButton("Yes") { _, _ ->
                                        database.reference.child(id).child("gameStatus").setValue(true)
                                        val intent = Intent(this@MainActivity, GameActivity::class.java)
                                        intent.putExtra("id", user.uid)
                                        startActivity(intent)
                                    }
                                    builder.setNegativeButton("No") {_, _ ->
                                        database.reference.child(id).child("gameStatus").setValue(false)
                                    }
                                    builder.setOnCancelListener {
                                        database.reference.child(id).child("gameStatus").setValue(false)
                                    }
                                    builder.show()
                                } else if (id != user.uid && gameStatus == true && position == "-1") {
                                    // daj odpowiedź zwrotną, czy gracz przyjął zaproszenie
                                    // position == -1, będzie ignorować dalsze ruchy graczy
                                    database.reference.child(id!!).child("gameStatus").setValue(0)
                                    val intent = Intent(this@MainActivity, GameActivity::class.java)
                                    intent.putExtra("id", user.uid)
                                    intent.putExtra("oppId", usersIds[oppPosition].toString())
                                    intent.putExtra("mail", user.email)
                                    intent.putExtra("oppMail", usersMails[oppPosition].toString())
                                    // na czas rozgrywki usuń użytkowników z dostępnej puli do grania
                                    database.reference.child("Users").child(id).removeValue()
                                    database.reference.child("Users").child(user.uid).removeValue()
                                    startActivity(intent)
                                } else if (id != user.uid && gameStatus == false) {
                                    // jeśli się nie zgodził wyświetl informację, usuń rozgrywkę
                                    val builder = AlertDialog.Builder(this@MainActivity)
                                    builder.setTitle("Notification")
                                    builder.setMessage("Player didn't accept your invitation")
                                    builder.setNeutralButton("Ok") { _, _ ->
                                        database.reference.child(id!!).removeValue()
                                    }
                                    builder.setOnCancelListener {
                                        database.reference.child(id!!).removeValue()
                                    }
                                    builder.show()
                                }
                            }
                        }
                        override fun onCancelled(databaseError: DatabaseError) {}
                    }
                )
            } else {
                // w przeciwnym razie zacznij rejestrację od nowa
                createLogin()
            }
        }
    }

    // przycisk wylogowujący się z gry
    fun onClick(view: View) {
        finish()
    }

    // zakończenie aplikacji wylogowuje użytkownika z bazy i usuwa go z tabeli użytkowników
    override fun onDestroy() {
        super.onDestroy()
        val ref = database.reference
        ref.child("Users").child(user.uid).removeValue()
        signOut()
    }

    // wylogowuje danego użytkownika
    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {}
    }

    companion object {
        private const val RC_SIGN_IN = 123
    }
}