package com.desperado.audioandvideo.audiorecord

import android.media.AudioFormat
import android.media.AudioRecord
import android.util.Log
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException


/**
 *Created liuxun on 2018/12/20
 *Email:liuxun@yy.com
 */

class PcmToWavUtil() {

    private var mBufferSize: Int = 0    //缓存的音频大小
    private var mSampleRate: Int = 0    //采样率
    private var mChannel: Int = 0   //声道数

    constructor(sampleRate: Int, channel: Int, encoding: Int) : this() {
        this.mSampleRate = sampleRate
        this.mChannel = channel
        this.mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannel, encoding)
    }

    fun pcmToWav(inputFilePath: String, outputFilePath: String) {
        Thread(Runnable {
            try {
                var fis = FileInputStream(inputFilePath)
                var fos = FileOutputStream(outputFilePath)
                var totalAudioLength = fis.channel.size()
                var totalDateLength = totalAudioLength + 36
                val data = ByteArray(mBufferSize)
                val channels = if (mChannel === AudioFormat.CHANNEL_IN_MONO) 1 else 2
                val byteRate = 16 * mSampleRate * channels / 8
                writeWaveFileHeader(fos, totalAudioLength, totalDateLength, mSampleRate.toLong(),
                        channels, byteRate.toLong())
                while (fis.read(data) != -1) {
                    fos.write(data)
                }
                fis.close()
                fos.close()
                Log.i("pcmToWav end", outputFilePath)
            } catch (e: IOException) {
                Log.i("pcmToWav ", "IOException" + e.toString())
            }
        }).start()
    }


    /**
     * 加入wav文件头
     */
    private fun writeWaveFileHeader(out: FileOutputStream, totalAudioLen: Long,
                                    totalDataLen: Long, longSampleRate: Long, channels: Int, byteRate: Long) {
        val header = ByteArray(44)
        // RIFF/WAVE header
        header[0] = 'R'.toByte()
        header[1] = 'I'.toByte()
        header[2] = 'F'.toByte()
        header[3] = 'F'.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()
        //WAVE
        header[8] = 'W'.toByte()
        header[9] = 'A'.toByte()
        header[10] = 'V'.toByte()
        header[11] = 'E'.toByte()
        // 'fmt ' chunk
        header[12] = 'f'.toByte()
        header[13] = 'm'.toByte()
        header[14] = 't'.toByte()
        header[15] = ' '.toByte()
        // 4 bytes: size of 'fmt ' chunk
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        // format = 1
        header[20] = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (longSampleRate and 0xff).toByte()
        header[25] = (longSampleRate shr 8 and 0xff).toByte()
        header[26] = (longSampleRate shr 16 and 0xff).toByte()
        header[27] = (longSampleRate shr 24 and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()
        // block align
        header[32] = (2 * 16 / 8).toByte()
        header[33] = 0
        // bits per sample
        header[34] = 16
        header[35] = 0
        //data
        header[36] = 'd'.toByte()
        header[37] = 'a'.toByte()
        header[38] = 't'.toByte()
        header[39] = 'a'.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = (totalAudioLen shr 8 and 0xff).toByte()
        header[42] = (totalAudioLen shr 16 and 0xff).toByte()
        header[43] = (totalAudioLen shr 24 and 0xff).toByte()
        out.write(header, 0, 44)
    }

}