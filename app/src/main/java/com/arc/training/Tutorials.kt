
package com.arc.training

fun exerciseTutorial(ex: Exercise): Map<String, String> {
    val equip = ex.equipment
    val pattern = ex.pattern
    val lower = ex.name.lowercase()
    val setup = when {
        lower.contains("bench press") -> "Set the bench on a stable surface. Place both feet firmly on the floor. Adjust the rack so the bar can be unracked without reaching or losing your shoulder position."
        lower.contains("lat pulldown") -> "Adjust the seat and thigh pad so your legs are secure. Select a light starting load and choose the grip shown for this variation."
        lower.contains("squat") -> "Set the rack or machine so the weight starts safely. Stand with your feet at the stance you can control comfortably."
        lower.contains("deadlift") || pattern == "hinge" -> "Place the weight where you can reach it with a stable stance. Before lifting, brace your trunk and set the weight close to your body."
        lower.contains("curl") -> "Choose a load you can control. Stand or sit as shown and position your shoulders and elbows so the starting position is stable."
        lower.contains("lateral raise") -> "Stand tall with the weights at your sides or use the cable setup shown. Keep a soft bend in the elbows."
        equip == "Cable" -> "Adjust the cable height for this variation, attach the correct handle, and check the pin before starting."
        equip == "Machine" || equip == "Assisted Machine" -> "Adjust the seat, pads and lever positions so your joints line up with the machine's pivot points. Test the range with a light load."
        else -> "Set up the equipment on a stable surface and choose a light load you can control. Put your feet, hands and body into the starting position shown in the animation."
    }
    val start = when (pattern) {
        "squat" -> "Stand balanced with your feet planted. Keep your head and chest in a natural position and brace your trunk before the first repetition."
        "hinge" -> "Start with the load close to you, knees slightly bent, hips set back and your spine in its natural position."
        "horizontal_push","incline_push","vertical_push" -> "Set the shoulders and wrists in a comfortable position, brace the trunk, and begin with the weight under control."
        "horizontal_pull","vertical_pull" -> "Set your torso stable, grip the handle or bar, and allow the target muscles to begin in a controlled lengthened position."
        "lateral_raise","front_raise","rear_delt" -> "Stand or sit tall with the weights in the starting position. Keep the trunk stable before moving the arms."
        "curl","triceps_extension","triceps_pushdown" -> "Keep the upper arm stable in the starting position and hold the handle or weight securely."
        "crunch","rotation","anti_rotation","isometric" -> "Set your body so the torso is controlled and your breathing remains comfortable before starting."
        else -> "Begin from the position shown in the animation. Make the first repetition deliberately slow so you can learn the movement path."
    }
    val dataSteps = if (ex.instructions.isNotEmpty()) ex.instructions.joinToString("\n\n") { idx -> "• $idx" } else null
    val steps = dataSteps ?: when (pattern) {
        "squat" -> "1. Brace your trunk. 2. Bend at the hips and knees together. 3. Lower only as far as you can keep a controlled position. 4. Drive through the floor to stand. 5. Reset your balance before the next repetition."
        "hinge" -> "1. Brace before moving. 2. Push the hips backward while keeping the weight close. 3. Lower until you reach a comfortable controlled range. 4. Drive the hips forward to return. 5. Finish tall without leaning backward."
        "horizontal_push","incline_push","vertical_push" -> "1. Set the body and grip. 2. Lower or bring the resistance toward the start position under control. 3. Press through the intended path without bouncing. 4. Stop before losing control. 5. Return smoothly and repeat."
        "horizontal_pull","vertical_pull" -> "1. Brace the torso. 2. Pull by driving the elbows or handles through the intended path. 3. Pause briefly at the controlled end position. 4. Let the resistance return slowly. 5. Keep the torso from swinging."
        "lateral_raise","front_raise","rear_delt" -> "1. Begin with the arms in the start position. 2. Raise them through the intended arc without swinging. 3. Stop at a range you can control. 4. Lower slowly. 5. Repeat without using momentum."
        "curl" -> "1. Keep the upper arm stable. 2. Curl the weight through the elbow. 3. Squeeze gently at the top. 4. Lower until the arm is back under control. 5. Repeat without swinging the torso."
        "triceps_extension","triceps_pushdown" -> "1. Set the upper arm. 2. Extend through the elbow while keeping the shoulder position controlled. 3. Finish without snapping the joint. 4. Return slowly. 5. Repeat."
        "crunch" -> "1. Brace the core. 2. Move through the trunk as designed for the exercise. 3. Pause briefly in the shortened position. 4. Return slowly. 5. Keep the movement controlled."
        "rotation","anti_rotation" -> "1. Set the feet and trunk. 2. Rotate or resist rotation from the intended area rather than swinging. 3. Move smoothly. 4. Return with control."
        "carry" -> "1. Pick up the load safely. 2. Stand tall with a stable grip. 3. Walk with controlled steps. 4. Keep the trunk steady. 5. Put the load down under control."
        else -> "1. Set the starting position. 2. Begin the movement slowly. 3. Follow the demonstrated path. 4. Control the return. 5. Repeat while keeping the same setup."
    }
    return mapOf(
        "setup" to setup,
        "start" to start,
        "steps" to steps,
        "breathing" to "In general, inhale during the controlled preparation/lowering phase and exhale during the main effort. Keep breathing controlled and do not force a breath-hold.",
        "tempo" to "Use a controlled rhythm. A useful beginner default is about 2 seconds on the lowering/returning phase and a smooth, deliberate lifting phase.",
        "rom" to "Use the range you can control while keeping the intended body position. Do not force an extreme joint position just to make the movement look bigger.",
        "stable" to "Keep the body parts not meant to move stable. Common examples are the feet, torso, head, and upper arm, depending on the exercise.",
        "mistakes" to "Common mistakes include using too much weight, swinging for momentum, rushing the return, changing body position between repetitions, and continuing after you can no longer control the movement.",
        "beginner" to "Start lighter than you think you need. Learn the movement with clean repetitions first. If the setup or movement feels confusing, stop and ask a qualified trainer to check your form. Stop if you experience sharp or concerning pain.",
        "quick" to "Stable setup • Controlled path • No unnecessary momentum • Comfortable range • Controlled return"
    )
}
