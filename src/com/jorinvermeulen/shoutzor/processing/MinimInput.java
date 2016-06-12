package com.jorinvermeulen.shoutzor.processing;

import java.io.IOException;
import java.io.InputStream;

/* Used to trick minim constructor (needs an object with those methods) */
public class MinimInput {
	String sketchPath( String fileName ) {
        return "";
    }
    InputStream createInput(String fileName) {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
    }
}
