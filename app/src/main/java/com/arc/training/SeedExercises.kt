
package com.arc.training

object SeedExercises {
    private fun e(name:String,muscle:String,eq:String,pattern:String,diff:String="beginner")=Exercise(name=name,primaryMuscle=muscle,equipment=eq,pattern=pattern,difficulty=diff)
    val all: List<Exercise> = listOf(
      e("Barbell Bench Press","Chest","Barbell","horizontal_push","intermediate"), e("Incline Barbell Bench Press","Upper Chest","Barbell","incline_push","intermediate"),
      e("Decline Barbell Bench Press","Lower Chest","Barbell","horizontal_push","intermediate"), e("Dumbbell Bench Press","Chest","Dumbbell","horizontal_push"),
      e("Incline Dumbbell Press","Upper Chest","Dumbbell","incline_push"), e("Dumbbell Fly","Chest","Dumbbell","isolation"),
      e("Cable Chest Fly","Chest","Cable","isolation"), e("Low Cable Fly","Upper Chest","Cable","isolation"), e("High Cable Fly","Lower Chest","Cable","isolation"),
      e("Machine Chest Press","Chest","Machine","horizontal_push"), e("Smith Machine Bench Press","Chest","Smith Machine","horizontal_push","intermediate"),
      e("Push Up","Chest","Bodyweight","horizontal_push"), e("Chest Dip","Chest","Bodyweight","horizontal_push","intermediate"),
      e("Barbell Row","Upper Back","Barbell","horizontal_pull","intermediate"), e("Pendlay Row","Upper Back","Barbell","horizontal_pull","intermediate"),
      e("Dumbbell Row","Lats","Dumbbell","horizontal_pull"), e("Chest Supported Dumbbell Row","Upper Back","Dumbbell","horizontal_pull"),
      e("Seated Cable Row","Upper Back","Cable","horizontal_pull"), e("Wide Grip Cable Row","Upper Back","Cable","horizontal_pull"),
      e("Lat Pulldown","Lats","Cable","vertical_pull"), e("Neutral Grip Lat Pulldown","Lats","Cable","vertical_pull"),
      e("Close Grip Lat Pulldown","Lats","Cable","vertical_pull"), e("Pull Up","Lats","Bodyweight","vertical_pull","intermediate"), e("Chin Up","Lats","Bodyweight","vertical_pull","intermediate"),
      e("Straight Arm Pulldown","Lats","Cable","isolation"), e("Deadlift","Back","Barbell","hinge","intermediate"), e("Rack Pull","Upper Back","Barbell","hinge","intermediate"),
      e("Dumbbell Shrug","Traps","Dumbbell","shrug"), e("Barbell Shrug","Traps","Barbell","shrug"), e("Face Pull","Rear Delts","Cable","rear_delt"),
      e("Barbell Overhead Press","Shoulders","Barbell","vertical_push","intermediate"), e("Seated Dumbbell Shoulder Press","Shoulders","Dumbbell","vertical_push"),
      e("Arnold Press","Shoulders","Dumbbell","vertical_push","intermediate"), e("Machine Shoulder Press","Shoulders","Machine","vertical_push"),
      e("Dumbbell Lateral Raise","Side Delts","Dumbbell","lateral_raise"), e("Cable Lateral Raise","Side Delts","Cable","lateral_raise"), e("Machine Lateral Raise","Side Delts","Machine","lateral_raise"),
      e("Front Raise","Front Delts","Dumbbell","front_raise"), e("Cable Front Raise","Front Delts","Cable","front_raise"), e("Reverse Pec Deck","Rear Delts","Machine","rear_delt"),
      e("Rear Delt Cable Fly","Rear Delts","Cable","rear_delt"), e("EZ Bar Curl","Biceps","EZ Bar","curl"), e("Barbell Curl","Biceps","Barbell","curl"),
      e("Dumbbell Curl","Biceps","Dumbbell","curl"), e("Alternating Dumbbell Curl","Biceps","Dumbbell","curl"), e("Incline Dumbbell Curl","Biceps","Dumbbell","curl"),
      e("Hammer Curl","Brachialis","Dumbbell","curl"), e("Cross Body Hammer Curl","Brachialis","Dumbbell","curl"), e("Concentration Curl","Biceps","Dumbbell","curl"),
      e("Preacher Curl","Biceps","Machine","curl"), e("Cable Curl","Biceps","Cable","curl"), e("Bayesian Cable Curl","Biceps","Cable","curl","intermediate"),
      e("Rope Triceps Pushdown","Triceps","Cable","triceps_pushdown"), e("Straight Bar Pushdown","Triceps","Cable","triceps_pushdown"), e("Overhead Cable Extension","Triceps","Cable","triceps_extension"),
      e("Skull Crusher","Triceps","EZ Bar","triceps_extension","intermediate"), e("Close Grip Bench Press","Triceps","Barbell","horizontal_push","intermediate"),
      e("Dumbbell Overhead Triceps Extension","Triceps","Dumbbell","triceps_extension"), e("Cable Kickback","Triceps","Cable","triceps_extension"),
      e("Back Squat","Quadriceps","Barbell","squat","intermediate"), e("Front Squat","Quadriceps","Barbell","squat","intermediate"), e("Goblet Squat","Quadriceps","Dumbbell","squat"),
      e("Smith Machine Squat","Quadriceps","Smith Machine","squat","intermediate"), e("Hack Squat","Quadriceps","Machine","squat","intermediate"), e("Leg Press","Quadriceps","Machine","squat"),
      e("Bulgarian Split Squat","Quadriceps","Dumbbell","lunge","intermediate"), e("Walking Lunge","Quadriceps","Dumbbell","lunge"), e("Reverse Lunge","Glutes","Dumbbell","lunge"),
      e("Step Up","Glutes","Dumbbell","lunge"), e("Leg Extension","Quadriceps","Machine","isolation"), e("Romanian Deadlift","Hamstrings","Barbell","hinge","intermediate"),
      e("Dumbbell Romanian Deadlift","Hamstrings","Dumbbell","hinge"), e("Seated Leg Curl","Hamstrings","Machine","isolation"), e("Lying Leg Curl","Hamstrings","Machine","isolation"),
      e("Nordic Curl","Hamstrings","Bodyweight","isolation","advanced"), e("Hip Thrust","Glutes","Barbell","hinge"), e("Dumbbell Hip Thrust","Glutes","Dumbbell","hinge"),
      e("Glute Bridge","Glutes","Bodyweight","hinge"), e("Cable Kickback","Glutes","Cable","isolation"), e("Hip Abduction","Glutes","Machine","isolation"), e("Hip Adduction","Adductors","Machine","isolation"),
      e("Standing Calf Raise","Calves","Machine","calf_raise"), e("Seated Calf Raise","Calves","Machine","calf_raise"), e("Leg Press Calf Raise","Calves","Machine","calf_raise"),
      e("Cable Crunch","Abdominals","Cable","crunch"), e("Machine Crunch","Abdominals","Machine","crunch"), e("Reverse Crunch","Abdominals","Bodyweight","crunch"),
      e("Hanging Knee Raise","Abdominals","Bodyweight","crunch","intermediate"), e("Hanging Leg Raise","Abdominals","Bodyweight","crunch","intermediate"), e("Ab Wheel Rollout","Abdominals","Other","anti_rotation","intermediate"),
      e("Plank","Abdominals","Bodyweight","isometric"), e("Side Plank","Obliques","Bodyweight","isometric"), e("Pallof Press","Obliques","Cable","anti_rotation"),
      e("Cable Woodchop","Obliques","Cable","rotation"), e("Farmer Carry","Forearms","Dumbbell","carry"), e("Wrist Curl","Forearms","Dumbbell","isolation"),
      e("Reverse Wrist Curl","Forearms","Dumbbell","isolation"), e("Kettlebell Swing","Glutes","Kettlebell","hinge","intermediate"),
      e("Landmine Press","Shoulders","Landmine","vertical_push"), e("Landmine Row","Upper Back","Landmine","horizontal_pull"), e("T-Bar Row","Upper Back","Machine","horizontal_pull","intermediate"),
      e("Machine Row","Upper Back","Machine","horizontal_pull"), e("Assisted Pull Up","Lats","Assisted Machine","vertical_pull"), e("Machine Preacher Curl","Biceps","Machine","curl"),
      e("Dip Machine","Triceps","Machine","triceps_extension"), e("Hack Squat Machine","Quadriceps","Machine","squat","intermediate")
    )
}
