package com.example.todofirebase.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.todofirebase.model.AuthState
import com.example.todofirebase.model.TodoItem
import com.example.todofirebase.model.TodoPriority
import com.example.todofirebase.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {
    val authState = authViewModel.authState.observeAsState()

    // --- ESTADOS ---
    var taskText by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(TodoPriority.LOW) }
    var taskList by remember { mutableStateOf(listOf<TodoItem>()) }

    // Filtros e Busca
    var searchQuery by remember { mutableStateOf("") }
    var currentFilter by remember { mutableStateOf("Todas") } // Opções: Todas, Pendentes, Concluídas
    var showAddDialog by remember { mutableStateOf(false) } // Controla se mostra o formulário

    // Redireciona se deslogar
    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login") { popUpTo("home") { inclusive = true } }
        }
    }

    // --- CÁLCULOS DO DASHBOARD ---
    val totalTasks = taskList.size
    val doneTasks = taskList.count { it.isDone }
    val pendingTasks = totalTasks - doneTasks
    val progressPercentage = if (totalTasks > 0) (doneTasks.toFloat() / totalTasks) * 100 else 0f

    // --- LÓGICA DE FILTRAGEM ---
    val filteredList = taskList.filter { item ->
        // 1. Filtra pelo texto da busca
        val matchesSearch = item.text.contains(searchQuery, ignoreCase = true)
        // 2. Filtra pelas abas (Todas, Pendentes...)
        val matchesTab = when (currentFilter) {
            "Pendentes" -> !item.isDone
            "Concluídas" -> item.isDone
            else -> true
        }
        matchesSearch && matchesTab
    }.sortedWith(compareBy<TodoItem> { it.isDone }.thenBy { it.priority.ordinal }) // Ordenação

    // --- UI PRINCIPAL ---
    Scaffold(
        containerColor = Color(0xFFF5F7FA), // Fundo cinza claro igual da imagem
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF4B6EFF), // Azul bonito
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Nova Tarefa")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // 1. Cabeçalho
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("TodoList", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4B6EFF))
                    Text("Vamos produzir hoje?", fontSize = 14.sp, color = Color.Gray)
                }
                IconButton(onClick = { authViewModel.signout() }) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Sair", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Dashboard Cards (Estatísticas)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DashboardCard(
                    title = "Pendentes",
                    count = pendingTasks.toString(),
                    icon = Icons.Outlined.PendingActions,
                    color = Color(0xFFFF9F1C), // Laranja
                    modifier = Modifier.weight(1f)
                )
                DashboardCard(
                    title = "Concluídas",
                    count = doneTasks.toString(),
                    icon = Icons.Outlined.CheckCircle,
                    color = Color(0xFF2EC4B6), // Verde água
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Card de Taxa de Conclusão (Largo)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Timeline, contentDescription = null, tint = Color(0xFF4B6EFF), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Taxa de Conclusão", fontSize = 14.sp, color = Color.Gray)
                        Text("${progressPercentage.toInt()}%", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Barra de Busca
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                placeholder = { Text("Buscar tarefas...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color(0xFF4B6EFF)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Filtros (Abas)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterTab("Todas", currentFilter == "Todas") { currentFilter = "Todas" }
                FilterTab("Pendentes", currentFilter == "Pendentes") { currentFilter = "Pendentes" }
                FilterTab("Concluídas", currentFilter == "Concluídas") { currentFilter = "Concluídas" }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Lista de Tarefas
            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredList, key = { it.id }) { item ->
                    BeautifulTodoItemCard(
                        item = item,
                        onDelete = { taskList = taskList.filter { it.id != item.id } },
                        onToggleCheck = {
                            taskList = taskList.map {
                                if (it.id == item.id) it.copy(isDone = !it.isDone) else it
                            }
                        }
                    )
                }
                // Espaço extra no final para o botão flutuante não cobrir o último item
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }

        // --- DIÁLOGO PARA ADICIONAR TAREFA ---
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Nova Tarefa") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = taskText,
                            onValueChange = { taskText = it },
                            label = { Text("O que precisa ser feito?") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("Prioridade", style = MaterialTheme.typography.bodySmall)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TodoPriority.values().forEach { priority ->
                                FilterChip(
                                    selected = selectedPriority == priority,
                                    onClick = { selectedPriority = priority },
                                    label = { Text(priority.label) },
                                    leadingIcon = {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(priority.color))
                                    }
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (taskText.isNotBlank()) {
                                val newItem = TodoItem(text = taskText, priority = selectedPriority)
                                taskList = taskList + newItem
                                taskText = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4B6EFF))
                    ) { Text("Adicionar") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}

// --- COMPONENTES VISUAIS AUXILIARES ---

@Composable
fun DashboardCard(title: String, count: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 12.sp, color = Color.Gray)
            Text(count, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FilterTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFF212121) else Color.White
    val textColor = if (isSelected) Color.White else Color.Black

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun BeautifulTodoItemCard(item: TodoItem, onDelete: () -> Unit, onToggleCheck: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox customizado (bolinha)
            IconButton(onClick = onToggleCheck) {
                Icon(
                    imageVector = if (item.isDone) Icons.Outlined.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if (item.isDone) Color(0xFF2EC4B6) else Color.Gray
                )
            }

            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(
                    text = item.text,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (item.isDone) TextDecoration.LineThrough else null,
                    color = if (item.isDone) Color.Gray else Color.Black
                )

                // Tag de Prioridade Pequena
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = item.priority.color.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.priority.label,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        color = Color.Black
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = Color.LightGray)
            }
        }
    }
}