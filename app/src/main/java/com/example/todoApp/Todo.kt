package com.example.todoApp

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
@androidx.room.Fts4
data class Todo(
    var title: String,
    var isChecked: Boolean = false,
    var description: String = "",
    var priority: Int = 4,
    var completion_date: String = ""
) : Parcelable