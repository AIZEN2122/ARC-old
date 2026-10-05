
package com.arc.training

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.KeyboardType
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.cos
import kotlin.math.sin

private val Bg = Color(0xFF0B0B0D)
private val Surface = Color(0xFF141418)
private val Surface2 = Color(0xFF1A1A20)
private val TextMain = Color(0xFFF4F4F5)
private val TextMuted = Color(0xFF9B9BA3)
private val Accent = Color(0xFFD7FF4B)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { ARCApp() }
    }
}

@Composable
fun ARCApp() {
    val context = LocalContext.current
    val store = remember { AppStateStore(context) }
    val state = remember { mutableStateOf(store.load()) }
    val exercises = remember { mutableStateListOf<Exercise>().also { it.addAll(SeedExercises.all) } }
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun save() = store.save(state.value)

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Accent,
        background = Bg,
        surface = Surface,
        onBackground = TextMain,
        onSurface = TextMain
    )) {
        Surface(modifier = Modifier.fillMaxSize(), color = Bg) {
            val nav = rememberNavController()
            Scaffold(
                containerColor = Bg,
                snackbarHost = { SnackbarHost(snackbar) },
                bottomBar = { ArcBottomBar(nav) }
            ) { padding ->
                NavHost(navController = nav, startDestination = "home", modifier = Modifier.padding(padding)) {
                    composable("home") { HomeScreen(nav, state.value, exercises, save) }
                    composable("exercises") { ExerciseLibraryScreen(nav, exercises, save, state.value) }
                    composable("plans") { PlansScreen(nav, state.value, exercises, save) }
                    composable("workout") { WorkoutScreen(nav, state.value, exercises, save) }
                    composable("goals") { GoalsScreen(state.value, exercises, save) }
                    composable("history") { HistoryScreen(state.value, exercises) }
                    composable("checklist") { ChecklistScreen(state.value, save) }
                    composable("reports") { ReportsScreen(state.value, exercises) }
                    composable("settings") { SettingsScreen(state, store, exercises, save, snackbar) }
                    composable("exercise/{id}") { back ->
                        val id = back.arguments?.getString("id") ?: return@composable
                        val ex = exercises.firstOrNull { it.id == id } ?: return@composable
                        ExerciseDetailScreen(nav, ex, exercises, state.value, save)
                    }
                    composable("plan/{id}") { back ->
                        val id = back.arguments?.getString("id") ?: return@composable
                        val plan = state.value.plans.firstOrNull { it.id == id } ?: return@composable
                        PlanEditorScreen(nav, state.value, plan, exercises, save)
                    }
                    composable("warmup/{planId}/{dayId}") { back ->
                        val planId = back.arguments?.getString("planId") ?: return@composable
                        val dayId = back.arguments?.getString("dayId") ?: return@composable
                        val plan = state.value.plans.firstOrNull { it.id == planId } ?: return@composable
                        val day = plan.days.firstOrNull { it.id == dayId } ?: return@composable
                        WarmupScreen(nav, day, exercises)
                    }
                    composable("stretch/{planId}/{dayId}") { back ->
                        val planId = back.arguments?.getString("planId") ?: return@composable
                        val dayId = back.arguments?.getString("dayId") ?: return@composable
                        val plan = state.value.plans.firstOrNull { it.id == planId } ?: return@composable
                        val day = plan.days.firstOrNull { it.id == dayId } ?: return@composable
                        StretchScreen(nav, day, exercises)
                    }
                }
            }
        }
    }
}

@Composable
fun ArcBottomBar(nav: NavHostController) {
    val items = listOf("home" to Icons.Default.Home, "exercises" to Icons.Default.FitnessCenter, "plans" to Icons.Default.CalendarMonth, "workout" to Icons.Default.PlayArrow, "goals" to Icons.Default.Flag)
    NavigationBar(containerColor = Color(0xFF0F0F12)) {
        items.forEach { (route, icon) ->
            NavigationBarItem(selected = false, onClick = { nav.navigate(route) { launchSingleTop = true } }, icon = { Icon(icon, null) }, label = { Text(route.replaceFirstChar { it.uppercase() }) })
        }
    }
}

@Composable fun ArcTop(title:String, subtitle:String?=null, back:(()->Unit)?=null) {
    Row(modifier=Modifier.fillMaxWidth().padding(horizontal=20.dp, vertical=16.dp), verticalAlignment=Alignment.CenterVertically) {
        if (back != null) IconButton(onClick=back) { Icon(Icons.Default.ArrowBack, null, tint=TextMain) }
        Column(modifier=Modifier.weight(1f)) { Text(title, fontSize=26.sp, fontWeight=FontWeight.Bold); subtitle?.let { Text(it, color=TextMuted, fontSize=13.sp) } }
        Text("ARC", color=Accent, fontWeight=FontWeight.Black, letterSpacing=2.sp)
    }
}

@Composable fun CardBox(modifier: Modifier=Modifier, content:@Composable ColumnScope.()->Unit) { Surface(modifier=modifier.clip(RoundedCornerShape(22.dp)), color=Surface) { Column(Modifier.padding(18.dp), content=content) } }

@Composable
fun HomeScreen(nav: NavHostController, state: AppState, exercises: List<Exercise>, save:()->Unit) {
    val context = LocalContext.current
    val selectedPlan = state.plans.firstOrNull { it.id == state.selectedPlanId } ?: state.plans.firstOrNull()
    val selectedDay = selectedPlan?.days?.firstOrNull { it.id == state.selectedDayId } ?: selectedPlan?.days?.firstOrNull()
    LazyColumn(modifier=Modifier.fillMaxSize(), contentPadding=PaddingValues(bottom=120.dp)) {
        item {
            ArcTop("ARC", "Train. Track. Evolve.")
            CardBox(Modifier.padding(horizontal=16.dp).fillMaxWidth()) {
                Image(painter = androidx.compose.ui.res.painterResource(com.arc.training.R.drawable.arc_thumbnail), contentDescription="ARC thumbnail", modifier=Modifier.fillMaxWidth().height(190.dp).clip(RoundedCornerShape(18.dp)), contentScale=ContentScale.Crop)
                Spacer(Modifier.height(14.dp))
                Text("TODAY'S SESSION", color=TextMuted, fontSize=12.sp, letterSpacing=1.5.sp, fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(selectedDay?.name ?: "Create a workout plan", fontSize=24.sp, fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp)) {
                    Button(onClick={ if(selectedPlan!=null&&selectedDay!=null) nav.navigate("warmup/${selectedPlan.id}/${selectedDay.id}") else nav.navigate("plans") }, colors=ButtonDefaults.buttonColors(containerColor=Accent, contentColor=Color.Black), modifier=Modifier.weight(1f)) { Text("WARM-UP") }
                    OutlinedButton(onClick={ nav.navigate("workout") }, modifier=Modifier.weight(1f)) { Text("WORKOUT") }
                }
            }
        }
        item { Spacer(Modifier.height(14.dp)) }
        item {
            Row(Modifier.padding(horizontal=16.dp), horizontalArrangement=Arrangement.spacedBy(12.dp)) {
                Metric("GOALS", state.goals.count { it.active }.toString(), Modifier.weight(1f))
                Metric("STREAK", state.currentStreak.toString(), Modifier.weight(1f))
                Metric("SESSIONS", state.sessions.size.toString(), Modifier.weight(1f))
            }
        }
        item { Spacer(Modifier.height(14.dp)) }
        item {
            CardBox(Modifier.padding(horizontal=16.dp).fillMaxWidth()) {
                Text("QUICK ACTIONS", fontWeight=FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                TextButton(onClick={ nav.navigate("exercises") }) { Text("Browse exercises") }
                TextButton(onClick={ nav.navigate("plans") }) { Text("Customize my split") }
                TextButton(onClick={ nav.navigate("checklist") }) { Text("Open checklist") }
                TextButton(onClick={ nav.navigate("reports") }) { Text("Generate report") }
            }
        }
    }
}

@Composable fun Metric(label:String,value:String,modifier:Modifier=Modifier){ CardBox(modifier){Text(label,color=TextMuted,fontSize=11.sp);Spacer(Modifier.height(4.dp));Text(value,fontSize=24.sp,fontWeight=FontWeight.Bold)} }

@Composable
fun ExerciseLibraryScreen(nav:NavHostController, exercises:MutableList<Exercise>, save:()->Unit, state:AppState){
    var query by remember{mutableStateOf("")}; var muscle by remember{mutableStateOf("All")}; var favoritesOnly by remember{mutableStateOf(false)}; var showCustom by remember{mutableStateOf(false)}
    val muscles=listOf("All","Chest","Back","Lats","Shoulders","Biceps","Triceps","Quadriceps","Hamstrings","Glutes","Calves","Abdominals","Obliques","Forearms")
    val filtered=exercises.filter { (query.isBlank()||it.name.contains(query,true)||it.primaryMuscle.contains(query,true)) && (muscle=="All"||it.primaryMuscle.contains(muscle,true)||it.secondaryMuscles.any{m->m.contains(muscle,true)}) && (!favoritesOnly||state.favorites.contains(it.id)) }
    Column(Modifier.fillMaxSize()){
        ArcTop("Exercises","Search, filter, learn")
        OutlinedTextField(query,{query=it},Modifier.fillMaxWidth().padding(horizontal=16.dp),singleLine=true,label={Text("Search exercises")})
        Spacer(Modifier.height(8.dp))
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal=16.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){muscles.forEach{FilterChip(selected=muscle==it,onClick={muscle=it},label={Text(it)})};FilterChip(selected=favoritesOnly,onClick={favoritesOnly=!favoritesOnly},label={Text("Favorites")})}; Spacer(Modifier.height(6.dp)); Button(onClick={showCustom=true},Modifier.padding(horizontal=16.dp).fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Accent,contentColor=Color.Black)){Icon(Icons.Default.Add,null);Spacer(Modifier.width(6.dp));Text("CREATE CUSTOM EXERCISE")}
        Spacer(Modifier.height(8.dp))
        Text("${filtered.size} exercises", color=TextMuted, modifier=Modifier.padding(horizontal=16.dp))
        LazyColumn(contentPadding=PaddingValues(16.dp,8.dp,16.dp,120.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
            items(filtered, key={it.id}){ex->
                CardBox(Modifier.fillMaxWidth().clickable{state.recentExerciseIds.remove(ex.id); state.recentExerciseIds.add(0,ex.id); if(state.recentExerciseIds.size>20)state.recentExerciseIds.removeLast(); save(); nav.navigate("exercise/${ex.id}")}){
                    Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(ex.name,fontWeight=FontWeight.SemiBold,fontSize=17.sp);Text("${ex.primaryMuscle} • ${ex.equipment}",color=TextMuted,fontSize=12.sp);Text(ex.difficulty.replaceFirstChar{it.uppercase()},color=Accent,fontSize=11.sp,fontWeight=FontWeight.Bold)};IconButton(onClick={if(state.favorites.contains(ex.id))state.favorites.remove(ex.id) else state.favorites.add(ex.id);save()}){Icon(if(state.favorites.contains(ex.id))Icons.Default.Star else Icons.Default.StarBorder,null,tint=Accent)};Icon(Icons.Default.ChevronRight,null,tint=TextMuted)}
                }
            }
        }
    }
    if(showCustom) CustomExerciseDialog(onCreate={name,muscle,eq,pattern->exercises.add(Exercise(name=name,primaryMuscle=muscle,equipment=eq,pattern=pattern,isCustom=true));save();showCustom=false},onDismiss={showCustom=false})
}

@Composable
fun CustomExerciseDialog(onCreate:(String,String,String,String)->Unit,onDismiss:()->Unit){var name by remember{mutableStateOf("")};var muscle by remember{mutableStateOf("Chest")};var eq by remember{mutableStateOf("Dumbbell")};var pattern by remember{mutableStateOf("isolation")};AlertDialog(onDismissRequest=onDismiss,title={Text("Custom exercise")},text={Column(verticalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(name,{name=it},label={Text("Name")});OutlinedTextField(muscle,{muscle=it},label={Text("Primary muscle")});OutlinedTextField(eq,{eq=it},label={Text("Equipment")});OutlinedTextField(pattern,{pattern=it},label={Text("Animation profile")})}},confirmButton={Button(onClick={if(name.isNotBlank())onCreate(name,muscle,eq,pattern)}){Text("Create")}},dismissButton={TextButton(onClick=onDismiss){Text("Cancel")}})}

@Composable
fun ExerciseDetailScreen(nav:NavHostController, ex:Exercise, exercises:List<Exercise>, state:AppState, save:()->Unit){
    val tutorial=remember(ex.id){exerciseTutorial(ex)}
    var mode by remember{mutableStateOf("BEGINNER")}
    LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(bottom=120.dp)){
        item{ArcTop(ex.name,"${ex.primaryMuscle} • ${ex.equipment}"){nav.popBackStack()}}
        item{CardBox(Modifier.padding(horizontal=16.dp).fillMaxWidth()){ExerciseAnimation(ex);Spacer(Modifier.height(12.dp));Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("BEGINNER","STANDARD","QUICK").forEach{FilterChip(selected=mode==it,onClick={mode=it},label={Text(it)})}}}}
        if(mode=="QUICK"){
            item{InfoSection("QUICK FORM CHECK",tutorial["quick"]!!)}
        }else{
            item{InfoSection("HOW TO PERFORM",tutorial["setup"]!!+"\n\n"+tutorial["start"]!!)}
            item{InfoSection("STEP-BY-STEP",tutorial["steps"]!!)}
            item{InfoSection("BREATHING",tutorial["breathing"]!!)}
            item{InfoSection("TEMPO / CONTROL",tutorial["tempo"]!!)}
            item{InfoSection("RANGE OF MOTION",tutorial["rom"]!!)}
            item{InfoSection("WHAT SHOULD STAY STABLE",tutorial["stable"]!!)}
            item{InfoSection("FORM CUES",tutorial["quick"]!!)}
            item{InfoSection("COMMON MISTAKES",tutorial["mistakes"]!!)}
            item{InfoSection("BEGINNER NOTES",tutorial["beginner"]!!)}
        }
        item{Row(Modifier.padding(16.dp),horizontalArrangement=Arrangement.spacedBy(10.dp)){Button(onClick={showToastlessAdd(ex,state,save)},colors=ButtonDefaults.buttonColors(containerColor=Accent,contentColor=Color.Black),modifier=Modifier.weight(1f)){Text("ADD TO PLAN")};OutlinedButton(onClick={nav::popBackStack},modifier=Modifier.weight(1f)){Text("BACK")}}}
    }
}
fun showToastlessAdd(ex:Exercise,state:AppState,save:()->Unit){
    val plan=state.plans.firstOrNull()?:WorkoutPlan(name="My Plan").also{it.days.add(WorkoutDay(name="Day 1"));state.plans.add(it);state.selectedPlanId=it.id;state.selectedDayId=it.days.first().id}
    val day=plan.days.firstOrNull()?:WorkoutDay(name="Day 1").also{plan.days.add(it)}
    if(day.exercises.none{it.exerciseId==ex.id}) day.exercises.add(PlannedExercise(ex.id))
    save()
}
@Composable fun InfoSection(title:String,text:String){itemSpacer();CardBox(Modifier.padding(horizontal=16.dp).fillMaxWidth()){Text(title,color=Accent,fontWeight=FontWeight.Bold,fontSize=12.sp,letterSpacing=1.2.sp);Spacer(Modifier.height(7.dp));Text(text,color=TextMain,lineHeight=21.sp)}}
@Composable fun itemSpacer(){Spacer(Modifier.height(10.dp))}

@Composable
fun ExerciseAnimation(ex:Exercise){
    val infinite= rememberInfiniteTransition(label="anim"); val t by infinite.animateFloat(0f,1f,infiniteRepeatable(tween(1600,easing=LinearEasing),RepeatMode.Reverse),label="phase")
    Canvas(Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(18.dp)).background(Color(0xFF0E0E11))){
        val cx=size.width/2; val ground=size.height*0.78f; val motion=(sin(t*Math.PI.toFloat()*2f)*0.22f)
        val head=Offset(cx,ground-135); drawCircle(TextMain,18f,head)
        val shoulder=Offset(cx,ground-105); val hip=Offset(cx,ground-45); drawLine(TextMain,shoulder,hip,8f); drawLine(TextMain,hip,Offset(cx-30,ground),7f);drawLine(TextMain,hip,Offset(cx+30,ground),7f)
        val armShift=(when(ex.pattern){"lateral_raise"->55f*motion;"front_raise"->35f*motion;"curl"->-35f*motion;"triceps_extension"->45f*motion;"horizontal_pull","vertical_pull"->-25f*motion;"horizontal_push","incline_push","vertical_push"->25f*motion;else->15f*motion})
        drawLine(TextMain,shoulder,Offset(cx-40,ground-80+armShift),7f); drawLine(TextMain,Offset(cx-40,ground-80+armShift),Offset(cx-60,ground-35+armShift),7f)
        drawLine(TextMain,shoulder,Offset(cx+40,ground-80-armShift),7f); drawLine(TextMain,Offset(cx+40,ground-80-armShift),Offset(cx+60,ground-35-armShift),7f)
        when(ex.equipment){"Barbell","EZ Bar","Smith Machine"->{drawLine(Accent,Offset(cx-75,ground-92+armShift),Offset(cx+75,ground-92-armShift),8f)};"Dumbbell"->{drawCircle(Accent,8f,Offset(cx-63,ground-35+armShift));drawCircle(Accent,8f,Offset(cx+63,ground-35-armShift))};"Cable"->{drawLine(Accent,Offset(cx+80,ground-10),Offset(cx+60,ground-35-armShift),3f)} }
        drawLine(Color(0xFF3A3A42),Offset(30f,ground+10),Offset(size.width-30f,ground+10),2f)
    }
}

@Composable
fun PlansScreen(nav:NavHostController,state:AppState,exercises:List<Exercise>,save:()->Unit){
    var show by remember{mutableStateOf(false)}; var name by remember{mutableStateOf("")}; var showConfig by remember{mutableStateOf<PlannedExercise?>(null)}
    if(show) AlertDialog(onDismissRequest={show=false},title={Text("New plan")},text={OutlinedTextField(name,{name=it},label={Text("Plan name")})},confirmButton={Button(onClick={if(name.isNotBlank()){val p=WorkoutPlan(name=name);p.days.add(WorkoutDay(name="Day 1"));state.plans.add(p);state.selectedPlanId=p.id;state.selectedDayId=p.days.first().id;save();show=false;name=""}}){Text("Create")}},dismissButton={TextButton(onClick={show=false}){Text("Cancel")}})
    Column(Modifier.fillMaxSize()){ArcTop("Plans","Build any split you want")
        Button(onClick={show=true},Modifier.padding(horizontal=16.dp).fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Accent,contentColor=Color.Black)){Icon(Icons.Default.Add,null);Spacer(Modifier.width(5.dp));Text("CREATE PLAN")}
        LazyColumn(contentPadding=PaddingValues(16.dp,12.dp,16.dp,120.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){items(state.plans,key={it.id}){p->CardBox(Modifier.fillMaxWidth().clickable{nav.navigate("plan/${p.id}")}){Text(p.name,fontSize=19.sp,fontWeight=FontWeight.Bold);Text("${p.days.size} days • ${p.days.sumOf{it.exercises.size}} exercises",color=TextMuted);Spacer(Modifier.height(8.dp));p.days.take(7).forEach{d->Text("• ${d.name}${if(d.restDay)"  · REST" else ""}",fontSize=13.sp,color=if(d.restDay)TextMuted else TextMain)}}}}
    }
}

@Composable
fun PlanEditorScreen(nav:NavHostController,state:AppState,plan:WorkoutPlan,exercises:List<Exercise>,save:()->Unit){
    var refresh by remember{mutableStateOf(0)}; var showDay by remember{mutableStateOf(false)}; var dayName by remember{mutableStateOf("")}; var selectedDay by remember{mutableStateOf(plan.days.firstOrNull())}; var showExercise by remember{mutableStateOf(false)}
    Column(Modifier.fillMaxSize()){ArcTop(plan.name,"Custom split"){nav.popBackStack()}
        Row(Modifier.padding(horizontal=16.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick={showDay=true},colors=ButtonDefaults.buttonColors(containerColor=Accent,contentColor=Color.Black),modifier=Modifier.weight(1f)){Text("ADD DAY")};OutlinedButton(onClick={plan.days.add(WorkoutDay(name="Rest",restDay=true));refresh++;save()},modifier=Modifier.weight(1f)){Text("REST DAY")}}
        LazyColumn(contentPadding=PaddingValues(16.dp,12.dp,16.dp,120.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){items(plan.days,key={it.id}){d->
            CardBox(Modifier.fillMaxWidth().clickable{selectedDay=d;state.selectedPlanId=plan.id;state.selectedDayId=d.id;save()}){Row(verticalAlignment=Alignment.CenterVertically){Column(Modifier.weight(1f)){Text(d.name,fontWeight=FontWeight.Bold);Text(if(d.restDay)"Rest day" else "${d.exercises.size} exercises",color=TextMuted)};IconButton(onClick={if(!d.restDay){selectedDay=d;showExercise=true}}){Icon(Icons.Default.Add,null)};IconButton(onClick={val idx=plan.days.indexOf(d);if(idx>0){java.util.Collections.swap(plan.days,idx,idx-1);refresh++;save()}}){Icon(Icons.Default.KeyboardArrowUp,null)};IconButton(onClick={val idx=plan.days.indexOf(d);if(idx<plan.days.lastIndex){java.util.Collections.swap(plan.days,idx,idx+1);refresh++;save()}}){Icon(Icons.Default.KeyboardArrowDown,null)};IconButton(onClick={plan.days.remove(d);refresh++;save()}){Icon(Icons.Default.Delete,null,tint=Color.Red)}}}
            d.exercises.forEachIndexed{idx,pe->val ex=exercises.firstOrNull{it.id==pe.exerciseId}; if(ex!=null) Row(Modifier.fillMaxWidth().clickable{selectedDay=d;showConfig=pe}.padding(top=6.dp),verticalAlignment=Alignment.CenterVertically){Text("${idx+1}. ${ex.name} • ${pe.sets}×${pe.repsMin}-${pe.repsMax} • ${pe.restSeconds}s",fontSize=13.sp,color=TextMuted,modifier=Modifier.weight(1f));Icon(Icons.Default.Edit,null,tint=TextMuted,modifier=Modifier.size(18.dp))}}
            }
        }}
    }
    if(showDay)AlertDialog(onDismissRequest={showDay=false},title={Text("New day")},text={OutlinedTextField(dayName,{dayName=it},label={Text("Day name")})},confirmButton={Button(onClick={if(dayName.isNotBlank()){plan.days.add(WorkoutDay(name=dayName));save();showDay=false;dayName=""}}){Text("Add")}},dismissButton={TextButton(onClick={showDay=false}){Text("Cancel")}})
    if(showExercise && selectedDay!=null)ExercisePickerDialog(exercises,{ex->{selectedDay!!.exercises.add(PlannedExercise(ex.id));save();showExercise=false;refresh++}},onDismiss={showExercise=false})
    if(showConfig!=null) PlanExerciseConfigDialog(showConfig!!,{save();showConfig=null},onDelete={selectedDay?.exercises?.remove(showConfig!!);save();showConfig=null})
}

@Composable
fun PlanExerciseConfigDialog(pe:PlannedExercise,onSave:()->Unit,onDelete:()->Unit){var sets by remember{mutableStateOf(pe.sets.toString())};var min by remember{mutableStateOf(pe.repsMin.toString())};var max by remember{mutableStateOf(pe.repsMax.toString())};var weight by remember{mutableStateOf(pe.weight.toString())};var rest by remember{mutableStateOf(pe.restSeconds.toString())};var rir by remember{mutableStateOf(pe.rir.toString())};AlertDialog(onDismissRequest=onSave,title={Text("Exercise settings")},text={Column(verticalArrangement=Arrangement.spacedBy(6.dp)){OutlinedTextField(sets,{sets=it},label={Text("Sets")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number));OutlinedTextField(min,{min=it},label={Text("Min reps")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number));OutlinedTextField(max,{max=it},label={Text("Max reps")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number));OutlinedTextField(weight,{weight=it},label={Text("Target weight")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal));OutlinedTextField(rest,{rest=it},label={Text("Rest seconds")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number));OutlinedTextField(rir,{rir=it},label={Text("RIR")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number))}},confirmButton={Button(onClick={pe.sets=sets.toIntOrNull()?:3;pe.repsMin=min.toIntOrNull()?:8;pe.repsMax=max.toIntOrNull()?:12;pe.weight=weight.toDoubleOrNull()?:0.0;pe.restSeconds=rest.toIntOrNull()?:90;pe.rir=rir.toIntOrNull()?:2;onSave()}){Text("Save")}},dismissButton={TextButton(onClick=onDelete){Text("Remove")}})}

@Composable
fun ExercisePickerDialog(exercises:List<Exercise>,onAdd:(Exercise)->Unit,onDismiss:()->Unit){var q by remember{mutableStateOf("")};val list=exercises.filter{q.isBlank()||it.name.contains(q,true)||it.primaryMuscle.contains(q,true)};AlertDialog(onDismissRequest=onDismiss,title={Text("Add exercise")},text={Column{OutlinedTextField(q,{q=it},label={Text("Search")},singleLine=true);Spacer(Modifier.height(8.dp));LazyColumn(Modifier.heightIn(max=420.dp)){items(list.take(100)){ex->Text(ex.name,Modifier.fillMaxWidth().clickable{onAdd(ex)}.padding(10.dp))}}}},confirmButton={TextButton(onClick=onDismiss){Text("Done")}})}

@Composable
fun WorkoutScreen(nav:NavHostController,state:AppState,exercises:List<Exercise>,save:()->Unit){
    val plan=state.plans.firstOrNull{it.id==state.selectedPlanId}?:state.plans.firstOrNull(); val day=plan?.days?.firstOrNull{it.id==state.selectedDayId}?:plan?.days?.firstOrNull(); var active by remember{mutableStateOf(false)}; var currentIdx by remember{mutableStateOf(0)}; var session by remember{mutableStateOf<WorkoutSession?>(null)}; var weightText by remember{mutableStateOf("")};var repsText by remember{mutableStateOf("")};var timer by remember{mutableStateOf(0)};var running by remember{mutableStateOf(false)}
    LaunchedEffect(running,timer){while(running&&timer>0){delay(1000);timer--;if(timer==0)running=false}}
    if(plan==null||day==null){Column(Modifier.fillMaxSize()){ArcTop("Workout");Text("Create a plan first.",modifier=Modifier.padding(20.dp),color=TextMuted)}} else if(!active){Column(Modifier.fillMaxSize()){ArcTop("Workout",day.name);CardBox(Modifier.padding(16.dp).fillMaxWidth()){Text("${day.exercises.size} exercises",fontSize=22.sp,fontWeight=FontWeight.Bold);Spacer(Modifier.height(10.dp));day.exercises.forEach{pe->Text("• ${exercises.firstOrNull{it.id==pe.exerciseId}?.name ?: "Exercise"}",color=TextMuted,modifier=Modifier.padding(vertical=3.dp))};Spacer(Modifier.height(12.dp));Button(onClick={session=WorkoutSession(planId=plan.id,dayId=day.id,startedAt=System.currentTimeMillis());active=true;currentIdx=0},colors=ButtonDefaults.buttonColors(containerColor=Accent,contentColor=Color.Black),modifier=Modifier.fillMaxWidth()){Text("START WORKOUT")}}}}
    else {val pe=day.exercises.getOrNull(currentIdx);val ex=pe?.let{p->exercises.firstOrNull{it.id==p.exerciseId}};Column(Modifier.fillMaxSize()){ArcTop(day.name,"Exercise ${currentIdx+1}/${day.exercises.size}"){active=false};if(ex!=null){LazyColumn(contentPadding=PaddingValues(16.dp,0.dp,16.dp,120.dp)){item{CardBox(Modifier.fillMaxWidth()){Text(ex.name,fontSize=24.sp,fontWeight=FontWeight.Bold);Spacer(Modifier.height(8.dp));ExerciseAnimation(ex);Spacer(Modifier.height(10.dp));Text("Target ${pe!!.sets} sets • ${pe.repsMin}-${pe.repsMax} reps • ${pe.restSeconds}s rest",color=TextMuted);Spacer(Modifier.height(10.dp));Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedTextField(weightText,{weightText=it},Modifier.weight(1f),label={Text("Weight")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal));OutlinedTextField(repsText,{repsText=it},Modifier.weight(1f),label={Text("Reps")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Number))};Spacer(Modifier.height(10.dp));Button(onClick={val w=weightText.toDoubleOrNull()?:pe.weight;val r=repsText.toIntOrNull()?:pe.repsMin;session!!.sets.add(LoggedSet(ex.id,w,r,pe.rir));timer=pe.restSeconds;running=true;weightText="";repsText="";save()},colors=ButtonDefaults.buttonColors(containerColor=Accent,contentColor=Color.Black),modifier=Modifier.fillMaxWidth()){Text("SAVE SET + START REST")};Spacer(Modifier.height(8.dp));if(timer>0){Text("REST ${timer/60}:${(timer%60).toString().padStart(2,'0')}",fontSize=34.sp,fontWeight=FontWeight.Bold);Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){OutlinedButton(onClick={timer+=15}){Text("+15")};OutlinedButton(onClick={timer+=30}){Text("+30")};OutlinedButton(onClick={timer=0;running=false}){Text("SKIP")}}};Spacer(Modifier.height(14.dp));Row(horizontalArrangement=Arrangement.spacedBy(8.dp),modifier=Modifier.fillMaxWidth()){OutlinedButton(onClick={if(currentIdx>0)currentIdx--},modifier=Modifier.weight(1f)){Text("PREVIOUS")};OutlinedButton(onClick={if(currentIdx<day.exercises.lastIndex)currentIdx++ else {state.sessions.add(session!!.copy(endedAt=System.currentTimeMillis()));save();active=false}},modifier=Modifier.weight(1f)){Text(if(currentIdx<day.exercises.lastIndex)"NEXT":"FINISH")}}}}}}
        }
    }
}

@Composable
fun WarmupScreen(nav:NavHostController,day:WorkoutDay,exercises:List<Exercise>){val items=remember(day.id){warmupFor(day,exercises)};var index by remember{mutableStateOf(0)};var seconds by remember{mutableStateOf(items.firstOrNull()?.seconds?:0)};var running by remember{mutableStateOf(false)};LaunchedEffect(running,seconds){while(running&&seconds>0){delay(1000);seconds--;if(seconds==0)running=false}};LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(bottom=120.dp)){item{ArcTop("Warm-up",day.name){nav.popBackStack()}};items.forEachIndexed{i,w->item{CardBox(Modifier.padding(horizontal=16.dp).fillMaxWidth()){Text("${i+1}. ${w.name}",fontWeight=FontWeight.Bold,fontSize=18.sp);Text(w.instructions,color=TextMuted,lineHeight=20.sp);Text(if(w.reps>0)"${w.reps} reps" else "${w.seconds}s",color=Accent,fontWeight=FontWeight.Bold);if(i==index){Spacer(Modifier.height(8.dp));Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick={running=!running},colors=ButtonDefaults.buttonColors(containerColor=Accent,contentColor=Color.Black)){Text(if(running)"PAUSE":"START")};OutlinedButton(onClick={seconds=w.seconds;running=false}){Text("RESET")};OutlinedButton(onClick={index=(index+1).coerceAtMost(items.lastIndex);seconds=items.getOrNull(index+1)?.seconds?:0;running=false}){Text("NEXT")}}}}}}}
}}

@Composable
fun StretchScreen(nav:NavHostController,day:WorkoutDay,exercises:List<Exercise>){val items=remember(day.id){stretchesFor(day,exercises)};var index by remember{mutableStateOf(0)};var seconds by remember{mutableStateOf(items.firstOrNull()?.seconds?:30)};var running by remember{mutableStateOf(false)};LaunchedEffect(running,seconds){while(running&&seconds>0){delay(1000);seconds--;if(seconds==0)running=false}};LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(bottom=120.dp)){item{ArcTop("Stretch & Cool-down",day.name){nav.popBackStack()}};items.forEachIndexed{i,s->item{CardBox(Modifier.padding(horizontal=16.dp).fillMaxWidth()){Text(s.name,fontWeight=FontWeight.Bold,fontSize=18.sp);Text(s.target,color=Accent,fontSize=12.sp);Text(s.instructions,color=TextMuted,lineHeight=20.sp);if(i==index){Spacer(Modifier.height(8.dp));Text("${seconds}s",fontSize=30.sp,fontWeight=FontWeight.Bold);Row(horizontalArrangement=Arrangement.spacedBy(8.dp)){Button(onClick={running=!running},colors=ButtonDefaults.buttonColors(containerColor=Accent,contentColor=Color.Black)){Text(if(running)"PAUSE":"START")};OutlinedButton(onClick={seconds=s.seconds;running=false}){Text("RESET")};OutlinedButton(onClick={index=(index+1).coerceAtMost(items.lastIndex);seconds=items.getOrNull(index+1)?.seconds?:30;running=false}){Text("NEXT")}}}}}}}
}}

@Composable
fun GoalsScreen(state:AppState,exercises:List<Exercise>,save:()->Unit){var show by remember{mutableStateOf(false)};var name by remember{mutableStateOf("")};var target by remember{mutableStateOf("")};var unit by remember{mutableStateOf("kg")};if(show)AlertDialog(onDismissRequest={show=false},title={Text("New goal")},text={Column{OutlinedTextField(name,{name=it},label={Text("Goal")});OutlinedTextField(target,{target=it},label={Text("Target")},keyboardOptions=KeyboardOptions(keyboardType=KeyboardType.Decimal));OutlinedTextField(unit,{unit=it},label={Text("Unit")}}},confirmButton={Button(onClick={if(name.isNotBlank()){state.goals.add(Goal(name=name,type="custom",target=target.toDoubleOrNull()?:0.0,unit=unit));save();show=false}}){Text("Create")}},dismissButton={TextButton(onClick={show=false}){Text("Cancel")}})
    Column(Modifier.fillMaxSize()){ArcTop("Goals","Track performance and consistency");Button(onClick={show=true},Modifier.padding(16.dp).fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Accent,contentColor=Color.Black)){Text("CREATE GOAL")};LazyColumn(contentPadding=PaddingValues(16.dp,0.dp,16.dp,120.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){items(state.goals,key={it.id}){g->val p=if(g.target>0)(g.current/g.target).coerceIn(0.0,1.0)else 0.0;CardBox(Modifier.fillMaxWidth()){Text(g.name,fontWeight=FontWeight.Bold,fontSize=18.sp);Text("${g.current} / ${g.target} ${g.unit}",color=TextMuted);Spacer(Modifier.height(8.dp));LinearProgressIndicator({p.toFloat()},modifier=Modifier.fillMaxWidth(),color=Accent,trackColor=Surface2)}}}}
}

@Composable
fun HistoryScreen(state:AppState,exercises:List<Exercise>){Column(Modifier.fillMaxSize()){ArcTop("History","Every completed workout");LazyColumn(contentPadding=PaddingValues(16.dp,0.dp,16.dp,120.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){items(state.sessions.asReversed(),key={it.id}){s->CardBox(Modifier.fillMaxWidth()){val plan=state.plans.firstOrNull{it.id==s.planId};val day=plan?.days?.firstOrNull{it.id==s.dayId};Text(day?.name ?: "Workout",fontWeight=FontWeight.Bold,fontSize=18.sp);Text("${s.sets.size} sets • ${(s.endedAt?:System.currentTimeMillis()-s.startedAt)/1000/60} min",color=TextMuted);s.sets.take(5).forEach{ls->Text("${exercises.firstOrNull{e->e.id==ls.exerciseId}?.name}: ${ls.weight} × ${ls.reps}",fontSize=12.sp,color=TextMuted)}}}}}}
}

@Composable
fun ChecklistScreen(state:AppState,save:()->Unit){var list by remember{mutableStateOf(state.checklists.firstOrNull())};var show by remember{mutableStateOf(false)};var text by remember{mutableStateOf("")};if(list==null){list=Checklist(name="Daily");state.checklists.add(list!!);save()};if(show)AlertDialog(onDismissRequest={show=false},title={Text("Add item")},text={OutlinedTextField(text,{text=it},label={Text("Item")})},confirmButton={Button(onClick={if(text.isNotBlank()){list!!.items.add(ChecklistItem(title=text));text="";show=false;save()}}){Text("Add")}},dismissButton={TextButton(onClick={show=false}){Text("Cancel")}});Column(Modifier.fillMaxSize()){ArcTop("Checklist","Daily and gym tasks");Button(onClick={show=true},Modifier.padding(16.dp).fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Accent,contentColor=Color.Black)){Text("ADD ITEM")};LazyColumn(contentPadding=PaddingValues(16.dp,0.dp,16.dp,120.dp)){items(list!!.items,key={it.id}){item->Row(Modifier.fillMaxWidth().padding(vertical=6.dp),verticalAlignment=Alignment.CenterVertically){Checkbox(item.done,{item.done=it;save()});Text(item.title,Modifier.weight(1f),textDecoration=if(item.done)androidx.compose.ui.text.style.TextDecoration.LineThrough else null);IconButton(onClick={list!!.items.remove(item);save()}){Icon(Icons.Default.Delete,null,tint=Color.Red)}}}}}}
}

@Composable
fun ReportsScreen(state:AppState,exercises:List<Exercise>){val context=LocalContext.current;var range by remember{mutableStateOf("WEEK")};val report=remember(state.sessions.size,state.goals.size,range){buildReport(state,exercises,range)};Column(Modifier.fillMaxSize()){ArcTop("Reports","Daily / weekly / monthly summaries");Row(Modifier.padding(horizontal=16.dp),horizontalArrangement=Arrangement.spacedBy(8.dp)){listOf("DAY","WEEK","MONTH").forEach{FilterChip(selected=range==it,onClick={range=it},label={Text(it)})}};CardBox(Modifier.padding(16.dp).fillMaxWidth()){Text(report,fontSize=13.sp,lineHeight=19.sp)};Button(onClick={val i=Intent(Intent.ACTION_SEND).apply{type="text/plain";putExtra(Intent.EXTRA_TEXT,report)};context.startActivity(Intent.createChooser(i,"Share ARC report"))},Modifier.padding(horizontal=16.dp).fillMaxWidth(),colors=ButtonDefaults.buttonColors(containerColor=Accent,contentColor=Color.Black)){Icon(Icons.Default.Share,null);Spacer(Modifier.width(8.dp));Text("SHARE REPORT")}}}
fun buildReport(state:AppState,exercises:List<Exercise>,range:String):String {val now=System.currentTimeMillis();val window=when(range){"DAY"->1*24*60*60*1000L;"MONTH"->30*24*60*60*1000L;else->7*24*60*60*1000L};val sessions=state.sessions.filter{now-it.startedAt<=window};return buildString{append("ARC REPORT — $range\n\nSessions: ${sessions.size}\nActive goals: ${state.goals.count{it.active}}\nChecklist items: ${state.checklists.sumOf{it.items.size}}\n\nRecent workouts:\n");if(sessions.isNotEmpty()){sessions.takeLast(10).asReversed().forEach{ss->append("• ${ss.sets.size} sets — ");append((state.plans.firstOrNull{it.id==ss.planId}?.days?.firstOrNull{it.id==ss.dayId}?.name)?:"Workout");append("\n")}}else append("No completed workout in this period.\n")}}

@Composable
fun SettingsScreen(state:MutableState<AppState>,store:AppStateStore,exercises:MutableList<Exercise>,save:()->Unit,snackbar:SnackbarHostState){val context=LocalContext.current;val scope=rememberCoroutineScope();var syncing by remember{mutableStateOf(false)};val exportLauncher=rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/octet-stream")){uri:Uri?->if(uri!=null)context.contentResolver.openOutputStream(uri)?.use{out->java.io.ObjectOutputStream(out).use{it.writeObject(state.value)}}};val importLauncher=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {uri:Uri?->if(uri!=null){try{context.contentResolver.openInputStream(uri)?.use{input->val imported=java.io.ObjectInputStream(input).use{it.readObject() as AppState};state.value=imported;save()};scope.launch{snackbar.showSnackbar("Backup restored")}}catch(e:Exception){scope.launch{snackbar.showSnackbar("Backup could not be read")}}}}
    Column(Modifier.fillMaxSize()){ArcTop("Settings","ARC data and preferences");LazyColumn(contentPadding=PaddingValues(16.dp,0.dp,16.dp,120.dp),verticalArrangement=Arrangement.spacedBy(10.dp)){
        item{CardBox(Modifier.fillMaxWidth()){Text("EXERCISE LIBRARY",fontWeight=FontWeight.Bold);Text("Bundled: ${exercises.size}",color=TextMuted);Spacer(Modifier.height(8.dp));Button(enabled=!syncing,onClick={syncing=true;scope.launch{val count=syncExercises(exercises);syncing=false;snackbar.showSnackbar("Synced $count open exercises")}},colors=ButtonDefaults.buttonColors(containerColor=Accent,contentColor=Color.Black)){Text(if(syncing)"SYNCING…":"SYNC 800+ OPEN EXERCISES")};Text("The sync uses an open dataset and caches the results locally. After syncing, the core exercise library works offline.",fontSize=12.sp,color=TextMuted)} }
        item{CardBox(Modifier.fillMaxWidth()){Text("DATA",fontWeight=FontWeight.Bold);Button(onClick={exportLauncher.launch("arc-backup.bin")},modifier=Modifier.fillMaxWidth()){Text("EXPORT BACKUP")};Spacer(Modifier.height(6.dp));OutlinedButton(onClick={importLauncher.launch(arrayOf("application/octet-stream","*/*"))},modifier=Modifier.fillMaxWidth()){Text("IMPORT BACKUP")}}}
        item{CardBox(Modifier.fillMaxWidth()){Text("DEFAULT REST",fontWeight=FontWeight.Bold);Row(verticalAlignment=Alignment.CenterVertically){Text("${state.value.defaultRest} seconds",Modifier.weight(1f));OutlinedButton(onClick={state.value.defaultRest=(state.value.defaultRest+15).coerceAtMost(300);save()}){Text("+15")}}}}
        item{CardBox(Modifier.fillMaxWidth()){Text("ABOUT ARC",fontWeight=FontWeight.Bold);Text("Native Android workout system. Offline-first, customizable and designed for learning as well as tracking.",color=TextMuted,fontSize=13.sp)}}
    }}
}

suspend fun syncExercises(exercises:MutableList<Exercise>):Int = withContext(Dispatchers.IO) {
    val url=URL("https://raw.githubusercontent.com/kinetic-place/exercises-json/main/en/exercises.json");val conn=url.openConnection() as HttpURLConnection;conn.connectTimeout=20000;conn.readTimeout=30000;conn.requestMethod="GET";return try{val text=conn.inputStream.bufferedReader().use{it.readText()};val arr=JSONArray(text);var added=0;for(i in 0 until arr.length()){val o=arr.getJSONObject(i);val id=o.optString("id");if(exercises.none{it.id==id}){val muscles=o.optJSONArray("muscleGroups")?:JSONArray();val prim=mutableListOf<String>();val sec=mutableListOf<String>();for(j in 0 until muscles.length()){val m=muscles.getJSONObject(j);val n=m.optString("name");if(m.optString("type")=="primary")prim.add(n) else sec.add(n)};val eqArr=o.optJSONArray("equipment")?:JSONArray();val eq=if(eqArr.length()>0)eqArr.getJSONObject(0).optString("name") else "Other";val ins=mutableListOf<String>();val inst=o.optJSONArray("instructions");if(inst!=null)for(j in 0 until inst.length())ins.add(inst.optString(j));val pattern=when(o.optString("forceType")){"push"->if(o.optString("mechanics")=="compound") "horizontal_push" else "isolation"; "pull"->if(o.optString("mechanics")=="compound") "horizontal_pull" else "isolation"; else->"isolation"};exercises.add(Exercise(id=id.ifBlank{java.util.UUID.randomUUID().toString()},name=o.optString("name"),primaryMuscle=prim.firstOrNull() ?: sec.firstOrNull() ?: "Full Body",secondaryMuscles=sec,equipment=eq,pattern=pattern,difficulty=o.optString("difficultyLevel","beginner"),instructions=ins,source="Kinetic.place MIT" ,isCustom=false,animationId=pattern));added++}};added}finally{conn.disconnect()}}
}
