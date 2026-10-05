
package com.arc.training

fun warmupFor(day: WorkoutDay, exercises: List<Exercise>): List<WarmupItem> {
    val muscles = day.exercises.flatMap { pe -> exercises.firstOrNull { it.id == pe.exerciseId }?.let { listOf(it.primaryMuscle) + it.secondaryMuscles } ?: emptyList() }.map { it.lowercase() }.toSet()
    val out = mutableListOf<WarmupItem>()
    out += WarmupItem("Easy general movement", "Walk around, march in place, or do another easy movement for a few minutes to raise body temperature.", 180)
    if (muscles.any { it.contains("shoulder") || it.contains("chest") || it.contains("delt") || it.contains("back") || it.contains("lat") }) out += WarmupItem("Upper-body movement", "Use slow arm circles and comfortable shoulder movements. Keep the range easy; this is preparation, not a workout.", 60)
    if (muscles.any { it.contains("leg") || it.contains("quad") || it.contains("hamstring") || it.contains("glute") || it.contains("calf") || it.contains("adductor") }) out += WarmupItem("Lower-body movement", "Use easy bodyweight squats or controlled leg movements to prepare the hips, knees and ankles.", 60)
    if (muscles.any { it.contains("biceps") || it.contains("triceps") || it.contains("forearm") }) out += WarmupItem("Elbow and arm preparation", "Move the elbows and wrists through comfortable ranges, then do a few very light practice repetitions of your first arm movement.", 60)
    val first = day.exercises.firstOrNull()
    if (first != null) out += WarmupItem("First exercise rehearsal", "Do 1–3 light practice sets of your first major exercise, gradually increasing the load only if the movement still feels controlled.", reps = 8)
    return out
}

fun stretchesFor(day: WorkoutDay, exercises: List<Exercise>): List<StretchItem> {
    val muscles = day.exercises.flatMap { pe -> exercises.firstOrNull { it.id == pe.exerciseId }?.let { listOf(it.primaryMuscle) + it.secondaryMuscles } ?: emptyList() }.map { it.lowercase() }.toSet()
    val out = mutableListOf<StretchItem>()
    if (muscles.any { it.contains("chest") }) out += StretchItem("Gentle chest doorway stretch", "Chest", "Place your forearm against a doorway and gently turn your body away until you feel a mild stretch. Do not force the range.")
    if (muscles.any { it.contains("back") || it.contains("lat") }) out += StretchItem("Gentle lat stretch", "Lats / upper body", "Reach forward and slightly to the side while keeping the movement comfortable. Avoid forcing the shoulder overhead.")
    if (muscles.any { it.contains("shoulder") || it.contains("delt") }) out += StretchItem("Cross-body shoulder stretch", "Shoulders", "Bring one arm across the body and use the other hand to hold it gently. Keep the stretch mild.")
    if (muscles.any { it.contains("biceps") }) out += StretchItem("Gentle biceps stretch", "Biceps", "Place the hand against a stable surface with the palm open and gently turn the body away.")
    if (muscles.any { it.contains("triceps") }) out += StretchItem("Gentle triceps stretch", "Triceps", "Bring one arm overhead, bend the elbow, and use the other hand to support it without forcing the position.")
    if (muscles.any { it.contains("quad") }) out += StretchItem("Standing quad stretch", "Quadriceps", "Hold a stable support, bend one knee and bring the heel toward you. Keep the knees close and stop well before discomfort becomes painful.")
    if (muscles.any { it.contains("hamstring") }) out += StretchItem("Seated hamstring stretch", "Hamstrings", "Extend one leg comfortably and lean forward from the hips until you feel a mild stretch.")
    if (muscles.any { it.contains("glute") }) out += StretchItem("Figure-four glute stretch", "Glutes", "Sit or lie comfortably, place one ankle over the opposite leg and gently move into the stretch.")
    if (muscles.any { it.contains("calf") }) out += StretchItem("Wall calf stretch", "Calves", "Keep the heel down and lean toward a wall until you feel a mild calf stretch.")
    if (out.isEmpty()) out += StretchItem("Easy whole-body cooldown", "Whole body", "Walk slowly and breathe comfortably for a few minutes, then perform only gentle stretches that feel comfortable.")
    return out
}
