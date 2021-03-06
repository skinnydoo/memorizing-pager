/*
 * Copyright (C) 2018 Ivan Zinovyev (https://github.com/lndmflngs)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lockwood.memorizingpager

import android.os.Parcel
import android.os.Parcelable
import java.util.Deque
import java.util.ArrayDeque

class NavigationHistory() : Parcelable {

    private var selectedPages: Deque<Int> = ArrayDeque<Int>(MAX_BOTTOM_DESTINATIONS)
    private var isBackPressed = false

    constructor(parcel: Parcel) : this() {
        isBackPressed = parcel.readByte() != 0.toByte()
        selectedPages = parcel.readSerializable() as ArrayDeque<Int>
    }

    fun pushItem(item: Int) {
        // remove if already was selected, move it to front
        if (selectedPages.contains(item)) selectedPages.remove(item)
        selectedPages.push(item)
        isBackPressed = false
    }

    fun onBackPressed(): Int {
        return if (selectedPages.size == 1 && !isBackPressed) {
            selectedPages.clear()
            0
        } else if (selectedPages.size >= 2 && !isBackPressed) {
            isBackPressed = true
            selectedPages.pop()
            selectedPages.pop()
        } else {
            selectedPages.pop()
        }
    }

    fun isEmpty() = selectedPages.isEmpty()

    override fun writeToParcel(parcel: Parcel, p1: Int) {
        parcel.writeByte((if (isBackPressed) 1 else 0).toByte())
        parcel.writeSerializable(selectedPages as ArrayDeque<Int>)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<NavigationHistory> {

        private const val MAX_BOTTOM_DESTINATIONS = 5

        override fun createFromParcel(parcel: Parcel): NavigationHistory {
            return NavigationHistory(parcel)
        }

        override fun newArray(size: Int): Array<NavigationHistory?> {
            return arrayOfNulls(size)
        }
    }
}