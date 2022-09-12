package com.example.todoApp

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Todo(
    val title: String,
    var isChecked: Boolean = false
) : Parcelable