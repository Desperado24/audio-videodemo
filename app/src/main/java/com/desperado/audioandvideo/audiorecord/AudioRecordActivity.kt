package com.desperado.audioandvideo.audiorecord

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.desperado.audioandvideo.R
import kotlinx.android.synthetic.main.activity_audio_record.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import android.media.AudioManager
import android.media.AudioAttributes
import android.support.annotation.RequiresApi


/**
 *Created liuxun on 2018/12/20
 *Email:liuxun@yy.com
 */

class AudioRecordActivity : AppCompatActivity() {
    val TAG = javaClass.name

    /**
     * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
     */
    val SAMPLE_RATE_INHZ = 44100

    /**
     * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
     */
    val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

    var audioRecord: AudioRecord? = null
    var minBufSize = 0
    var isRecording = false

    var audioTrack: AudioTrack? = null
    var audioData = byteArrayOf()

    var recordFilePath = ""
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_record)
        minBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT)
        recordFilePath = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.wav")?.absolutePath
        tv_control.setOnClickListener {
            if (isRecording) {
                stopRecord()
            } else {
                startRecord()
            }
            tv_control.text = "isRecording: " + isRecording
        }
        tv_convert.setOnClickListener {
            val pcmToWavUtil = PcmToWavUtil(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT)
            val pcmFile = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
            val wavFile = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.wav")
            if (!wavFile.mkdirs()) {
                Log.e(TAG, "wavFile Directory not created")
            }
            if (wavFile.exists()) {
                wavFile.delete()
            }
            pcmToWavUtil.pcmToWav(pcmFile.absolutePath, wavFile.absolutePath)
        }
        tv_play_static.setOnClickListener {
            playByStatic()
        }
        tv_play_steam.setOnClickListener {
            playBySteam()
        }
        tv_stop.setOnClickListener {
            stopPlay()
        }
    }



    fun startRecord() {
        audioRecord = AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT, minBufSize)
        var data = ByteArray(minBufSize)
        var file = File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.pcm")
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        if (file.exists()) {
            file.delete();
        }
        audioRecord?.startRecording()
        isRecording = true

        Thread(Runnable {
            var fos = FileOutputStream(file)
            if (fos != null) {
                while (isRecording) {
                    if (AudioRecord.ERROR_INVALID_OPERATION != audioRecord?.read(data, 0, minBufSize)) {
                        fos.write(data)
                    }
                }
                fos.close()
            }
        }).start()
    }

    fun stopRecord() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }


    fun playByStatic() {
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                var fis = FileInputStream(recordFilePath)
                var out = ByteArrayOutputStream()
                var b = fis.read();
                Log.i(TAG, "Get byte audio data..." + recordFilePath);
                while (b != -1) {
                    out.write(b)
                    b = fis.read()
                }
                audioData = out.toByteArray()
                fis.close()
                out.close()
                Log.i(TAG, "Get byte audio data. end..");
                return null
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onPostExecute(result: Void?) {
                val channelConfig = AudioFormat.CHANNEL_OUT_MONO
                audioTrack = AudioTrack(
                        AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build(),
                        AudioFormat.Builder().setSampleRate(SAMPLE_RATE_INHZ)
                                .setEncoding(AUDIO_FORMAT)
                                .setChannelMask(channelConfig)
                                .build(),
                        audioData.size,
                        AudioTrack.MODE_STATIC,
                        AudioManager.AUDIO_SESSION_ID_GENERATE)
                Log.i(TAG, "Writing audio data...");
                audioTrack?.write(audioData, 0, audioData.size);
                Log.i(TAG, "Starting playback");
                audioTrack?.play();
                Log.i(TAG, "Playing")
            }

        }.execute()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun playBySteam() {
        audioTrack = AudioTrack(
                AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build(),
                AudioFormat.Builder().setSampleRate(SAMPLE_RATE_INHZ).setEncoding(AUDIO_FORMAT).setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build(),
                minBufSize, AudioTrack.MODE_STREAM, AudioManager.AUDIO_SESSION_ID_GENERATE
        )
        audioTrack?.play()
        var fis = FileInputStream(recordFilePath)
        Thread(Runnable {
            var tempBuf = ByteArray(minBufSize)
            while (fis.available() > 0){
                var readCount = fis.read(tempBuf)
                if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
                        readCount == AudioTrack.ERROR_BAD_VALUE) {
                    continue;
                }
                if(readCount != 0 && readCount != -1){
                    audioTrack?.write(tempBuf, 0 , readCount)
                }
            }
        }).start()
    }

    fun stopPlay() {
        audioTrack?.stop()
        audioTrack?.release()
    }


}
