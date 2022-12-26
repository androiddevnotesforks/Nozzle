package com.kaiwolfram.nozzle.data.utils

object Bech32 {
    private const val CHARSET = "qpzry9x8gf2tvdw0s3jn54khce6mua7l"

    private fun polymod(values: ByteArray): Int {
        var c = 1
        for (v_i in values) {
            val c0 = c.ushr(25) and 0xff
            c = c and 0x1ffffff shl 5 xor (v_i.toInt() and 0xff)
            if (c0 and 1 != 0) c = c xor 0x3b6a57b2
            if (c0 and 2 != 0) c = c xor 0x26508e6d
            if (c0 and 4 != 0) c = c xor 0x1ea119fa
            if (c0 and 8 != 0) c = c xor 0x3d4233dd
            if (c0 and 16 != 0) c = c xor 0x2a1462b3
        }
        return c
    }

    private fun expandHrp(hrp: String): ByteArray {
        val hrpLength = hrp.length
        val ret = ByteArray(hrpLength * 2 + 1)
        for (i in 0 until hrpLength) {
            val c = hrp[i].code and 0x7f
            ret[i] = (c.ushr(5) and 0x07).toByte()
            ret[i + hrpLength + 1] = (c and 0x1f).toByte()
        }
        ret[hrpLength] = 0
        return ret
    }

    private fun createChecksum(hrp: String, values: ByteArray): ByteArray {
        val hrpExpanded = expandHrp(hrp)
        val enc = ByteArray(hrpExpanded.size + values.size + 6)
        hrpExpanded.copyInto(enc)
        values.copyInto(enc, startIndex = 0, destinationOffset = hrpExpanded.size)

        val mod = polymod(enc) xor 1
        val ret = ByteArray(6)
        for (i in 0..5) {
            ret[i] = (mod.ushr(5 * (5 - i)) and 31).toByte()
        }
        return ret
    }

    private fun convertTo5BitData(data: ByteArray): ByteArray {
        val output = ArrayList<Byte>()
        var buffer = 0L
        var count = 0
        data.forEach { b ->
            buffer = (buffer shl 8) or (b.toLong() and 0xff)
            count += 8
            while (count >= 5) {
                output.add(((buffer shr (count - 5)) and 31).toByte())
                count -= 5
            }
        }
        if (count > 0) output.add(((buffer shl (5 - count)) and 31).toByte())

        return output.toByteArray()
    }

    fun encode(humanReadablePart: String, data: ByteArray): String {
        var hrp = humanReadablePart
        val as5Bit = convertTo5BitData(data)

        check(hrp.isNotEmpty())
        check(hrp.length <= 83)

        hrp = hrp.lowercase()
        val checksum = createChecksum(hrp, as5Bit)
        val combined = ByteArray(as5Bit.size + checksum.size)
        as5Bit.copyInto(combined)
        checksum.copyInto(combined, startIndex = 0, destinationOffset = as5Bit.size)

        val sb = StringBuilder(hrp.length + 1 + combined.size)
        sb.append(hrp)
        sb.append('1')
        for (b in combined) {
            sb.append(CHARSET[b.toInt()])
        }
        return sb.toString()
    }
}
