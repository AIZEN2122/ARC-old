
package com.arc.training

import android.content.Context
import java.io.*

class AppStateStore(private val context: Context) {
    private val file = File(context.filesDir, "arc_state.bin")

    fun load(): AppState {
        if (!file.exists()) return AppState()
        return try {
            ObjectInputStream(FileInputStream(file)).use { it.readObject() as AppState }
        } catch (_: Exception) { AppState() }
    }

    fun save(state: AppState) {
        try {
            FileOutputStream(file).use { output -> ObjectOutputStream(output).use { it.writeObject(state) } }
        } catch (_: Exception) { }
    }

    fun exportTo(path: File, state: AppState) {
        FileOutputStream(path).use { output -> ObjectOutputStream(output).use { it.writeObject(state) } }
    }

    fun importFrom(path: File): AppState = FileInputStream(path).use { input -> ObjectInputStream(input).use { it.readObject() as AppState } }
}
