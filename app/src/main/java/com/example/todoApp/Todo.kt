package com.example.todoApp

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true) var tid: Int = 0,
    var title: String,
    var isChecked: Boolean = false,
    var description: String = "",
    var priority: Int = 4,
    var completion_date: String = ""
) : Parcelable