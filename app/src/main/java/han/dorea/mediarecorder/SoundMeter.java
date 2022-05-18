package han.dorea.mediarecorder;

import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;

public class SoundMeter {
    static final private double EMA_FILTER = 0.6;

    private MediaRecorder mRecorder = null;
    private double mEMA = 0.0;

    static final private double decibelConst = 32767.0;


    public void start(java.io.File externalCacheDir) {

        if (mRecorder == null) {

            String fileName = externalCacheDir.getAbsolutePath() + "/audiotest.3gp";

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setOutputFile(fileName);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                mRecorder.prepare();
            } catch (IOException e) {
                Log.e("noise", "prepare() failed");
            }

            mRecorder.start();
            Log.i("noise", "started successfully");

            mEMA = 0.0;
        }
    }

    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if(mRecorder == null)
            return 0;

        return  (mRecorder.getMaxAmplitude()/2700.0);
    }

    public double getAmplitudeRaw() {
        if(mRecorder != null) {
            return mRecorder.getMaxAmplitude();
        }

        return 0;
    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    public double getDecibels() {
        if(mRecorder == null)
            return 0;
        return 20 * Math.log10(mRecorder.getMaxAmplitude() / decibelConst);
    }

}
