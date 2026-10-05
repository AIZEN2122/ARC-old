
package com.arc.training

import java.io.Serializable
import java.util.UUID

data class Exercise(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val primaryMuscle: String,
    val secondaryMuscles: List<String> = emptyList(),
    val equipment: String,
    val pattern: String = "isolation",
    val difficulty: String = "beginner",
    val instructions: List<String> = emptyList(),
    val source: String = "ARC",
    val isCustom: Boolean = false,
    val animationId: String = pattern
) : Serializable

data class PlannedExercise(
    val exerciseId: String,
    var sets: Int = 3,
    var repsMin: Int = 8,
    var repsMax: Int = 12,
    var weight: Double = 0.0,
    var restSeconds: Int = 90,
    var rir: Int = 2,
    var notes: String = ""
) : Serializable

data class WorkoutDay(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var restDay: Boolean = false,
    val exercises: MutableList<PlannedExercise> = mutableListOf()
) : Serializable

data class WorkoutPlan(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    val days: MutableList<WorkoutDay> = mutableListOf()
) : Serializable

data class LoggedSet(
    val exerciseId: String,
    val weight: Double,
    val reps: Int,
    val rir: Int,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable

data class WorkoutSession(
    val id: String = UUID.randomUUID().toString(),
    val planId: String,
    val dayId: String,
    val startedAt: Long,
    val endedAt: Long? = null,
    val sets: MutableList<LoggedSet> = mutableListOf(),
    val notes: String = ""
) : Serializable

data class Goal(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    var type: String,
    var target: Double,
    var current: Double = 0.0,
    var unit: String = "",
    var exerciseId: String? = null,
    var active: Boolean = true
) : Serializable

data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var done: Boolean = false,
    var recurring: Boolean = true
) : Serializable

data class Checklist(
    val id: String = UUID.randomUUID().toString(),
    var name: String,
    val items: MutableList<ChecklistItem> = mutableListOf()
) : Serializable

data class WarmupItem(val name: String, val instructions: String, val seconds: Int = 45, val reps: Int = 0) : Serializable
data class StretchItem(val name: String, val target: String, val instructions: String, val seconds: Int = 30) : Serializable

data class AppState(
    val plans: MutableList<WorkoutPlan> = mutableListOf(),
    val sessions: MutableList<WorkoutSession> = mutableListOf(),
    val goals: MutableList<Goal> = mutableListOf(),
    val checklists: MutableList<Checklist> = mutableListOf(),
    var selectedPlanId: String? = null,
    var selectedDayId: String? = null,
    var defaultRest: Int = 90,
    var weightUnit: String = "kg",
    var currentStreak: Int = 0,
    val favorites: MutableSet<String> = mutableSetOf(),
    val recentExerciseIds: MutableList<String> = mutableListOf()
) : Serializable
