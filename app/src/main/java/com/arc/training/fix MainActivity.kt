package com.arc.training

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.sin

private val Bg = Color(0xFF0B0B0D)
private val SurfaceColor = Color(0xFF141418)
private val Surface2 = Color(0xFF1A1A20)
private val TextMain = Color(0xFFF4F4F5)
private val TextMuted = Color(0xFF9B9BA3)
private val Accent = Color(0xFFD7FF4B)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ARCApp()
        }
    }
}

@Composable
fun ARCApp() {
    val context = LocalContext.current
    val store = remember { AppStateStore(context) }
    val state = remember { mutableStateOf(store.load()) }

    val exercises = remember {
        mutableStateListOf<Exercise>().also {
            it.addAll(SeedExercises.all)
        }
    }

    val snackbar = remember { SnackbarHostState() }
    val nav = rememberNavController()

    val save: () -> Unit = {
        store.save(state.value)
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Accent,
            background = Bg,
            surface = SurfaceColor,
            onBackground = TextMain,
            onSurface = TextMain
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Bg
        ) {
            Scaffold(
                containerColor = Bg,
                snackbarHost = {
                    SnackbarHost(snackbar)
                },
                bottomBar = {
                    ArcBottomBar(nav)
                }
            ) { padding ->

                NavHost(
                    navController = nav,
                    startDestination = "home",
                    modifier = Modifier.padding(padding)
                ) {

                    composable("home") {
                        HomeScreen(
                            nav = nav,
                            state = state.value,
                            exercises = exercises,
                            save = save
                        )
                    }

                    composable("exercises") {
                        ExerciseLibraryScreen(
                            nav = nav,
                            exercises = exercises,
                            save = save,
                            state = state.value
                        )
                    }

                    composable("plans") {
                        PlansScreen(
                            nav = nav,
                            state = state.value,
                            exercises = exercises,
                            save = save
                        )
                    }

                    composable("workout") {
                        WorkoutScreen(
                            nav = nav,
                            state = state.value,
                            exercises = exercises,
                            save = save
                        )
                    }

                    composable("goals") {
                        GoalsScreen(
                            state = state.value,
                            save = save
                        )
                    }

                    composable("history") {
                        HistoryScreen(
                            state = state.value,
                            exercises = exercises
                        )
                    }

                    composable("checklist") {
                        ChecklistScreen(
                            state = state.value,
                            save = save
                        )
                    }

                    composable("reports") {
                        ReportsScreen(
                            state = state.value,
                            exercises = exercises
                        )
                    }

                    composable("settings") {
                        SettingsScreen(
                            state = state,
                            exercises = exercises,
                            save = save,
                            snackbar = snackbar
                        )
                    }

                    composable("exercise/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")

                        if (id != null) {
                            val exercise = exercises.firstOrNull { it.id == id }

                            if (exercise != null) {
                                ExerciseDetailScreen(
                                    nav = nav,
                                    ex = exercise,
                                    state = state.value,
                                    save = save
                                )
                            }
                        }
                    }

                    composable("plan/{id}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")

                        if (id != null) {
                            val plan = state.value.plans.firstOrNull { it.id == id }

                            if (plan != null) {
                                PlanEditorScreen(
                                    nav = nav,
                                    state = state.value,
                                    plan = plan,
                                    exercises = exercises,
                                    save = save
                                )
                            }
                        }
                    }

                    composable("warmup/{planId}/{dayId}") { backStackEntry ->
                        val planId = backStackEntry.arguments?.getString("planId")
                        val dayId = backStackEntry.arguments?.getString("dayId")

                        val plan = state.value.plans.firstOrNull {
                            it.id == planId
                        }

                        val day = plan?.days?.firstOrNull {
                            it.id == dayId
                        }

                        if (day != null) {
                            WarmupScreen(
                                nav = nav,
                                day = day,
                                exercises = exercises
                            )
                        }
                    }

                    composable("stretch/{planId}/{dayId}") { backStackEntry ->
                        val planId = backStackEntry.arguments?.getString("planId")
                        val dayId = backStackEntry.arguments?.getString("dayId")

                        val plan = state.value.plans.firstOrNull {
                            it.id == planId
                        }

                        val day = plan?.days?.firstOrNull {
                            it.id == dayId
                        }

                        if (day != null) {
                            StretchScreen(
                                nav = nav,
                                day = day,
                                exercises = exercises
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArcBottomBar(nav: NavHostController) {

    val items = listOf(
        "home" to Icons.Default.Home,
        "exercises" to Icons.Default.FitnessCenter,
        "plans" to Icons.Default.CalendarMonth,
        "workout" to Icons.Default.PlayArrow,
        "goals" to Icons.Default.Flag
    )

    NavigationBar(
        containerColor = Color(0xFF0F0F12)
    ) {
        items.forEach { (route, icon) ->

            NavigationBarItem(
                selected = false,
                onClick = {
                    nav.navigate(route) {
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                },
                label = {
                    Text(
                        route.replaceFirstChar {
                            it.uppercase()
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun ArcTop(
    title: String,
    subtitle: String? = null,
    back: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (back != null) {
            IconButton(
                onClick = back
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextMain
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 13.sp
                )
            }
        }

        Text(
            text = "ARC",
            color = Accent,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun CardBox(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.clip(
            RoundedCornerShape(22.dp)
        ),
        color = SurfaceColor
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
fun HomeScreen(
    nav: NavHostController,
    state: AppState,
    exercises: List<Exercise>,
    save: () -> Unit
) {

    val selectedPlan =
        state.plans.firstOrNull {
            it.id == state.selectedPlanId
        } ?: state.plans.firstOrNull()

    val selectedDay =
        selectedPlan?.days?.firstOrNull {
            it.id == state.selectedDayId
        } ?: selectedPlan?.days?.firstOrNull()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = 120.dp
        )
    ) {

        item {
            ArcTop(
                title = "ARC",
                subtitle = "Train. Track. Evolve."
            )

            CardBox(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {

                Image(
                    painter = androidx.compose.ui.res.painterResource(
                        com.arc.training.R.drawable.arc_thumbnail
                    ),
                    contentDescription = "ARC thumbnail",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(190.dp)
                        .clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text = "TODAY'S SESSION",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = selectedDay?.name
                        ?: "Create a workout plan",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Button(
                        onClick = {
                            if (
                                selectedPlan != null &&
                                selectedDay != null
                            ) {
                                nav.navigate(
                                    "warmup/${selectedPlan.id}/${selectedDay.id}"
                                )
                            } else {
                                nav.navigate("plans")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Accent,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("WARM-UP")
                    }

                    OutlinedButton(
                        onClick = {
                            nav.navigate("workout")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("WORKOUT")
                    }
                }
            }
        }

        item {
            Spacer(
                modifier = Modifier.height(14.dp)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Metric(
                    label = "GOALS",
                    value = state.goals.count {
                        it.active
                    }.toString(),
                    modifier = Modifier.weight(1f)
                )

                Metric(
                    label = "STREAK",
                    value = state.currentStreak.toString(),
                    modifier = Modifier.weight(1f)
                )

                Metric(
                    label = "SESSIONS",
                    value = state.sessions.size.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(
                modifier = Modifier.height(14.dp)
            )
        }

        item {
            CardBox(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    text = "QUICK ACTIONS",
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                TextButton(
                    onClick = {
                        nav.navigate("exercises")
                    }
                ) {
                    Text("Browse exercises")
                }

                TextButton(
                    onClick = {
                        nav.navigate("plans")
                    }
                ) {
                    Text("Customize my split")
                }

                TextButton(
                    onClick = {
                        nav.navigate("checklist")
                    }
                ) {
                    Text("Open checklist")
                }

                TextButton(
                    onClick = {
                        nav.navigate("reports")
                    }
                ) {
                    Text("Generate report")
                }
            }
        }
    }
}

@Composable
fun Metric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    CardBox(
        modifier = modifier
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.sp
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ExerciseLibraryScreen(
    nav: NavHostController,
    exercises: MutableList<Exercise>,
    save: () -> Unit,
    state: AppState
) {

    var query by remember {
        mutableStateOf("")
    }

    var muscle by remember {
        mutableStateOf("All")
    }

    var favoritesOnly by remember {
        mutableStateOf(false)
    }

    var showCustom by remember {
        mutableStateOf(false)
    }

    var refresh by remember {
        mutableStateOf(0)
    }

    val muscles = listOf(
        "All",
        "Chest",
        "Back",
        "Lats",
        "Shoulders",
        "Biceps",
        "Triceps",
        "Quadriceps",
        "Hamstrings",
        "Glutes",
        "Calves",
        "Abdominals",
        "Obliques",
        "Forearms"
    )

    val filtered = exercises.filter { ex ->
        val matchesQuery =
            query.isBlank() ||
                ex.name.contains(query, true) ||
                ex.primaryMuscle.contains(query, true)

        val matchesMuscle =
            muscle == "All" ||
                ex.primaryMuscle.contains(muscle, true) ||
                ex.secondaryMuscles.any {
                    it.contains(muscle, true)
                }

        val matchesFavorite =
            !favoritesOnly ||
                state.favorites.contains(ex.id)

        matchesQuery &&
            matchesMuscle &&
            matchesFavorite
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ArcTop(
            title = "Exercises",
            subtitle = "Search, filter, learn"
        )

        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true,
            label = {
                Text("Search exercises")
            }
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(
                    rememberScrollState()
                )
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            muscles.forEach { item ->

                FilterChip(
                    selected = muscle == item,
                    onClick = {
                        muscle = item
                    },
                    label = {
                        Text(item)
                    }
                )
            }

            FilterChip(
                selected = favoritesOnly,
                onClick = {
                    favoritesOnly = !favoritesOnly
                },
                label = {
                    Text("Favorites")
                }
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Button(
            onClick = {
                showCustom = true
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                contentColor = Color.Black
            )
        ) {

            Icon(
                Icons.Default.Add,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(6.dp)
            )

            Text("CREATE CUSTOM EXERCISE")
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "${filtered.size} exercises",
            color = TextMuted,
            modifier = Modifier.padding(
                horizontal = 16.dp
            )
        )

        key(refresh) {
            LazyColumn(
                contentPadding = PaddingValues(
                    16.dp,
                    8.dp,
                    16.dp,
                    120.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = filtered,
                    key = {
                        it.id
                    }
                ) { ex ->

                    CardBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {

                                state.recentExerciseIds.remove(
                                    ex.id
                                )

                                state.recentExerciseIds.add(
                                    0,
                                    ex.id
                                )

                                if (
                                    state.recentExerciseIds.size > 20
                                ) {
                                    state.recentExerciseIds.removeLast()
                                }

                                save()

                                nav.navigate(
                                    "exercise/${ex.id}"
                                )
                            }
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = ex.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp
                                )

                                Text(
                                    text = "${ex.primaryMuscle} • ${ex.equipment}",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )

                                Text(
                                    text = ex.difficulty.replaceFirstChar {
                                        it.uppercase()
                                    },
                                    color = Accent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = {

                                    if (
                                        state.favorites.contains(ex.id)
                                    ) {
                                        state.favorites.remove(ex.id)
                                    } else {
                                        state.favorites.add(ex.id)
                                    }

                                    save()
                                    refresh++
                                }
                            ) {

                                Icon(
                                    imageVector =
                                        if (state.favorites.contains(ex.id)) {
                                            Icons.Default.Star
                                        } else {
                                            Icons.Default.StarBorder
                                        },
                                    contentDescription = "Favorite",
                                    tint = Accent
                                )
                            }

                            Icon(
                                imageVector =
                                    Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCustom) {
        CustomExerciseDialog(
            onCreate = { name, primaryMuscle, equipment, pattern ->

                exercises.add(
                    Exercise(
                        name = name,
                        primaryMuscle = primaryMuscle,
                        equipment = equipment,
                        pattern = pattern,
                        isCustom = true
                    )
                )

                save()
                refresh++
                showCustom = false
            },
            onDismiss = {
                showCustom = false
            }
        )
    }
}

@Composable
fun CustomExerciseDialog(
    onCreate: (
        String,
        String,
        String,
        String
    ) -> Unit,
    onDismiss: () -> Unit
) {

    var name by remember {
        mutableStateOf("")
    }

    var muscle by remember {
        mutableStateOf("Chest")
    }

    var equipment by remember {
        mutableStateOf("Dumbbell")
    }

    var pattern by remember {
        mutableStateOf("isolation")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Custom exercise")
        },
        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Name")
                    }
                )

                OutlinedTextField(
                    value = muscle,
                    onValueChange = {
                        muscle = it
                    },
                    label = {
                        Text("Primary muscle")
                    }
                )

                OutlinedTextField(
                    value = equipment,
                    onValueChange = {
                        equipment = it
                    },
                    label = {
                        Text("Equipment")
                    }
                )

                OutlinedTextField(
                    value = pattern,
                    onValueChange = {
                        pattern = it
                    },
                    label = {
                        Text("Animation profile")
                    }
                )
            }
        },
        confirmButton = {

            Button(
                onClick = {

                    if (name.isNotBlank()) {
                        onCreate(
                            name,
                            muscle,
                            equipment,
                            pattern
                        )
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ExerciseDetailScreen(
    nav: NavHostController,
    ex: Exercise,
    state: AppState,
    save: () -> Unit
) {

    val tutorial = remember(ex.id) {
        exerciseTutorial(ex)
    }

    var mode by remember {
        mutableStateOf("BEGINNER")
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = 120.dp
        )
    ) {

        item {

            ArcTop(
                title = ex.name,
                subtitle = "${ex.primaryMuscle} • ${ex.equipment}",
                back = {
                    nav.navigateUp()
                }
            )
        }

        item {

            CardBox(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {

                ExerciseAnimation(ex)

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    listOf(
                        "BEGINNER",
                        "STANDARD",
                        "QUICK"
                    ).forEach { item ->

                        FilterChip(
                            selected = mode == item,
                            onClick = {
                                mode = item
                            },
                            label = {
                                Text(item)
                            }
                        )
                    }
                }
            }
        }

        if (mode == "QUICK") {

            item {
                InfoSection(
                    title = "QUICK FORM CHECK",
                    text = tutorial["quick"]
                        ?: ""
                )
            }

        } else {

            item {
                InfoSection(
                    title = "HOW TO PERFORM",
                    text =
                        (tutorial["setup"] ?: "") +
                            "\n\n" +
                            (tutorial["start"] ?: "")
                )
            }

            item {
                InfoSection(
                    title = "STEP-BY-STEP",
                    text = tutorial["steps"] ?: ""
                )
            }

            item {
                InfoSection(
                    title = "BREATHING",
                    text = tutorial["breathing"] ?: ""
                )
            }

            item {
                InfoSection(
                    title = "TEMPO / CONTROL",
                    text = tutorial["tempo"] ?: ""
                )
            }

            item {
                InfoSection(
                    title = "RANGE OF MOTION",
                    text = tutorial["rom"] ?: ""
                )
            }

            item {
                InfoSection(
                    title = "WHAT SHOULD STAY STABLE",
                    text = tutorial["stable"] ?: ""
                )
            }

            item {
                InfoSection(
                    title = "FORM CUES",
                    text = tutorial["quick"] ?: ""
                )
            }

            item {
                InfoSection(
                    title = "COMMON MISTAKES",
                    text = tutorial["mistakes"] ?: ""
                )
            }

            item {
                InfoSection(
                    title = "BEGINNER NOTES",
                    text = tutorial["beginner"] ?: ""
                )
            }
        }

        item {

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Button(
                    onClick = {

                        val plan =
                            state.plans.firstOrNull()
                                ?: WorkoutPlan(
                                    name = "My Plan"
                                ).also { created ->

                                    created.days.add(
                                        WorkoutDay(
                                            name = "Day 1"
                                        )
                                    )

                                    state.plans.add(
                                        created
                                    )

                                    state.selectedPlanId =
                                        created.id

                                    state.selectedDayId =
                                        created.days.first().id
                                }

                        val day =
                            plan.days.firstOrNull()
                                ?: WorkoutDay(
                                    name = "Day 1"
                                ).also {
                                    plan.days.add(it)
                                }

                        if (
                            day.exercises.none {
                                it.exerciseId == ex.id
                            }
                        ) {
                            day.exercises.add(
                                PlannedExercise(ex.id)
                            )
                        }

                        save()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("ADD TO PLAN")
                }

                OutlinedButton(
                    onClick = {
                        nav.navigateUp()
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("BACK")
                }
            }
        }
    }
}

@Composable
fun InfoSection(
    title: String,
    text: String
) {

    Spacer(
        modifier = Modifier.height(10.dp)
    )

    CardBox(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {

        Text(
            text = title,
            color = Accent,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.2.sp
        )

        Spacer(
            modifier = Modifier.height(7.dp)
        )

        Text(
            text = text,
            color = TextMain,
            lineHeight = 21.sp
        )
    }
}

@Composable
fun ExerciseAnimation(
    ex: Exercise
) {

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "exerciseAnimation"
        )

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1600,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "phase"
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF0E0E11))
    ) {

        val cx = size.width / 2f
        val ground = size.height * 0.78f
        val motion =
            sin(
                phase *
                    Math.PI.toFloat() *
                    2f
            ) * 0.22f

        val head = Offset(
            cx,
            ground - 135f
        )

        val shoulder = Offset(
            cx,
            ground - 105f
        )

        val hip = Offset(
            cx,
            ground - 45f
        )

        drawCircle(
            color = TextMain,
            radius = 18f,
            center = head
        )

        drawLine(
            color = TextMain,
            start = shoulder,
            end = hip,
            strokeWidth = 8f
        )

        drawLine(
            color = TextMain,
            start = hip,
            end = Offset(
                cx - 30f,
                ground
            ),
            strokeWidth = 7f
        )

        drawLine(
            color = TextMain,
            start = hip,
            end = Offset(
                cx + 30f,
                ground
            ),
            strokeWidth = 7f
        )

        val armShift = when (ex.pattern) {
            "lateral_raise" ->
                55f * motion

            "front_raise" ->
                35f * motion

            "curl" ->
                -35f * motion

            "triceps_extension" ->
                45f * motion

            "horizontal_pull",
            "vertical_pull" ->
                -25f * motion

            "horizontal_push",
            "incline_push",
            "vertical_push" ->
                25f * motion

            else ->
                15f * motion
        }

        drawLine(
            color = TextMain,
            start = shoulder,
            end = Offset(
                cx - 40f,
                ground - 80f + armShift
            ),
            strokeWidth = 7f
        )

        drawLine(
            color = TextMain,
            start = Offset(
                cx - 40f,
                ground - 80f + armShift
            ),
            end = Offset(
                cx - 60f,
                ground - 35f + armShift
            ),
            strokeWidth = 7f
        )

        drawLine(
            color = TextMain,
            start = shoulder,
            end = Offset(
                cx + 40f,
                ground - 80f - armShift
            ),
            strokeWidth = 7f
        )

        drawLine(
            color = TextMain,
            start = Offset(
                cx + 40f,
                ground - 80f - armShift
            ),
            end = Offset(
                cx + 60f,
                ground - 35f - armShift
            ),
            strokeWidth = 7f
        )

        when (ex.equipment) {

            "Barbell",
            "EZ Bar",
            "Smith Machine" -> {

                drawLine(
                    color = Accent,
                    start = Offset(
                        cx - 75f,
                        ground - 92f + armShift
                    ),
                    end = Offset(
                        cx + 75f,
                        ground - 92f - armShift
                    ),
                    strokeWidth = 8f
                )
            }

            "Dumbbell" -> {

                drawCircle(
                    color = Accent,
                    radius = 8f,
                    center = Offset(
                        cx - 63f,
                        ground - 35f + armShift
                    )
                )

                drawCircle(
                    color = Accent,
                    radius = 8f,
                    center = Offset(
                        cx + 63f,
                        ground - 35f - armShift
                    )
                )
            }

            "Cable" -> {

                drawLine(
                    color = Accent,
                    start = Offset(
                        cx + 80f,
                        ground - 10f
                    ),
                    end = Offset(
                        cx + 60f,
                        ground - 35f - armShift
                    ),
                    strokeWidth = 3f
                )
            }
        }

        drawLine(
            color = Color(0xFF3A3A42),
            start = Offset(
                30f,
                ground + 10f
            ),
            end = Offset(
                size.width - 30f,
                ground + 10f
            ),
            strokeWidth = 2f
        )
    }
}

@Composable
fun PlansScreen(
    nav: NavHostController,
    state: AppState,
    exercises: List<Exercise>,
    save: () -> Unit
) {

    var showNewPlan by remember {
        mutableStateOf(false)
    }

    var planName by remember {
        mutableStateOf("")
    }

    var refresh by remember {
        mutableStateOf(0)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ArcTop(
            title = "Plans",
            subtitle = "Build any split you want"
        )

        Button(
            onClick = {
                showNewPlan = true
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                contentColor = Color.Black
            )
        ) {

            Icon(
                Icons.Default.Add,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(5.dp)
            )

            Text("CREATE PLAN")
        }

        key(refresh) {
            LazyColumn(
                contentPadding = PaddingValues(
                    16.dp,
                    12.dp,
                    16.dp,
                    120.dp
                ),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = state.plans,
                    key = {
                        it.id
                    }
                ) { plan ->

                    CardBox(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                nav.navigate(
                                    "plan/${plan.id}"
                                )
                            }
                    ) {

                        Text(
                            text = plan.name,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text =
                                "${plan.days.size} days • " +
                                    "${plan.days.sumOf { it.exercises.size }} exercises",
                            color = TextMuted
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        plan.days.take(7).forEach { day ->

                            Text(
                                text =
                                    "• " + day.name +
                                        if (day.restDay) {
                                            " · REST"
                                        } else {
                                            ""
                                        },
                                fontSize = 13.sp,
                                color =
                                    if (day.restDay) {
                                        TextMuted
                                    } else {
                                        TextMain
                                    }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showNewPlan) {

        AlertDialog(
            onDismissRequest = {
                showNewPlan = false
            },
            title = {
                Text("New plan")
            },
            text = {

                OutlinedTextField(
                    value = planName,
                    onValueChange = {
                        planName = it
                    },
                    label = {
                        Text("Plan name")
                    }
                )
            },
            confirmButton = {

                Button(
                    onClick = {

                        if (planName.isNotBlank()) {

                            val plan =
                                WorkoutPlan(
                                    name = planName
                                )

                            plan.days.add(
                                WorkoutDay(
                                    name = "Day 1"
                                )
                            )

                            state.plans.add(
                                plan
                            )

                            state.selectedPlanId =
                                plan.id

                            state.selectedDayId =
                                plan.days.first().id

                            save()

                            planName = ""
                            showNewPlan = false
                            refresh++
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        showNewPlan = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PlanEditorScreen(
    nav: NavHostController,
    state: AppState,
    plan: WorkoutPlan,
    exercises: List<Exercise>,
    save: () -> Unit
) {

    var refresh by remember {
        mutableStateOf(0)
    }

    var showDay by remember {
        mutableStateOf(false)
    }

    var dayName by remember {
        mutableStateOf("")
    }

    var selectedDay by remember {
        mutableStateOf(
            plan.days.firstOrNull()
        )
    }

    var showExercise by remember {
        mutableStateOf(false)
    }

    var showConfig by remember {
        mutableStateOf<PlannedExercise?>(null)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ArcTop(
            title = plan.name,
            subtitle = "Custom split",
            back = {
                nav.navigateUp()
            }
        )

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            Button(
                onClick = {
                    showDay = true
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent,
                    contentColor = Color.Black
                )
            ) {
                Text("ADD DAY")
            }

            OutlinedButton(
                onClick = {

                    plan.days.add(
                        WorkoutDay(
                            name = "Rest",
                            restDay = true
                        )
                    )

                    refresh++
                    save()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("REST DAY")
            }
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        key(refresh) {
            LazyColumn(
                contentPadding = PaddingValues(
                    16.dp,
                    4.dp,
                    16.dp,
                    120.dp
                ),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = plan.days,
                    key = {
                        it.id
                    }
                ) { day ->

                    CardBox(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {

                                Text(
                                    text = day.name,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text =
                                        if (day.restDay) {
                                            "Rest day"
                                        } else {
                                            "${day.exercises.size} exercises"
                                        },
                                    color = TextMuted
                                )
                            }

                            if (!day.restDay) {

                                IconButton(
                                    onClick = {
                                        selectedDay = day
                                        showExercise = true
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Add,
                                        contentDescription = "Add exercise"
                                    )
                                }
                            }

                            IconButton(
                                onClick = {

                                    val index =
                                        plan.days.indexOf(day)

                                    if (index > 0) {

                                        java.util.Collections.swap(
                                            plan.days,
                                            index,
                                            index - 1
                                        )

                                        refresh++
                                        save()
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Move up"
                                )
                            }

                            IconButton(
                                onClick = {

                                    val index =
                                        plan.days.indexOf(day)

                                    if (
                                        index >= 0 &&
                                        index < plan.days.lastIndex
                                    ) {

                                        java.util.Collections.swap(
                                            plan.days,
                                            index,
                                            index + 1
                                        )

                                        refresh++
                                        save()
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Move down"
                                )
                            }

                            IconButton(
                                onClick = {

                                    if (plan.days.size > 1) {

                                        plan.days.remove(day)

                                        if (
                                            selectedDay?.id ==
                                            day.id
                                        ) {
                                            selectedDay =
                                                plan.days.firstOrNull()
                                        }

                                        refresh++
                                        save()
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }

                        day.exercises.forEachIndexed {
                            index,
                            plannedExercise ->

                            val exercise =
                                exercises.firstOrNull {
                                    it.id ==
                                        plannedExercise.exerciseId
                                }

                            if (exercise != null) {

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedDay =
                                                day

                                            showConfig =
                                                plannedExercise
                                        }
                                        .padding(
                                            top = 8.dp
                                        ),
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {

                                    Text(
                                        text =
                                            "${index + 1}. " +
                                                exercise.name +
                                                " • " +
                                                plannedExercise.sets +
                                                "×" +
                                                plannedExercise.repsMin +
                                                "-" +
                                                plannedExercise.repsMax +
                                                " • " +
                                                plannedExercise.restSeconds +
                                                "s",
                                        fontSize = 13.sp,
                                        color = TextMuted,
                                        modifier =
                                            Modifier.weight(1f)
                                    )

                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDay) {

        AlertDialog(
            onDismissRequest = {
                showDay = false
            },
            title = {
                Text("New day")
            },
            text = {

                OutlinedTextField(
                    value = dayName,
                    onValueChange = {
                        dayName = it
                    },
                    label = {
                        Text("Day name")
                    }
                )
            },
            confirmButton = {

                Button(
                    onClick = {

                        if (dayName.isNotBlank()) {

                            val newDay =
                                WorkoutDay(
                                    name = dayName
                                )

                            plan.days.add(
                                newDay
                            )

                            selectedDay =
                                newDay

                            dayName = ""
                            showDay = false

                            refresh++
                            save()
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        showDay = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (
        showExercise &&
        selectedDay != null
    ) {

        ExercisePickerDialog(
            exercises = exercises,
            onAdd = { exercise ->

                selectedDay!!
                    .exercises
                    .add(
                        PlannedExercise(
                            exercise.id
                        )
                    )

                showExercise = false

                refresh++
                save()
            },
            onDismiss = {
                showExercise = false
            }
        )
    }

    val config =
        showConfig

    if (config != null) {

        PlanExerciseConfigDialog(
            pe = config,
            onSave = {

                refresh++
                save()
                showConfig = null
            },
            onDelete = {

                selectedDay
                    ?.exercises
                    ?.remove(config)

                refresh++
                save()

                showConfig = null
            }
        )
    }
}

@Composable
fun PlanExerciseConfigDialog(
    pe: PlannedExercise,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {

    var sets by remember {
        mutableStateOf(
            pe.sets.toString()
        )
    }

    var minReps by remember {
        mutableStateOf(
            pe.repsMin.toString()
        )
    }

    var maxReps by remember {
        mutableStateOf(
            pe.repsMax.toString()
        )
    }

    var weight by remember {
        mutableStateOf(
            pe.weight.toString()
        )
    }

    var rest by remember {
        mutableStateOf(
            pe.restSeconds.toString()
        )
    }

    var rir by remember {
        mutableStateOf(
            pe.rir.toString()
        )
    }

    AlertDialog(
        onDismissRequest = onSave,
        title = {
            Text("Exercise settings")
        },
        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(6.dp)
            ) {

                OutlinedTextField(
                    value = sets,
                    onValueChange = {
                        sets = it
                    },
                    label = {
                        Text("Sets")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        )
                )

                OutlinedTextField(
                    value = minReps,
                    onValueChange = {
                        minReps = it
                    },
                    label = {
                        Text("Min reps")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        )
                )

                OutlinedTextField(
                    value = maxReps,
                    onValueChange = {
                        maxReps = it
                    },
                    label = {
                        Text("Max reps")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        )
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = {
                        weight = it
                    },
                    label = {
                        Text("Target weight")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        )
                )

                OutlinedTextField(
                    value = rest,
                    onValueChange = {
                        rest = it
                    },
                    label = {
                        Text("Rest seconds")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        )
                )

                OutlinedTextField(
                    value = rir,
                    onValueChange = {
                        rir = it
                    },
                    label = {
                        Text("RIR")
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Number
                        )
                )
            }
        },
        confirmButton = {

            Button(
                onClick = {

                    pe.sets =
                        sets.toIntOrNull()
                            ?: 3

                    pe.repsMin =
                        minReps.toIntOrNull()
                            ?: 8

                    pe.repsMax =
                        maxReps.toIntOrNull()
                            ?: 12

                    pe.weight =
                        weight.toDoubleOrNull()
                            ?: 0.0

                    pe.restSeconds =
                        rest.toIntOrNull()
                            ?: 90

                    pe.rir =
                        rir.toIntOrNull()
                            ?: 2

                    onSave()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {

            TextButton(
                onClick = onDelete
            ) {
                Text("Remove")
            }
        }
    )
}

@Composable
fun ExercisePickerDialog(
    exercises: List<Exercise>,
    onAdd: (Exercise) -> Unit,
    onDismiss: () -> Unit
) {

    var query by remember {
        mutableStateOf("")
    }

    val filtered = exercises.filter {
        query.isBlank() ||
            it.name.contains(query, true) ||
            it.primaryMuscle.contains(query, true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add exercise")
        },
        text = {

            Column {

                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                    },
                    label = {
                        Text("Search")
                    },
                    singleLine = true
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                LazyColumn(
                    modifier = Modifier.heightIn(
                        max = 420.dp
                    )
                ) {

                    items(
                        items = filtered.take(100),
                        key = {
                            it.id
                        }
                    ) { exercise ->

                        Text(
                            text = exercise.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onAdd(exercise)
                                }
                                .padding(10.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {

            TextButton(
                onClick = onDismiss
            ) {
                Text("Done")
            }
        }
    )
}

@Composable
fun WorkoutScreen(
    nav: NavHostController,
    state: AppState,
    exercises: List<Exercise>,
    save: () -> Unit
) {

    val plan =
        state.plans.firstOrNull {
            it.id == state.selectedPlanId
        } ?: state.plans.firstOrNull()

    val day =
        plan?.days?.firstOrNull {
            it.id == state.selectedDayId
        } ?: plan?.days?.firstOrNull()

    var active by remember {
        mutableStateOf(false)
    }

    var currentIndex by remember {
        mutableStateOf(0)
    }

    var session by remember {
        mutableStateOf<WorkoutSession?>(null)
    }

    var weightText by remember {
        mutableStateOf("")
    }

    var repsText by remember {
        mutableStateOf("")
    }

    var restTimer by remember {
        mutableStateOf(0)
    }

    var timerRunning by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(
        timerRunning,
        restTimer
    ) {

        while (
            timerRunning &&
            restTimer > 0
        ) {
            delay(1000)
            restTimer--
        }

        if (restTimer <= 0) {
            timerRunning = false
        }
    }

    if (plan == null || day == null) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            ArcTop(
                title = "Workout"
            )

            Text(
                text = "Create a plan first.",
                modifier = Modifier.padding(20.dp),
                color = TextMuted
            )
        }

    } else if (!active) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            ArcTop(
                title = "Workout",
                subtitle = day.name
            )

            CardBox(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {

                Text(
                    text =
                        "${day.exercises.size} exercises",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                day.exercises.forEach { plannedExercise ->

                    val name =
                        exercises.firstOrNull {
                            it.id ==
                                plannedExercise.exerciseId
                        }?.name
                            ?: "Exercise"

                    Text(
                        text = "• $name",
                        color = TextMuted,
                        modifier = Modifier.padding(
                            vertical = 3.dp
                        )
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = {

                        if (day.exercises.isNotEmpty()) {

                            session =
                                WorkoutSession(
                                    planId = plan.id,
                                    dayId = day.id,
                                    startedAt =
                                        System.currentTimeMillis()
                                )

                            currentIndex = 0
                            active = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("START WORKOUT")
                }
            }
        }

    } else {

        val plannedExercise =
            day.exercises.getOrNull(
                currentIndex
            )

        val exercise =
            plannedExercise?.let { target ->
                exercises.firstOrNull {
                    it.id ==
                        target.exerciseId
                }
            }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            ArcTop(
                title = day.name,
                subtitle =
                    "Exercise " +
                        (currentIndex + 1) +
                        "/" +
                        day.exercises.size,
                back = {
                    timerRunning = false
                    restTimer = 0
                    active = false
                }
            )

            if (
                exercise == null ||
                plannedExercise == null
            ) {

                Text(
                    text = "Exercise not found.",
                    modifier = Modifier.padding(20.dp)
                )

            } else {

                LazyColumn(
                    contentPadding = PaddingValues(
                        16.dp,
                        0.dp,
                        16.dp,
                        120.dp
                    )
                ) {

                    item {

                        CardBox(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = exercise.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(8.dp)
                            )

                            ExerciseAnimation(exercise)

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Text(
                                text =
                                    "Target " +
                                        plannedExercise.sets +
                                        " sets • " +
                                        plannedExercise.repsMin +
                                        "-" +
                                        plannedExercise.repsMax +
                                        " reps • " +
                                        plannedExercise.restSeconds +
                                        "s rest",
                                color = TextMuted
                            )

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {

                                OutlinedTextField(
                                    value = weightText,
                                    onValueChange = {
                                        weightText = it
                                    },
                                    modifier =
                                        Modifier.weight(1f),
                                    label = {
                                        Text("Weight")
                                    },
                                    keyboardOptions =
                                        KeyboardOptions(
                                            keyboardType =
                                                KeyboardType.Decimal
                                        )
                                )

                                OutlinedTextField(
                                    value = repsText,
                                    onValueChange = {
                                        repsText = it
                                    },
                                    modifier =
                                        Modifier.weight(1f),
                                    label = {
                                        Text("Reps")
                                    },
                                    keyboardOptions =
                                        KeyboardOptions(
                                            keyboardType =
                                                KeyboardType.Number
                                        )
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(10.dp)
                            )

                            Button(
                                onClick = {

                                    val weight =
                                        weightText
                                            .toDoubleOrNull()
                                            ?: plannedExercise.weight

                                    val reps =
                                        repsText
                                            .toIntOrNull()
                                            ?: plannedExercise.repsMin

                                    session
                                        ?.sets
                                        ?.add(
                                            LoggedSet(
                                                exerciseId =
                                                    exercise.id,
                                                weight =
                                                    weight,
                                                reps =
                                                    reps,
                                                rir =
                                                    plannedExercise.rir
                                            )
                                        )

                                    restTimer =
                                        plannedExercise.restSeconds

                                    timerRunning =
                                        restTimer > 0

                                    weightText = ""
                                    repsText = ""

                                    save()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Accent,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "SAVE SET + START REST"
                                )
                            }

                            if (restTimer > 0) {

                                Spacer(
                                    modifier = Modifier.height(10.dp)
                                )

                                Text(
                                    text =
                                        "REST " +
                                            (restTimer / 60) +
                                            ":" +
                                            (
                                                restTimer % 60
                                            )
                                                .toString()
                                                .padStart(
                                                    2,
                                                    '0'
                                                ),
                                    fontSize = 34.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    horizontalArrangement =
                                        Arrangement.spacedBy(
                                            8.dp
                                        )
                                ) {

                                    OutlinedButton(
                                        onClick = {
                                            restTimer += 15
                                        }
                                    ) {
                                        Text("+15")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            restTimer += 30
                                        }
                                    ) {
                                        Text("+30")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            restTimer = 0
                                            timerRunning = false
                                        }
                                    ) {
                                        Text("SKIP")
                                    }
                                }
                            }

                            Spacer(
                                modifier = Modifier.height(14.dp)
                            )

                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                OutlinedButton(
                                    onClick = {

                                        if (currentIndex > 0) {
                                            currentIndex--
                                        }
                                    },
                                    modifier =
                                        Modifier.weight(1f)
                                ) {
                                    Text("PREVIOUS")
                                }

                                OutlinedButton(
                                    onClick = {

                                        if (
                                            currentIndex <
                                            day.exercises.lastIndex
                                        ) {

                                            currentIndex++

                                        } else {

                                            val finished =
                                                session

                                            if (
                                                finished != null
                                            ) {
                                                state.sessions.add(
                                                    finished.copy(
                                                        endedAt =
                                                            System.currentTimeMillis()
                                                    )
                                                )
                                            }

                                            restTimer = 0
                                            timerRunning = false
                                            save()
                                            active = false
                                        }
                                    },
                                    modifier =
                                        Modifier.weight(1f)
                                ) {
                                    Text(
                                        if (
                                            currentIndex <
                                            day.exercises.lastIndex
                                        ) {
                                            "NEXT"
                                        } else {
                                            "FINISH"
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WarmupScreen(
    nav: NavHostController,
    day: WorkoutDay,
    exercises: List<Exercise>
) {

    val items =
        remember(day.id) {
            warmupFor(
                day,
                exercises
            )
        }

    var currentIndex by remember {
        mutableStateOf(0)
    }

    var seconds by remember {
        mutableStateOf(
            items.firstOrNull()?.seconds ?: 0
        )
    }

    var running by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(running) {

        while (
            running &&
            seconds > 0
        ) {
            delay(1000)
            seconds--
        }

        if (seconds <= 0) {
            running = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = 120.dp
        )
    ) {

        item {

            ArcTop(
                title = "Warm-up",
                subtitle = day.name,
                back = {
                    nav.navigateUp()
                }
            )
        }

        items.forEachIndexed {
            index,
            item ->

            item {

                CardBox(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        text =
                            "${index + 1}. ${item.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text(
                        text = item.instructions,
                        color = TextMuted,
                        lineHeight = 20.sp
                    )

                    Text(
                        text =
                            if (item.reps > 0) {
                                "${item.reps} reps"
                            } else {
                                "${item.seconds}s"
                            },
                        color = Accent,
                        fontWeight = FontWeight.Bold
                    )

                    if (index == currentIndex) {

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Button(
                                onClick = {
                                    running = !running
                                },
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = Accent,
                                        contentColor = Color.Black
                                    )
                            ) {
                                Text(
                                    if (running) {
                                        "PAUSE"
                                    } else {
                                        "START"
                                    }
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    seconds = item.seconds
                                    running = false
                                }
                            ) {
                                Text("RESET")
                            }

                            OutlinedButton(
                                onClick = {

                                    if (
                                        currentIndex <
                                        items.lastIndex
                                    ) {
                                        currentIndex++
                                        seconds =
                                            items[
                                                currentIndex
                                            ].seconds

                                        running = false
                                    }
                                }
                            ) {
                                Text("NEXT")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StretchScreen(
    nav: NavHostController,
    day: WorkoutDay,
    exercises: List<Exercise>
) {

    val items =
        remember(day.id) {
            stretchesFor(
                day,
                exercises
            )
        }

    var currentIndex by remember {
        mutableStateOf(0)
    }

    var seconds by remember {
        mutableStateOf(
            items.firstOrNull()?.seconds ?: 30
        )
    }

    var running by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(running) {

        while (
            running &&
            seconds > 0
        ) {
            delay(1000)
            seconds--
        }

        if (seconds <= 0) {
            running = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            bottom = 120.dp
        )
    ) {

        item {

            ArcTop(
                title = "Stretch & Cool-down",
                subtitle = day.name,
                back = {
                    nav.navigateUp()
                }
            )
        }

        items.forEachIndexed {
            index,
            item ->

            item {

                CardBox(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        text =
                            "${index + 1}. ${item.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text(
                        text = item.target,
                        color = Accent,
                        fontSize = 12.sp
                    )

                    Text(
                        text = item.instructions,
                        color = TextMuted,
                        lineHeight = 20.sp
                    )

                    if (index == currentIndex) {

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Text(
                            text = "${seconds}s",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Button(
                                onClick = {
                                    running = !running
                                },
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = Accent,
                                        contentColor = Color.Black
                                    )
                            ) {
                                Text(
                                    if (running) {
                                        "PAUSE"
                                    } else {
                                        "START"
                                    }
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    seconds = item.seconds
                                    running = false
                                }
                            ) {
                                Text("RESET")
                            }

                            OutlinedButton(
                                onClick = {

                                    if (
                                        currentIndex <
                                        items.lastIndex
                                    ) {

                                        currentIndex++

                                        seconds =
                                            items[
                                                currentIndex
                                            ].seconds

                                        running = false
                                    }
                                }
                            ) {
                                Text("NEXT")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalsScreen(
    state: AppState,
    save: () -> Unit
) {

    var show by remember {
        mutableStateOf(false)
    }

    var name by remember {
        mutableStateOf("")
    }

    var target by remember {
        mutableStateOf("")
    }

    var unit by remember {
        mutableStateOf("kg")
    }

    var refresh by remember {
        mutableStateOf(0)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ArcTop(
            title = "Goals",
            subtitle = "Track performance and consistency"
        )

        Button(
            onClick = {
                show = true
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                contentColor = Color.Black
            )
        ) {
            Text("CREATE GOAL")
        }

        key(refresh) {
            LazyColumn(
                contentPadding = PaddingValues(
                    16.dp,
                    0.dp,
                    16.dp,
                    120.dp
                ),
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                items(
                    items = state.goals,
                    key = {
                        it.id
                    }
                ) { goal ->

                    val progress =
                        if (goal.target > 0) {
                            (
                                goal.current /
                                    goal.target
                            ).coerceIn(
                                0.0,
                                1.0
                            )
                        } else {
                            0.0
                        }

                    CardBox(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Text(
                            text = goal.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Text(
                            text =
                                "${goal.current} / " +
                                    "${goal.target} " +
                                    goal.unit,
                            color = TextMuted
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        LinearProgressIndicator(
                            progress = {
                                progress.toFloat()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            color = Accent,
                            trackColor = Surface2
                        )
                    }
                }
            }
        }
    }

    if (show) {

        AlertDialog(
            onDismissRequest = {
                show = false
            },
            title = {
                Text("New goal")
            },
            text = {

                Column(
                    verticalArrangement =
                        Arrangement.spacedBy(6.dp)
                ) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                        },
                        label = {
                            Text("Goal")
                        }
                    )

                    OutlinedTextField(
                        value = target,
                        onValueChange = {
                            target = it
                        },
                        label = {
                            Text("Target")
                        },
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Decimal
                            )
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = {
                            unit = it
                        },
                        label = {
                            Text("Unit")
                        }
                    )
                }
            },
            confirmButton = {

                Button(
                    onClick = {

                        if (name.isNotBlank()) {

                            state.goals.add(
                                Goal(
                                    name = name,
                                    type = "custom",
                                    target =
                                        target
                                            .toDoubleOrNull()
                                            ?: 0.0,
                                    unit = unit
                                )
                            )

                            save()

                            name = ""
                            target = ""
                            show = false

                            refresh++
                        }
                    }
                ) {
                    Text("Create")
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        show = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HistoryScreen(
    state: AppState,
    exercises: List<Exercise>
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ArcTop(
            title = "History",
            subtitle = "Every completed workout"
        )

        LazyColumn(
            contentPadding = PaddingValues(
                16.dp,
                0.dp,
                16.dp,
                120.dp
            ),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            items(
                items = state.sessions.asReversed(),
                key = {
                    it.id
                }
            ) { session ->

                val plan =
                    state.plans.firstOrNull {
                        it.id == session.planId
                    }

                val day =
                    plan?.days?.firstOrNull {
                        it.id == session.dayId
                    }

                val durationMinutes =
                    (
                        (
                            session.endedAt
                                ?: System.currentTimeMillis()
                        ) -
                            session.startedAt
                        ) /
                        1000 /
                        60

                CardBox(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text =
                            day?.name
                                ?: "Workout",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Text(
                        text =
                            "${session.sets.size} sets • " +
                                "$durationMinutes min",
                        color = TextMuted
                    )

                    session.sets.take(5).forEach { loggedSet ->

                        val exerciseName =
                            exercises.firstOrNull {
                                it.id ==
                                    loggedSet.exerciseId
                            }?.name
                                ?: "Exercise"

                        Text(
                            text =
                                "$exerciseName: " +
                                    "${loggedSet.weight} × " +
                                    loggedSet.reps,
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChecklistScreen(
    state: AppState,
    save: () -> Unit
) {

    var list by remember {
        mutableStateOf(
            state.checklists.firstOrNull()
        )
    }

    var show by remember {
        mutableStateOf(false)
    }

    var itemText by remember {
        mutableStateOf("")
    }

    var refresh by remember {
        mutableStateOf(0)
    }

    if (list == null) {

        val created =
            Checklist(
                name = "Daily"
            )

        state.checklists.add(
            created
        )

        list = created
        save()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ArcTop(
            title = "Checklist",
            subtitle = "Daily and gym tasks"
        )

        Button(
            onClick = {
                show = true
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                contentColor = Color.Black
            )
        ) {
            Text("ADD ITEM")
        }

        val currentList =
            list

        key(refresh) {

            if (currentList != null) {

                LazyColumn(
                    contentPadding = PaddingValues(
                        16.dp,
                        0.dp,
                        16.dp,
                        120.dp
                    )
                ) {

                    items(
                        items = currentList.items,
                        key = {
                            it.id
                        }
                    ) { checklistItem ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 6.dp
                                ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Checkbox(
                                checked =
                                    checklistItem.done,
                                onCheckedChange = {
                                    checklistItem.done = it
                                    save()
                                    refresh++
                                }
                            )

                            Text(
                                text =
                                    checklistItem.title,
                                modifier =
                                    Modifier.weight(1f),
                                textDecoration =
                                    if (
                                        checklistItem.done
                                    ) {
                                        TextDecoration.LineThrough
                                    } else {
                                        null
                                    }
                            )

                            IconButton(
                                onClick = {
                                    currentList.items.remove(
                                        checklistItem
                                    )

                                    save()
                                    refresh++
                                }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (show) {

        AlertDialog(
            onDismissRequest = {
                show = false
            },
            title = {
                Text("Add item")
            },
            text = {

                OutlinedTextField(
                    value = itemText,
                    onValueChange = {
                        itemText = it
                    },
                    label = {
                        Text("Item")
                    }
                )
            },
            confirmButton = {

                Button(
                    onClick = {

                        if (itemText.isNotBlank()) {

                            list
                                ?.items
                                ?.add(
                                    ChecklistItem(
                                        title = itemText
                                    )
                                )

                            itemText = ""
                            show = false

                            save()
                            refresh++
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        show = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ReportsScreen(
    state: AppState,
    exercises: List<Exercise>
) {

    val context = LocalContext.current

    var range by remember {
        mutableStateOf("WEEK")
    }

    val report =
        remember(
            state.sessions.size,
            state.goals.size,
            range
        ) {
            buildReport(
                state,
                exercises,
                range
            )
        }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ArcTop(
            title = "Reports",
            subtitle = "Daily / weekly / monthly summaries"
        )

        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp
            ),
            horizontalArrangement =
                Arrangement.spacedBy(8.dp)
        ) {

            listOf(
                "DAY",
                "WEEK",
                "MONTH"
            ).forEach { item ->

                FilterChip(
                    selected = range == item,
                    onClick = {
                        range = item
                    },
                    label = {
                        Text(item)
                    }
                )
            }
        }

        CardBox(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            Text(
                text = report,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }

        Button(
            onClick = {

                val intent =
                    Intent(
                        Intent.ACTION_SEND
                    ).apply {
                        type =
                            "text/plain"

                        putExtra(
                            Intent.EXTRA_TEXT,
                            report
                        )
                    }

                context.startActivity(
                    Intent.createChooser(
                        intent,
                        "Share ARC report"
                    )
                )
            },
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Accent,
                contentColor = Color.Black
            )
        ) {

            Icon(
                Icons.Default.Share,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text("SHARE REPORT")
        }
    }
}

fun buildReport(
    state: AppState,
    exercises: List<Exercise>,
    range: String
): String {

    val now =
        System.currentTimeMillis()

    val window =
        when (range) {
            "DAY" ->
                24L * 60L * 60L * 1000L

            "MONTH" ->
                30L * 24L * 60L * 60L * 1000L

            else ->
                7L * 24L * 60L * 60L * 1000L
        }

    val sessions =
        state.sessions.filter {
            now - it.startedAt <= window
        }

    return buildString {

        append(
            "ARC REPORT — $range\n\n"
        )

        append(
            "Sessions: ${sessions.size}\n"
        )

        append(
            "Active goals: " +
                state.goals.count {
                    it.active
                } +
                "\n"
        )

        append(
            "Checklist items: " +
                state.checklists.sumOf {
                    it.items.size
                } +
                "\n\n"
        )

        append(
            "Recent workouts:\n"
        )

        if (sessions.isNotEmpty()) {

            sessions
                .takeLast(10)
                .asReversed()
                .forEach { session ->

                    val dayName =
                        state.plans
                            .firstOrNull {
                                it.id ==
                                    session.planId
                            }
                            ?.days
                            ?.firstOrNull {
                                it.id ==
                                    session.dayId
                            }
                            ?.name
                            ?: "Workout"

                    append(
                        "• ${session.sets.size} sets — " +
                            dayName +
                            "\n"
                    )
                }

        } else {

            append(
                "No completed workout in this period.\n"
            )
        }
    }
}

@Composable
fun SettingsScreen(
    state: androidx.compose.runtime.MutableState<AppState>,
    exercises: MutableList<Exercise>,
    save: () -> Unit,
    snackbar: SnackbarHostState
) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var syncing by remember {
        mutableStateOf(false)
    }

    val exportLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.CreateDocument(
                    "application/octet-stream"
                )
        ) { uri: Uri? ->

            if (uri != null) {

                try {

                    context
                        .contentResolver
                        .openOutputStream(uri)
                        ?.use { output ->

                            ObjectOutputStream(
                                output
                            ).use { objectOutput ->

                                objectOutput.writeObject(
                                    state.value
                                )
                            }
                        }

                    scope.launch {
                        snackbar.showSnackbar(
                            "Backup exported"
                        )
                    }

                } catch (_: Exception) {

                    scope.launch {
                        snackbar.showSnackbar(
                            "Backup could not be written"
                        )
                    }
                }
            }
        }

    val importLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->

            if (uri != null) {

                try {

                    context
                        .contentResolver
                        .openInputStream(uri)
                        ?.use { input ->

                            val imported =
                                ObjectInputStream(
                                    input
                                ).use {
                                    it.readObject()
                                        as AppState
                                }

                            state.value =
                                imported
                        }

                    save()

                    scope.launch {
                        snackbar.showSnackbar(
                            "Backup restored"
                        )
                    }

                } catch (_: Exception) {

                    scope.launch {
                        snackbar.showSnackbar(
                            "Backup could not be read"
                        )
                    }
                }
            }
        }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ArcTop(
            title = "Settings",
            subtitle = "ARC data and preferences"
        )

        LazyColumn(
            contentPadding = PaddingValues(
                16.dp,
                0.dp,
                16.dp,
                120.dp
            ),
            verticalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {

            item {

                CardBox(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "EXERCISE LIBRARY",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text =
                            "Bundled: ${exercises.size}",
                        color = TextMuted
                    )

                    Spacer(
                        modifier = Modifier.height(8.dp)
                    )

                    Button(
                        enabled = !syncing,
                        onClick = {

                            syncing = true

                            scope.launch {

                                try {

                                    val count =
                                        syncExercises(
                                            exercises
                                        )

                                    snackbar.showSnackbar(
                                        "Synced $count open exercises"
                                    )

                                } catch (_: Exception) {

                                    snackbar.showSnackbar(
                                        "Exercise sync failed"
                                    )

                                } finally {

                                    syncing = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Accent,
                            contentColor = Color.Black
                        )
                    ) {

                        Text(
                            if (syncing) {
                                "SYNCING…"
                            } else {
                                "SYNC OPEN EXERCISES"
                            }
                        )
                    }

                    Text(
                        text =
                            "The downloaded exercise data is cached locally after syncing.",
                        fontSize = 12.sp,
                        color = TextMuted
                    )
                }
            }

            item {

                CardBox(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "DATA",
                        fontWeight = FontWeight.Bold
                    )

                    Button(
                        onClick = {
                            exportLauncher.launch(
                                "arc-backup.bin"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("EXPORT BACKUP")
                    }

                    Spacer(
                        modifier = Modifier.height(6.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            importLauncher.launch(
                                arrayOf(
                                    "application/octet-stream",
                                    "*/*"
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("IMPORT BACKUP")
                    }
                }
            }

            item {

                CardBox(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "DEFAULT REST",
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Text(
                            text =
                                "${state.value.defaultRest} seconds",
                            modifier =
                                Modifier.weight(1f)
                        )

                        OutlinedButton(
                            onClick = {

                                state.value.defaultRest =
                                    (
                                        state.value.defaultRest +
                                            15
                                        ).coerceAtMost(
                                            300
                                        )

                                save()
                            }
                        ) {
                            Text("+15")
                        }
                    }
                }
            }

            item {

                CardBox(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "ABOUT ARC",
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text =
                            "Native Android workout system. Offline-first, customizable and designed for learning as well as tracking.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

suspend fun syncExercises(
    exercises: MutableList<Exercise>
): Int = withContext(Dispatchers.IO) {

    val url =
        URL(
            "https://raw.githubusercontent.com/" +
                "kinetic-place/exercises-json/" +
                "main/en/exercises.json"
        )

    val connection =
        url.openConnection()
            as HttpURLConnection

    connection.connectTimeout = 20000
    connection.readTimeout = 30000
    connection.requestMethod = "GET"

    try {

        val raw =
            connection
                .inputStream
                .bufferedReader()
                .use {
                    it.readText()
                }

        val array =
            JSONArray(raw)

        var added = 0

        for (i in 0 until array.length()) {

            val item =
                array.getJSONObject(i)

            val id =
                item.optString("id")

            if (
                id.isBlank() ||
                exercises.any {
                    it.id == id
                }
            ) {
                continue
            }

            val muscles =
                item.optJSONArray(
                    "muscleGroups"
                ) ?: JSONArray()

            val primary =
                mutableListOf<String>()

            val secondary =
                mutableListOf<String>()

            for (
                j in 0 until muscles.length()
            ) {

                val muscle =
                    muscles.getJSONObject(j)

                val name =
                    muscle.optString("name")

                if (
                    muscle.optString("type") ==
                    "primary"
                ) {
                    primary.add(name)
                } else {
                    secondary.add(name)
                }
            }

            val equipmentArray =
                item.optJSONArray(
                    "equipment"
                ) ?: JSONArray()

            val equipment =
                if (
                    equipmentArray.length() > 0
                ) {
                    equipmentArray
                        .getJSONObject(0)
                        .optString("name")
                        .ifBlank {
                            "Other"
                        }
                } else {
                    "Other"
                }

            val instructions =
                mutableListOf<String>()

            val instructionArray =
                item.optJSONArray(
                    "instructions"
                )

            if (instructionArray != null) {

                for (
                    j in 0 until
                        instructionArray.length()
                ) {
                    instructions.add(
                        instructionArray.optString(
                            j
                        )
                    )
                }
            }

            val pattern =
                when (
                    item.optString(
                        "forceType"
                    )
                ) {

                    "push" ->
                        if (
                            item.optString(
                                "mechanics"
                            ) == "compound"
                        ) {
                            "horizontal_push"
                        } else {
                            "isolation"
                        }

                    "pull" ->
                        if (
                            item.optString(
                                "mechanics"
                            ) == "compound"
                        ) {
                            "horizontal_pull"
                        } else {
                            "isolation"
                        }

                    else ->
                        "isolation"
                }

            exercises.add(
                Exercise(
                    id = id,
                    name =
                        item.optString(
                            "name"
                        ),
                    primaryMuscle =
                        primary.firstOrNull()
                            ?: secondary.firstOrNull()
                            ?: "Full Body",
                    secondaryMuscles =
                        secondary,
                    equipment =
                        equipment,
                    pattern =
                        pattern,
                    difficulty =
                        item.optString(
                            "difficultyLevel",
                            "beginner"
                        ),
                    instructions =
                        instructions,
                    source =
                        "Kinetic.place MIT",
                    isCustom = false,
                    animationId =
                        pattern
                )
            )

            added++
        }

        added

    } finally {

        connection.disconnect()
    }
}
