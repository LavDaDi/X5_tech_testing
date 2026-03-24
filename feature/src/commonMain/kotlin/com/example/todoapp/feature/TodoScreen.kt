package com.example.todoapp.feature

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.model.domain.TodoFilter
import com.example.todoapp.model.domain.TodoItem
import com.example.todoapp.model.domain.TodoTag
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TodoScreen() {
    val viewModel: TodoViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()

    TodoScreenContent(
        state = state,
        onInputChanged = viewModel::onInputTextChanged,
        onAddClick = viewModel::addTodo,
        onToggle = viewModel::toggleTodo,
        onDelete = viewModel::deleteTodo,
        onFilterSelected = viewModel::setFilter,
        onTagSelected = viewModel::onTagSelected,
        onStartEditing = viewModel::startEditing,
        onEditingTextChanged = viewModel::onEditingTextChanged,
        onSaveEditing = viewModel::saveEditing,
        onCancelEditing = viewModel::cancelEditing,
    )
}

@Composable
internal fun TodoScreenContent(
    state: TodoScreenState,
    onInputChanged: (String) -> Unit,
    onAddClick: () -> Unit,
    onToggle: (String) -> Unit,
    onDelete: (String) -> Unit,
    onFilterSelected: (TodoFilter) -> Unit,
    onTagSelected: (TodoTag?) -> Unit,
    onStartEditing: (String, String) -> Unit,
    onEditingTextChanged: (String) -> Unit,
    onSaveEditing: () -> Unit,
    onCancelEditing: () -> Unit,
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = paddingValues.calculateBottomPadding()),
        ) {
            HeaderSection(
                completedCount = state.completedCount,
                totalCount = state.totalCount,
            )

            InputSection(
                inputText = state.inputText,
                onInputChanged = onInputChanged,
                onAddClick = onAddClick,
            )

            TagSelector(
                selectedTag = state.selectedTag,
                onTagSelected = onTagSelected,
            )

            FilterSection(
                currentFilter = state.filter,
                onFilterSelected = onFilterSelected,
                totalCount = state.totalCount,
                activeCount = state.totalCount - state.completedCount,
                completedCount = state.completedCount,
            )

            TodoList(
                todos = state.todos,
                onToggle = onToggle,
                onDelete = onDelete,
                editingTodoId = state.editingTodoId,
                editingText = state.editingText,
                onStartEditing = onStartEditing,
                onEditingTextChanged = onEditingTextChanged,
                onSaveEditing = onSaveEditing,
                onCancelEditing = onCancelEditing,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ==========================================
// HEADER
// ==========================================

@Composable
private fun HeaderSection(
    completedCount: Int,
    totalCount: Int,
) {
    val progress = if (totalCount > 0) {
        completedCount.toFloat() / totalCount.toFloat()
    } else {
        0f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background,
                    ),
                ),
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Мои задачи",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when {
                            totalCount == 0 -> "Пока нет задач"
                            completedCount == totalCount -> "Всё выполнено! 🎉"
                            else -> "$completedCount из $totalCount выполнено"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                AnimatedVisibility(
                    visible = totalCount > 0,
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut(),
                ) {
                    ProgressCircle(progress = animatedProgress)
                }
            }

            AnimatedVisibility(
                visible = totalCount > 0,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = when {
                            progress >= 1f -> Color(0xFF4CAF50)
                            progress >= 0.5f -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.tertiary
                        },
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeCap = StrokeCap.Round,
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressCircle(progress: Float) {
    val percentage = (progress * 100).toInt()

    Surface(
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = when {
            progress >= 1f -> Color(0xFF4CAF50).copy(alpha = 0.15f)
            progress >= 0.5f -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            else -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
        },
    ) {
        Box(contentAlignment = Alignment.Center) {
            AnimatedContent(
                targetState = percentage,
                transitionSpec = {
                    (fadeIn(tween(200)) + scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(200),
                    )).togetherWith(
                        fadeOut(tween(200)) + scaleOut(
                            targetScale = 1.2f,
                            animationSpec = tween(200),
                        ),
                    )
                },
            ) { targetPercentage ->
                Text(
                    text = "$targetPercentage%",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                    ),
                    color = when {
                        progress >= 1f -> Color(0xFF4CAF50)
                        progress >= 0.5f -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.tertiary
                    },
                )
            }
        }
    }
}

// ==========================================
// INPUT
// ==========================================

@Composable
private fun InputSection(
    inputText: String,
    onInputChanged: (String) -> Unit,
    onAddClick: () -> Unit,
) {
    val isNotBlank = inputText.isNotBlank()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onInputChanged,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    "Что нужно сделать?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (isNotBlank) onAddClick() }),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                    alpha = 0.3f,
                ),
            ),
        )

        val buttonScale by animateFloatAsState(
            targetValue = if (isNotBlank) 1f else 0.85f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        )
        val buttonColor by animateColorAsState(
            targetValue = if (isNotBlank) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            animationSpec = tween(300),
        )

        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .size(48.dp)
                .scale(buttonScale),
            containerColor = buttonColor,
            contentColor = if (isNotBlank) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            shape = RoundedCornerShape(14.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить задачу",
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

// ==========================================
// TAG SELECTOR
// ==========================================

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TagSelector(
    selectedTag: TodoTag?,
    onTagSelected: (TodoTag?) -> Unit,
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            text = "Тег:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(4.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TodoTag.allTags.forEach { tag ->
                val isSelected = selectedTag == tag
                val tagColor = Color(tag.color)

                Surface(
                    modifier = Modifier.clickable { onTagSelected(tag) },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) {
                        tagColor.copy(alpha = 0.2f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    },
                    border = if (isSelected) {
                        androidx.compose.foundation.BorderStroke(
                            width = 1.5.dp,
                            color = tagColor,
                        )
                    } else {
                        null
                    },
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(tagColor),
                        )
                        Text(
                            text = tag.name,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                },
                            ),
                            color = if (isSelected) {
                                tagColor
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

// ==========================================
// FILTERS
// ==========================================

@Composable
private fun FilterSection(
    currentFilter: TodoFilter,
    onFilterSelected: (TodoFilter) -> Unit,
    totalCount: Int,
    activeCount: Int,
    completedCount: Int,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TodoFilter.entries.forEach { filter ->
            val count = when (filter) {
                TodoFilter.ALL -> totalCount
                TodoFilter.ACTIVE -> activeCount
                TodoFilter.COMPLETED -> completedCount
            }
            val label = when (filter) {
                TodoFilter.ALL -> "Все"
                TodoFilter.ACTIVE -> "Активные"
                TodoFilter.COMPLETED -> "Готово"
            }
            val isSelected = currentFilter == filter

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) {
                                    FontWeight.SemiBold
                                } else {
                                    FontWeight.Normal
                                },
                            ),
                        )
                        if (count > 0) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                },
                                modifier = Modifier.size(20.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = count.toString(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                    )
                                }
                            }
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = MaterialTheme.colorScheme.outlineVariant.copy(
                        alpha = 0.5f,
                    ),
                    selectedBorderColor = Color.Transparent,
                    enabled = true,
                    selected = isSelected,
                ),
            )
        }
    }
}

// ==========================================
// TODO LIST
// ==========================================

@Composable
private fun TodoList(
    todos: List<TodoItem>,
    onToggle: (String) -> Unit,
    onDelete: (String) -> Unit,
    editingTodoId: String?,
    editingText: String,
    onStartEditing: (String, String) -> Unit,
    onEditingTextChanged: (String) -> Unit,
    onSaveEditing: () -> Unit,
    onCancelEditing: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = todos.isEmpty(),
        modifier = modifier,
        transitionSpec = {
            fadeIn(tween(300)).togetherWith(fadeOut(tween(300)))
        },
    ) { isEmpty ->
        if (isEmpty) {
            EmptyState()
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    horizontal = 16.dp,
                    vertical = 8.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(
                    items = todos,
                    key = { it.id },
                ) { todo ->
                    val isEditing = editingTodoId == todo.id

                    SwipeableTodoItem(
                        todo = todo,
                        isEditing = isEditing,
                        editingText = editingText,
                        onToggle = { onToggle(todo.id) },
                        onDelete = { onDelete(todo.id) },
                        onStartEditing = {
                            onStartEditing(todo.id, todo.text)
                        },
                        onEditingTextChanged = onEditingTextChanged,
                        onSaveEditing = onSaveEditing,
                        onCancelEditing = onCancelEditing,
                        modifier = Modifier.animateItem(
                            fadeInSpec = spring(
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                            fadeOutSpec = spring(
                                stiffness = Spring.StiffnessMediumLow,
                            ),
                            placementSpec = spring(
                                stiffness = Spring.StiffnessMediumLow,
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                            ),
                        ),
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ==========================================
// EMPTY STATE
// ==========================================

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val scale = remember { Animatable(0.3f) }
        LaunchedEffect(Unit) {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow,
                ),
            )
        }

        Text(
            text = "📝",
            fontSize = 64.sp,
            modifier = Modifier.scale(scale.value),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Нет задач",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Добавьте первую задачу выше",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ==========================================
// SWIPEABLE TODO ITEM
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTodoItem(
    todo: TodoItem,
    isEditing: Boolean,
    editingText: String,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onStartEditing: () -> Unit,
    onEditingTextChanged: (String) -> Unit,
    onSaveEditing: () -> Unit,
    onCancelEditing: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isEditing) {
        EditingCard(
            editingText = editingText,
            tag = todo.tag,
            onEditingTextChanged = onEditingTextChanged,
            onSaveEditing = onSaveEditing,
            onCancelEditing = onCancelEditing,
            modifier = modifier,
        )
    } else {
        val dismissState = rememberSwipeToDismissBoxState(
            confirmValueChange = { value ->
                if (value == SwipeToDismissBoxValue.EndToStart) {
                    onDelete()
                    true
                } else {
                    false
                }
            },
        )

        SwipeToDismissBox(
            state = dismissState,
            modifier = modifier,
            backgroundContent = {
                val color by animateColorAsState(
                    targetValue = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.EndToStart -> Color(0xFFEF5350)
                        else -> Color.Transparent
                    },
                    animationSpec = tween(200),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(color)
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
            },
            enableDismissFromStartToEnd = false,
            enableDismissFromEndToStart = true,
        ) {
            TodoCard(
                todo = todo,
                onToggle = onToggle,
                onDelete = onDelete,
                onStartEditing = onStartEditing,
            )
        }
    }
}

// ==========================================
// EDITING CARD — карточка редактирования
// ==========================================

@Composable
private fun EditingCard(
    editingText: String,
    tag: TodoTag?,
    onEditingTextChanged: (String) -> Unit,
    onSaveEditing: () -> Unit,
    onCancelEditing: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant, // <-- Убрали прозрачность!
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            if (tag != null) {
                TagBadge(tag = tag)
                Spacer(modifier = Modifier.height(8.dp))
            }

            TextField(
                value = editingText,
                onValueChange = onEditingTextChanged,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSaveEditing() }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    onClick = onCancelEditing,
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(
                        alpha = 0.5f,
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Отмена",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error,
                        )
                        Text(
                            text = "Отмена",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Surface(
                    onClick = onSaveEditing,
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Row(
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 6.dp,
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Сохранить",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onPrimary,
                        )
                        Text(
                            text = "Сохранить",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// TODO CARD
// ==========================================

@Composable
private fun TodoCard(
    todo: TodoItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onStartEditing: () -> Unit,
) {
    val cardColor by animateColorAsState(
        targetValue = if (todo.isCompleted) {
            MaterialTheme.colorScheme.surfaceVariant // <-- Убрали прозрачность!
        } else {
            MaterialTheme.colorScheme.surface
        },
        animationSpec = tween(300),
    )

    val checkScale = remember { Animatable(1f) }
    LaunchedEffect(todo.isCompleted) {
        checkScale.animateTo(
            targetValue = 0.8f,
            animationSpec = tween(100),
        )
        checkScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
            ),
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (todo.isCompleted) 0.dp else 2.dp,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onToggle,
                modifier = Modifier
                    .size(40.dp)
                    .scale(checkScale.value),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (todo.isCompleted) {
                        Color(0xFF4CAF50).copy(alpha = 0.15f)
                    } else {
                        Color.Transparent
                    },
                ),
            ) {
                AnimatedContent(
                    targetState = todo.isCompleted,
                    transitionSpec = {
                        (scaleIn(
                            initialScale = 0.5f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                            ),
                        ) + fadeIn()).togetherWith(
                            scaleOut(targetScale = 0.5f) + fadeOut(),
                        )
                    },
                ) { isCompleted ->
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Выполнено",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(22.dp),
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = "Не выполнено",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.5f,
                            ),
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                val tag = todo.tag
                if (tag != null) {
                    TagBadge(tag = tag)
                    Spacer(modifier = Modifier.height(2.dp))
                }

                val textColor by animateColorAsState(
                    targetValue = if (todo.isCompleted) {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    animationSpec = tween(300),
                )

                Text(
                    text = todo.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (todo.isCompleted) {
                            FontWeight.Normal
                        } else {
                            FontWeight.Medium
                        },
                        textDecoration = if (todo.isCompleted) {
                            TextDecoration.LineThrough
                        } else {
                            TextDecoration.None
                        },
                    ),
                    color = textColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            IconButton(
                onClick = onStartEditing,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать",
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp),
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить задачу",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

// ==========================================
// TAG BADGE
// ==========================================

@Composable
private fun TagBadge(tag: TodoTag) {
    val tagColor = Color(tag.color)
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = tagColor.copy(alpha = 0.15f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(tagColor),
            )
            Text(
                text = tag.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                ),
                color = tagColor,
            )
        }
    }
}