package jp.truetech.shell;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Shell {

    private BufferedReader in;
    private PrintStream out;
    private Map<String, String> variableMap = new HashMap<>();

    public Shell() {
        this(System.in, System.out);
    }

    public Shell(InputStream in) {
        this(in, System.out);
    }

    public Shell(InputStream in, PrintStream out) {
        this.in = new BufferedReader(new InputStreamReader(in));
        this.out = out;
    }

    public void start() throws Exception {
        int exitValue = run();
        Runtime.getRuntime().exit(exitValue);
    }

    public int run() throws Exception {
        int exitStatus = 0;

        while (true) {
            out.print("@ ");
            String line = in.readLine();
            out.println("+ " + line);
            if (line == null) {
                break;
            }

            if (line.startsWith("#")) {
                continue;
            }

            line = expandVariables(line);

            String[] words = parseLine(line);
            if (words.length == 0) {
                continue;
            }
            
            String command = words[0];

            if (command.equals("exit")) {
                if (words.length >= 2) {
                    exitStatus = exitValue(words[1]);
                }
                break;
            }

            if (command.equals("echo")) {
                for (int i = 1; i < words.length; i++) {
                    if (i > 1) {
                        out.print(" ");
                    }
                    out.print(words[i]);
                }
                out.println();
                continue;
            }

            if (command.contains("=")) {
                setVariable(line);
                continue;
            }

            if (command.equals("set")) {
                pritVariables();
                continue;
            }

            if (command.startsWith("/")) {
                exec(line);
                continue;
            }
        }

        return exitStatus;
    }

    String expandVariables(String line) {
        if (!line.contains("$")) {
            return line;
        }
        StringBuilder sb = new StringBuilder(line);
        variableMap.forEach((name, value) -> {
            String s = sb.toString();
            if (s.contains("$" + name)) {
                String regex = "\\$" + name;
                s = s.replaceAll(regex, value);
                sb.setLength(0);
                sb.append(s);
            }
        });
        return sb.toString();
    }

    void exec(String line) throws Exception {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(line);
        InputStream in = process.getInputStream();
        Thread thread = new Thread(() -> {
            try {
                while (true) {
                    int b = in.read();
                    if (b == -1) {
                        break;
                    }
                    this.out.write(b);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        int status = process.waitFor();
        setVariable("?", String.valueOf(status));
        thread.join();
    }

    void setVariable(String line) {
        String[] nameValue = line.split("=", -1);
        String name = nameValue[0];
        String value = nameValue[1];
        setVariable(name, value);
    }

    void setVariable(String name, String value) {
        variableMap.put(name, value);
    }

    void pritVariables() {
        variableMap.forEach((k, v) -> out.println(k + " : " + v));
    }

    String[] parseLine(String line) {
        Objects.requireNonNull(line);
        return line.split("( |¥t)+");
    }

    int exitValue(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }
}
