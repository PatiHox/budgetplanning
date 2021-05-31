package com.example.budgetplanning.utils

import java.nio.ByteBuffer

object ByteUtils {
    fun Long.lastThreeBytesToFloat(): Float {
        val itemByteBuffer = ByteBuffer.allocate(Long.SIZE_BYTES)
        itemByteBuffer.putLong(this)

        val bytes = itemByteBuffer.array()
        val intByteArr = byteArrayOf(0x0000, bytes[5], bytes[6], bytes[7])
        val intBuffer = ByteBuffer.allocate(Int.SIZE_BYTES)
        intBuffer.put(intByteArr)
        intBuffer.flip()
        val int = intBuffer.int
        return int.toFloat()

    }
}
