package com.example.l5z2

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class GameActivity : AppCompatActivity() {

    private var player: Int = 0
    private val images = arrayOf (
        R.drawable.cross,
        R.drawable.circle
    )
    private var board: ArrayList<Int> = ArrayList() // lista kwadratów planszy
    private val database = Firebase.database("https://l5z2-c6296-default-rtdb.europe-west1.firebasedatabase.app")
    // dane graczy
    private var id: String = ""
    private var oppId: String = ""
    private var mail: String = ""
    private var oppMail: String = ""
    private var canMove: Boolean = false    // granie na przemian
    private var canBeWinner: Boolean = false    // po każdym ruchu gracza ustawia na true, jeśli był to ruch wygrany, gracz zostaje zwyciezcą

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        for(i in 0..8){
            board.add(-1)   // -1 oznacza pole puste
        }

        val extras = intent.extras
        if (extras != null) { // gracz inicjujący rozgrywkę
            if (extras.containsKey("oppId")) {
                id = extras.getString("id").toString()
                oppId = extras.getString("oppId").toString()
                mail = extras.getString("mail").toString()
                oppMail = extras.getString("oppMail").toString()
                canMove = true
            } else { // pozostały gracz
                oppId = extras.getString("id").toString()
            }
        }

        // każdy ruch zmienia position w bazie, więc gracze reagują na zmiany
        database.reference.child(oppId).addValueEventListener (
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (ds in dataSnapshot.children) {
                        if (ds.key == "gameStatus")
                            continue
                        val position: String? = ds.value as String?
                        if (position != null && position != "-1") {
                            val bId = resources.getIdentifier("button$position", "id", packageName)
                            val btn = findViewById<ImageButton>(bId)
                            btn.isEnabled = false   // dezaktywacja zajętego pola
                            btn.setImageResource(images[player])    // dodanie obrazka
                            canMove = !canMove      // zablokowanie lub odblokowanie ruchu
                            val playerSign = findViewById<ImageView>(R.id.imageView2)
                            playerSign.setImageResource(images[player]) // zmiana pokazywania gracza, który wykona następny ruch
                            mark(position)
                        }
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {}
            }
        )
    }

    // funkcja wywołująca się po naciśnięciu kwadratu
    fun move(view: View) {
        if (canMove) {
            val button = view as ImageButton
            var buttonName = resources.getResourceEntryName(view.getId())
            buttonName = buttonName.substring(6) // wzięcie numeracji przycisku
            button.isEnabled = false
            button.setImageResource(images[player])     // zaznaczenie symbolem gracza
            canBeWinner = true  // możliwość bycia ruchem wygranym
            database.reference.child(oppId).child("position").setValue(buttonName) // zmiana pozycji w bazie
        }
    }

    // zaznacza ruch w logice gry
    private fun mark(buttonName: String) {
        board[buttonName.toInt()] = player //zajmuje pole
        checkGameState()    // sprawdza czy to koniec gry
        canBeWinner = false // skoro gra się nie zakończyła, nie był to ruch wygrywający
        player = (player + 1) % 2 // zmiana gracza, który wykona następny ruch
        val playerSign = findViewById<ImageView>(R.id.imageView2)
        playerSign.setImageResource(images[player]) // zmiana graficzna gracza
    }

    private fun checkGameState() {
        // wszystkie pola są zajęte
        if (!board.contains(-1)) {
            endGame(false)
        }
        else {
            for (i in 0..2) {
                var flag1 = true
                var flag2 = true
                val m = i * 3
                for (j in 0..2) {
                    val n = j * 3
                    // poziomo
                    if (board.elementAt(m + j) != player) {
                        flag1 = false
                    }
                    // pionowo
                    if (board.elementAt(i + n) != player) {
                        flag2 = false
                    }
                }
                if (flag1 || flag2) {
                    endGame(canBeWinner)
                    return
                }
            }
            var flag1 = true
            var flag2 = true
            // skośnie
            for (i in 0..2) {
                val m = i * 4
                val n = (i+1) * 2
                if (board.elementAt(m) != player) {
                    flag1 = false
                }
                if (board.elementAt(n) != player) {
                    flag2 = false
                }
            }
            if (flag1 || flag2) {
                endGame(canBeWinner)
            }
        }
    }

    // gdy koniec gry usuwa zapis gry z bazy danych i nowo dodaje uzytkowników, by mogli przeprowadzić rozgrywkę
    private fun endGame(isWinner: Boolean) {
        if (id != "") {
            database.reference.child(oppId).removeValue()
            database.reference.child("Users").child(id).setValue(mail)
            database.reference.child("Users").child(oppId).setValue(oppMail)
        }
        if (isWinner) {
            Toast.makeText(applicationContext, "Wygrałeś!", Toast.LENGTH_LONG).show()
        }
        finish()
    }
}