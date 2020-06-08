package com.muxi.barcodereader.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

fun Fragment.navigateTo(destinationFragment: Fragment) {
    this.requireActivity().supportFragmentManager.beginTransaction().replace(
        this.id,
        destinationFragment
    ).commit()
}
