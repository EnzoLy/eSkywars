package me.enzol.skywars.utils

interface Callback<T> {
    fun call(value: T)
}