package com.example.tree.audio;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

/**
 * 音频转换 wav到opus
 * @Date 2024/2/23 11:35
 */
public class AudioConvert {
    public static void main(String[] args) {
        try {
            FFmpegFrameGrabber grabber = new FFmpegFrameGrabber("C:\\Users\\Administrator\\Documents\\drama.wav");
            grabber.start();

            FFmpegFrameRecorder recorder = new FFmpegFrameRecorder("C:\\Users\\Administrator\\Documents\\output.opus", grabber.getImageWidth(), grabber.getImageHeight(), grabber.getAudioChannels());
            recorder.setAudioCodecName("libopus");
            recorder.setAudioBitrate(grabber.getAudioBitrate()); // 设置音频比特率
            recorder.setSampleRate(16000); // 设置采样率
            recorder.setAudioChannels(1); // 单声道
            recorder.start();

            Frame frame;
            while ((frame = grabber.grab()) != null) {
                recorder.record(frame);
            }

            recorder.stop();
            recorder.release();
            grabber.stop();
            grabber.release();
        } catch (FrameGrabber.Exception | FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }
}
