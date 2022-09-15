package com.example.todoApp

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true) var tid: Int = 0,
    val title: String,
    var isChecked: Boolean = false
) : Parcelable