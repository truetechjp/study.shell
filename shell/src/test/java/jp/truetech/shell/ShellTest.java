package jp.truetech.shell;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;

import org.junit.Test;

public class ShellTest {
    
    static final String EOL = System.getProperty("line.separator");

    @Test
    public void コマンドライン分解() {
        String[] lines = {
                "",
                "1 2 3",
                "A     B C   D",
                "X¥t¥t¥tY Z   ¥t0"
        };
        String[][] expects = {
                {""},
                {"1", "2", "3"},
                {"A", "B", "C", "D"},
                {"X", "Y", "Z", "0"}
        };
        Shell shell = new Shell(System.in);
        for (int i = 0; i < lines.length; i++) {
            assertThat(shell.parseLine(lines[i]), is(expects[i]));
        }
    }

    @Test
    public void exit() throws Exception {
        String line = "exit";
        ByteArrayInputStream in = new ByteArrayInputStream(line.getBytes());
        Shell shell = new Shell(in);
        int exitValue = shell.run();
        int expected = 0;
        assertThat(exitValue, is(expected));

        line = "exit 0";
        in = new ByteArrayInputStream(line.getBytes());
        shell = new Shell(in);
        exitValue = shell.run();
        expected = 0;
        assertThat(exitValue, is(expected));

    }

    @Test
    public void exitN() throws Exception {
        String line = "exit 1";
        ByteArrayInputStream in = new ByteArrayInputStream(line.getBytes());
        Shell shell = new Shell(in);
        int exitValue = shell.run();
        int expected = 1;
        assertThat(exitValue, is(expected));
    }

    @Test
    public void echo() throws Exception {
        String line = "echo 1 2 3 4" + EOL + "echo aa bb cc";
        ByteArrayInputStream in = new ByteArrayInputStream(line.getBytes());
        Shell shell = new Shell(in);
        shell.run();
    }

    @Test
    public void variables() throws Exception {
        String line = "AAA=valueAAA" + EOL + "XXX=valueXXX" + EOL + "set";
        ByteArrayInputStream in = new ByteArrayInputStream(line.getBytes());
        Shell shell = new Shell(in);
        shell.run();
    }

    @Test
    public void 変数展開() {
        Shell shell = new Shell();
        shell.setVariable("AAA", "value AAA");
        String line = "$AAA";
        line = shell.expandVariables(line);
        assertThat(line, is("value AAA"));

        shell.setVariable("XXX", "value XXX");
        line = "AAA=[$AAA], XXX=[$XXX], ZZZ=[$ZZZ]";
        line = shell.expandVariables(line);
        assertThat(line, is("AAA=[value AAA], XXX=[value XXX], ZZZ=[$ZZZ]"));

    }
}
