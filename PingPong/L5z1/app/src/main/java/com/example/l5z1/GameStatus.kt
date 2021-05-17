package com.example.l5z1

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class GameStatus (
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id : Long = 0,
    @ColumnInfo(name = "leftPoints") var leftPoints : Int,
    @ColumnInfo(name = "rightPoints") var rightPoints : Int
)
