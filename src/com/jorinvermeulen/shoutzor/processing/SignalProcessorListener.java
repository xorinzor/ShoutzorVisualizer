/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jorinvermeulen.shoutzor.processing;

/**
 *
 * @author lucasjordan
 */
public interface SignalProcessorListener {
    void process( float[] leftChannel, float[] rightChannel, float frameRateRatioHint );
}
