package com.bakursu.palitosmensajes

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

class viewextensions {

    fun View.hideKeyboard(){

        val inm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inm.hideSoftInputFromWindow(windowToken, 0)

    }


}