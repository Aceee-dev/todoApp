package com.example.todoApp

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView


class PopupWindowUtil {

    companion object {
        @SuppressLint("ClickableViewAccessibility")
        fun showPopWindow(view: View) {
            // inflate the layout of the popup window
            val inflater =
                view.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val popupView: View = inflater.inflate(R.layout.about_info_popup_window, null)

            // create the popup window
            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true // lets taps outside the popup also dismiss it

            //add text
            val tview = popupView.findViewById(R.id.tvAboutInfo) as TextView
            val content = "This App is made for usability purpose and stores the data locally and" +
                    " thus there is full control of user over the data. \n\n Made By Anukul Chand. \n\n" +
                    "App version: ${BuildConfig.VERSION_NAME}"
            tview.text = content

            val popupWindow = PopupWindow(popupView, width, height, focusable)

            // show the popup window
            // which view you pass in doesn't matter, it is only used for the window token
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

            /*
            dismiss the popup window when touched
            */
            popupView.setOnTouchListener { _, _ ->
                popupWindow.dismiss()
                true
            }
        }
    }
}