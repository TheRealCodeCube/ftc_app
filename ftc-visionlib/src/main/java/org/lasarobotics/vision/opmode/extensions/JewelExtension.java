/*
 * Copyright (c) 2016 Arthur Pachachura, LASA Robotics, and contributors
 * MIT licensed
 */
package org.lasarobotics.vision.opmode.extensions;

import org.lasarobotics.vision.detection.objects.Rectangle;
import org.lasarobotics.vision.ftc.resq.Jewel;
import org.lasarobotics.vision.opmode.VisionOpMode;
import org.lasarobotics.vision.util.ScreenOrientation;
import org.opencv.core.Mat;

/**
 * Extension that supports finding and reading beacon color data
 */
public class JewelExtension implements VisionExtension {
    private Jewel jewel;

    private Jewel.JewelAnalysis analysis = new Jewel.JewelAnalysis();

    /**
     * Get latest jewel analysis
     *
     * @return A Jewel.JewelAnalysis struct
     */
    public Jewel.JewelAnalysis getAnalysis() {
        return analysis;
    }

    /**
     * Get the currently used analysis method
     *
     * @return Analysis method
     */
    public Jewel.AnalysisMethod getAnalysisMethod() {
        return jewel.getAnalysisMethod();
    }

    /**
     * Set the analysis method to use for beacon analysis
     *
     * @param method Analysis method to use
     */
    public void setAnalysisMethod(Jewel.AnalysisMethod method) {
        jewel.setAnalysisMethod(method);
    }

    /**
     * Set color tolerance for red beacon detector
     *
     * @param tolerance A color tolerance value from -1 to 1, where 0 is unmodified, 1 is maximum
     *                  tolerance (more colors detect as red), -1 is minimum
     *                  (very few colors detect as red)
     */
    public void setColorToleranceRed(double tolerance) {
        jewel.setColorToleranceRed(tolerance);
    }

    /**
     * Set color tolerance for blue jewel detector
     *
     * @param tolerance A color tolerance value from -1 to 1, where 0 is unmodified, 1 is maximum
     *                  tolerance (more colors detect as blue), -1 is minimum
     *                  (very few colors detect as blue)
     */
    public void setColorToleranceBlue(double tolerance) {
        jewel.setColorToleranceBlue(tolerance);
    }

    /**
     * Set analysis bounds
     * Areas of the image outside of the bounded area will not be processed
     *
     * @param bounds A rectangle containing the boundary
     */
    public void setAnalysisBounds(Rectangle bounds) {
        jewel.setAnalysisBounds(bounds);
    }

    /**
     * Enable debug drawing. Use this on testing apps only, not the robot controller.
     */
    public void enableDebug() {
        jewel.enableDebug();
    }

    /**
     * Disable debug drawing (default). Use this on the robot controller.
     */
    public void disableDebug() {
        jewel.disableDebug();
    }

    @Override
    public void init(VisionOpMode opmode) {
        //Initialize all detectors here
        jewel = new Jewel();
    }

    @Override
    public void loop(VisionOpMode opmode) {

    }

    @Override
    public Mat frame(VisionOpMode opmode, Mat rgba, Mat gray) {
        try {
            //Get screen orientation data
            ScreenOrientation orientation = ScreenOrientation.getFromAngle(
                    VisionOpMode.rotation.getRotationCompensationAngle());

            //Get color analysis
            this.analysis = jewel.analyzeFrame(rgba, gray, orientation);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rgba;
    }

    @Override
    public void stop(VisionOpMode opmode) {

    }
}