package com.aayu.jnx;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        Jnx runtime = Jnx.GetInstance();
        if (args.length > 1) {
            System.out.println("Usage: jnx [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runtime.SetFilePath(args[0]);
        }

        runtime.init();
    }
}
