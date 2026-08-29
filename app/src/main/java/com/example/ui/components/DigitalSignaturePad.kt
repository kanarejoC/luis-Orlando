package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.TealPrimary
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SignaturePath(
    val points: List<Offset>
)

@Composable
fun DigitalSignaturePad(
    signerRoleTitle: String,
    signerName: String,
    signerIdCard: String,
    existingSignatureSvg: String = "",
    onSignatureConfirmed: (svgData: String, hash: String, timestamp: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentPath = remember { mutableStateListOf<Offset>() }
    val paths = remember { mutableStateListOf<SignaturePath>() }
    var hasSigned by remember { mutableStateOf(existingSignatureSvg.isNotEmpty()) }
    var confirmedHash by remember { mutableStateOf("") }
    var signatureTimestamp by remember { mutableStateOf(0L) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Create,
                        contentDescription = "Firma",
                        tint = TealPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = signerRoleTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyDark
                        )
                        Text(
                            text = "$signerName • CI: $signerIdCard",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                    }
                }

                if (hasSigned) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GreenSuccess.copy(alpha = 0.15f),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Firmado",
                                tint = GreenSuccess,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Firmado",
                                color = GreenSuccess,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Canvas drawing box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFFAFAFC), RoundedCornerShape(12.dp))
                    .border(
                        width = 1.5.dp,
                        color = if (hasSigned) GreenSuccess else Slate400,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .testTag("signature_canvas_box")
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(hasSigned) {
                            if (!hasSigned) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentPath.clear()
                                        currentPath.add(offset)
                                    },
                                    onDrag = { change, _ ->
                                        change.consume()
                                        currentPath.add(change.position)
                                    },
                                    onDragEnd = {
                                        if (currentPath.isNotEmpty()) {
                                            paths.add(SignaturePath(currentPath.toList()))
                                            currentPath.clear()
                                        }
                                    }
                                )
                            }
                        }
                ) {
                    // Draw guideline dotted/dashed line
                    val lineY = size.height * 0.75f
                    drawLine(
                        color = Color(0xFFCBD5E1),
                        start = Offset(24f, lineY),
                        end = Offset(size.width - 24f, lineY),
                        strokeWidth = 2f
                    )

                    // Draw previous completed strokes
                    for (signaturePath in paths) {
                        if (signaturePath.points.size > 1) {
                            val path = Path().apply {
                                val pts = signaturePath.points
                                moveTo(pts[0].x, pts[0].y)
                                for (i in 1 until pts.size) {
                                    lineTo(pts[i].x, pts[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = NavyDark,
                                style = Stroke(
                                    width = 4.5f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }

                    // Draw current in-progress stroke
                    if (currentPath.size > 1) {
                        val path = Path().apply {
                            moveTo(currentPath[0].x, currentPath[0].y)
                            for (i in 1 until currentPath.size) {
                                lineTo(currentPath[i].x, currentPath[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = NavyPrimary,
                            style = Stroke(
                                width = 4.5f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                if (paths.isEmpty() && currentPath.isEmpty() && !hasSigned) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Firma digital",
                            tint = Slate400,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Firme aquí con su dedo o lápiz táctil",
                            style = MaterialTheme.typography.bodySmall,
                            color = Slate500
                        )
                        Text(
                            text = "Firma electrónica válida según la ley de comercio y contratos",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate400,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            if (!hasSigned) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            paths.clear()
                            currentPath.clear()
                        },
                        modifier = Modifier.testTag("clear_signature_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Limpiar",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Borrar Trazo")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            if (paths.isNotEmpty()) {
                                val timestamp = System.currentTimeMillis()
                                val rawData = "$signerName:$signerIdCard:$timestamp:${paths.size}"
                                val hash = generateSha256(rawData)
                                val svgPathString = serializePathsToString(paths)
                                confirmedHash = hash
                                signatureTimestamp = timestamp
                                hasSigned = true
                                onSignatureConfirmed(svgPathString, hash, timestamp)
                            }
                        },
                        enabled = paths.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                        modifier = Modifier.testTag("confirm_signature_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Confirmar Firma",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Estampar Firma Digital")
                    }
                }
            } else {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val displayDate = if (signatureTimestamp > 0) dateFormat.format(Date(signatureTimestamp)) else "Registrado"
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0FDF4), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = "Validado",
                            tint = GreenSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Firma Electrónica Autenticada y Estampada",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = GreenSuccess
                        )
                    }
                    Text(
                        text = "Fecha y Hora: $displayDate",
                        style = MaterialTheme.typography.labelSmall,
                        color = Slate700
                    )
                    if (confirmedHash.isNotEmpty()) {
                        Text(
                            text = "Hash Criptográfico: $confirmedHash",
                            style = MaterialTheme.typography.labelSmall,
                            color = Slate500,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }
    }
}

private fun generateSha256(input: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(input.toByteArray())
    return "SHA256-" + digest.fold("") { str, it -> str + "%02x".format(it) }.take(32).uppercase()
}

private fun serializePathsToString(paths: List<SignaturePath>): String {
    val builder = StringBuilder()
    for (path in paths) {
        if (path.points.isNotEmpty()) {
            builder.append("M")
            builder.append("${path.points[0].x.toInt()},${path.points[0].y.toInt()}")
            for (i in 1 until path.points.size) {
                builder.append(" L${path.points[i].x.toInt()},${path.points[i].y.toInt()}")
            }
            builder.append(";")
        }
    }
    return builder.toString()
}
