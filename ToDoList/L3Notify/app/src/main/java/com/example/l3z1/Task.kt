package com.example.l3z1

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

// parcelable - obiekt, który można przesyłać pomiędzy aktywnościami
@Entity
data class Task(
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "description") var description: String?,
    @ColumnInfo var date: String?,
    @ColumnInfo(name = "icon") var icon: String?,
    @ColumnInfo(name = "priority") var priority: String?,
    @PrimaryKey(autoGenerate = true) var addingOrder: Int?): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(description)
        dest.writeString(date)
        dest.writeString(icon)
        dest.writeString(priority)
    }

    override fun describeContents(): Int {
        return 0
    }

    // pojedynczy obiekt, którego atrybuty i funkcje są związane z klasą, a nie do instancji obiektu
    // odpowiednik static w Java
    companion object CREATOR : Parcelable.Creator<Task> {
        override fun createFromParcel(source: Parcel): Task {
            return Task(source)
        }

        override fun newArray(size: Int): Array<Task?> {
            return arrayOfNulls(size)
        }
    }
}
