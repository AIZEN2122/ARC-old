package com.arc.training

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.UUID
import kotlin.math.max

private val ArcBg = Color(0xFF08090B)
private val ArcPanel = Color(0xFF12151A)
private val ArcPanel2 = Color(0xFF181C22)
private val ArcText = Color(0xFFF2F4F7)
private val ArcMuted = Color(0xFF959CA8)
private val ArcAccent = Color(0xFFD7FF4B)
private val ArcDanger = Color(0xFFFF6666)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ArcAppV2() }
    }
}

@Composable
private fun ArcAppV2() {
    val context = LocalContext.current
    val store = remember { AppStateStore(context) }
    val state = remember { mutableStateOf(store.load()) }
    val exercises = remember {
        mutableStateListOf<Exercise>().also {
            it.addAll(SeedExercises.all)
            it.addAll(state.value.customExercises)
        }
    }
    var route by remember { mutableStateOf("home") }

    fun save() = store.save(state.value)

    fun refreshExercises() {
        exercises.clear()
        exercises.addAll(SeedExercises.all)
        exercises.addAll(state.value.customExercises)
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = ArcBg,
            surface = ArcPanel,
            primary = ArcAccent,
            onPrimary = Color.Black,
            onBackground = ArcText,
            onSurface = ArcText
        )
    ) {
        Surface(Modifier.fillMaxSize(), color = ArcBg) {
            Scaffold(
                containerColor = ArcBg,
                bottomBar = {
                    ArcBottomNav(route) { route = it }
                }
            ) { padding ->
                Box(Modifier.padding(padding)) {
                    when {
                        route == "home" -> HomeScreen(state.value, exercises, save) { route = it }
                        route == "exercises" -> ExerciseLibraryScreen(state.value, exercises, save, refreshExercises) { route = "exercise:" + it }
                        route == "plans" -> PlansScreen(state.value, save) { route = "plan:" + it }
                        route == "workout" -> WorkoutScreen(state.value, exercises, save) { route = it }
                        route == "goals" -> GoalsScreen(state.value, save) { route = "more" }
                        route == "checklist" -> ChecklistScreen(state.value, save) { route = "more" }
                        route == "history" -> HistoryScreen(state.value, exercises) { route = "more" }
                        route == "settings" -> SettingsScreen(state.value, save) { route = "more" }
                        route == "more" -> MoreScreen { route = it }
                        route.startsWith("exercise:") -> {
                            val id = route.removePrefix("exercise:")
                            val ex = exercises.firstOrNull { it.id == id }
                            if (ex != null) ExerciseDetailScreen(ex, state.value, save) { route = "exercises" }
                        }
                        route.startsWith("plan:") -> {
                            val id = route.removePrefix("plan:")
                            val plan = state.value.plans.firstOrNull { it.id == id }
                            if (plan != null) PlanEditorScreen(plan, state.value, exercises, save) { route = "plans" }
                        }
                        route.startsWith("warmup:") -> WarmupScreen(route.removePrefix("warmup:")) { route = "plans" }
                        route.startsWith("stretch:") -> StretchScreen(route.removePrefix("stretch:")) { route = "workout" }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArcBottomNav(current: String, navigate: (String) -> Unit) {
    val items = listOf(
        "home" to Icons.Default.Home,
        "exercises" to Icons.Default.FitnessCenter,
        "plans" to Icons.Default.CalendarMonth,
        "workout" to Icons.Default.PlayArrow,
        "more" to Icons.Default.MoreHoriz
    )
    NavigationBar(containerColor = Color(0xFF0D0F13)) {
        items.forEach { pair ->
            val selected = current == pair.first || (current.contains(":") && pair.first == "more")
            NavigationBarItem(
                selected = selected,
                onClick = { navigate(pair.first) },
                icon = { Icon(pair.second, null) },
                label = { Text(pair.first.replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

@Composable
private fun HomeScreen(
    state: AppState,
    exercises: List<Exercise>,
    save: () -> Unit,
    navigate: (String) -> Unit
) {
    val plan = state.plans.firstOrNull { it.id == state.selectedPlanId } ?: state.plans.firstOrNull()
    val day = plan?.days?.firstOrNull { it.id == state.selectedDayId } ?: plan?.days?.firstOrNull()

    LazyColumn(
        contentPadding = PaddingValues(bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Header("ARC", "Train. Track. Evolve.") }
        item {
            CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(R.drawable.arc_thumbnail),
                    contentDescription = "ARC",
                    modifier = Modifier.fillMaxWidth().height(210.dp).clip(RoundedCornerShape(18.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(13.dp))
                Text("TODAY'S WORKOUT", color = ArcMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(day?.name ?: "Build your first plan", fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text(
                    if (day == null) "Create a split and choose today's training day."
                    else day.exercises.size.toString() + " exercises • " + day.exercises.sumOf { it.sets }.toString() + " work sets",
                    color = ArcMuted
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            navigate(
                                if (day == null) "plans"
                                else "warmup:" + primaryMuscleForDay(day, exercises)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)
                    ) { Text("WARM-UP") }
                    OutlinedButton(onClick = { navigate("workout") }, modifier = Modifier.weight(1f)) { Text("START") }
                }
            }
        }
        item {
            Row(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                MetricCard("SESSIONS", state.sessions.size.toString(), Modifier.weight(1f))
                MetricCard("STREAK", state.currentStreak.toString(), Modifier.weight(1f))
                MetricCard("GOALS", state.goals.count { it.active }.toString(), Modifier.weight(1f))
            }
        }
        item {
            CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Text("QUICK ACTIONS", fontWeight = FontWeight.Bold)
                QuickAction("Customize split", Icons.Default.Tune) { navigate("plans") }
                QuickAction("Browse exercises", Icons.Default.FitnessCenter) { navigate("exercises") }
                QuickAction("Warm-up library", Icons.Default.PlayArrow) { navigate("warmup:Full Body") }
                QuickAction("Goals & progress", Icons.Default.Flag) { navigate("goals") }
                QuickAction("Checklist", Icons.Default.Check) { navigate("checklist") }
            }
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String, modifier: Modifier) {
    CardBox(modifier) {
        Text(label, color = ArcMuted, fontSize = 10.sp)
        Text(value, fontSize = 23.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun QuickAction(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = ArcAccent)
        Spacer(Modifier.width(10.dp))
        Text(title, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
        Icon(Icons.Default.ArrowUpward, null, tint = ArcMuted, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun ExerciseLibraryScreen(
    state: AppState,
    exercises: MutableList<Exercise>,
    save: () -> Unit,
    refreshExercises: () -> Unit,
    open: (String) -> Unit
) {
    var search by remember { mutableStateOf("") }
    var muscle by remember { mutableStateOf("All") }
    var equipment by remember { mutableStateOf("All") }
    var favoritesOnly by remember { mutableStateOf(false) }
    var showCreate by remember { mutableStateOf(false) }
    var tick by remember { mutableIntStateOf(0) }

    val muscles = listOf("All", "Chest", "Back", "Lats", "Shoulders", "Biceps", "Triceps", "Quadriceps", "Hamstrings", "Glutes", "Calves", "Abdominals", "Forearms")
    val equipmentOptions = listOf("All", "Barbell", "Dumbbell", "Cable", "Machine", "Bodyweight", "Smith Machine", "Other")

    val filtered = exercises.filter { ex ->
        val q = search.isBlank() || ex.name.contains(search, true) || ex.primaryMuscle.contains(search, true) || ex.equipment.contains(search, true)
        val m = muscle == "All" || ex.primaryMuscle.contains(muscle, true) || ex.secondaryMuscles.any { it.contains(muscle, true) }
        val e = equipment == "All" || ex.equipment.contains(equipment, true)
        val f = !favoritesOnly || state.favorites.contains(ex.id)
        q && m && e && f
    }

    LazyColumn(contentPadding = PaddingValues(bottom = 110.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { Header("EXERCISES", "Muscle → equipment → movement → demo") }
        item {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search exercises") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
        }
        item { ChipRow(muscles, muscle) { muscle = it } }
        item { ChipRow(equipmentOptions, equipment) { equipment = it } }
        item {
            Row(Modifier.padding(horizontal = 16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = favoritesOnly,
                    onClick = { favoritesOnly = !favoritesOnly; tick++ },
                    label = { Text("Favorites") }
                )
                Surface(color = ArcPanel2, shape = RoundedCornerShape(99.dp)) {
                    Text(filtered.size.toString() + " shown", Modifier.padding(horizontal = 11.dp, vertical = 7.dp), color = ArcMuted, fontSize = 12.sp)
                }
            }
        }
        item {
            Button(
                onClick = { showCreate = true },
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)
            ) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(5.dp)); Text("CREATE CUSTOM EXERCISE") }
        }
        items(filtered, key = { it.id }) { ex ->
            ExerciseCard(
                ex = ex,
                favorite = state.favorites.contains(ex.id),
                toggleFavorite = {
                    if (state.favorites.contains(ex.id)) state.favorites.remove(ex.id) else state.favorites.add(ex.id)
                    save()
                    tick++
                },
                open = {
                    state.recentExerciseIds.remove(ex.id)
                    state.recentExerciseIds.add(0, ex.id)
                    if (state.recentExerciseIds.size > 20) state.recentExerciseIds.removeLast()
                    save()
                    open(ex.id)
                }
            )
        }
    }

    if (showCreate) {
        CustomExerciseDialog(
            onDismiss = { showCreate = false },
            onCreate = { name, primary, eq ->
                state.customExercises.add(
                    Exercise(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        primaryMuscle = primary,
                        equipment = eq,
                        isCustom = true
                    )
                )
                save()
                refreshExercises()
                showCreate = false
            }
        )
    }
}

@Composable
private fun ExerciseCard(
    ex: Exercise,
    favorite: Boolean,
    toggleFavorite: () -> Unit,
    open: () -> Unit
) {
    CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth().clickable(onClick = open)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ExerciseThumb(ex, Modifier.size(78.dp))
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(ex.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(ex.primaryMuscle, color = ArcAccent, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text(ex.equipment + " • " + ex.difficulty.capitalizeSafe(), color = ArcMuted, fontSize = 12.sp)
                Text(patternLabel(ex.pattern), color = ArcMuted, fontSize = 11.sp)
            }
            IconButton(onClick = toggleFavorite) {
                Text(if (favorite) "★" else "☆", color = if (favorite) ArcAccent else ArcMuted, fontSize = 23.sp)
            }
        }
    }
}

@Composable
private fun ExerciseThumb(ex: Exercise, modifier: Modifier) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(exerciseGifFallbackUrl(ex)).build(),
        contentDescription = ex.name,
        modifier = modifier.clip(RoundedCornerShape(14.dp)).background(ArcPanel2),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun ExerciseDetailScreen(ex: Exercise, state: AppState, save: () -> Unit, back: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(bottom = 110.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item { Header(ex.name.uppercase(), ex.primaryMuscle + " • " + ex.equipment, back) }
        item { CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            ExerciseHero(ex)
            Spacer(Modifier.height(10.dp))
            Text(ex.primaryMuscle, color = ArcAccent, fontWeight = FontWeight.Bold)
            if (ex.secondaryMuscles.isNotEmpty()) Text("Secondary: " + ex.secondaryMuscles.joinToString(), color = ArcMuted, fontSize = 12.sp)
            Row(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Tag(ex.equipment)
                Tag(ex.difficulty.capitalizeSafe())
                Tag(patternLabel(ex.pattern))
            }
        } }
        val guide = detailedGuide(ex)
        item { TutorialCard("HOW TO PERFORM", guide.setup) }
        item { TutorialCard("START POSITION", guide.start) }
        item { TutorialCard("STEP-BY-STEP", guide.steps) }
        item { TutorialCard("BREATHING", guide.breathing) }
        item { TutorialCard("TEMPO & CONTROL", guide.tempo) }
        item { TutorialCard("RANGE OF MOTION", guide.rom) }
        item { TutorialCard("COMMON MISTAKES", guide.mistakes) }
        item { TutorialCard("BEGINNER NOTES", guide.beginner) }
        item {
            CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Button(
                    onClick = {
                        val plan = state.plans.firstOrNull() ?: WorkoutPlan(name = "My First Plan").also {
                            it.days.add(WorkoutDay(name = "Day 1"))
                            state.plans.add(it)
                            state.selectedPlanId = it.id
                            state.selectedDayId = it.days.first().id
                        }
                        val day = plan.days.firstOrNull { it.id == state.selectedDayId } ?: plan.days.first()
                        if (day.exercises.none { it.exerciseId == ex.id }) day.exercises.add(PlannedExercise(ex.id))
                        save()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)
                ) { Text("ADD TO CURRENT PLAN") }
            }
        }
    }
}

@Composable
private fun ExerciseHero(ex: Exercise) {
    var gifUrl by remember(ex.name) { mutableStateOf(exerciseGifFallbackUrl(ex)) }
    LaunchedEffect(ex.name) {
        val resolved = resolveExerciseGifUrl(ex.name)
        if (resolved.isNotBlank()) gifUrl = resolved
    }
    Box(
        Modifier.fillMaxWidth().height(285.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF0C0E12)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(gifUrl).build(),
            contentDescription = ex.name,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Fit
        )
        Surface(Modifier.align(Alignment.TopStart).padding(10.dp), color = ArcAccent, shape = RoundedCornerShape(8.dp)) {
            Text("REAL HUMAN DEMO", Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Black)
        }
    }
}

private suspend fun resolveExerciseGifUrl(name: String): String = withContext(Dispatchers.IO) {
    try {
        val query = URLEncoder.encode(name, "UTF-8")
        val url = URL("https://api.anatome.dev/searchExercises?q=" + query + "&limit=1")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            connectTimeout = 12000
            readTimeout = 15000
            requestMethod = "GET"
        }
        try {
            val raw = connection.inputStream.bufferedReader().use { it.readText() }
            val root = JSONObject(raw)
            val array = when {
                root.has("results") -> root.optJSONArray("results")
                root.has("data") -> root.optJSONArray("data")
                else -> JSONArray()
            } ?: JSONArray()

            if (array.length() == 0) return@withContext ""
            val item = array.optJSONObject(0) ?: return@withContext ""
            val direct = item.optString("gifUrl").ifBlank { item.optString("gifURL") }
            if (direct.isNotBlank()) return@withContext direct
            val id = item.optString("id").ifBlank { item.optString("exerciseId") }
            if (id.isNotBlank()) return@withContext "https://api.anatome.dev/exerciseGif?id=" + URLEncoder.encode(id, "UTF-8")
            ""
        } finally {
            connection.disconnect()
        }
    } catch (_: Exception) {
        ""
    }
}

@Composable
private fun PlansScreen(state: AppState, save: () -> Unit, open: (String) -> Unit) {
    var show by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }

    LazyColumn(contentPadding = PaddingValues(bottom = 110.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { Header("WORKOUT PLANS", "Unlimited splits with full exercise configuration") }
        item {
            Button(
                onClick = { show = true },
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)
            ) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(5.dp)); Text("CREATE PLAN") }
        }
        items(state.plans, key = { it.id }) { plan ->
            CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth().clickable { open(plan.id) }) {
                Text(plan.name, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text(plan.days.size.toString() + " days • " + plan.days.count { !it.restDay }.toString() + " training days", color = ArcMuted)
                Text("Tap to edit days, order, exercises, sets, reps, rest, RIR/RPE and notes.", color = ArcMuted, fontSize = 12.sp)
            }
        }
        if (state.plans.isEmpty()) item { Text("No plans yet. Create your exact split here.", color = ArcMuted, modifier = Modifier.padding(16.dp)) }
    }

    if (show) {
        AlertDialog(
            onDismissRequest = { show = false },
            title = { Text("Create plan") },
            text = { OutlinedTextField(name, { name = it }, label = { Text("Plan name") }, singleLine = true) },
            confirmButton = {
                Button({
                    if (name.isNotBlank()) {
                        val plan = WorkoutPlan(name = name.trim())
                        plan.days.add(WorkoutDay(name = "Day 1"))
                        state.plans.add(plan)
                        state.selectedPlanId = plan.id
                        state.selectedDayId = plan.days.first().id
                        save()
                        name = ""
                        show = false
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) { Text("CREATE") }
            },
            dismissButton = { TextButton({ show = false }) { Text("CANCEL") } }
        )
    }
}

@Composable
private fun PlanEditorScreen(plan: WorkoutPlan, state: AppState, exercises: List<Exercise>, save: () -> Unit, back: () -> Unit) {
    var selectedDayId by remember(plan.id) { mutableStateOf(plan.days.firstOrNull()?.id) }
    var addDay by remember { mutableStateOf(false) }
    var addExercise by remember { mutableStateOf(false) }
    var editDay by remember { mutableStateOf<WorkoutDay?>(null) }
    var editExercise by remember { mutableStateOf<PlannedExercise?>(null) }
    var tick by remember { mutableIntStateOf(0) }

    val selectedDay = plan.days.firstOrNull { it.id == selectedDayId } ?: plan.days.firstOrNull()

    LazyColumn(contentPadding = PaddingValues(bottom = 110.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { Header(plan.name.uppercase(), "Edit everything in this split", back) }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                plan.days.forEach { day ->
                    FilterChip(selected = day.id == selectedDay?.id, onClick = { selectedDayId = day.id }, label = { Text(day.name) })
                }
            }
        }
        item {
            Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button({ addDay = true }, Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) { Text("ADD DAY") }
                OutlinedButton({
                    plan.days.add(WorkoutDay(name = "Rest", restDay = true))
                    selectedDayId = plan.days.last().id
                    tick++
                    save()
                }, Modifier.weight(1f)) { Text("REST DAY") }
            }
        }
        selectedDay?.let { day ->
            item {
                CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(day.name, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                            Text(if (day.restDay) "Rest day" else day.exercises.size.toString() + " exercises • " + day.exercises.sumOf { it.sets }.toString() + " work sets", color = ArcMuted)
                        }
                        IconButton({ editDay = day }) { Icon(Icons.Default.Edit, null) }
                    }
                    if (!day.restDay) {
                        Button(
                            onClick = { addExercise = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)
                        ) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(5.dp)); Text("ADD EXERCISE") }
                    }
                }
            }
            itemsIndexed(day.exercises) { index, planned ->
                val ex = exercises.firstOrNull { it.id == planned.exerciseId }
                if (ex != null) {
                    CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ExerciseThumb(ex, Modifier.size(58.dp))
                            Spacer(Modifier.width(9.dp))
                            Column(Modifier.weight(1f)) {
                                Text((index + 1).toString() + ". " + ex.name, fontWeight = FontWeight.Bold)
                                Text(planned.sets.toString() + " × " + planned.repsMin + "-" + planned.repsMax + " • " + planned.weight.clean() + " " + state.weightUnit + " • " + planned.restSeconds + "s", color = ArcMuted, fontSize = 12.sp)
                                Text("RIR " + planned.rir + " • RPE " + planned.rpe.clean() + " • tempo " + planned.tempo + " • warm-up sets " + planned.warmupSets, color = ArcMuted, fontSize = 11.sp)
                            }
                            IconButton({ editExercise = planned }) { Icon(Icons.Default.Edit, null) }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(enabled = index > 0, onClick = { java.util.Collections.swap(day.exercises, index, index - 1); tick++; save() }) { Icon(Icons.Default.ArrowUpward, null, tint = if (index > 0) ArcText else ArcMuted) }
                            IconButton(enabled = index < day.exercises.lastIndex, onClick = { java.util.Collections.swap(day.exercises, index, index + 1); tick++; save() }) { Icon(Icons.Default.ArrowDownward, null, tint = if (index < day.exercises.lastIndex) ArcText else ArcMuted) }
                            IconButton(onClick = { day.exercises.removeAt(index); tick++; save() }) { Icon(Icons.Default.Delete, null, tint = ArcDanger) }
                        }
                    }
                }
            }
        }
        if (selectedDay != null && !selectedDay.restDay) {
            item {
                Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton({
                        state.selectedPlanId = plan.id
                        state.selectedDayId = selectedDay.id
                        save()
                    }, Modifier.weight(1f)) { Text("SET AS TODAY") }
                    OutlinedButton({
                        if (plan.days.size > 1) {
                            plan.days.removeAll { it.id == selectedDay.id }
                            selectedDayId = plan.days.firstOrNull()?.id
                            tick++
                            save()
                        }
                    }, Modifier.weight(1f)) { Text("DELETE DAY") }
                }
            }
        }
    }

    if (addDay) {
        DayNameDialog(
            title = "Add training day",
            start = "",
            confirm = { text ->
                if (text.isNotBlank()) {
                    plan.days.add(WorkoutDay(name = text.trim()))
                    selectedDayId = plan.days.last().id
                    tick++
                    save()
                }
            },
            close = { addDay = false }
        )
    }
    if (editDay != null) {
        DayNameDialog(
            title = "Rename day",
            start = editDay!!.name,
            confirm = { text ->
                if (text.isNotBlank()) editDay!!.name = text.trim()
                save()
                tick++
            },
            close = { editDay = null }
        )
    }
    if (addExercise && selectedDay != null) {
        AddExerciseDialog(exercises, selectedDay!!) { addExercise = false; save(); tick++ }
    }
    if (editExercise != null) {
        PlannedExerciseDialog(editExercise!!, state.weightUnit, save) { editExercise = null; tick++ }
    }
}

@Composable
private fun DayNameDialog(title: String, start: String, confirm: (String) -> Unit, close: () -> Unit) {
    var text by remember(start) { mutableStateOf(start) }
    AlertDialog(
        onDismissRequest = close,
        title = { Text(title) },
        text = { OutlinedTextField(text, { text = it }, label = { Text("Name") }, singleLine = true) },
        confirmButton = {
            Button({ confirm(text); close() }, colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) { Text("SAVE") }
        },
        dismissButton = { TextButton(close) { Text("CANCEL") } }
    )
}

@Composable
private fun AddExerciseDialog(exercises: List<Exercise>, day: WorkoutDay, close: () -> Unit) {
    var search by remember { mutableStateOf("") }
    val filtered = exercises.filter {
        search.isBlank() || it.name.contains(search, true) || it.primaryMuscle.contains(search, true) || it.equipment.contains(search, true)
    }.take(120)

    AlertDialog(
        onDismissRequest = close,
        title = { Text("Add exercise") },
        text = {
            Column {
                OutlinedTextField(search, { search = it }, label = { Text("Search by exercise or muscle") }, singleLine = true)
                Spacer(Modifier.height(7.dp))
                LazyColumn(Modifier.height(430.dp)) {
                    items(filtered, key = { it.id }) { ex ->
                        Row(
                            Modifier.fillMaxWidth().clickable {
                                if (day.exercises.none { it.exerciseId == ex.id }) day.exercises.add(PlannedExercise(ex.id))
                                close()
                            }.padding(vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExerciseThumb(ex, Modifier.size(55.dp))
                            Spacer(Modifier.width(8.dp))
                            Column(Modifier.weight(1f)) {
                                Text(ex.name, fontWeight = FontWeight.SemiBold)
                                Text(ex.primaryMuscle + " • " + ex.equipment, color = ArcMuted, fontSize = 11.sp)
                            }
                            Icon(Icons.Default.Add, null, tint = ArcAccent)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(close) { Text("DONE") } }
    )
}

@Composable
private fun PlannedExerciseDialog(
    planned: PlannedExercise,
    unit: String,
    save: () -> Unit,
    close: () -> Unit
) {
    var sets by remember { mutableStateOf(planned.sets.toString()) }
    var minReps by remember { mutableStateOf(planned.repsMin.toString()) }
    var maxReps by remember { mutableStateOf(planned.repsMax.toString()) }
    var weight by remember { mutableStateOf(planned.weight.toString()) }
    var rest by remember { mutableStateOf(planned.restSeconds.toString()) }
    var rir by remember { mutableStateOf(planned.rir.toString()) }
    var rpe by remember { mutableStateOf(planned.rpe.toString()) }
    var tempo by remember { mutableStateOf(planned.tempo) }
    var warmups by remember { mutableStateOf(planned.warmupSets.toString()) }
    var notes by remember { mutableStateOf(planned.notes) }

    AlertDialog(
        onDismissRequest = close,
        title = { Text("Configure exercise") },
        text = {
            LazyColumn(Modifier.height(520.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                item { NumberField("Sets", sets) { sets = it } }
                item { NumberField("Minimum reps", minReps) { minReps = it } }
                item { NumberField("Maximum reps", maxReps) { maxReps = it } }
                item { NumberField("Target weight (" + unit + ")", weight) { weight = it } }
                item { NumberField("Rest seconds", rest) { rest = it } }
                item { NumberField("RIR", rir) { rir = it } }
                item { NumberField("RPE", rpe) { rpe = it } }
                item { NumberField("Warm-up sets", warmups) { warmups = it } }
                item { OutlinedTextField(tempo, { tempo = it }, label = { Text("Tempo e.g. 2-1-2") }, singleLine = true) }
                item { OutlinedTextField(notes, { notes = it }, label = { Text("Notes / cues") }, minLines = 3) }
            }
        },
        confirmButton = {
            Button({
                planned.sets = sets.toIntOrNull()?.coerceIn(1, 30) ?: planned.sets
                planned.repsMin = minReps.toIntOrNull()?.coerceIn(1, 100) ?: planned.repsMin
                planned.repsMax = maxReps.toIntOrNull()?.coerceIn(planned.repsMin, 100) ?: planned.repsMax
                planned.weight = weight.toDoubleOrNull()?.coerceAtLeast(0.0) ?: planned.weight
                planned.restSeconds = rest.toIntOrNull()?.coerceIn(15, 900) ?: planned.restSeconds
                planned.rir = rir.toIntOrNull()?.coerceIn(0, 5) ?: planned.rir
                planned.rpe = rpe.toDoubleOrNull()?.coerceIn(1.0, 10.0) ?: planned.rpe
                planned.tempo = tempo.ifBlank { "2-1-2" }
                planned.warmupSets = warmups.toIntOrNull()?.coerceIn(0, 8) ?: planned.warmupSets
                planned.notes = notes
                save()
                close()
            }, colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) { Text("SAVE") }
        }
    )
}

@Composable
private fun NumberField(label: String, value: String, change: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = change,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun WorkoutScreen(state: AppState, exercises: List<Exercise>, save: () -> Unit, navigate: (String) -> Unit) {
    val plan = state.plans.firstOrNull { it.id == state.selectedPlanId } ?: state.plans.firstOrNull()
    val day = plan?.days?.firstOrNull { it.id == state.selectedDayId } ?: plan?.days?.firstOrNull()

    var active by remember { mutableStateOf(false) }
    var exerciseIndex by remember { mutableIntStateOf(0) }
    var setIndex by remember { mutableIntStateOf(0) }
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var rir by remember { mutableStateOf("2") }
    var restSeconds by remember { mutableIntStateOf(0) }
    var timerRunning by remember { mutableStateOf(false) }
    var session by remember { mutableStateOf<WorkoutSession?>(null) }

    LaunchedEffect(timerRunning) {
        while (timerRunning && restSeconds > 0) {
            delay(1000)
            restSeconds--
        }
        if (restSeconds <= 0) timerRunning = false
    }

    if (plan == null || day == null || day.restDay || day.exercises.isEmpty()) {
        Column {
            Header("WORKOUT", "Configure a plan first")
            CardBox(Modifier.padding(16.dp).fillMaxWidth()) {
                Text("No active training day", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("Build your split, select a day, then start the workout.", color = ArcMuted)
                Spacer(Modifier.height(10.dp))
                Button({ navigate("plans") }, colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) { Text("OPEN PLANS") }
            }
        }
        return
    }

    if (!active) {
        Column {
            Header("WORKOUT", day.name)
            CardBox(Modifier.padding(16.dp).fillMaxWidth()) {
                Text(day.exercises.size.toString() + " exercises", fontSize = 24.sp, fontWeight = FontWeight.Black)
                Text(day.exercises.sumOf { it.sets }.toString() + " work sets • per-exercise warm-ups configured in the plan", color = ArcMuted)
                Spacer(Modifier.height(9.dp))
                day.exercises.forEach { pe ->
                    val ex = exercises.firstOrNull { it.id == pe.exerciseId }
                    if (ex != null) {
                        Text(ex.name, fontWeight = FontWeight.SemiBold)
                        Text(pe.sets.toString() + " × " + pe.repsMin + "-" + pe.repsMax + " • " + pe.restSeconds + "s", color = ArcMuted, fontSize = 12.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button({
                    state.selectedPlanId = plan.id
                    state.selectedDayId = day.id
                    session = WorkoutSession(planId = plan.id, dayId = day.id, startedAt = System.currentTimeMillis())
                    exerciseIndex = 0
                    setIndex = 0
                    weight = ""
                    reps = ""
                    rir = "2"
                    active = true
                    save()
                }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) {
                    Icon(Icons.Default.PlayArrow, null); Spacer(Modifier.width(5.dp)); Text("START WORKOUT")
                }
                OutlinedButton({ navigate("warmup:" + primaryMuscleForDay(day, exercises)) }, Modifier.fillMaxWidth()) { Text("WARM-UP FIRST") }
            }
        }
        return
    }

    val planned = day.exercises.getOrNull(exerciseIndex)
    val ex = planned?.let { p -> exercises.firstOrNull { it.id == p.exerciseId } }

    if (planned == null || ex == null) {
        active = false
        return
    }

    LazyColumn(contentPadding = PaddingValues(bottom = 110.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { Header("SET " + (setIndex + 1).toString() + " / " + planned.sets.toString(), ex.name, back = {
            active = false
            timerRunning = false
        }) }
        item { CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            ExerciseHero(ex)
            Spacer(Modifier.height(9.dp))
            Text("TARGET", color = ArcMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(planned.repsMin.toString() + "-" + planned.repsMax + " reps • " + planned.weight.clean() + " " + state.weightUnit + " • RIR " + planned.rir, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            if (planned.warmupSets > 0) Text("Warm-up sets before work: " + planned.warmupSets, color = ArcAccent, fontSize = 12.sp)
        } }
        item { CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                NumberFieldInline("Weight", weight, Modifier.weight(1f)) { weight = it }
                NumberFieldInline("Reps", reps, Modifier.weight(1f)) { reps = it }
                NumberFieldInline("RIR", rir, Modifier.weight(0.8f)) { rir = it }
            }
            Spacer(Modifier.height(10.dp))
            Button({
                val w = weight.toDoubleOrNull() ?: planned.weight
                val r = reps.toIntOrNull() ?: planned.repsMin
                val rr = rir.toIntOrNull() ?: planned.rir
                session?.sets?.add(LoggedSet(ex.id, w, r, rr))
                restSeconds = planned.restSeconds
                timerRunning = true
                save()
                weight = w.toString()
                reps = ""
                if (setIndex + 1 < planned.sets) {
                    setIndex++
                } else if (exerciseIndex + 1 < day.exercises.size) {
                    exerciseIndex++
                    setIndex = 0
                } else {
                    val finished = session?.copy(endedAt = System.currentTimeMillis())
                    if (finished != null) state.sessions.add(finished)
                    state.currentStreak += 1
                    save()
                    active = false
                    navigate("stretch:" + primaryMuscleForDay(day, exercises))
                }
            }, Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) { Text("SAVE SET") }

            if (restSeconds > 0) {
                Spacer(Modifier.height(8.dp))
                Text(restSeconds.toString() + "s", fontSize = 32.sp, fontWeight = FontWeight.Black)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton({ restSeconds += 15 }) { Text("+15") }
                    OutlinedButton({ restSeconds = max(0, restSeconds - 15) }) { Text("-15") }
                    OutlinedButton({ restSeconds = 0; timerRunning = false }) { Text("SKIP") }
                }
            }
        } }
        item { CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            Text("PROGRAMMED", fontWeight = FontWeight.Bold)
            Text("Tempo " + planned.tempo + " • Rest " + planned.restSeconds + "s • Warm-up sets " + planned.warmupSets, color = ArcMuted, fontSize = 12.sp)
            if (planned.notes.isNotBlank()) Text(planned.notes, color = ArcMuted, fontSize = 12.sp, modifier = Modifier.padding(top = 5.dp))
        } }
    }
}

@Composable
private fun NumberFieldInline(label: String, value: String, modifier: Modifier, change: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = change,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier
    )
}

@Composable
private fun WarmupScreen(muscle: String, back: () -> Unit) {
    val steps = warmupLibrary[muscle] ?: warmupLibrary["Full Body"]!!
    var index by remember(muscle) { mutableIntStateOf(0) }
    var seconds by remember(muscle) { mutableIntStateOf(steps.first().seconds) }
    var running by remember { mutableStateOf(false) }

    LaunchedEffect(index) {
        seconds = steps[index].seconds
        running = false
    }
    LaunchedEffect(running) {
        while (running && seconds > 0) {
            delay(1000)
            seconds--
        }
        if (seconds <= 0) running = false
    }

    LazyColumn(contentPadding = PaddingValues(bottom = 110.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { Header("WARM-UP", muscle, back) }
        item { CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            Text("ADAPTIVE PREP", color = ArcAccent, fontWeight = FontWeight.Bold)
            Text("General heat → joints → muscle preparation → movement rehearsal.", color = ArcMuted)
        } }
        itemsIndexed(steps) { i, step ->
            CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Text((i + 1).toString() + ". " + step.title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(step.instructions, color = ArcMuted, lineHeight = 19.sp)
                if (i == index) {
                    Spacer(Modifier.height(8.dp))
                    Text(seconds.toString() + "s", fontSize = 28.sp, fontWeight = FontWeight.Black)
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        Button({ running = !running }, colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) { Text(if (running) "PAUSE" else "START") }
                        OutlinedButton({ seconds = step.seconds; running = false }) { Text("RESET") }
                        OutlinedButton({ if (index < steps.lastIndex) index++ }) { Text("NEXT") }
                    }
                }
            }
        }
    }
}

@Composable
private fun StretchScreen(muscle: String, back: () -> Unit) {
    val steps = stretchLibrary[muscle] ?: stretchLibrary["Full Body"]!!
    LazyColumn(contentPadding = PaddingValues(bottom = 110.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { Header("COOL-DOWN", muscle, back) }
        item { CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
            Text("CONTROLLED STRETCHING", color = ArcAccent, fontWeight = FontWeight.Bold)
            Text("Gentle, comfortable range only. No bouncing or forcing.", color = ArcMuted)
        } }
        items(steps) { step ->
            CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Text(step.title, fontWeight = FontWeight.Bold)
                Text(step.target, color = ArcAccent, fontSize = 11.sp)
                Text(step.instructions, color = ArcMuted, lineHeight = 19.sp)
            }
        }
    }
}

@Composable
private fun MoreScreen(open: (String) -> Unit) {
    LazyColumn(contentPadding = PaddingValues(bottom = 110.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { Header("MORE", "Progress and ARC tools") }
        item {
            CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                QuickAction("Goals & progress", Icons.Default.Flag) { open("goals") }
                QuickAction("Workout history", Icons.Default.History) { open("history") }
                QuickAction("Checklist", Icons.Default.Check) { open("checklist") }
                QuickAction("Settings", Icons.Default.Settings) { open("settings") }
            }
        }
    }
}

@Composable
private fun GoalsScreen(state: AppState, save: () -> Unit, back: () -> Unit) {
    var show by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }

    LazyColumn(contentPadding = PaddingValues(bottom = 110.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { Header("GOALS", "Strength • reps • frequency • consistency", back) }
        item {
            Button({ show = true }, Modifier.padding(horizontal = 16.dp).fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) { Text("CREATE GOAL") }
        }
        items(state.goals, key = { it.id }) { goal ->
            val progress = if (goal.target > 0) (goal.current / goal.target).coerceIn(0.0, 1.0) else 0.0
            CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Text(goal.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(goal.current.clean() + " / " + goal.target.clean() + " " + goal.unit, color = ArcMuted)
                LinearProgressIndicator(progress = progress.toFloat(), modifier = Modifier.fillMaxWidth(), color = ArcAccent)
            }
        }
    }
    if (show) {
        AlertDialog(
            onDismissRequest = { show = false },
            title = { Text("Create goal") },
            text = { Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                OutlinedTextField(name, { name = it }, label = { Text("Goal name") }, singleLine = true)
                OutlinedTextField(target, { target = it }, label = { Text("Target") }, singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
            } },
            confirmButton = {
                Button({
                    if (name.isNotBlank()) {
                        state.goals.add(Goal(name = name.trim(), type = "custom", target = target.toDoubleOrNull() ?: 1.0))
                        name = ""
                        target = ""
                        show = false
                        save()
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) { Text("CREATE") }
            },
            dismissButton = { TextButton({ show = false }) { Text("CANCEL") } }
        )
    }
}

@Composable
private fun ChecklistScreen(state: AppState, save: () -> Unit, back: () -> Unit) {
    var tick by remember { mutableIntStateOf(0) }
    if (state.checklists.isEmpty()) {
        state.checklists.add(Checklist(name = "Daily"))
        save()
    }
    var selectedId by remember { mutableStateOf(state.checklists.firstOrNull()?.id) }
    val selected = state.checklists.firstOrNull { it.id == selectedId } ?: state.checklists.firstOrNull()
    var show by remember { mutableStateOf(false) }
    var itemText by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        Header("CHECKLIST", "Daily / Gym / Study / Business / Personal", back)
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            state.checklists.forEach { list ->
                FilterChip(selected = list.id == selected?.id, onClick = { selectedId = list.id; tick++ }, label = { Text(list.name) })
            }
        }
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            Button({ show = true }, Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) { Text("ADD ITEM") }
            OutlinedButton({
                val list = Checklist(name = "Custom " + (state.checklists.size + 1))
                state.checklists.add(list)
                selectedId = list.id
                save()
                tick++
            }, Modifier.weight(1f)) { Text("NEW LIST") }
        }
        LazyColumn(contentPadding = PaddingValues(16.dp, 0.dp, 16.dp, 110.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            selected?.let { list ->
                items(list.items, key = { it.id }) { item ->
                    CardBox(Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = item.done, onCheckedChange = { item.done = it; save(); tick++ })
                            Text(item.title, Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                            IconButton({ list.items.remove(item); save(); tick++ }) { Icon(Icons.Default.Delete, null, tint = ArcDanger) }
                        }
                    }
                }
            }
        }
    }

    if (show) {
        AlertDialog(
            onDismissRequest = { show = false },
            title = { Text("Checklist item") },
            text = { OutlinedTextField(itemText, { itemText = it }, label = { Text("Item") }, singleLine = true) },
            confirmButton = {
                Button({
                    if (itemText.isNotBlank()) selected?.items?.add(ChecklistItem(title = itemText.trim()))
                    itemText = ""
                    show = false
                    save()
                    tick++
                }, colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) { Text("ADD") }
            },
            dismissButton = { TextButton({ show = false }) { Text("CANCEL") } }
        )
    }
}

@Composable
private fun HistoryScreen(state: AppState, exercises: List<Exercise>, back: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(bottom = 110.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { Header("HISTORY", "Logged workouts and sets", back) }
        if (state.sessions.isEmpty()) item { Text("No completed workouts yet.", color = ArcMuted, modifier = Modifier.padding(16.dp)) }
        items(state.sessions.asReversed()) { session ->
            CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                val day = state.plans.flatMap { it.days }.firstOrNull { it.id == session.dayId }
                Text(day?.name ?: "Workout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(session.sets.size.toString() + " logged sets", color = ArcMuted)
                session.sets.take(10).forEach { logged ->
                    val ex = exercises.firstOrNull { it.id == logged.exerciseId }
                    Text((ex?.name ?: "Exercise") + " — " + logged.weight.clean() + " " + state.weightUnit + " × " + logged.reps + " • RIR " + logged.rir, color = ArcMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(state: AppState, save: () -> Unit, back: () -> Unit) {
    val context = LocalContext.current
    var haptics by remember { mutableStateOf(state.haptics) }
    var demos by remember { mutableStateOf(state.showExerciseDemos) }

    LazyColumn(contentPadding = PaddingValues(bottom = 110.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
        item { Header("SETTINGS", "ARC preferences", back) }
        item {
            CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Text("DEFAULT REST", fontWeight = FontWeight.Bold)
                Text(state.defaultRest.toString() + " seconds", fontSize = 22.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton({ state.defaultRest = (state.defaultRest - 15).coerceAtLeast(15); save() }) { Text("-15") }
                    OutlinedButton({ state.defaultRest = (state.defaultRest + 15).coerceAtMost(900); save() }) { Text("+15") }
                }
            }
        }
        item {
            CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Haptic cues", fontWeight = FontWeight.Bold)
                        Text("Timer and control feedback", color = ArcMuted, fontSize = 12.sp)
                    }
                    Switch(checked = haptics, onCheckedChange = { haptics = it; state.haptics = it; save() })
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Exercise demonstrations", fontWeight = FontWeight.Bold)
                        Text("Use real human exercise GIFs", color = ArcMuted, fontSize = 12.sp)
                    }
                    Switch(checked = demos, onCheckedChange = { demos = it; state.showExerciseDemos = it; save() })
                }
            }
        }
        item {
            CardBox(Modifier.padding(horizontal = 16.dp).fillMaxWidth()) {
                Text("DEMO SOURCE", fontWeight = FontWeight.Bold)
                Text("ARC requests exercise demonstration media from the open Anatome/ExerciseDB ecosystem and caches media through the image loader.", color = ArcMuted, fontSize = 12.sp)
            }
        }
        item {
            Button(
                onClick = {
                    val summary = "ARC\nPlans: " + state.plans.size + "\nSessions: " + state.sessions.size + "\nGoals: " + state.goals.size + "\nStreak: " + state.currentStreak
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, summary)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share ARC"))
                },
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
            ) { Icon(Icons.Default.Share, null); Spacer(Modifier.width(5.dp)); Text("SHARE ARC SUMMARY") }
        }
    }
}

private data class Guide(
    val setup: String,
    val start: String,
    val steps: String,
    val breathing: String,
    val tempo: String,
    val rom: String,
    val mistakes: String,
    val beginner: String
)

private fun detailedGuide(ex: Exercise): Guide {
    val n = ex.name.lowercase()
    return when {
        n.contains("bench press") -> Guide(
            "Set the bench securely. Place both feet on the floor and position your eyes under the bar. Grip the bar evenly and keep the shoulders stable against the bench.",
            "Unrack with control. Start with the bar above the middle of the chest and keep the wrists stacked over the forearms.",
            "1. Brace your trunk. 2. Lower the bar toward the lower-to-mid chest with a controlled elbow path. 3. Touch lightly or stop at the depth you can control. 4. Press smoothly back up. 5. Reset before the next repetition.",
            "Inhale and brace before lowering. Exhale through the pressing effort.",
            "Use a controlled lowering phase and a smooth press. Avoid bouncing.",
            "Use the depth you can control without losing the intended shoulder position.",
            "Bouncing the bar, uneven grip, hips leaving the bench, or using a load you cannot control.",
            "Start light. Use a safe rack setup and appropriate spotting when needed."
        )
        n.contains("lat pulldown") -> Guide(
            "Adjust the seat and thigh pad so you are secure. Choose the grip shown and use a manageable load.",
            "Sit tall with the torso stable and the arms reaching toward the bar.",
            "1. Brace lightly. 2. Pull the elbows down. 3. Bring the bar toward the upper chest without swinging. 4. Pause briefly. 5. Return the bar slowly.",
            "Inhale during the return. Exhale while pulling.",
            "Smooth pull, controlled return, no jerking.",
            "Use a comfortable shoulder range and keep the torso stable.",
            "Swinging, leaning excessively, pulling behind the neck, or rushing the return.",
            "Think about driving the elbows rather than yanking with the hands."
        )
        n.contains("squat") -> Guide(
            "Set the rack or machine safely. Use a stance you can balance and control.",
            "Brace your trunk and keep both feet firmly connected to the floor.",
            "1. Brace. 2. Bend the hips and knees together. 3. Lower with balance. 4. Let the knees track naturally with the feet. 5. Drive up through the floor and finish tall.",
            "Inhale and brace before the descent. Exhale as you stand.",
            "Controlled descent, stable bottom, deliberate ascent.",
            "Use the deepest range you can control without losing balance or position.",
            "Rushing the bottom, collapsing the knees, losing balance, or adding too much load.",
            "Learn the movement with an unloaded or light version first."
        )
        n.contains("curl") -> Guide(
            "Choose a manageable load. Set the elbows so the upper arms stay mostly fixed.",
            "Start with the arms extended as comfortably as possible and wrists neutral.",
            "1. Keep the upper arm stable. 2. Curl through the elbow. 3. Pause near the top. 4. Lower slowly. 5. Repeat without swinging.",
            "Exhale on the curl. Inhale on the controlled lowering phase.",
            "Smooth curl with a slower return.",
            "Use the range your elbow and shoulder can control comfortably.",
            "Swinging the body, lifting the shoulders, or cutting the return short.",
            "Reduce the load until the full repetition stays controlled."
        )
        else -> Guide(
            "Adjust the equipment so the joints line up with the intended movement. Start with a light load and verify the setup.",
            "Begin exactly where the demonstration shows. Keep non-moving body parts stable.",
            "1. Set the body. 2. Move the resistance along the intended path. 3. Keep the torso and other stabilizers controlled. 4. Return the resistance smoothly. 5. Repeat.",
            "Inhale during preparation or return. Exhale during the main effort while keeping breathing comfortable.",
            "Controlled rhythm; do not bounce or rush the return.",
            "Use the pain-free, controlled range appropriate to the exercise.",
            "Too much load, momentum, poor setup, rushed reps, or changing body position.",
            "Choose a lighter load than you think you need. Learn consistent technique before adding weight."
        )
    }
}

private data class WarmupStep(val title: String, val instructions: String, val seconds: Int)
private data class StretchStep(val title: String, val target: String, val instructions: String)

private val warmupLibrary = mapOf(
    "Full Body" to listOf(
        WarmupStep("Easy whole-body movement", "Walk, cycle, march, or use another easy movement until you feel generally warm.", 180),
        WarmupStep("Joint preparation", "Move shoulders, elbows, wrists, hips, knees and ankles through comfortable circles.", 60),
        WarmupStep("Bodyweight squat", "Perform controlled squats through an easy range with steady breathing.", 45),
        WarmupStep("First movement rehearsal", "Perform light practice repetitions of your first major movement.", 60)
    ),
    "Chest" to listOf(
        WarmupStep("General heat", "Easy walking or cycling.", 180),
        WarmupStep("Shoulder circles", "Controlled circles, gradually increasing the range.", 45),
        WarmupStep("Scapular push-up", "Keep the elbows mostly straight and move the shoulder blades under control.", 45),
        WarmupStep("Press rehearsal", "Use an empty bar or very light resistance before the work sets.", 60)
    ),
    "Back" to listOf(
        WarmupStep("General heat", "Easy cardio for a few minutes.", 180),
        WarmupStep("Thoracic rotations", "Gently rotate the upper back through a comfortable range.", 45),
        WarmupStep("Light pull-aparts", "Use very light resistance and keep the motion controlled.", 45),
        WarmupStep("Row/pulldown rehearsal", "Rehearse your first pulling pattern with light resistance.", 60)
    ),
    "Lats" to listOf(
        WarmupStep("General heat", "Easy cardio.", 150),
        WarmupStep("Overhead reach", "Reach overhead comfortably without forcing the shoulder.", 45),
        WarmupStep("Straight-arm rehearsal", "Use very light resistance and focus on controlled shoulder movement.", 45),
        WarmupStep("First pull rehearsal", "Perform easy vertical pulling repetitions.", 60)
    ),
    "Shoulders" to listOf(
        WarmupStep("General heat", "Easy whole-body movement.", 150),
        WarmupStep("Arm circles", "Small, controlled circles progressing to a comfortable range.", 45),
        WarmupStep("Wall slides", "Keep the movement controlled and comfortable.", 45),
        WarmupStep("Press rehearsal", "Use very light resistance for slow practice reps.", 60)
    ),
    "Biceps" to listOf(
        WarmupStep("General heat", "Easy movement until warm.", 120),
        WarmupStep("Wrist circles", "Move wrists through comfortable circles.", 30),
        WarmupStep("Light curl", "Perform very light curls with no swinging.", 45),
        WarmupStep("First-set rehearsal", "Use a light load before working weight.", 60)
    ),
    "Triceps" to listOf(
        WarmupStep("General heat", "Easy movement until warm.", 120),
        WarmupStep("Elbow and wrist prep", "Small, comfortable circles.", 30),
        WarmupStep("Light pushdown", "Practice the elbow path with very light cable resistance.", 45),
        WarmupStep("First-set rehearsal", "Perform a light practice set.", 60)
    ),
    "Quadriceps" to listOf(
        WarmupStep("General heat", "Easy cycling or walking.", 180),
        WarmupStep("Ankle rocks", "Move the knee forward over the foot while keeping the heel down.", 45),
        WarmupStep("Bodyweight squat", "Controlled easy squats.", 45),
        WarmupStep("Leg movement rehearsal", "Practice the first leg movement with light resistance.", 60)
    ),
    "Hamstrings" to listOf(
        WarmupStep("General heat", "Easy cycling or walking.", 180),
        WarmupStep("Hip hinge drill", "Push the hips back while keeping the torso controlled.", 45),
        WarmupStep("Glute bridge", "Slow bridge repetitions with a brief pause at the top.", 45),
        WarmupStep("Hinge rehearsal", "Use a very light load for the first hinge.", 60)
    ),
    "Glutes" to listOf(
        WarmupStep("General heat", "Easy walking or cycling.", 150),
        WarmupStep("Glute bridge", "Slow and controlled.", 45),
        WarmupStep("Bodyweight lunge", "Use a stable, comfortable range.", 45),
        WarmupStep("Hip thrust rehearsal", "Practice with minimal load.", 60)
    ),
    "Calves" to listOf(
        WarmupStep("General heat", "Easy movement.", 120),
        WarmupStep("Ankle circles", "Slow circles through a comfortable range.", 30),
        WarmupStep("Bodyweight calf raise", "Controlled repetitions.", 45),
        WarmupStep("First-set rehearsal", "Use a light load before work sets.", 60)
    ),
    "Abdominals" to listOf(
        WarmupStep("General heat", "Easy movement.", 120),
        WarmupStep("Pelvic control", "Practice gentle pelvic tilts with relaxed breathing.", 45),
        WarmupStep("Dead-bug rehearsal", "Slow alternating movements while keeping the trunk controlled.", 45),
        WarmupStep("First core pattern", "Perform easy practice reps of your first core exercise.", 60)
    ),
    "Forearms" to listOf(
        WarmupStep("General heat", "Easy movement.", 90),
        WarmupStep("Wrist flexion/extension", "Move the wrists through a comfortable range.", 45),
        WarmupStep("Grip rehearsal", "Use a light load and practice the intended grip.", 45),
        WarmupStep("First-set rehearsal", "Perform an easy first set.", 60)
    )
)

private val stretchLibrary = mapOf(
    "Full Body" to listOf(
        StretchStep("Easy walk", "Whole body", "Walk slowly and breathe comfortably for a few minutes."),
        StretchStep("Chest opener", "Chest / shoulders", "Use a doorway or wall and gently open the chest."),
        StretchStep("Hip flexor stretch", "Hips", "Use a comfortable split stance and gently shift forward."),
        StretchStep("Calf stretch", "Calves", "Keep the heel down and lean gently toward a stable surface.")
    ),
    "Chest" to listOf(
        StretchStep("Doorway chest stretch", "Chest", "Turn away gently from a doorway with the forearm supported."),
        StretchStep("Cross-body shoulder", "Shoulders", "Bring one arm across the body and hold gently.")
    ),
    "Back" to listOf(
        StretchStep("Gentle upper-back reach", "Upper back", "Reach forward comfortably and breathe slowly."),
        StretchStep("Cross-body reach", "Upper back", "Reach across the body and rotate gently.")
    ),
    "Shoulders" to listOf(
        StretchStep("Cross-body shoulder", "Shoulders", "Hold one arm across the body gently."),
        StretchStep("Gentle triceps stretch", "Triceps", "Bring one arm overhead and support the elbow without forcing.")
    ),
    "Biceps" to listOf(
        StretchStep("Gentle biceps stretch", "Biceps", "Place the hand on a stable surface and turn away slowly.")
    ),
    "Triceps" to listOf(
        StretchStep("Overhead triceps", "Triceps", "Support the elbow gently without forcing the range.")
    ),
    "Quadriceps" to listOf(
        StretchStep("Standing quad stretch", "Quadriceps", "Hold a stable support and gently bring the heel toward you.")
    ),
    "Hamstrings" to listOf(
        StretchStep("Gentle hamstring", "Hamstrings", "Extend one leg comfortably and lean from the hips.")
    ),
    "Glutes" to listOf(
        StretchStep("Figure-four", "Glutes", "Place one ankle over the opposite leg and move gently into the stretch.")
    ),
    "Calves" to listOf(
        StretchStep("Wall calf stretch", "Calves", "Keep the heel down and lean gently toward the wall.")
    ),
    "Abdominals" to listOf(
        StretchStep("Gentle trunk extension", "Abdominals", "Use a comfortable range and avoid forcing the lower back.")
    ),
    "Forearms" to listOf(
        StretchStep("Wrist stretch", "Forearms", "Extend the arm and gently move the fingers back with the other hand.")
    )
)

private fun primaryMuscleForDay(day: WorkoutDay, exercises: List<Exercise>): String {
    val count = mutableMapOf<String, Int>()
    day.exercises.forEach { planned ->
        val muscle = exercises.firstOrNull { it.id == planned.exerciseId }?.primaryMuscle ?: return@forEach
        count[muscle] = (count[muscle] ?: 0) + 1
    }
    return count.maxByOrNull { it.value }?.key ?: "Full Body"
}

private fun exerciseGifFallbackUrl(ex: Exercise): String {
    val slug = ex.name.trim().replace(Regex("[^A-Za-z0-9]+"), "_").trim('_')
    return "https://api.anatome.dev/exerciseGif?id=" + slug
}

private fun patternLabel(pattern: String): String = when (pattern) {
    "horizontal_push" -> "Horizontal push"
    "incline_push" -> "Incline push"
    "decline_push" -> "Decline push"
    "vertical_push" -> "Vertical push"
    "horizontal_pull" -> "Horizontal pull"
    "vertical_pull" -> "Vertical pull"
    "squat" -> "Squat"
    "hinge" -> "Hinge"
    "lunge" -> "Lunge"
    "curl" -> "Curl"
    "triceps_extension" -> "Triceps extension"
    "triceps_pushdown" -> "Triceps pushdown"
    "lateral_raise" -> "Lateral raise"
    "front_raise" -> "Front raise"
    "rear_delt" -> "Rear delt"
    "crunch" -> "Core"
    "carry" -> "Carry"
    else -> pattern.replace('_', ' ').capitalizeSafe()
}

private fun String.capitalizeSafe(): String =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }

private fun Double.clean(): String =
    if (this % 1.0 == 0.0) this.toInt().toString() else "%.1f".format(this)

@Composable
private fun CardBox(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ArcPanel)
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
private fun Header(title: String, subtitle: String = "", back: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        if (back != null) {
            IconButton(back) { Icon(Icons.Default.ArrowBack, null) }
            Spacer(Modifier.width(1.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 27.sp, fontWeight = FontWeight.Black, letterSpacing = 0.8.sp)
            if (subtitle.isNotBlank()) Text(subtitle, color = ArcMuted, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ChipRow(options: List<String>, selected: String, select: (String) -> Unit) {
    Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        options.forEach { option ->
            FilterChip(selected = option == selected, onClick = { select(option) }, label = { Text(option) })
        }
    }
}

@Composable
private fun Tag(text: String) {
    Surface(color = ArcPanel2, shape = RoundedCornerShape(99.dp)) {
        Text(text, Modifier.padding(horizontal = 9.dp, vertical = 5.dp), fontSize = 10.sp)
    }
}

@Composable
private fun CustomExerciseDialog(onDismiss: () -> Unit, onCreate: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var muscle by remember { mutableStateOf("Chest") }
    var equipment by remember { mutableStateOf("Dumbbell") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create custom exercise") },
        text = { Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            OutlinedTextField(name, { name = it }, label = { Text("Exercise name") }, singleLine = true)
            OutlinedTextField(muscle, { muscle = it }, label = { Text("Primary muscle") }, singleLine = true)
            OutlinedTextField(equipment, { equipment = it }, label = { Text("Equipment") }, singleLine = true)
        } },
        confirmButton = {
            Button({
                if (name.isNotBlank()) onCreate(name.trim(), muscle.trim().ifBlank { "Full Body" }, equipment.trim().ifBlank { "Other" })
            }, colors = ButtonDefaults.buttonColors(containerColor = ArcAccent, contentColor = Color.Black)) { Text("CREATE") }
        },
        dismissButton = { TextButton(onDismiss) { Text("CANCEL") } }
    )
}
