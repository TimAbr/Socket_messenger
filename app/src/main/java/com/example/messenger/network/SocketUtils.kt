// SocketUtils.kt
package com.example.messenger.network

import java.io.DataInputStream
import java.io.DataOutputStream

fun DataOutputStream.sendCommand(cmd: Commands) {
    writeInt(MessageType.COMMAND_MESSAGE.ordinal)
    writeInt(cmd.ordinal)
    flush()
}

fun DataOutputStream.sendString(value: String) {
    val bytes = value.toByteArray(Charsets.UTF_8)
    writeInt(MessageType.DATA_MESSAGE.ordinal)
    writeInt(bytes.size)
    write(bytes)
    flush()
}

fun DataOutputStream.sendIntValue(value: Int) {
    writeInt(MessageType.DATA_MESSAGE.ordinal)
    writeInt(Int.SIZE_BYTES)
    writeInt(value)
    flush()
}

fun DataInputStream.readStringValue(): String {
    // Предполагаем, что перед этим уже прочитан MessageType.DATA_MESSAGE
    //readInt()
    val len = readInt()
    val buf = ByteArray(len)
    readFully(buf)
    return String(buf, Charsets.UTF_8)
}

fun DataInputStream.readIntValue(): Int {
    // Предполагаем, что перед этим уже прочитан MessageType.DATA_MESSAGE
    //readInt()
    val size = readInt() // всегда 4
    return readInt()
}

fun DataInputStream.readBooleanValue(): Boolean{
    //readInt()
    val size = readInt()
    return readBoolean()
}
